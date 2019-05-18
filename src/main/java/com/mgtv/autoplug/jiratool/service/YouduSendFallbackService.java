package com.mgtv.autoplug.jiratool.service;

import org.springframework.stereotype.Component;

@Component
public class YouduSendFallbackService implements YouduSendService{

	@Override
	public String sendText(String toUsers, String message) {
		//服务降级
		return "error";
	}

}
