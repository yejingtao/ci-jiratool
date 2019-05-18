package com.mgtv.autoplug.jiratool.service;

import com.mgtv.autoplug.jiratool.message.JiraUser;

public interface JiraUserService {
	
	JiraUser findUserByKeyWord(String restUrl, String keyWord,  String authInfo);
}
