package com.mgtv.autoplug.jiratool.message;

import java.util.Date;

public class JiraOpopEntity extends JiraEntity{
	
	//操作目的
	//10162
	private String opTarget;
	
	//操作方案
	//10157
	private String opSchema;
	
	//计划操作时间
	//10165
	private Date opTime;
	
	//方案Review人
	//10161
	private JiraUser reviewer;
	
	//验证负责人
	//10239
	private JiraUser tester;
	
	//操作执行人
	//10238
	private JiraUser operator;
	
	//开发负责人
	//10243
	private JiraUser developer;
	
	//操作风险
	//10163
	private String opRisk;
	
	//可能影响的业务
	//10237
	private String implicated;
	
	//应急预案
	//10240
	private String planb;
	
	//运维变更邮件通知组
	//10258
	private String[] notifyEmail;
	
	//邮件通知，个人
	//10259
	private String[] notifyEmailPerson;

	public String getOpTarget() {
		return opTarget;
	}

	public void setOpTarget(String opTarget) {
		this.opTarget = opTarget;
	}

	public String getOpSchema() {
		return opSchema;
	}

	public void setOpSchema(String opSchema) {
		this.opSchema = opSchema;
	}

	public Date getOpTime() {
		return opTime;
	}

	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}

	public JiraUser getReviewer() {
		return reviewer;
	}

	public void setReviewer(JiraUser reviewer) {
		this.reviewer = reviewer;
	}

	public JiraUser getTester() {
		return tester;
	}

	public void setTester(JiraUser tester) {
		this.tester = tester;
	}

	public JiraUser getOperator() {
		return operator;
	}

	public void setOperator(JiraUser operator) {
		this.operator = operator;
	}

	public JiraUser getDeveloper() {
		return developer;
	}

	public void setDeveloper(JiraUser developer) {
		this.developer = developer;
	}

	public String getOpRisk() {
		return opRisk;
	}

	public void setOpRisk(String opRisk) {
		this.opRisk = opRisk;
	}

	public String getImplicated() {
		return implicated;
	}

	public void setImplicated(String implicated) {
		this.implicated = implicated;
	}

	public String getPlanb() {
		return planb;
	}

	public void setPlanb(String planb) {
		this.planb = planb;
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
	
	
}
