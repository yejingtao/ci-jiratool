package com.mgtv.autoplug.jiratool.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mgtv.autoplug.jiratool.message.JiraUser;
import net.sf.json.JSONArray;

@Service
public class JiraUserServiceImpl implements JiraUserService{
	
	public final Logger logger = LoggerFactory.getLogger(JiraUserServiceImpl.class);

	@Override
	public JiraUser findUserByKeyWord(String restUrl, String keyWord, String authInfo) {
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpGet httpGet = new HttpGet(restUrl+keyWord);
		httpGet.setHeader("Authorization", authInfo);
		httpGet.setHeader("Content-type", "application/json");
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpGet);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JiraUser jiraUser = null;
		if (responseBody != null && responseBody!="[]") {				
			JSONArray jsonArray = JSONArray.fromObject(responseBody);
			if (jsonArray != null && jsonArray.size() > 0) {
				for(int i = 0; i < jsonArray.size(); i++) {
					if(jsonArray.getJSONObject(0).getBoolean("active")) {
						jiraUser = new JiraUser();
						jiraUser.setJiraName(jsonArray.getJSONObject(0).getString("name"));
						jiraUser.setEmailAddress(jsonArray.getJSONObject(0).getString("emailAddress"));
						jiraUser.setDisplayName(jsonArray.getJSONObject(0).getString("displayName"));
						break;
					}
				}					
			}			
		}
		logger.info("find jira user: "+(jiraUser==null?null:jiraUser.getJiraName()));
		return jiraUser;
	}

}
