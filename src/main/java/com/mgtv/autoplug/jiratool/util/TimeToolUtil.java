package com.mgtv.autoplug.jiratool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeToolUtil {
	
	public static final String DATE_FORMAT_1 = "yyyy/MM/dd HH:mm:ss";
	
	public static final String DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	public static final String DATE_FORMAT_3 = "yyyy-MM-dd";
	
	public static String getOffsetString(String format, Date date, int unit, int offset) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
        Calendar rightNow = Calendar.getInstance(); 
        if(date==null) {
        	rightNow.setTimeInMillis(System.currentTimeMillis());
        }else {
        	rightNow.setTime(date);
        }
        rightNow.add(unit,offset);
        Date newDate=rightNow.getTime();
        String reStr = sdf.format(newDate);
        return reStr;
	}
	
	public static Date getOffsetDate(Date date, int unit, int offset) {
        Calendar rightNow = Calendar.getInstance(); 
        if(date==null) {
        	rightNow.setTimeInMillis(System.currentTimeMillis());
        }else {
        	rightNow.setTime(date);
        }
        rightNow.add(unit,offset);
        return rightNow.getTime();
	}
	
	public static Date parseString2Date(String format, String dateStr) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.parse(dateStr);
	}
	
	public static String formatDate(String format, Date date){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	public static void main(String[] args) {
		String text = "2019-02-19T10:10:00.000+0800";
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		//Instant.parse(text);
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try {
			Date date = sdf.parse(text);
			System.out.println(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	*/
}
