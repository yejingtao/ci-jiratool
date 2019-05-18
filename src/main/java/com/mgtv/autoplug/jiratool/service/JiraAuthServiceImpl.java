package com.mgtv.autoplug.jiratool.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class JiraAuthServiceImpl implements JiraAuthService{

	@Override
	public String getBasicAuth(String userName, String password) {
		 String basic_auth = "Basic ";
	     basic_auth += Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
	     return basic_auth;
	}
	
}
