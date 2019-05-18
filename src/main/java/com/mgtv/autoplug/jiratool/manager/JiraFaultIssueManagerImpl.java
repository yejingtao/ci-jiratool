package com.mgtv.autoplug.jiratool.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgtv.autoplug.jiratool.message.JiraFaultEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;
import com.mgtv.autoplug.jiratool.service.EmailSender;
import com.mgtv.autoplug.jiratool.service.ExcelService;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.util.AutoFillUtil;
import com.mgtv.autoplug.jiratool.util.EmailUtil;
import com.mgtv.autoplug.jiratool.util.TimeToolUtil;

@Service
public class JiraFaultIssueManagerImpl implements JiraFaultIssueManager{
	
	public final Logger logger = LoggerFactory.getLogger(JiraFaultIssueManagerImpl.class);
	
	@Value("${jira_auth_username}")
	private String userName;
	
	@Value("${jira_auth_password}")
	private String passWord;
	
	@Value("${jira.fault.project.key}")
	private String projectKey;
	
	@Value("${jira.baseUrl}")
	private String baseUrl;
	
	@Value("${jira.default.reporter}")
	private String reporter;
	
	@Value("${jira.fault.project.issue.type}")
	private String issueType;
	
	@Value("${jira.fault.email.from.username}")
	private String fromName;
	
	@Value("${jira.fault.email.from.displayname}")
	private String fromDisplayName;
	
	
	@Value("${jira.fault.email.to.person}")
	private String toPerson;
	
	@Value("${jira.fault.email.to.group}")
	private String toGroup;
	
	
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private EmailSender emailSender;
	
	public static final String PROJECT_KEY_BUG = "BUG";
	
	@Override
	public void importExcel(String file) {
		//构建excel解析的map
		Map<Integer,String> excelMap = excelService.getFaultParseMap();
		
		//解析excel
		List<Map<String,Object>> paramsMapList = excelService.parseExcel(file, excelMap);
		
		//清洗和转化内容，为了后续的反射
		//这里还得去获得一次老任务的summary
		
		if(paramsMapList!=null) {
			for(Map<String,Object> params : paramsMapList) {
				//封装业务负责人
				if(params.get("faultAllMinute")!=null) {				
					params.put("faultAllMinute", Double.parseDouble(params.get("faultAllMinute").toString()));
				}				
			}
		}
		
		
		
		//构建Jira实体类
		List<JiraFaultEntity> jiraEntityList = AutoFillUtil.fillBean(paramsMapList, JiraFaultEntity.class);
		
		String basicAuthHeader = jiraAuthService.getBasicAuth(userName, passWord);
		
		if(jiraEntityList!=null) {
			for(JiraFaultEntity entity: jiraEntityList) {
				//单个插入jira任务
				
				//1 封装reporter
				JiraUser reporter = new JiraUser();
				reporter.setJiraName("wenhao@imgo.tv");				
				entity.setReporter(reporter);
				
				//2 根据Jira地址去获取jira的summary
				int split = entity.getJiraUrl().lastIndexOf("/");
				String issueKey = entity.getJiraUrl().substring(split+1);
				String issueSummary = jiraIssueService.findFaultTitleByIssueKey(baseUrl+"/rest/api/2/search", PROJECT_KEY_BUG, issueKey, basicAuthHeader);
				if(issueSummary==null || issueSummary.trim().length()==0) {
					entity.setSummary(issueKey);
				}else {
					entity.setSummary(issueSummary);
				}
				
				//3 插入
				this.createIssue(entity);
			}
		}		
	}

	@Override
	public String createIssue(JiraFaultEntity jiraFaultEntity) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectKey", projectKey);
		map.put("summary", jiraFaultEntity.getSummary());
		map.put("reporter", jiraFaultEntity.getReporter().getJiraName());
		map.put("issuetype", issueType);
		map.put("date", jiraFaultEntity.getDate());
		map.put("startTime", jiraFaultEntity.getStartTime());
		map.put("findTime", jiraFaultEntity.getFindTime());
		map.put("endTime", jiraFaultEntity.getEndTime());
		map.put("impactNumber", jiraFaultEntity.getImpactNumber());
		map.put("impactArea", jiraFaultEntity.getImpactArea());
		map.put("impactBusiness", jiraFaultEntity.getImpactBusiness());
		map.put("faultType", jiraFaultEntity.getFaultType());
		map.put("faultDetailType", jiraFaultEntity.getFaultDetailType());
		map.put("faultDescription", jiraFaultEntity.getFaultDescription());
		map.put("faultCause", jiraFaultEntity.getFaultCause());
		map.put("whetherOnlineFault", jiraFaultEntity.getWhetherOnlineFault());
		map.put("whetherOnlineImpact", jiraFaultEntity.getWhetherOnlineImpact());
		map.put("improvePoint", jiraFaultEntity.getImprovePoint());
		map.put("temporaryImprove", jiraFaultEntity.getTemporaryImprove());
		map.put("stationaryImprove", jiraFaultEntity.getStationaryImprove());
		map.put("improveOwner", jiraFaultEntity.getImproveOwner());
		map.put("businessOwner", jiraFaultEntity.getBusinessOwner());
		map.put("serviceLevel", jiraFaultEntity.getServiceLevel());
		map.put("faultLevel", jiraFaultEntity.getFaultLevel());
		map.put("department", jiraFaultEntity.getDepartment());
		map.put("faultAllMinute", jiraFaultEntity.getFaultAllMinute());
		
		String postBody = jiraIssueService.constructFaultPostBody(map);
		logger.info("postBody: "+postBody);
		return jiraIssueService.createIssue(baseUrl+"/rest/api/2/issue", postBody, authInfo);
	}

	@Override
	public JiraFaultEntity getIssueByKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiraFaultEntity findIssueById(String issueId) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		return jiraIssueService.findFaultIssueById(baseUrl+"/rest/api/2/search", projectKey, issueId, authInfo);
	}

	@Override
	public void emailNofity(JiraFaultEntity jiraEntity, short type) {
		//init params
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("key", jiraEntity.getKey());
		params.put("title", jiraEntity.getSummary());

		params.put("impactNumber", jiraEntity.getImpactNumber()==null?"":jiraEntity.getImpactNumber());
		params.put("impactArea", jiraEntity.getImpactArea()==null?"":jiraEntity.getImpactArea());
		params.put("impactBusiness", jiraEntity.getImpactBusiness()==null?"":jiraEntity.getImpactBusiness());
		//params.put("faultType", jiraEntity.getFaultType());
		//params.put("faultDetailType", jiraEntity.getFaultDetailType());
		params.put("faultDescription", jiraEntity.getFaultDescription()==null?"":jiraEntity.getFaultDescription());
		params.put("faultCause", jiraEntity.getFaultCause()==null?"":jiraEntity.getFaultCause());
		//params.put("whetherOnlineFault", jiraEntity.getWhetherOnlineFault());
		//params.put("whetherOnlineImpact", jiraEntity.getWhetherOnlineImpact());
		//params.put("improvePoint", jiraEntity.getImprovePoint());
		if(jiraEntity.getTemporaryImprove()!=null && jiraEntity.getTemporaryImprove().trim().length()>3) {
			params.put("temporaryImprove", jiraEntity.getTemporaryImprove().substring(3, jiraEntity.getTemporaryImprove().length()-4));
		}else {
			params.put("temporaryImprove","");
		}
		if(jiraEntity.getStationaryImprove()!=null && jiraEntity.getStationaryImprove().trim().length()>3) {
			params.put("stationaryImprove", jiraEntity.getStationaryImprove().substring(3, jiraEntity.getStationaryImprove().length()-4));
		}else {
			params.put("stationaryImprove","");
		}
		
		//params.put("stationaryImprove", jiraEntity.getStationaryImprove().substring(3, jiraEntity.getStationaryImprove().length()-4));
		//params.put("improveOwner", jiraEntity.getImproveOwner());
		//params.put("businessOwner", jiraEntity.getBusinessOwner());
		//params.put("serviceLevel", jiraEntity.getServiceLevel());
		params.put("faultLevel", jiraEntity.getFaultLevel()==null?"":jiraEntity.getFaultLevel());
		//params.put("department", jiraEntity.getDepartment());
		params.put("faultAllMinute", jiraEntity.getFaultAllMinute()==-1?"":jiraEntity.getFaultAllMinute());
		if(jiraEntity.getDetailProcess()!=null && jiraEntity.getDetailProcess().trim().length()>3) {
			params.put("detailProcess", jiraEntity.getDetailProcess().substring(3, jiraEntity.getDetailProcess().length()-4));
		}else {
			params.put("detailProcess", "");
		}
		

		//时间
		params.put("date", jiraEntity.getDate()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_3, jiraEntity.getDate()));
		params.put("startTime", jiraEntity.getStartTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getStartTime()));
		params.put("findTime", jiraEntity.getFindTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getFindTime()));
		params.put("endTime", jiraEntity.getEndTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getEndTime()));
		
		JiraUser jiraUser = new JiraUser();
		jiraUser.setDisplayName(fromDisplayName);
		jiraUser.setEmailAddress(fromName);
		if(type==0) {
			emailSender.sendHtmlEmail(jiraUser, new String[] {toPerson}, null, "[通告预览]--"+jiraEntity.getSummary(), "fault_html.vm", params);
		}else if(type==1){
			emailSender.sendHtmlEmail(jiraUser, new String[] {toGroup}, null, "[故障通报]--"+jiraEntity.getSummary(), "fault_html.vm", params);
		}		
		logger.info("Send email successfully : " + jiraEntity.getKey());
	}

}
