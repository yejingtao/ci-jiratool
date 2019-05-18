package com.mgtv.autoplug.jiratool.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.mgtv.autoplug.jiratool.manager.JiraAlarmIssueManager;
import com.mgtv.autoplug.jiratool.message.AlarmResponse;
import com.mgtv.autoplug.jiratool.message.JiraAlarmEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;
import com.mgtv.autoplug.jiratool.util.TimeToolUtil;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/alarm")
public class JiraAlarmController {
	
	public final Logger logger = LoggerFactory.getLogger(JiraAlarmController.class);
	
	@Value("${jira_auth_username}")
	private String userName;
	
	@Value("${jira_auth_password}")
	private String passWord;
	
	@Autowired
	private JiraAlarmIssueManager jiraAlarmIssueManager;
	
	public static final String STATUS_FAILED = "failed";
	public static final String STATUS_SUCCEED = "succeed";
	
	
	
	@RequestMapping(value="/createIssue", method=RequestMethod.POST, consumes="application/json;charset=UTF-8",produces = "application/json;charset=UTF-8")
	public String createIssue(@RequestBody JSONObject jsonParam) {
		AlarmResponse response = new AlarmResponse();
		response.setStatus(AlarmResponse.STATUS_FAIL);
		
		logger.info("Alarm createIssue:"+jsonParam.toString());
		//validation
		if(jsonParam.get("alamrId")==null || jsonParam.get("alarmDetail")==null
				|| jsonParam.get("startTime")==null) {
			logger.error("alamrId  alarmDetail startTime can not be null");			
			return JSON.toJSONString(response);
		}
		
		//查询是否重复报警
		List<JiraAlarmEntity> jiraAlarmList = null;;
		try {
			jiraAlarmList = jiraAlarmIssueManager.findIssueByAlarmId(userName, passWord, jsonParam.getString("alamrId"));
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
			logger.error(e1.getMessage());
			return JSON.toJSONString(response);
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error(e1.getMessage());
			return JSON.toJSONString(response);
		}
		
		if(jiraAlarmList==null || jiraAlarmList.size()==0) {
			//正常，第一次接收本报警
			logger.info("Create new Jira issue: "+ jsonParam.getString("alamrId"));
			JiraAlarmEntity entity = new JiraAlarmEntity();
			entity.setAlamrId(jsonParam.getString("alamrId"));
			//entity.setAlarmType(JiraAlarmEntity.CDN);
			entity.setAlarmType((jsonParam.get("alarmType")==null || jsonParam.getString("alarmType").trim().length()==0)?null:jsonParam.getString("alarmType"));
			entity.setInstantane(jsonParam.get("instantane")==null?null:jsonParam.getString("instantane"));
			entity.setMessageSummary(jsonParam.get("summary")==null?null:jsonParam.getString("summary"));
			entity.setAlarmDetail(jsonParam.getString("alarmDetail"));
			try {
				Date startDate = TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_1, jsonParam.getString("startTime"));
				entity.setStartTime(startDate);
				if(jsonParam.get("endTime")!=null && jsonParam.get("endTime").toString().trim().length()>0) {
					entity.setEndTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_1, jsonParam.getString("endTime")));
				}
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error("Date format error");
				return JSON.toJSONString(response);
			}
			//获取经办人
			if(jsonParam.get("email")!=null) {
				entity.setAssigner(jiraAlarmIssueManager.getJiraUserName(userName, passWord, jsonParam.getString("email")));
			}
			
			String issueKey = jiraAlarmIssueManager.createIssue(userName, passWord, entity);
			response.setStatus(AlarmResponse.STATUS_CREATED);
			response.setIssueKey(issueKey);
			return JSON.toJSONString(response);
		}else if(jiraAlarmList.size()==1) {
			logger.info("Add  jira comment : "+ jsonParam.getString("alamrId"));
			//重复报警，直接在原任务基础上添加comment
			JiraAlarmEntity entity = jiraAlarmList.get(0);
			entity.setInstantane(jsonParam.get("instantane")==null?null:jsonParam.getString("instantane"));
			jiraAlarmIssueManager.addAlarmComment(userName, passWord, entity);
			response.setStatus(AlarmResponse.STATUS_UPDATED);
			response.setIssueKey(entity.getKey());
			return JSON.toJSONString(response);
		}else {
			//不正常，不应该存在多个issue的任务
			logger.error(jsonParam.getString("alamrId")+" 事件ID重复");
			return JSON.toJSONString(response);
		}
	}
	
	@RequestMapping(value="/updateIssue", method=RequestMethod.POST, consumes="application/json;charset=UTF-8",produces = "application/json;charset=UTF-8")
	public String updateIssue(@RequestBody JSONObject jsonParam) {
		logger.info("Alarm updateIssue:"+jsonParam.toString());
		AlarmResponse response = new AlarmResponse();
		response.setStatus(AlarmResponse.STATUS_FAIL);
		
		if(jsonParam.get("alamrId")==null) {
			logger.error("AlamrId is empty");
			return JSON.toJSONString(response);
		}
		if(jsonParam.get("endTime")==null) {
			logger.error("EndTime is empty");
			return JSON.toJSONString(response);
		}
		JiraAlarmEntity entity = new JiraAlarmEntity();
		entity.setAlamrId(jsonParam.getString("alamrId"));
		//entity.setAlarmType(JiraAlarmEntity.CDN);
		entity.setAlarmDetail(jsonParam.get("alarmDetail")==null?null:jsonParam.getString("alarmDetail"));
		try {
			entity.setEndTime(TimeToolUtil.parseString2Date(TimeToolUtil.DATE_FORMAT_1, jsonParam.getString("endTime")));
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("Date format error");
			return JSON.toJSONString(response);
		}
		String issueKey = jiraAlarmIssueManager.updateIssue(userName, passWord, entity);
		if(issueKey!=null) {
			response.setIssueKey(issueKey);
			response.setStatus(AlarmResponse.STATUS_UPDATED);
		}
		
		return JSON.toJSONString(response);
	}
	
	@RequestMapping(value="/getJiraUser", method=RequestMethod.GET)
	public String getJiraUser(@RequestParam(required=true) String userKey) {
		JiraUser jiraUser = jiraAlarmIssueManager.getJiraUserName(userName, passWord, userKey);
		return jiraUser==null?null:jiraUser.getJiraName();
	}
}
