package com.mgtv.autoplug.jiratool.message;

public class JiraReleaseSubEntity extends JiraEntity{
	
	private String gitProject;
	
	private String onlineGitTag;
	
	private JiraUser operator;
	
	private JiraUser tester;
	
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
}
