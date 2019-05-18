package com.mgtv.autoplug.jiratool.message;


public class JiraReleaseEntity extends JiraEntity{
	
	private String gitProject;
	
	private String onlineGitTag;
	
	private String rollbackGitTag;
	
	private String detail;
	
	private String[] notifyEmail;
	
	//10259
	private String[] notifyEmailPerson;
	
	private JiraUser operator;
	
	private JiraUser tester;
	
	private JiraUser approver;

	public String getGitProject() {
		return gitProject;
	}

	public void setGitProject(String gitProject) {
		this.gitProject = gitProject;
	}

	public String getOnlineGitTag() {
		return onlineGitTag;
	}

	public void setOnlineGitTag(String onlineGitTag) {
		this.onlineGitTag = onlineGitTag;
	}

	public String getRollbackGitTag() {
		return rollbackGitTag;
	}

	public void setRollbackGitTag(String rollbackGitTag) {
		this.rollbackGitTag = rollbackGitTag;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String[] getNotifyEmail() {
		return notifyEmail;
	}

	public void setNotifyEmail(String[] notifyEmail) {
		this.notifyEmail = notifyEmail;
	}

	public JiraUser getOperator() {
		return operator;
	}

	public void setOperator(JiraUser operator) {
		this.operator = operator;
	}

	public JiraUser getTester() {
		return tester;
	}

	public void setTester(JiraUser tester) {
		this.tester = tester;
	}

	public JiraUser getApprover() {
		return approver;
	}

	public void setApprover(JiraUser approver) {
		this.approver = approver;
	}

	public String[] getNotifyEmailPerson() {
		return notifyEmailPerson;
	}

	public void setNotifyEmailPerson(String[] notifyEmailPerson) {
		this.notifyEmailPerson = notifyEmailPerson;
	}

}
