package com.mgtv.autoplug.jiratool.message;

import java.util.Date;

public class JiraEntity {
	
	private String id;
	
	private String key;
	
	private JiraUser reporter;
	
	private String summary;
	
	private JiraUser assigner;
	
	private String currentStage;
	
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public JiraUser getReporter() {
		return reporter;
	}

	public void setReporter(JiraUser reporter) {
		this.reporter = reporter;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public JiraUser getAssigner() {
		return assigner;
	}

	public void setAssigner(JiraUser assigner) {
		this.assigner = assigner;
	}

	public String getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
