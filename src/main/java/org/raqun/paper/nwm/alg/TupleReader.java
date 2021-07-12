package org.raqun.paper.nwm.alg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;

public class TupleReader {

	private ArrayList<Tuple> result = new ArrayList<Tuple>();

	public TupleReader(ArrayList<Model> models, String fileLoc) {

		FileInputStream file;
		HSSFWorkbook workbook;
		
		try {
			file = new FileInputStream(new File(fileLoc));
        
		//Get the workbook instance for XLS file 
			workbook = new HSSFWorkbook(file);
			 
			//Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheet("HMatch");
			Hashtable<String, Model> id2model = makeMapOfModels(models);
			//Get iterator to all the rows in current sheet
			int TT=0;
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()){
					String tupleValue = cellIterator.next().getStringCellValue();
					if(!tupleValue.startsWith("TUPLE :")) continue;
					tupleValue = tupleValue.substring(8).trim();
					String[] allElems = tupleValue.split(">");
					Tuple t = new Tuple();
					for(int i=0;i<allElems.length;i++){
						String elem = allElems[i];
						String[] contents = elem.split("<");
						Model m = id2model.get(new Long(contents[1]));
						t.addElement(m.getElementByLabel(contents[0]));
						t.setWeight(t.calcWeight(models));
					}
					this.result.add(t);
					System.out.println(TT +")READ : "+tupleValue);
					System.out.println(TT+")GOT : "+t);TT++;
					//System.out.println(tupleValue);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Get iterator to all cells of current row
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public RunResult getResult(){
		BigDecimal resultWeight = AlgoUtil.calcGroupWeight(result);
		//System.out.println(result.size());
		RunResult rr = new RunResult(0,resultWeight , resultWeight.divide(new BigDecimal( result.size(), N_WAY.MATH_CTX)), result);
		rr.setTitle("Manual");
		return rr;
	}
	
	private Hashtable<String, Model> makeMapOfModels(ArrayList<Model> models){
		Hashtable<String, Model> id2model = new Hashtable<String, Model>();
		for(Model m:models){
			id2model.put(m.getId(), m);
		}
		return id2model;
	}

}
