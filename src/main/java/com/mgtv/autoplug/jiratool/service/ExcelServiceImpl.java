package com.mgtv.autoplug.jiratool.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExcelServiceImpl implements ExcelService{
	
	public static final String EXCEL_XLS = "xls";
	
	public static final String EXCEL_XLSX = "xlsx";
	
	public final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);
	
	//parseMapping第一个参数是excel中列号，第二个参数是返回map中的key
	@Override
	public List<Map<String,Object>> parseExcel(String file, Map<Integer, String> parseMapping) {
		List<Map<String,Object>> resultList = null;
		File excel = new File(file);
		if (excel.isFile() && excel.exists()) { 
			//根据后缀来创建不同的excel处理类
			String[] split = excel.getName().split("\\.");
			Workbook wb = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(excel);   //文件流对象
				//根据文件后缀（xls/xlsx）进行判断
	            if ( "xls".equals(split[1])){          
	                wb = new HSSFWorkbook(fis);
	            }else if ("xlsx".equals(split[1])){
	                wb = new XSSFWorkbook(fis);
	            }else {
	            	logger.error("文件类型错误!");
	                return resultList;
	            }
	            
	            resultList = new ArrayList<Map<String,Object>>();
	            
	            //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0
                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
	            
                for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
                	//遍历每一行
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                    	Map<String,Object> thisRowMap = new HashMap<String,Object>();
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {
                        	//遍历每一列
                            Cell cell = row.getCell(cIndex);                            
                            if (cell != null && parseMapping.get(cIndex)!=null) {
                            	//String格式解析
                            	if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            		thisRowMap.put(parseMapping.get(cIndex), cell.toString());
                                }else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                	if(HSSFDateUtil.isCellDateFormatted(cell)) {
                                		thisRowMap.put(parseMapping.get(cIndex),HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                                	}else {
                                		thisRowMap.put(parseMapping.get(cIndex), cell.toString());
                                	}                           		
                                }
                            }
                        }
                        resultList.add(thisRowMap);
                    }
                }
                
			}catch(IOException ioe) {
				logger.error("找不到指定的文件");
				ioe.printStackTrace();
			}finally {
				if(wb!=null) {
					try {
						wb.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
												
		} else {
			logger.error("找不到指定的文件");
        }


		return resultList;
	}

	@Override
	public Map<Integer, String> getFaultParseMap() {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		mapping.put(1, "date");
		mapping.put(2, "startTime");
		mapping.put(3, "findTime");
		mapping.put(9, "endTime");
		mapping.put(12, "impactNumber");
		mapping.put(11, "impactArea");
		mapping.put(15, "impactBusiness");
		mapping.put(13, "faultType");
		mapping.put(14, "faultDetailType");
		mapping.put(20, "faultDescription");
		mapping.put(22, "faultCause");
		mapping.put(18, "whetherOnlineFault");
		mapping.put(19, "whetherOnlineImpact");
		mapping.put(23, "improvePoint");
		mapping.put(24, "temporaryImprove");
		mapping.put(25, "stationaryImprove");
		mapping.put(28, "improveOwner");
		mapping.put(29, "businessOwner");
		mapping.put(16, "serviceLevel");
		mapping.put(30, "faultLevel");
		mapping.put(31, "department");
		mapping.put(32, "jiraUrl");
		mapping.put(10, "faultAllMinute");
		return mapping;
	}

}
