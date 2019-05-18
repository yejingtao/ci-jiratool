package com.mgtv.autoplug.jiratool.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoFillUtil {
	
	//利用反射构造实体对象
	public static <T> T fillBean(Map<String,Object> paramMap, Class<T> cls) {
		T bean = null;
		try {
			bean = cls.newInstance();
			Field[] fields = cls.getDeclaredFields();
			for(Field field : fields) {
                String fieldName = field.getName();
                Class<?> type = field.getType();
                Method method = cls.getDeclaredMethod("set" 
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1), type);
                Object value = paramMap.get(fieldName);
                
                if (value == null || value.toString().equals("")) {
                	continue;                	
                }
                method.invoke(bean, paramMap.get(fieldName));
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	//利用反射构造实体对象
	public static <T> List<T> fillBean(List<Map<String,Object>> paramMap, Class<T> cls) {
		List<T> resultList = new ArrayList<T>();
		if(paramMap!=null) {
			for(Map<String,Object> map : paramMap) {
				T bean = fillBean(map,cls);
				if(bean!=null) {
					resultList.add(bean);
				}
			}
		}
		return resultList;
	}
}
