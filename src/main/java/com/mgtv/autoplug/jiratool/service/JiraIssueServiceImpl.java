package com.mgtv.autoplug.jiratool.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgtv.autoplug.jiratool.message.JiraAlarmEntity;
import com.mgtv.autoplug.jiratool.message.JiraEntity;
import com.mgtv.autoplug.jiratool.message.JiraFaultEntity;
import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseSubEntity;
import com.mgtv.autoplug.jiratool.message.JiraTechqaEntity;
import com.mgtv.autoplug.jiratool.message.JiraTechqaSubEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;
import com.mgtv.autoplug.jiratool.util.TimeToolUtil;

import net.sf.json.JSONArray;

@Service
public class JiraIssueServiceImpl implements JiraIssueService {

	public final Logger logger = LoggerFactory.getLogger(JiraIssueServiceImpl.class);

	@Value("${jira.alarm.calmdown}")
	private int calmdown; // alarm创建静默期，单位h

	@Override
	public String createIssue(String restUrl, String postBody, String authInfo) {
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String ticketNum = null;
		try {
			if (responseBody != null) {
				ticketNum = new JSONObject(responseBody).getString("key");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ticket Number: " + ticketNum);
		return ticketNum;
	}

	@Override
	public JiraReleaseEntity findReleaseIssueById(String restUrl, String projectKey, String issueId, String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"id\",\"key\",\"reporter\",\"customfield_10151\",\"customfield_10208\", \"customfield_10223\", \"customfield_10245\", \"customfield_10241\", \"summary\", \"customfield_10147\", \"customfield_10148\", \"customfield_10242\",\"customfield_10259\" ]}";

		ResponseHandler<String> handler = new BasicResponseHandler();

		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JiraReleaseEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraReleaseEntity();
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setSummary(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary"));
						jiraEntity.setGitProject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10208").trim());
						jiraEntity.setOnlineGitTag(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10151"));
						jiraEntity.setRollbackGitTag(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10223"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						jiraEntity.setOperator(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10147"))));
						jiraEntity.setTester(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10148"))));
						jiraEntity.setApprover(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10242"))));
						jiraEntity.setDetail(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10241"));

						String emailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10245");
						if (emailObject != null) {
							JSONArray emailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10245"));
							if (emailArray != null && emailArray.size() > 0) {
								String[] emails = new String[emailArray.size()];
								for (int i = 0; i < emailArray.size(); i++) {
									emails[i] = emailArray.getJSONObject(i).getString("value");
								}
								jiraEntity.setNotifyEmail(emails);
							}
						}

						String personEmailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10259");
						if (personEmailObject != null) {
							JSONArray personEmailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10259"));
							if (personEmailArray != null && personEmailArray.size() > 0) {
								String[] emails = new String[personEmailArray.size()];
								for (int i = 0; i < personEmailArray.size(); i++) {
									emails[i] = personEmailArray.getJSONObject(i).getString("emailAddress");
								}
								jiraEntity.setNotifyEmailPerson(emails);
							}
						}

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	private JiraUser buildJiraUser(JSONObject jSONObject) {
		JiraUser jiraUser = null;
		if (jSONObject != null) {
			jiraUser = new JiraUser();
			try {
				if (jSONObject.get("name") != null) {
					jiraUser.setJiraName(jSONObject.getString("name"));
				}
				if (jSONObject.get("displayName") != null) {
					jiraUser.setDisplayName(jSONObject.getString("displayName"));
				}
				if (jSONObject.get("emailAddress") != null) {
					jiraUser.setEmailAddress(jSONObject.getString("emailAddress"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

		}
		return jiraUser;
	}
	
	
	private String getSelectValue(String str) throws JSONException {
		if(str!=null) {
			return (new JSONObject(str)).getString("value");
		}else {
			return null;
		}		
	}
	
	private Date getDateValue(String str) throws ParseException {
		return TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_2,str);
	}

	@Override
	public String constructReleaseSubPostBody(Map<String, String> map) {
		String postBody = "{\"fields\": { \"project\": { \"key\": \"" + map.get("projectKey")
				+ "\" }, \"parent\":{\"key\": \"" + map.get("parentIssueKey") + "\"}," + "\"reporter\": {\"name\": \""
				+ map.get("reporter") + "\"}, " + "\"summary\": \"" + map.get("summary") + "\", "
				+ "\"customfield_10151\": \"" + map.get("gitOnlineTag") + "\", " + "\"customfield_10208\": \""
				+ map.get("gitProject") + "\", " + "\"customfield_10223\": \"" + map.get("gitRollbackTag") + "\", "
				+ "\"issuetype\": {\"id\": \"" + map.get("issuetype") + "\"}" + "}}";
		return postBody;
	}

	@Override
	public String constructAlarmPostBody(Map<String, Object> map) {
		String postBody = "{\"fields\": { \"project\": { \"key\": \"" + map.get("projectKey") + "\" },"
				+ "\"summary\": \"" + map.get("summary") + "\", " + "\"issuetype\": {\"id\": \"" + map.get("issuetype")
				+ "\"}";
		if (map.get("alamrId") != null) {
			postBody = postBody + ",\"customfield_10246\": \"" + map.get("alamrId") + "\"";
		}
		if (map.get("alarmDetail") != null) {
			postBody = postBody + ",\"customfield_10108\": \"" + map.get("alarmDetail") + "\"";
		}
		if (map.get("alarmType") != null) {
			postBody = postBody + ",\"customfield_10247\": {\"value\": \"" + map.get("alarmType") + "\"}";
		}
		if (map.get("instantane") != null) {
			postBody = postBody + ",\"customfield_10251\": \"" + map.get("instantane") + "\"";
		}
		if (map.get("startTime") != null) {
			// 时间格式按照jira要求提前转换成String
			// postBody = postBody + ",\"customfield_10252\": \"" + map.get("startTime") +
			// "\"" ;
			// postBody = postBody + ",\"customfield_10252\": \"" +
			// ((Date)map.get("startTime")).getTime() + "\"" ;
			postBody = postBody + ",\"customfield_10252\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("startTime")) + "\"";
		}
		if (map.get("endTime") != null) {
			// 时间格式按照jira要求提前转换成String
			// postBody = postBody + ",\"customfield_10119\": \"" + map.get("endTime") +
			// "\"" ;
			postBody = postBody + ",\"customfield_10119\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("endTime")) + "\"";
		}
		if(map.get("assignee") != null) {
			postBody = postBody + ",\"assignee\": {\"name\": \"" + map.get("assignee") + "\"}";
		}
		postBody = postBody + "}}";
		return postBody;
	}

	@Override
	public String[] findReleaseIssueByGit(String restUrl, String projectKey, String issueType, String gitProjectStr,
			String gitTagStr, String authInfo) {
		String[] resultIssueKeys = null;

		String postBody = "{\"jql\":\"project = " + projectKey + "&issuetype=" + issueType
		// + "&statusCategory in (done,new,indeterminate)"
		// + "&cf[10256] in (上线中,成功)"
				+ gitProjectStr + gitTagStr + "\",\"startAt\":0,\"maxResults\":10,\"fields\":[\"id\",\"key\"]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						resultIssueKeys = new String[jsonArray.size()];
						for (int i = 0; i < jsonArray.size(); i++) {
							resultIssueKeys[i] = jsonArray.getJSONObject(i).getString("key");
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return resultIssueKeys;
	}

	@Override
	public void transIssue(String restUrl, String transitionId, String authInfo) {
		String postBody = "{\"transition\":{\"id\":\"" + transitionId + "\"}}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
			logger.info("transIssue is OK, response is: " + responseBody);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}

	}

	@Override
	public void addCommon(String restUrl, String common, String authInfo) {
		String putBody = "{\"update\": {\"comment\": [{\"add\": {\"body\":\"" + common + "\"}}]}}";
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPut httpPut = new HttpPut(restUrl);
		httpPut.setHeader("Authorization", authInfo);
		httpPut.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(putBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPut.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPut);
			responseBody = handler.handleResponse(response);
			logger.info("addCommon is OK, response is: " + responseBody);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}
	}

	@Override
	public Map<String, Double> findAlarmIssue(String restUrl, String projectKey, String issueType,
			Map<String, Object> map, String authInfo) throws ClientProtocolException, IOException {
		Map<String, Double> resultMap = null;

		String postBody = "{\"jql\":\"project = " + projectKey + "&issuetype=" + issueType;
		if (map.get("alamrId") != null) {
			postBody = postBody + "&cf[10246]~" + map.get("alamrId");
		} else {
			// postBody = postBody + "&cf[10252]>'" +
			// TimeToolUtil.getOffsetString(TimeToolUtil.DATE_FORMAT_1, null, Calendar.HOUR,
			// calmdown) + "'";
			// postBody = postBody + "&cf[10252]>" + "'2011-07-05T11:05:00.00+0000'";
			postBody = postBody + "&cf[10252]>" + TimeToolUtil.getOffsetDate(null, Calendar.HOUR, calmdown).getTime();
		}
		if (map.get("alarmDetail") != null) {
			postBody = postBody + "&cf[10108]~" + map.get("alarmDetail");
		}
		postBody = postBody + "\",\"startAt\":0,\"maxResults\":10,\"fields\":[\"id\",\"key\",\"customfield_10260\"]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		response = httpclient.execute(httpPost);
		responseBody = handler.handleResponse(response);

		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						resultMap = new HashMap<String, Double>();
						for (int i = 0; i < jsonArray.size(); i++) {
							resultMap.put(jsonArray.getJSONObject(i).getString("key"),
									new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getDouble("customfield_10260"));
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return resultMap;
	}

	// 构造alarm修改issue的post报文
	// 目前先只支持修改故障恢复时间
	@Override
	public void updateAlarmIssue(String restUrl, Map<String, Object> map, String authInfo) {
		String putBody = null;
		if (map.get("endTime") != null) {
			// 修改告警恢复时间
			putBody = "{\"fields\": {\"customfield_10119\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("endTime")) + "\"}}";
		} else if (map.get("sameCount") != null) {
			// 修改重复告警次数
			putBody = "{\"fields\": {\"customfield_10260\": "
					+ (Double.parseDouble(map.get("sameCount").toString()) + 1) + "}}";
		} else {
			logger.error("Error reqeust");
			return;
		}

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPut httpPut = new HttpPut(restUrl);
		httpPut.setHeader("Authorization", authInfo);
		httpPut.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(putBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPut.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPut);
			responseBody = handler.handleResponse(response);
			logger.info("updateAlarmIssue is OK, response is: " + responseBody);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}
	}

	@Override
	public JiraOpopEntity findOpopIssueById(String restUrl, String projectKey, String issueId, String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"id\",\"key\",\"reporter\",\"customfield_10162\",\"customfield_10157\", \"customfield_10165\", \"customfield_10161\", \"customfield_10239\", \"summary\", \"customfield_10238\", \"customfield_10243\", \"customfield_10163\", \"customfield_10237\", \"customfield_10240\", \"customfield_10258\",\"customfield_10259\" ]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}

		JiraOpopEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraOpopEntity();
						// 基础构造，id、key、报告这、主题
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						jiraEntity.setSummary(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary"));

						// 自定义字段-文本类型
						jiraEntity.setOpTarget(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10162"));

						jiraEntity.setOpSchema(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10157"));
						jiraEntity.setOpRisk(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10163"));
						jiraEntity.setImplicated(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10237"));
						jiraEntity.setPlanb(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10240"));

						// 自定义字段-人员类型
						jiraEntity.setOperator(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10238"))));
						jiraEntity.setReviewer(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10161"))));
						jiraEntity.setTester(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10239"))));
						jiraEntity.setDeveloper(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10243"))));
						String emailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10258");
						if (emailObject != null) {
							JSONArray emailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10258"));
							if (emailArray != null && emailArray.size() > 0) {
								String[] emails = new String[emailArray.size()];
								for (int i = 0; i < emailArray.size(); i++) {
									emails[i] = emailArray.getJSONObject(i).getString("value");
								}
								jiraEntity.setNotifyEmail(emails);
							}
						}
						// 自定义字段-时间类型
						try {
							jiraEntity.setOpTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_2,
									new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10165")));
						} catch (ParseException e) {
							logger.error("Date parse error: "
									+ new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10165"));
						}
						
						String personEmailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10259");
						if (personEmailObject != null) {
							JSONArray personEmailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10259"));
							if (personEmailArray != null && personEmailArray.size() > 0) {
								String[] emails = new String[personEmailArray.size()];
								for (int i = 0; i < personEmailArray.size(); i++) {
									emails[i] = personEmailArray.getJSONObject(i).getString("emailAddress");
								}
								jiraEntity.setNotifyEmailPerson(emails);
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	@Override
	public String constructFaultPostBody(Map<String, Object> map) {
		String postBody = "{\"fields\": { \"project\": { \"key\": \"" + map.get("projectKey") + "\" },"
				//+ "\"reporter\": {\"name\": \""+ map.get("reporter") + "\"}, "
				+ "\"summary\": \"" + map.get("summary") + "\", " + "\"issuetype\": {\"id\": \"" + map.get("issuetype")
				+ "\"}";
		
		//先拼接最简单的 字符类型
		if (map.get("impactNumber") != null) {
			postBody = postBody + ",\"customfield_10122\": \"" + map.get("impactNumber") + "\"";
		}
		if (map.get("impactArea") != null) {
			postBody = postBody + ",\"customfield_10262\": \"" + map.get("impactArea") + "\"";
		}
		if (map.get("impactBusiness") != null) {
			postBody = postBody + ",\"customfield_10134\": \"" + map.get("impactBusiness") + "\"";
		}				
		if (map.get("faultDescription") != null) {
			postBody = postBody + ",\"customfield_10265\": \"" + map.get("faultDescription") + "\"";
		}						
		if (map.get("temporaryImprove") != null) {
			postBody = postBody + ",\"customfield_10274\": \"" + map.get("temporaryImprove") + "\"";
		}
		if (map.get("stationaryImprove") != null) {
			postBody = postBody + ",\"customfield_10275\": \"" + map.get("stationaryImprove") + "\"";
		}
		if (map.get("improveOwner") != null) {
			postBody = postBody + ",\"customfield_10273\": \"" + map.get("improveOwner") + "\"";
		}				
		if (map.get("department") != null) {
			postBody = postBody + ",\"customfield_10272\": \"" + map.get("department") + "\"";
		}
		if (map.get("businessOwner") != null) {
			postBody = postBody + ",\"customfield_10280\": \"" + map.get("businessOwner") + "\"";
		}
		if (map.get("improvePoint") != null) {
			postBody = postBody + ",\"customfield_10127\": \"" + map.get("improvePoint") + "\"";
		}
		if (map.get("faultAllMinute") != null) {
			postBody = postBody + ",\"customfield_10281\": " + map.get("faultAllMinute") + "";
		}
		if (map.get("faultDetailType") != null) {
			postBody = postBody + ",\"customfield_10123\": \"" + map.get("faultDetailType") + "\"";
		}
		if (map.get("faultCause") != null) {
			postBody = postBody + ",\"customfield_10136\": \"" + map.get("faultCause") + "\"";
		}
		
		//下拉列表框类型
		if (map.get("faultType") != null) {
			postBody = postBody + ",\"customfield_10263\": {\"value\": \"" + map.get("faultType") + "\"}";
		}
		
		
		if (map.get("whetherOnlineFault") != null) {
			postBody = postBody + ",\"customfield_10267\": {\"value\": \"" + map.get("whetherOnlineFault") + "\"}";
		}
		if (map.get("whetherOnlineImpact") != null) {
			postBody = postBody + ",\"customfield_10268\": {\"value\": \"" + map.get("whetherOnlineImpact") + "\"}";
		}
			
		if (map.get("serviceLevel") != null) {
			postBody = postBody + ",\"customfield_10270\": {\"value\": \"" + map.get("serviceLevel") + "\"}";
		}
		if (map.get("faultLevel") != null) {
			postBody = postBody + ",\"customfield_10271\": {\"value\": \"" + map.get("faultLevel") + "\"}";
		}
		
		//再拼接比较难处理的 人员类型		
		
		//最后是时间类型
		if (map.get("startTime") != null) {
			postBody = postBody + ",\"customfield_10114\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("startTime")) + "\"";
		}
		if (map.get("date") != null) {
			postBody = postBody + ",\"customfield_10261\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("date")) + "\"";
		}
		if (map.get("findTime") != null) {
			postBody = postBody + ",\"customfield_10116\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("findTime")) + "\"";
		}
		if (map.get("endTime") != null) {
			postBody = postBody + ",\"customfield_10137\": \""
					+ TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_2, (Date) map.get("endTime")) + "\"";
		}
		postBody = postBody + "}}";
		return postBody;
	}

	@Override
	public JiraFaultEntity findFaultIssueById(String restUrl, String projectKey, String issueId, String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"id\",\"key\",\"reporter\",\"summary\",\"customfield_10261\", \"customfield_10114\", \"customfield_10116\", \"customfield_10137\", \"customfield_10122\", \"customfield_10262\", \"customfield_10134\", \"customfield_10263\",\"customfield_10123\",\"customfield_10265\",\"customfield_10136\",\"customfield_10267\",\"customfield_10268\",\"customfield_10127\",\"customfield_10274\",\"customfield_10275\",\"customfield_10273\",\"customfield_10280\",\"customfield_10270\",\"customfield_10271\",\"customfield_10272\",\"customfield_10281\",\"customfield_10287\" ]}";

		ResponseHandler<String> handler = new BasicResponseHandler();

		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JiraFaultEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraFaultEntity();
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setSummary(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						
						//转化文本类型
						jiraEntity.setImpactNumber(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10122"));
						jiraEntity.setImpactArea(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10262"));
						jiraEntity.setImpactBusiness(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10134"));
						jiraEntity.setFaultDetailType(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10123"));
						jiraEntity.setFaultDescription(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10265"));
						jiraEntity.setFaultCause(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10136"));
						jiraEntity.setImprovePoint(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10127"));
						jiraEntity.setImproveOwner(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10273"));
						jiraEntity.setBusinessOwner(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10280"));
						jiraEntity.setDepartment(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10272"));
						
						//渲染类型
						jiraEntity.setTemporaryImprove(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10274"));
						jiraEntity.setStationaryImprove(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10275"));
						jiraEntity.setDetailProcess(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10287"));
						
						//数字类型
						jiraEntity.setFaultAllMinute(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getDouble("customfield_10281"));

						//转化日期类型
						try {
							jiraEntity.setDate(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_3,new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
									.getString("customfield_10261")));
							jiraEntity.setStartTime(getDateValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
									.getString("customfield_10114")));
							jiraEntity.setFindTime(getDateValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
									.getString("customfield_10116")));
							jiraEntity.setEndTime(getDateValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
									.getString("customfield_10137")));
						} catch (ParseException e) {
							e.printStackTrace();
							logger.error("Date parse error: " + e.getMessage());
						}
						
						//转化下拉列表框类型
						jiraEntity.setFaultType(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10263")));
						jiraEntity.setWhetherOnlineFault(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10267")));
						jiraEntity.setWhetherOnlineImpact(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10268")));
						jiraEntity.setServiceLevel(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10270")));
						jiraEntity.setFaultLevel(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10271")));

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	@Override
	public String findFaultTitleByIssueKey(String restUrl, String projectKey, String issueKey, String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&key=" + issueKey
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"summary\"]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						return new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary");
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public JiraReleaseSubEntity findReleaseSubIssueById(String restUrl, String projectKey, String issueId,
			String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"fields\":[\"id\",\"key\",\"reporter\",\"customfield_10151\",\"customfield_10208\", \"customfield_10147\", \"customfield_10148\" ]}";

		ResponseHandler<String> handler = new BasicResponseHandler();

		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JiraReleaseSubEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraReleaseSubEntity();
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setGitProject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10208"));
						jiraEntity.setOnlineGitTag(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10151"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						jiraEntity.setOperator(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10147"))));
						jiraEntity.setTester(buildJiraUser(
								new JSONObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
										.getString("customfield_10148"))));

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	@Override
	public JiraEntity findIssueById(String baseUrl, String issueId, String authInfo) {

		String restUrl= baseUrl + issueId+"?fields=assignee,summary,status";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpGet httpGet = new HttpGet(restUrl);
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
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}

		JiraEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				Boolean findFlag = new JSONObject(responseBody).has("key");
				if (findFlag) {
					jiraEntity = new JiraOpopEntity();
					// 基础构造，id、key、报告这、主题
					jiraEntity.setKey(new JSONObject(responseBody).getString("key"));
					jiraEntity.setAssigner(buildJiraUser(new JSONObject(new JSONObject(new JSONObject(responseBody).getString("fields")).getString("assignee"))));
					jiraEntity.setSummary(new JSONObject(new JSONObject(responseBody).getString("fields")).getString("summary"));
					if(new JSONObject(new JSONObject(responseBody).getString("fields")).has("status")) {
						jiraEntity.setCurrentStage(new JSONObject(new JSONObject(new JSONObject(responseBody).getString("fields")).getString("status")).getString("name"));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	@Override
	public JiraTechqaSubEntity findTechqaSubById(String restUrl, String projectKey, String issueType, String issueId,
			String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"id\",\"key\",\"reporter\",\"customfield_10377\",\"customfield_10350\", \"customfield_10139\", \"customfield_10369\", \"customfield_10378\", \"summary\", \"assignee\", \"customfield_10259\", \"customfield_10384\", \"customfield_10385\", \"customfield_10386\", \"customfield_10381\", \"customfield_10211\", \"created\"]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}

		JiraTechqaSubEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraTechqaSubEntity();
						// 基础构造，id、key、报告这、主题
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						jiraEntity.setSummary(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary"));
						//jiraEntity.setAssigner(buildJiraUser(new JSONObject(new JSONObject(new JSONObject(responseBody).getString("fields")).getString("assignee"))));
						
						// 自定义字段-文本类型
						jiraEntity.setTestDescribe(new JSONObject(jsonArray.getJSONObject(0).getString("renderedFields"))
								.getString("customfield_10378"));
						
						jiraEntity.setReportDetail(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10385"));
						
						jiraEntity.setReportComment(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10386"));


						// 自定义字段-时间类型
						try {
							jiraEntity.setOnlineTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_2,
									new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10139")));
						} catch (ParseException e) {
							logger.error("Date parse error: "
									+ new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10139"));
						}
						
						try {
							jiraEntity.setTestEndTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_2,
									new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10211")));
						} catch (ParseException e) {
							logger.error("Date parse error: "
									+ new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10211"));
						}
						
						try {
							jiraEntity.setCreateTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_2,
									new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("created")));
						} catch (ParseException e) {
							logger.error("Date parse error: "
									+ new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("created"));
						}
						
						// 下拉列表
						jiraEntity.setTestProject(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10377")));
						if(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10369"))!=null) {
							jiraEntity.setTestRound(Integer.parseInt(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
									.getString("customfield_10369"))));
						}
						
						jiraEntity.setTestResult(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10381")));
						
						// 下拉列表复选
						String emailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10384");
						if (emailObject != null) {
							JSONArray emailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10384"));
							if (emailArray != null && emailArray.size() > 0) {
								String[] emails = new String[emailArray.size()];
								for (int i = 0; i < emailArray.size(); i++) {
									emails[i] = emailArray.getJSONObject(i).getString("value");
								}
								jiraEntity.setNotifyEmail(emails);
							}
						}

						String personEmailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10259");
						if (personEmailObject != null) {
							JSONArray personEmailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10259"));
							if (personEmailArray != null && personEmailArray.size() > 0) {
								String[] emails = new String[personEmailArray.size()];
								for (int i = 0; i < personEmailArray.size(); i++) {
									emails[i] = personEmailArray.getJSONObject(i).getString("emailAddress");
								}
								jiraEntity.setNotifyEmailPerson(emails);
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

	@Override
	public JiraTechqaEntity findTechqaById(String restUrl, String projectKey, String issueType, String issueId,
			String authInfo) {

		String postBody = "{\"jql\":\"project = " + projectKey + "&id=" + issueId
				+ "\",\"startAt\":0,\"maxResults\":1,\"expand\":[\"renderedFields\"],\"fields\":[\"id\",\"key\",\"reporter\",\"customfield_10384\",\"customfield_10259\", \"customfield_10377\", \"customfield_10350\", \"customfield_10383\", \"summary\",\"assignee\"]}";

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(restUrl);
		httpPost.setHeader("Authorization", authInfo);
		httpPost.setHeader("Content-type", "application/json");
		StringEntity entity = new StringEntity(postBody, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(entity);
		String responseBody = null;
		HttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			response = httpclient.execute(httpPost);
			responseBody = handler.handleResponse(response);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}

		JiraTechqaEntity jiraEntity = null;
		try {
			if (responseBody != null) {
				int entityNum = new JSONObject(responseBody).getInt("total");
				if (entityNum > 0) {
					String issuesString = new JSONObject(responseBody).getString("issues");
					JSONArray jsonArray = JSONArray.fromObject(issuesString);
					if (jsonArray != null && jsonArray.size() > 0) {
						jiraEntity = new JiraTechqaEntity();
						// 基础构造，id、key、报告这、主题
						jiraEntity.setId(jsonArray.getJSONObject(0).getString("id"));
						jiraEntity.setKey(jsonArray.getJSONObject(0).getString("key"));
						jiraEntity.setReporter(buildJiraUser(new JSONObject(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("reporter"))));
						jiraEntity.setSummary(
								new JSONObject(jsonArray.getJSONObject(0).getString("fields")).getString("summary"));
						//jiraEntity.setAssigner(buildJiraUser(new JSONObject(new JSONObject(new JSONObject(responseBody).getString("fields")).getString("assignee"))));

						// 自定义字段-文本类型

						jiraEntity.setCaseDetail(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10383"));
						
						// 下拉列表单选
						jiraEntity.setTestProject(getSelectValue(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10377")));
						
						// 下拉列表复选
						String emailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10384");
						if (emailObject != null) {
							JSONArray emailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10384"));
							if (emailArray != null && emailArray.size() > 0) {
								String[] emails = new String[emailArray.size()];
								for (int i = 0; i < emailArray.size(); i++) {
									emails[i] = emailArray.getJSONObject(i).getString("value");
								}
								jiraEntity.setNotifyEmail(emails);
							}
						}

						String personEmailObject = new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
								.getString("customfield_10259");
						if (personEmailObject != null) {
							JSONArray personEmailArray = JSONArray
									.fromObject(new JSONObject(jsonArray.getJSONObject(0).getString("fields"))
											.getString("customfield_10259"));
							if (personEmailArray != null && personEmailArray.size() > 0) {
								String[] emails = new String[personEmailArray.size()];
								for (int i = 0; i < personEmailArray.size(); i++) {
									emails[i] = personEmailArray.getJSONObject(i).getString("emailAddress");
								}
								jiraEntity.setNotifyEmailPerson(emails);
							}
						}
						
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return jiraEntity;
	}

}
