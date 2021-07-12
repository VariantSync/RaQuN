package org.raqun.paper.nwm.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;

import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;

public class Experiment {

	private String modelInputFilePath;
	private ArrayList<Model> models;
	
	private N_WAY.Strategy strategy;
	private int totalRuns = 0;
	private Hashtable<String, RunResult> runsResultsAccum;
	private ArrayList<AlgoBase> sampleRun;
	private int algsToUse = -1;
	private String outputFileName;

	private Experiment( N_WAY.Strategy strategy, int algsToUse, String outputFileName){
		this.strategy = strategy;
		this.algsToUse = algsToUse;
		this.outputFileName = outputFileName;
	}
	
	public Experiment(ArrayList<Model> models, N_WAY.Strategy strategy, int algsToUse, String outputFileName){
		this(strategy, algsToUse, outputFileName);
		this.models = models;
	}
	
	public Experiment(String inputFilePath, N_WAY.Strategy strategy, int algsToUse, String outputFileName){
		this(strategy, algsToUse, outputFileName);
		modelInputFilePath = inputFilePath;
		if(modelInputFilePath != null){
			models = Model.readModelsFile(modelInputFilePath);
		}
//		else{
//			models = new ArrayList<Model>();
//			for(int i=0; i< k;i++)	models.add(Model.generate(i, n ,l));
//			String fileName = (modelOutputFileName==null)?"l"+l+"k"+k+"n"+n+"t"+t:modelOutputFileName;
//			writeModels(fileName+".csv",models);
//		}
	}
	
	private void runOnModelsSubset(int subsetSize, int currIndex, ArrayList<Model> accumSubset){
		if(accumSubset.size() == subsetSize){
			doSingleRun(accumSubset);
			return;
		}
			

		int currentSubgroupSize = accumSubset.size();
		int remainingModelsToCheck = models.size() - currIndex;
		int remaingModelsToFillInSubgroup = subsetSize - currentSubgroupSize;
		if( remaingModelsToFillInSubgroup > remainingModelsToCheck ) //we cannot fill the subgroup with the remaining tuples at *all* - returning without success
			return;

		
		for(int i=currIndex ; i<models.size();i++){
			Model m = models.get(i);
			accumSubset.add(m);
			runOnModelsSubset(subsetSize, i+1, accumSubset);
			accumSubset.remove(m);
		}
	}
	
	private void doSingleRun(ArrayList<Model> mdls){
		totalRuns++;
		System.out.println(totalRuns);
		SingleRun sr = new SingleRun(mdls, algsToUse);
		ArrayList<AlgoBase> allExecAlgos = sr.run();
		for(int i=0;i<allExecAlgos.size();i++){
			AlgoBase alg = allExecAlgos.get(i);
			addToResult(alg.getName(), alg.getRunResult());
		}
	}
	
	private void addToResult(String algName, RunResult result){
		RunResult accumulated = runsResultsAccum.get(algName);
		if(accumulated == null){
			accumulated = new RunResult(0, BigDecimal.ZERO, BigDecimal.ZERO, null);
			runsResultsAccum.put(algName,accumulated);
		}
		accumulated.add(result);
	}
	
	@SuppressWarnings("unchecked")
	public RunResult run(){
		AlgoUtil.reset();
		if(models == null)
			return null;
		
		totalRuns = 0;
		runsResultsAccum = new Hashtable<String, RunResult>();
		sampleRun = new SingleRun(models, algsToUse).getAlgsToRun();
		
		if(strategy == N_WAY.Strategy.MODEL_TRIPLETS){
			runOnModelsSubset(3,0,new ArrayList<Model>());
		}
		if(strategy ==  N_WAY.Strategy.ENTIRE_INPUT){
			runOnModelsSubset(models.size(),0,(ArrayList<Model>) models.clone());
		}
		
		if(totalRuns != 0)
			for(RunResult r:runsResultsAccum.values())
				r.divideBy(totalRuns);
		
		
		System.out.println(runsResultsAccum);
		return null;
	}
	
	public String reportOnResults(){
		StringBuilder sb = new StringBuilder();
		for(AlgoBase alg: sampleRun){
			String algName = alg.getName();
			//RunResult res = runsResultsAccum.get(algName);
			sb.append(algName).append(",");
			sb.append(runsResultsAccum.get(algName).toCSV());
		}
		return sb.toString();
		
	}
	
	public void writeResultsAsCSV(){
		try{
			FileWriter fstream = new FileWriter("results/"+outputFileName+".csv");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(reportOnResults());
			out.close();
			}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	
}
