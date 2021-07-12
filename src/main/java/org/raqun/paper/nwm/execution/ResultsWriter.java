package org.raqun.paper.nwm.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class ResultsWriter {

	protected String excelFilePath;
	
	
	public ResultsWriter(String excelFilePath) {
		this.excelFilePath = excelFilePath;
	}
	
	protected boolean canWriteToFile(){
		return excelFilePath != null;
	}
	
	protected synchronized void  writeResults(ArrayList<RunResult> results, String sheetName) {
		if(!canWriteToFile())
			return;
		FileOutputStream fileOut;
		FileInputStream fileIn;
		HSSFWorkbook workbook;
		
		try {
			
			fileIn = new FileInputStream(new File(excelFilePath));
	        
		//Get the workbook instance for XLS file 
			workbook = new HSSFWorkbook(fileIn);
			HSSFSheet sheet = workbook.getSheet(sheetName);
			if(sheet == null)
				sheet = workbook.createSheet(sheetName);
			Row firstRow = sheet.createRow(0);
			int rowInd = 1;
			RunResult.writeExcelHeaderLine(firstRow);
			for(RunResult rr:results){
				Row row = sheet.createRow(rowInd++);
				rr.toExcel(row);
			}
			fileIn.close();
			fileOut = new FileOutputStream(new File(excelFilePath));
			workbook.write(fileOut); 
			fileOut.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
			
	}

}
