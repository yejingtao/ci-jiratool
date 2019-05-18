package com.mgtv.autoplug.jiratool.message;

import java.util.Date;

public class JiraFaultEntity extends JiraEntity{
	
	//日期 10261 
	private Date date;
	
	//开始时间 10114
	private Date startTime;
	
	//发现时间 10116
	private Date findTime;
	
	//结束时间 10137
	private Date endTime;
	
	//影响用户数 10122
	private String impactNumber;
	
	//影响范围 10262
	private String impactArea;
	
	//影响业务 10134
	private String impactBusiness;
	
	//故障类型 10263
	private String faultType;
	
	//故障细分 10123
	private String faultDetailType;
	
	//故障描述 10265
	private String faultDescription;
	
	//触发原因 10136
	private String faultCause;
	
	//是否为上线故障 10267
	private String whetherOnlineFault;
	
	//是否影响线上业务 10268
	private String whetherOnlineImpact;
	
	//改进点 10127
	private String improvePoint;
	
	//临时改进措施 10274
	private String temporaryImprove;
	
	//长期改进措施 10275
	private String stationaryImprove;
	
	//改进负责人 10273
	private String improveOwner;
	
	//业务负责人 10280
	private String businessOwner;
	
	//服务级别 10270
	private String serviceLevel;
	
	//故障等级 10271
	private String faultLevel;
	
	//负责部门 10272
	private String department;
	
	//Jira地址
	//只为了解析excel用，不会插入jira任务
	private String jiraUrl;
	
	//故障恢复时间（分钟）10281
	private double faultAllMinute;
	
	
	//详细处理流程 10287
	private String detailProcess;
	



	public double getFaultAllMinute() {
		return faultAllMinute;
	}

	public void setFaultAllMinute(double faultAllMinute) {
		this.faultAllMinute = faultAllMinute;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getFindTime() {
		return findTime;
	}

	public void setFindTime(Date findTime) {
		this.findTime = findTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getImpactNumber() {
		return impactNumber;
	}

	public void setImpactNumber(String impactNumber) {
		this.impactNumber = impactNumber;
	}

	public String getImpactArea() {
		return impactArea;
	}

	public void setImpactArea(String impactArea) {
		this.impactArea = impactArea;
	}

	public String getImpactBusiness() {
		return impactBusiness;
	}

	public void setImpactBusiness(String impactBusiness) {
		this.impactBusiness = impactBusiness;
	}

	public String getFaultType() {
		return faultType;
	}

	public void setFaultType(String faultType) {
		this.faultType = faultType;
	}

	public String getFaultDetailType() {
		return faultDetailType;
	}

	public void setFaultDetailType(String faultDetailType) {
		this.faultDetailType = faultDetailType;
	}

	public String getFaultDescription() {
		return faultDescription;
	}

	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}

	public String getFaultCause() {
		return faultCause;
	}

	public void setFaultCause(String faultCause) {
		this.faultCause = faultCause;
	}

	public String getWhetherOnlineFault() {
		return whetherOnlineFault;
	}

	public void setWhetherOnlineFault(String whetherOnlineFault) {
		this.whetherOnlineFault = whetherOnlineFault;
	}

	public String getWhetherOnlineImpact() {
		return whetherOnlineImpact;
	}

	public void setWhetherOnlineImpact(String whetherOnlineImpact) {
		this.whetherOnlineImpact = whetherOnlineImpact;
	}

	public String getImprovePoint() {
		return improvePoint;
	}

	public void setImprovePoint(String improvePoint) {
		this.improvePoint = improvePoint;
	}

	public String getTemporaryImprove() {
		return temporaryImprove;
	}

	public void setTemporaryImprove(String temporaryImprove) {
		this.temporaryImprove = temporaryImprove;
	}

	public String getStationaryImprove() {
		return stationaryImprove;
	}

	public void setStationaryImprove(String stationaryImprove) {
		this.stationaryImprove = stationaryImprove;
	}

	public String getImproveOwner() {
		return improveOwner;
	}

	public void setImproveOwner(String improveOwner) {
		this.improveOwner = improveOwner;
	}



	public String getBusinessOwner() {
		return businessOwner;
	}

	public void setBusinessOwner(String businessOwner) {
		this.businessOwner = businessOwner;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getFaultLevel() {
		return faultLevel;
	}

	public void setFaultLevel(String faultLevel) {
		this.faultLevel = faultLevel;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getJiraUrl() {
		return jiraUrl;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}

	public String getDetailProcess() {
		return detailProcess;
	}

	public void setDetailProcess(String detailProcess) {
		this.detailProcess = detailProcess;
	}	
		
}
