package de.variantsync.matching.nwm.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import de.variantsync.matching.nwm.alg.greedy.Greedy;
import de.variantsync.matching.nwm.alg.greedy.GreedyStepper;
import de.variantsync.matching.nwm.alg.local.FirstFoundLocalSearch;
import de.variantsync.matching.nwm.alg.local.LocalSearch;
import de.variantsync.matching.nwm.alg.pair.CardinalityBasedPairWiseMatcher;
import de.variantsync.matching.nwm.alg.pair.ModelSizeBasedPairWiseMatcher;
import de.variantsync.matching.nwm.domain.Element;
import de.variantsync.matching.nwm.alg.AlgoBase;
import de.variantsync.matching.nwm.alg.pair.WeightBasedPairWiseMatcher;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.common.N_WAY;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;

/**
 * Undocumented code by Rubin and Chechik
 */
public class SingleRun {
	
	private int l; // length of label
	private int t = 2;// num of elements in a subgroup
	private int k; // num om models
	private int n; // num of elements in a model
	
	private ArrayList<AlgoBase> algsToRun;
	

	private String modelOutputFileName;
	private String resultsFilePath;
	
	private ArrayList<Model> models;
	
	private boolean doAllPairs = true;
	private boolean doGreedy = true;
	private boolean doAllLocalSearch = true;
	
	public SingleRun(ArrayList<Model> mdls, int algsToUse){
		models = mdls;
		this.doAllPairs = (algsToUse & N_WAY.PAIR_WISE) != 0;
		this.doGreedy = (algsToUse & N_WAY.GREEDY) != 0;
		this.doAllLocalSearch = (algsToUse & (N_WAY.BEST_LOCAL_SEARCH | N_WAY.FIRST_LOCAL_SEARCH) ) != 0;
		initAlgsToRun();		
	}
	
	public SingleRun(ArrayList<Model> mdls){
		this(mdls, N_WAY.ALL_ALGOS);
	}
	

	public ArrayList<AlgoBase> getAlgsToRun(){
		return algsToRun;
	}

	public void setModelOutputFileName(String modelOutputFileName) {
		this.modelOutputFileName = modelOutputFileName;
	}

	public void setResultsFilePath(String resultsFilePath) {
		this.resultsFilePath = resultsFilePath;
	}
	
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
	public int getT() {
		return t;
	}
	public void setT(int t) {
		this.t = t;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}

	public static void writeModels(String fileName, ArrayList<Model> models){
		try{
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			for (Model model : models) {
				for (Element e : model.getElements()) {
					out.write(model.getId()+"," +e.getPrintLabel()+ "," +e.toPrint());
					out.newLine();
				}
			}
			
			  //Close the output stream
			out.close();
			}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initAlgsToRun(){
		algsToRun = new ArrayList<AlgoBase>();
		if(doAllPairs){
			algsToRun.add(new WeightBasedPairWiseMatcher((ArrayList<Model>) models.clone(), true));
			algsToRun.add(new WeightBasedPairWiseMatcher((ArrayList<Model>) models.clone(), false));
	
			algsToRun.add(new CardinalityBasedPairWiseMatcher((ArrayList<Model>) models.clone(), true));
			algsToRun.add(new CardinalityBasedPairWiseMatcher((ArrayList<Model>) models.clone(), false));
	
			algsToRun.add(new ModelSizeBasedPairWiseMatcher((ArrayList<Model>) models.clone(), true));
			algsToRun.add(new ModelSizeBasedPairWiseMatcher((ArrayList<Model>) models.clone(), false));
		}
		if(doGreedy){
			algsToRun.add(new Greedy("Greedy",models));
			algsToRun.add(new GreedyStepper(models));
		}
		if(doAllLocalSearch){
			algsToRun.add(new FirstFoundLocalSearch(models, false));
			//algsToRun.add(new BestFoundLocalSearch(models, t));
		}
	}
	
	
	public ArrayList<AlgoBase> run(){

		
		//System.out.println("-----------------\nPair-wise pairing threshold = " + AlgoUtil.pairWisePairingTreshold);
		//System.out.println("Final filtering threshold (not devided by k)= " + AlgoUtil.tresholdRation);

		AlgoBase algo;
		long tupleSetupTime = 0;
		for(int a = 0; a< algsToRun.size();a++){
			algo = algsToRun.get(a);
			algo.run();
			if(algo instanceof Greedy){
				tupleSetupTime = ((Greedy)algo).getTupleSetupTime();
				algo.addToExecTime(tupleSetupTime);
			}
			if(algo instanceof LocalSearch){
				algo.addToExecTime(tupleSetupTime);
			}
			System.out.println(algo.getRunResult());
			ArrayList<Tuple> sortedResult = new ArrayList<Tuple>(algo.getResult());
			Collections.sort(sortedResult, new AlgoUtil.TupleComparator());
			System.out.println(sortedResult);
		}
		AlgoUtil.reset();
		return algsToRun;
	}
	
}