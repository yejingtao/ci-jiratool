package com.mgtv.autoplug.jiratool.message;

public class AlarmResponse {
	
	public static final String STATUS_FAIL = "fail";
	
	public static final String STATUS_CREATED = "created";
	
	public static final String STATUS_NOT_FOUND = "notfound";
	
	public static final String STATUS_UPDATED = "updated";
	
	private String status;
	
	private String issueKey;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}
	
}
