package com.mgtv.autoplug.jiratool.service;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.mgtv.autoplug.jiratool.message.JiraEntity;
import com.mgtv.autoplug.jiratool.message.JiraFaultEntity;
import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseSubEntity;
import com.mgtv.autoplug.jiratool.message.JiraTechqaEntity;
import com.mgtv.autoplug.jiratool.message.JiraTechqaSubEntity;

public interface JiraIssueService {
	
	String createIssue(String restUrl, String postBody, String authInfo);	

	String constructReleaseSubPostBody(Map<String,String> map);
	
	//构造alarm创建issue的post报文
	String constructAlarmPostBody(Map<String,Object> map);
	
	//修改alarm issue
	void updateAlarmIssue(String restUrl,Map<String,Object> map, String authInfo);
	
	//主任务
	JiraReleaseEntity findReleaseIssueById(String restUrl, String projectKey, String issueId, String authInfo);
	
	//子任务
	JiraReleaseSubEntity findReleaseSubIssueById(String restUrl, String projectKey, String issueId, String authInfo);
	
	String[] findReleaseIssueByGit(String restUrl, String projectKey, String issueType, String gitProjectStr, String gitTagStr, String authInfo);
	
	//提交issue
	void transIssue(String restUrl,String transitionId, String authInfo);
	
	//添加common
	void addCommon(String restUrl, String common, String authInfo);
	
	//该接口有2个应用场景，1根据事件ID来查找存量任务；2根据主机名、主机描述、问题来查找任务
	Map<String,Double> findAlarmIssue(String restUrl, String projectKey, String issueType, Map<String, Object> map, String authInfo) throws ClientProtocolException, IOException;
	
	JiraOpopEntity findOpopIssueById(String restUrl, String projectKey, String issueId, String authInfo);
	
	//构造alarm创建issue的post报文
	String constructFaultPostBody(Map<String,Object> map);
	
	JiraFaultEntity findFaultIssueById(String restUrl, String projectKey, String issueId, String authInfo);
	
	String findFaultTitleByIssueKey(String restUrl, String projectKey, String issueKey, String authInfo);
	
	JiraEntity findIssueById(String restUrl,  String issueId, String authInfo);
	
	JiraTechqaSubEntity findTechqaSubById(String restUrl, String projectKey, String issueType, String issueId, String authInfo);
	
	JiraTechqaEntity findTechqaById(String restUrl, String projectKey, String issueType, String issueId, String authInfo);
		
}
