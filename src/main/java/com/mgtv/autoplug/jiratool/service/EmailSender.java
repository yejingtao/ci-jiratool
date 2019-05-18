package com.mgtv.autoplug.jiratool.service;

import java.util.Map;

import com.mgtv.autoplug.jiratool.message.JiraUser;

public interface EmailSender {

	void sendSimpleEmail(String eFrom, String[] eTo, String[] eCc, String title, String velocity, Map<String, Object> params);
	
	void sendHtmlEmail(JiraUser eFrom, String[] eTo, String[] eCc, String title, String velocity, Map<String, Object> params);
}
