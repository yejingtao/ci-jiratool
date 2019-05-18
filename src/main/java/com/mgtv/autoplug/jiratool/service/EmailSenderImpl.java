package com.mgtv.autoplug.jiratool.service;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mgtv.autoplug.jiratool.message.JiraUser;

@Service
public class EmailSenderImpl implements EmailSender {

	public final Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

	@Autowired
	private JavaMailSender mailSender; // 自动注入的Bean
	
	@Override
	public void sendSimpleEmail(String eFrom, String[] eTo, String[] eCc, String title, String velocity, Map<String, Object> params) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(eFrom);
		message.setTo(eTo);
		if (!StringUtils.isEmpty(eCc)) {
			message.setCc(eCc);
		}
		message.setSubject(title);
		message.setText(mergeMessage(velocity, params));
		mailSender.send(message);

	}

	private String mergeMessage(String velocity, Map<String, Object> params) {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		// 载入（获取）模板对象
		Template t = ve.getTemplate(velocity);
		VelocityContext ctx = new VelocityContext(params);
		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);
		return sw.toString();
	}

	@Override
	public void sendHtmlEmail(JiraUser eFrom, String[] eTo, String[] eCc, String title, String velocity,
			Map<String, Object> params) {
		MimeMessage message=mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message,true);			
			InternetAddress from = new InternetAddress();
			from.setAddress(eFrom.getEmailAddress());
			if(eFrom.getDisplayName()!=null) {
				from.setPersonal(eFrom.getDisplayName());
			}
			helper.setFrom(from);
	        helper.setTo(eTo);
	        if (!StringUtils.isEmpty(eCc)) {
	        	helper.setCc(eCc);
			}
	        
			
			/**
			 * for dev test
			InternetAddress from = new InternetAddress();
			from.setAddress("jingtao@mgtv.com");
			from.setPersonal("叶静涛");
			helper.setFrom(from);
	        helper.setTo(new String[] {"jingtao@mgtv.com"});
	        */
	        helper.setSubject(title);
	        
	        helper.setText(mergeMessage(velocity, params),true);
	        mailSender.send(message);
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
