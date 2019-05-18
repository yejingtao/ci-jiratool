package com.mgtv.autoplug.jiratool.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="ci-youdu",fallback=YouduSendFallbackService.class)
public interface YouduSendService {
	
	@RequestMapping(value="/sendText",method=RequestMethod.POST)
	String sendText(@RequestParam("toUsers") String toUsers, @RequestParam("message") String message);
}
