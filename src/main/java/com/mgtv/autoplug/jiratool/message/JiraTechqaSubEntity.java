package com.mgtv.autoplug.jiratool.message;

import java.util.Date;

public class JiraTechqaSubEntity extends JiraEntity{
	
	private String testProject; //测试项目 10377
	
	private Date onlineTime; //上线时间 10139
	
	private Date testEndTime; //测试完成时间 10211
	
	private int testRound; //测试轮数  10369
	
	private String testDescribe; //提测工单描述 10378
	
	private String reportDetail; //测试报告详情 10385
	
	private String reportComment; //测试报告备注 10386
	
	private String testResult; //测试子任务结果 10381
	
	//测试任务邮件通知组
	//10384
	private String[] notifyEmail;
		
	//邮件通知，个人
	//10259
	private String[] notifyEmailPerson;
	
	public String getReportDetail() {
		return reportDetail;
	}

	public void setReportDetail(String reportDetail) {
		this.reportDetail = reportDetail;
	}

	public String getReportComment() {
		return reportComment;
	}

	public void setReportComment(String reportComment) {
		this.reportComment = reportComment;
	}

	public String getTestProject() {
		return testProject;
	}

	public void setTestProject(String testProject) {
		this.testProject = testProject;
	}


	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public int getTestRound() {
		return testRound;
	}

	public void setTestRound(int testRound) {
		this.testRound = testRound;
	}

	public String getTestDescribe() {
		return testDescribe;
	}

	public void setTestDescribe(String testDescribe) {
		this.testDescribe = testDescribe;
	}

	public String[] getNotifyEmail() {
		return notifyEmail;
	}

	public void setNotifyEmail(String[] notifyEmail) {
		this.notifyEmail = notifyEmail;
	}

	public String[] getNotifyEmailPerson() {
		return notifyEmailPerson;
	}

	public void setNotifyEmailPerson(String[] notifyEmailPerson) {
		this.notifyEmailPerson = notifyEmailPerson;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public Date getTestEndTime() {
		return testEndTime;
	}

	public void setTestEndTime(Date testEndTime) {
		this.testEndTime = testEndTime;
	}
	
	
}
