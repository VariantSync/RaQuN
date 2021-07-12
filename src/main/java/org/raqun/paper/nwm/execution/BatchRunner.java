package org.raqun.paper.nwm.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.raqun.paper.nwm.domain.Element;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;

public class BatchRunner extends ResultsWriter {

	private BatchRunDescriptor desc;
	private ArrayList<ArrayList<Model>> models;
	private int numOfBatches;

	public BatchRunner(BatchRunDescriptor desc, String excelFilePath) {
		super(excelFilePath);
		this.desc = desc;
		this.numOfBatches = desc.numOfBatches;
	}
	
	public BatchRunner(String modelsSourceFileName, int numOfBatches, String excelFilePath){
		super(excelFilePath);
		this.numOfBatches = numOfBatches;
		ArrayList<Model> mdls = Model.readModelsFile(modelsSourceFileName);
		models = new ArrayList<ArrayList<Model>>();
		int numOfModelsInBatch = mdls.size() / numOfBatches;
		for(int i=0,currModel=0;i<numOfBatches;i++){
			ArrayList<Model> batch = new ArrayList<Model>();
			for(int j=0;j<numOfModelsInBatch;j++){
				batch.add(mdls.get(currModel++));
			}
			models.add(batch);
		}
	}
	
	private void combineResults(ArrayList<ArrayList<RunResult>> accum, ArrayList<RunResult> addition){
		//if(accum.size() == 0){
			accum.add(addition);
			//for(int i=0;i<addition.size();i++){
			//	accum.get(i).add(addition.get(i));
			return;
		//}
			
		//for(int i=0;i<accum.size();i++){
		//	accum.get(i).add(addition.get(i));
		//}
	}
	
	private ArrayList<ArrayList<Model>> getModels(){
		if(this.models == null)
			this.models = readModels();
		return this.models;
	}
	
	
	private ArrayList<ArrayList<Model>> readModels() {
		ArrayList<ArrayList<Model>> allModels = new ArrayList<ArrayList<Model>>();
		for(int i=0; i<numOfBatches;i++){
			System.out.println("starting batch "+i);
			ArrayList<Model> models = Model.batchModelGeneration(i*desc.numOfModelsInBatch,
																   desc.numOfModelsInBatch, 
																	desc.minNumOfElements, desc.maxNumOfElements, 
																	desc.minPropLength, desc.maxPropLength,
																	desc.commonVacabularyMin, desc.diffVacabularyMin);
			allModels.add(models);
		}
		
		writeModelsToFile(allModels);
		return allModels;
	}
	
	public void runOnLocalSearch(N_WAY.ALG_POLICY pol, ArrayList<ArrayList<Model>> allModels) {
		ArrayList<ArrayList<RunResult>> results = new ArrayList<ArrayList<RunResult>>();
		for(int i=0; i<numOfBatches;i++){
			ArrayList<Model> models = allModels.get(i);
			Runner rnr = new Runner(models);
			combineResults(results, rnr.runOnLocalSearch(pol, null,3));
		}
		//averageResults(results);
		writeResults("Randomized - LS "+pol, results);
	}
	

	private void writeResults(String sheetName, ArrayList<ArrayList<RunResult>> results) {
		ArrayList<RunResult> rrArr = new ArrayList<RunResult>();
		for(ArrayList<RunResult> ar:results){
			rrArr.addAll(ar);
		}
		
		Collections.sort(rrArr, new Comparator<RunResult>() {

			@Override
			public int compare(RunResult rr1, RunResult rr2) {
				// TODO Auto-generated method stub
				return rr1.title.compareTo(rr2.title);
			}
		});
		super.writeResults(rrArr, sheetName);
	}

	public void runOnGreedy(int splitSize, ArrayList<ArrayList<Model>> allModels) {
		ArrayList<ArrayList<RunResult>> runOnGreedyResults = new ArrayList<ArrayList<RunResult>>();
		for(int i=0; i<numOfBatches;i++){
			ArrayList<Model> models = allModels.get(i);
			Runner rnr = new Runner(models);
			combineResults(runOnGreedyResults, rnr.runOnGreedy(splitSize));
		}
		//averageResults(runOnGreedyResults);
		writeResults("Randomized - Greedy on "+splitSize, runOnGreedyResults);
	}
	
	
	private void runNewHungarian(ArrayList<ArrayList<Model>> allModels) {
		ArrayList<ArrayList<RunResult>> runNewHungarianResults = new ArrayList<ArrayList<RunResult>>();
		for(int i=0; i<numOfBatches;i++){
			ArrayList<Model> models = allModels.get(i);
			Runner rnr = new Runner(models);
			combineResults(runNewHungarianResults, rnr.runBigHungarian());
		}
		//averageResults(runNewHungarianResults);
		writeResults("New Hungarian", runNewHungarianResults);
	}

	private void runOnPairs(ArrayList<ArrayList<Model>> allModels) {
		ArrayList<ArrayList<RunResult>> runOnPairsResults = new ArrayList<ArrayList<RunResult>>();
		for(int i=0; i<numOfBatches;i++){
			ArrayList<Model> models = allModels.get(i);
			Runner rnr = new Runner(models);
			combineResults(runOnPairsResults, rnr.runOnPairs());
		}
		//averageResults(runOnPairsResults);
		writeResults("Randomized - pairs", runOnPairsResults);
	}

	protected void averageResults(ArrayList<RunResult> results) {
		for(int i=0;i<results.size();i++){
			results.get(i).divideBy(numOfBatches);
		}
	}

	public void run(){
		ArrayList<ArrayList<Model>> allModels =getModels();
		
		//runOnPairs(allModels);
		//runNewHungarian(allModels);
		runGreedies(allModels);
		
		//runLocalSearches(allModels);

	}
	
	
	private void runLocalSearches(ArrayList<ArrayList<Model>> allModels) {
		runOnLocalSearch(N_WAY.ALG_POLICY.REPLACE_FIRST, allModels);
		runOnLocalSearch(N_WAY.ALG_POLICY.REPLACE_BEST, allModels);
		runOnLocalSearch(N_WAY.ALG_POLICY.REPLACE_BEST_BY_SQUARES, allModels);
		
	}

	private void runGreedies(ArrayList<ArrayList<Model>> allModels) {
		GreedyRunnerThread grt;
		//grt = new GreedyRunnerThread(this, allModels, 3);
		//grt.start();
		grt = new GreedyRunnerThread(this, allModels, 4);
		grt.start();
//		grt = new GreedyRunnerThread(this, allModels, 5);
//		grt.start();
//		grt = new GreedyRunnerThread(this, allModels, 6);
//		grt.start();
	}

	private void writeModelsToFile(ArrayList<ArrayList<Model>> allModels)  {
		FileOutputStream fileOut;
		FileInputStream fileIn;
		HSSFWorkbook workbook;
		String sheetName ="Models";
		try {
			
			fileIn = new FileInputStream(new File(excelFilePath));
	        
		//Get the workbook instance for XLS file 
			workbook = new HSSFWorkbook(fileIn);
			HSSFSheet sheet = workbook.getSheet(sheetName);
			if(sheet == null)
				sheet = workbook.createSheet(sheetName);
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("num of batches");
			row.createCell(1).setCellValue("num of models in batch");
			row = sheet.createRow(1);
			row.createCell(0).setCellValue(numOfBatches);
			row.createCell(1).setCellValue(desc.numOfModelsInBatch);

			int rowInd = 2;
			for(ArrayList<Model> models:allModels){
				for(Model m:models){
					for(Element e:m.getElements()){
						row = sheet.createRow(rowInd++);
						e.writeElementToRow(row);
					}
				}
			}
			fileIn.close();
			fileOut = new FileOutputStream(new File(excelFilePath));
			workbook.write(fileOut); 
			fileOut.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	


	public static class BatchRunDescriptor{
		public int numOfModelsInBatch;
		public int numOfBatches;
		public int minNumOfElements;
		public int maxNumOfElements;
		public int minPropLength;
		public int maxPropLength;
		public int commonVacabularyMin;
		public int diffVacabularyMin;
	}
	
	public class GreedyRunnerThread extends Thread {
		
		private int splitSize;
		private ArrayList<ArrayList<Model>> allModels;
		private BatchRunner bRunner;

		public GreedyRunnerThread(BatchRunner bRunner, ArrayList<ArrayList<Model>> allModels, int splitSize){
			this.splitSize = splitSize;
			this.allModels = allModels;
			this.bRunner = bRunner;
		}
		
	    public void run() {
	       bRunner.runOnGreedy(splitSize,allModels);
	    }
    }
}



