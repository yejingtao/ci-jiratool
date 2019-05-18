package com.mgtv.autoplug.jiratool.service;

import java.util.List;
import java.util.Map;

public interface ExcelService {
	
	//解析excel
	List<Map<String,Object>> parseExcel(String file, Map<Integer,String> parseMapping);
	
	Map<Integer,String> getFaultParseMap();
}
