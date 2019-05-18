package com.mgtv.autoplug.jiratool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EmailUtil {
	
	public static final String IMGO = "@imgo.tv";
	
	public static final String MGTV = "@mgtv.com";
	
	//完全是历史包袱
	//将jira中imgo的邮箱转化为mgtv
	public static String[] cleanout(Set<String> originalEmail) {
		String[] array =new String[originalEmail.size()];
		List<String> newEmails = new ArrayList<String>();
		for(String oldEmail: originalEmail) {
			newEmails.add(oldEmail.replaceAll(IMGO, MGTV));			
		}
		newEmails.toArray(array);
		return array;
	}
	
}
