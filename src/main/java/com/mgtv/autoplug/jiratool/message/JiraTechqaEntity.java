package com.mgtv.autoplug.jiratool.message;

public class JiraTechqaEntity extends JiraEntity{
	
	//测试任务邮件通知组
	//10384
	private String[] notifyEmail;
	
	//邮件通知，个人
	//10259
	private String[] notifyEmailPerson;
	
	private String testProject; //测试项目 10377
	
	private String caseDetail; //用例方案详情 10383

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

	public String getTestProject() {
		return testProject;
	}

	public void setTestProject(String testProject) {
		this.testProject = testProject;
	}


	public String getCaseDetail() {
		return caseDetail;
	}

	public void setCaseDetail(String caseDetail) {
		this.caseDetail = caseDetail;
	}
	
	

}
