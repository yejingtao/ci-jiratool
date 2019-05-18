package com.mgtv.autoplug.jiratool.message;

import java.util.Date;

public class JiraAlarmEntity extends JiraEntity{
	
	public static final String CDN = "CDN";
	
	
	//事件ID
	//10246
	private String alamrId;
	
	//问题
	//10108
	private String alarmDetail;
	
	//报警瞬时值
	//10251
	private String instantane;
	
	//故障时间
	//10252
	private Date startTime;
	
	//恢复时间
	//10119
	private Date endTime;
	
	//告警类型
	//10247
	private String alarmType;
	
	//重复告警次数
	//10260
	//这个类型是因为Jira中的数字都是小数类型
	private double sameCount;
	
	//不直接入库，报文中送来的jira主题部分
	private String messageSummary;

	public String getAlamrId() {
		return alamrId;
	}

	public void setAlamrId(String alamrId) {
		this.alamrId = alamrId;
	}



	public String getAlarmDetail() {
		return alarmDetail;
	}

	public void setAlarmDetail(String alarmDetail) {
		this.alarmDetail = alarmDetail;
	}

	public String getInstantane() {
		return instantane;
	}

	public void setInstantane(String instantane) {
		this.instantane = instantane;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public double getSameCount() {
		return sameCount;
	}

	public void setSameCount(double sameCount) {
		this.sameCount = sameCount;
	}

	public String getMessageSummary() {
		return messageSummary;
	}

	public void setMessageSummary(String messageSummary) {
		this.messageSummary = messageSummary;
	}

	
	
}
