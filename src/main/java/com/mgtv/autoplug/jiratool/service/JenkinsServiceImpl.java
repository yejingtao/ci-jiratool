package com.mgtv.autoplug.jiratool.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JenkinsServiceImpl implements JenkinsService{
	
	public final Logger logger = LoggerFactory.getLogger(JenkinsServiceImpl.class);


	@Override
	public boolean chechPackage(String baseUrl, String gitProject, String gitTag, String postfix, String auth) {
		String reqeustUrl = baseUrl + "/" + gitProject + "/" + gitTag + postfix;
		
		HttpHead HttpHead = new HttpHead(reqeustUrl);
		HttpHead.setHeader("Authorization", auth);
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(HttpHead);
			logger.info(reqeustUrl+" result is :"+response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode()==org.apache.http.HttpStatus.SC_OK) {
				return true;
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		return false;
	}

}
