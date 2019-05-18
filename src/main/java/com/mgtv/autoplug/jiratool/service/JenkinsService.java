package com.mgtv.autoplug.jiratool.service;

public interface JenkinsService {
	
	boolean chechPackage(String baseUrl, String gitProject, String gitTag, String postfix, String auth);
}
