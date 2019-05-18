package com.mgtv.autoplug.jiratool.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.mgtv.autoplug.jiratool.message.JiraAlarmEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.service.JiraUserService;
import com.mgtv.autoplug.jiratool.util.EmailUtil;

@Service
public class JiraAlarmIssueManagerImpl implements JiraAlarmIssueManager{
	
	@Value("${jira.alarm.project.key}")
	private String projectKey;
	
	@Value("${jira.default.reporter}")
	private String reporter;
	
	@Value("${jira.alarm.project.issue.type}")
	private String issueType;
	
	@Value("${jira.baseUrl}")
	private String baseUrl;

	public final Logger logger = LoggerFactory.getLogger(JiraAlarmIssueManagerImpl.class);
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private JiraUserService jiraUserService;
	
	@Override
	public String createIssue(String userName, String passWord, JiraAlarmEntity alarmEntity) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);		
		
		//建报文
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("alarmDetail", alarmEntity.getAlarmDetail());
		searchMap.put("projectKey", projectKey);
		searchMap.put("reporter", reporter);
		String summary = "告警追踪"+(alarmEntity.getMessageSummary()==null?"":("-"+alarmEntity.getMessageSummary()))+"-"+alarmEntity.getAlamrId();
		searchMap.put("summary", summary);
		searchMap.put("alarmDetail", alarmEntity.getAlarmDetail());
		searchMap.put("issuetype", issueType);
		searchMap.put("alamrId", alarmEntity.getAlamrId());
		searchMap.put("alarmType", alarmEntity.getAlarmType());
		searchMap.put("instantane", alarmEntity.getInstantane());
		searchMap.put("assignee", alarmEntity.getAssigner()==null?null:alarmEntity.getAssigner().getJiraName());
		if(alarmEntity.getStartTime()!=null) {
			//searchMap.put("startTime", TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, alarmEntity.getStartTime()));
			searchMap.put("startTime", alarmEntity.getStartTime());
		}
		
		if(alarmEntity.getEndTime()!=null) {
			//searchMap.put("endTime", TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, alarmEntity.getEndTime()));
			searchMap.put("endTime", alarmEntity.getEndTime());
		}
		
		String postBody = jiraIssueService.constructAlarmPostBody(searchMap);
		//创建
		return jiraIssueService.createIssue(baseUrl+"/rest/api/2/issue", postBody, authInfo);
	}
	
	
	/**
	 * 静默期的设计，被否决了，完全按照事件ID来
	@Override
	public String createIssue(String userName, String passWord, JiraAlarmEntity alarmEntity) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		
		//校验是否静默期内重复报警
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("alarmDetail", alarmEntity.getAlarmDetail());
		Map<String,Double> issueInfos;
		try {
			issueInfos = jiraIssueService.findAlarmIssue(baseUrl+"/rest/api/2/search", projectKey, issueType, searchMap, authInfo);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		if(issueInfos!=null) {
			logger.info("重复告警:"+JSON.toJSONString(alarmEntity));
			issueInfos.entrySet().forEach(
					entry->{
						//comment是否已经太多需要熔断
						if(entry.getValue()<=20) {
							String restUrl = baseUrl+"/rest/api/2/issue/"+entry.getKey();
							//重复告警次数加1
							Map<String,Object> paraMap = new HashMap<String,Object>();
							paraMap.put("sameCount", entry.getValue());
							jiraIssueService.updateAlarmIssue(restUrl, paraMap, authInfo);
							//添加一条common信息
							if(alarmEntity.getInstantane()!=null && alarmEntity.getInstantane().trim().length()>0) {
								jiraIssueService.addCommon(restUrl, alarmEntity.getInstantane(), authInfo);
							}
							}
						}
						
					);
			return issueInfos.keySet().iterator().next();
		}
		
		//再构建报文
		searchMap.put("projectKey", projectKey);
		searchMap.put("reporter", reporter);
		searchMap.put("summary", "告警自动追踪-"+alarmEntity.getHostDescribe());
		searchMap.put("alarmDetail", alarmEntity.getAlarmDetail());
		searchMap.put("issuetype", issueType);
		searchMap.put("alamrId", alarmEntity.getAlamrId());
		searchMap.put("alarmType", alarmEntity.getAlarmType());
		searchMap.put("instantane", alarmEntity.getInstantane());
		searchMap.put("assignee", alarmEntity.getAssigner()==null?null:alarmEntity.getAssigner().getJiraName());
		if(alarmEntity.getStartTime()!=null) {
			//searchMap.put("startTime", TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, alarmEntity.getStartTime()));
			searchMap.put("startTime", alarmEntity.getStartTime());
		}
		
		if(alarmEntity.getEndTime()!=null) {
			//searchMap.put("endTime", TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, alarmEntity.getEndTime()));
			searchMap.put("endTime", alarmEntity.getEndTime());
		}
		
		String postBody = jiraIssueService.constructAlarmPostBody(searchMap);
		//创建
		return jiraIssueService.createIssue(baseUrl+"/rest/api/2/issue", postBody, authInfo);
	}
	

	@Override
	public void updateIssue(String userName, String passWord, JiraAlarmEntity alarmEntity) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("alamrId", alarmEntity.getAlamrId());
		//再找到存量issue	
		Map<String,Double> issueInfos;
		try {
			//先通过事件ID尝试精确匹配
			issueInfos = jiraIssueService.findAlarmIssue(baseUrl+"/rest/api/2/search", projectKey, issueType, searchMap, authInfo);
			if(issueInfos==null) {
				//再通过其它提交去模糊匹配
				searchMap.put("alamrId", null);
				searchMap.put("alarmDetail", alarmEntity.getAlarmDetail());
				issueInfos = jiraIssueService.findAlarmIssue(baseUrl+"/rest/api/2/search", projectKey, issueType, searchMap, authInfo);
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return;
		}
		if(issueInfos==null) {
			logger.info("被修改的告警信息不存在:"+JSON.toJSONString(alarmEntity));
			return;
		}else if(issueInfos.keySet().size()>1) {
			logger.info("事件ID在Jira中重复:"+alarmEntity.getAlamrId());
			return;
		}
		searchMap.put("endTime", alarmEntity.getEndTime());
		String restUrl = baseUrl+"/rest/api/2/issue/"+issueInfos.keySet().iterator().next();
		jiraIssueService.updateAlarmIssue(restUrl, searchMap, authInfo);		
	}
	*/
	
	@Override
	public String updateIssue(String userName, String passWord, JiraAlarmEntity alarmEntity) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		List<JiraAlarmEntity> jiraAlarmList = null;
		try {
			//先通过事件ID尝试精确匹配
			jiraAlarmList = this.findIssueByAlarmId(userName, passWord, alarmEntity.getAlamrId());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		if(jiraAlarmList==null) {
			logger.error("被修改的告警信息不存在:"+JSON.toJSONString(alarmEntity));
			return null;
		}else if(jiraAlarmList.size()>1) {
			logger.info("事件ID在Jira中重复:"+alarmEntity.getAlamrId());
			return null;
		}
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("alamrId", alarmEntity.getAlamrId());
		searchMap.put("endTime", alarmEntity.getEndTime());
		String restUrl = baseUrl+"/rest/api/2/issue/"+jiraAlarmList.get(0).getKey();
		//修改故障恢复时间
		jiraIssueService.updateAlarmIssue(restUrl, searchMap, authInfo);
		return jiraAlarmList.get(0).getKey();
	}

	@Override
	public JiraUser getJiraUserName(String userName, String passWord, String keyName) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		//查询
		JiraUser  jiraUser = jiraUserService.findUserByKeyWord(baseUrl+"/rest/api/2/user/search?username=", keyName, authInfo);
		if(jiraUser==null && keyName.endsWith(EmailUtil.MGTV)) {
			//这又是一个历史包袱，jira中邮箱跟告警发送平台邮箱对不上
			keyName = keyName.replaceAll(EmailUtil.MGTV, EmailUtil.IMGO);
			jiraUser = jiraUserService.findUserByKeyWord(baseUrl+"/rest/api/2/user/search?username=", keyName, authInfo);
		}
		return jiraUser;
	}


	@Override
	public List<JiraAlarmEntity> findIssueByAlarmId(String userName, String passWord, String alarmId) throws ClientProtocolException, IOException {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("alamrId", alarmId);

		List<JiraAlarmEntity> entityList = null;
		
		Map<String, Double> issueInfos = jiraIssueService.findAlarmIssue(baseUrl+"/rest/api/2/search", projectKey, issueType, searchMap, authInfo);
		if(issueInfos!=null) {
			entityList = new ArrayList<JiraAlarmEntity>();
			for(Entry<String, Double> entry : issueInfos.entrySet()) {
				JiraAlarmEntity entity = new JiraAlarmEntity();
				entity.setKey(entry.getKey());
				entity.setSameCount(entry.getValue());
				entityList.add(entity);
				
			}
		}
		return entityList;
	}


	@Override
	public void addAlarmComment(String userName, String passWord, JiraAlarmEntity entity) {
		//先构建鉴权
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
	
		//comment是否已经太多需要熔断
		if(entity.getSameCount()<=20) {
			String restUrl = baseUrl+"/rest/api/2/issue/"+entity.getKey();
			//重复告警次数加1
			Map<String,Object> paraMap = new HashMap<String,Object>();
			paraMap.put("sameCount", entity.getSameCount());
			jiraIssueService.updateAlarmIssue(restUrl, paraMap, authInfo);
			//添加一条common信息
			if(entity.getInstantane()!=null && entity.getInstantane().trim().length()>0) {
				jiraIssueService.addCommon(restUrl, "报警瞬时值:"+entity.getInstantane(), authInfo);
			}
		}
		
	}

	

}
