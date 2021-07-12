package org.raqun.paper.nwm.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.raqun.paper.nwm.alg.TupleReader;
import org.raqun.paper.nwm.alg.merge.ChainingOptimizingMerger;
import org.raqun.paper.nwm.alg.merge.GreedyMerger;
import org.raqun.paper.nwm.alg.merge.LocalSearchMerger;
import org.raqun.paper.nwm.alg.merge.MergeDescriptor;
import org.raqun.paper.nwm.alg.merge.Merger;
import org.raqun.paper.nwm.alg.merge.MultiModelMerger;
import org.raqun.paper.nwm.alg.merge.PairWiseMerger;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.ModelComparator;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;

public class Runner extends ResultsWriter{

	private ArrayList<Model> models;
	private String manualResultsFileLoc;
	private boolean toChunkify;
	

	public Runner(ArrayList<Model> models){
		this(models, null, null, models.size(), true);
	}
	
	public Runner(ArrayList<Model> models, String excelFilePath, String manualResultsFileLoc, int numOfModelsToUse, boolean toChunkify) {
		super(excelFilePath);
		ArrayList<Model> modelSubset = new ArrayList<Model>();
		for(int i=0;i<numOfModelsToUse;i++){
			modelSubset.add(models.get(i));
		}
		if(modelSubset.size() == 0)
			this.models = models;
		else
			this.models = modelSubset;
		this.manualResultsFileLoc = manualResultsFileLoc;
		this.toChunkify = toChunkify;
	}
	
	private void addManualRun() {
		if(manualResultsFileLoc == null)
			return;
		TupleReader reader = new TupleReader(models, manualResultsFileLoc);
		RunResult manual = reader.getResult();
		ArrayList<RunResult> man = new ArrayList<RunResult>();
		man.add(manual);
		writeResults(man, "Manual");
	}

	public void execute(){
				
//		runOnLocalSearch(N_WAY.ALG_POLICY.REPLACE_BEST, "LS triwise");
	//	runOnLocalSearch(N_WAY.ALG_POLICY.REPLACE_FIRST_BY_SQUARES, "LS triwise");
		//addManualRun();
		
		//runOnPairs();
		if(!toChunkify){
			//runOnGreedy(models.size());
		}
		else{
			//runOnGreedy(3);
		//	runOnGreedy(4);
		//runOnGreedy(5);
		}
		AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
		runBigHungarian();
		
		
		
		//runLocalSearches(3);
		if(! toChunkify){
			runLocalSearches(models.size());
		}
		else{
	//		runLocalSearches(3);
		}
	//	runOnGreedy(5);
		//runOnGreedy(6);
		//runOnGreedy(7);
		//runOnGreedy(8);
		//runOnLocalSearch(3); // without graph
		//runOnLocalSearchWithBuckets(3, 1600, 17);
		//runOnLocalSearchWithBuckets(4, 40^3, 17);
		//runOnLocalSearchWithBuckets(5, 40^4, 17);
		//runOnLocalSearchWithBuckets(6, 40^5, 17);
		//runOnLocalSearchWithBuckets(7, 40^6, shift2);
		//runOnLocalSearchWithBuckets(8, 40^7, shift2);
		//runOnTotalLocalSearchWithBuckets(3, 1600, shift2);
		
		
	}
	
	private void runLocalSearches(int splitSize) {
		runLSOnThread(N_WAY.ALG_POLICY.REPLACE_FIRST, "Replace first triwise", splitSize);
		//runLSOnThread(N_WAY.ALG_POLICY.REPLACE_BEST, "Replace best triwise",splitSize);
		//runLSOnThread(N_WAY.ALG_POLICY.REPLACE_BEST_BY_SQUARES, "Replace best triwise by squares",splitSize);
	}

	private void runLSOnThread(final N_WAY.ALG_POLICY pol, final String sheetName,int splitSize) {
//		RunnerThread rt;
//		rt = new RunnerThread(this) {
//			@Override
//			protected void perform(Runner rnr) { 
				runOnLocalSearch(pol, sheetName, splitSize);
//				}};
//		rt.start();
	}

	private RunResult runLS(MergeDescriptor md, int splitSize){
		return performMerge(md, splitSize);
	}
	
	public ArrayList<RunResult> runOnLocalSearch(N_WAY.ALG_POLICY pol, String sheetName, int splitSize){
		ArrayList<RunResult> results = new ArrayList<RunResult>();
		ArrayList<MergeDescriptor> mds = allPermOnAlg(pol);
		for(MergeDescriptor md:mds){
			results.add(runLS(md, splitSize));
		}
		writeResults(results, sheetName+splitSize);
		return results;
	}
	
	public ArrayList<RunResult> runOnGreedy(int splitSize) {
		ArrayList<RunResult> results = new ArrayList<RunResult>();
		ArrayList<MergeDescriptor> mds = allPermOnAlg(N_WAY.ALG_POLICY.GREEDY);
		for(MergeDescriptor md:mds){
			results.add(performMerge(md, splitSize));
		}
		
		writeResults(results, "Greedy "+splitSize+(toChunkify?"-chunked":"-on-all")+(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY?"-classic":"-improved"));
		return results;
	}
	
	private Merger getMergerBasedOnPolicy(N_WAY.ALG_POLICY pol, ArrayList<Model> modelsToUse){
		N_WAY.ALG_POLICY policyToUse = pol;
		if(pol == N_WAY.ALG_POLICY.GREEDY)
			return new GreedyMerger(modelsToUse);
		boolean doSquaring = false;
		if(pol == N_WAY.ALG_POLICY.REPLACE_FIRST_BY_SQUARES || pol == N_WAY.ALG_POLICY.REPLACE_BEST_BY_SQUARES)
			 doSquaring = true; 
		
		if(pol == N_WAY.ALG_POLICY.REPLACE_FIRST_BY_SQUARES)
			policyToUse =  N_WAY.ALG_POLICY.REPLACE_FIRST;
		
		if(pol == N_WAY.ALG_POLICY.REPLACE_BEST_BY_SQUARES)
			policyToUse =  N_WAY.ALG_POLICY.REPLACE_BEST;
		return new LocalSearchMerger(modelsToUse, policyToUse, doSquaring);
	}
	
	private RunResult performMerge(MergeDescriptor md, int splitSize){
		long startTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		ArrayList<Model> mdls = (ArrayList<Model>) models.clone();
		GreedyMerger merger = null;
		Model matched = null;
		while(mdls.size() >= 2){
			mdls = sortModels(mdls, md);
			ArrayList<Model> modelsToUse = new ArrayList<Model>();
			for(int i=0;i<splitSize && i < mdls.size();i++){
				modelsToUse.add(mdls.get(i));
			}
			merger = (GreedyMerger) getMergerBasedOnPolicy(md.algPolicy, modelsToUse);
			matched = merger.mergeMatchedModels();
			mdls.removeAll(modelsToUse);
			mdls.add(matched);
		}
		RunResult rr = null;
		
		boolean storedVal = AlgoUtil.COMPUTE_RESULTS_CLASSICALLY;
		AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;

		
		if(mdls.size() == 2){
			PairWiseMerger pwm = new PairWiseMerger(mdls,md, true);
			pwm.run();
			pwm.refreshResultTuplesWeight(pwm.mergeMatchedModels());
			rr = pwm.getRunResult(models.size());
		}
		else{
			if(storedVal)
				merger.refreshTuplesElements();
			else
				merger.refreshResultTuplesWeight(matched);
			if(AlgoUtil.areThereDuplicates(merger.extractMerge())){
				System.out.println("PROBLEM");
			}
			AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;		
			rr = merger.getRunResult(models.size());
			AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
			//System.out.println(merger.mergeMatchedModels());
			//AlgoUtil.printTuples(merger.extractMerge());
		}
		
		rr.setTitle(AlgoUtil.nameOfMergeDescription(md, splitSize));
		long endTime = System.currentTimeMillis();
		rr.setExecTime(endTime-startTime);
		System.out.println(rr);
		AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;		
		return rr;
	}

	public ArrayList<RunResult> runOnPairs() {
		ArrayList<RunResult> results = new ArrayList<RunResult>();
		ArrayList<MergeDescriptor> mds = allPermOnAlg(N_WAY.ALG_POLICY.PAIR_WISE);
		for(MergeDescriptor md:mds){
			results.add(runPair(md));
		}
		writeResults(results, "Pairwise"+models.size()+(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY?"-classic":"-improved"));
		return results;
	}

	private RunResult runPair(MergeDescriptor md) {
		@SuppressWarnings("unchecked")
		PairWiseMerger pwm = new PairWiseMerger((ArrayList<Model>) models.clone(), md, false);
		pwm.run();
		pwm.refreshResultTuplesWeight(pwm.mergeMatchedModels());
		RunResult rr = pwm.getRunResult(models.size());
		rr.setTitle(AlgoUtil.nameOfMergeDescription(md, -1));
		System.out.println(rr);
		return rr;
	}
	
	public ArrayList<RunResult> runBigHungarian(){
		@SuppressWarnings("unchecked")
		MultiModelMerger mmm = new ChainingOptimizingMerger((ArrayList<Model>) models.clone());
		mmm.run();
		RunResult rr = mmm.getRunResult(models.size());
		rr.setTitle("New Hungarian");
		ArrayList<RunResult> result = new ArrayList<RunResult>();
		result.add(rr);
		System.out.println(rr);
		AlgoUtil.printTuples(mmm.getTuplesInMatch());
		writeResults(result, "New Hungarian");
		return result;
		
	}
	
	private ArrayList<MergeDescriptor> allPermOnAlg(N_WAY.ALG_POLICY pol){
		ArrayList<MergeDescriptor> retVal = new ArrayList<MergeDescriptor>();

		retVal.add(new MergeDescriptor(pol, true, N_WAY.ORDER_BY.MODEL_ID));
		if(toChunkify || pol == N_WAY.ALG_POLICY.PAIR_WISE){
			retVal.add(new MergeDescriptor(pol, true, N_WAY.ORDER_BY.MODEL_SIZE));
			retVal.add(new MergeDescriptor(pol, false, N_WAY.ORDER_BY.MODEL_SIZE));
		}
//		if(pol == N_WAY.ALG_POLICY.PAIR_WISE){
//			retVal.add(new MergeDescriptor(pol, true, N_WAY.ORDER_BY.MATCH_QUALITY));
//			retVal.add(new MergeDescriptor(pol, false, N_WAY.ORDER_BY.MATCH_QUALITY));
//		}

		//retVal.add(new MergeDescriptor(pol, true, N_WAY.ORDER_BY.COHESIVENESS));
		//retVal.add(new MergeDescriptor(pol, false, N_WAY.ORDER_BY.COHESIVENESS));

	
		
		//retVal.add(new MergeDescriptor(pol, false, N_WAY.ORDER_BY.MODEL_ID));
		return retVal;
	}

	private ArrayList<Model> sortModels(ArrayList<Model> mdls, final MergeDescriptor desc){
		if(desc.orderBy == N_WAY.ORDER_BY.MODEL_SIZE){
			Collections.sort(mdls, new ModelComparator(desc.asc));
			return mdls;
		}
		else if (desc.orderBy == N_WAY.ORDER_BY.COHESIVENESS){
			return AlgoUtil.getModelsByCohesiveness(mdls, desc.asc);
		}
		else{
			Comparator<Model> cmp = new Comparator<Model>() {
				@Override
				public int compare(Model m1, Model m2) {
					if(desc.asc)
						return  (int) (m1.getId().compareTo(m2.getId()));
					else
						return  (int) (m2.getId().compareTo(m1.getId()));
				}
				
			};
			Collections.sort(mdls, cmp);
			return mdls;
		}
			
	}
	
//	private RunResult runOn(MergeDescriptor md, int modelSplitSize, int bucketSize){
//		 
//		@SuppressWarnings("unchecked")
//		MultiModelMerger mmm = new MultiModelMerger((ArrayList<Model>) models.clone(), md, modelSplitSize, bucketSize);
//		mmm.run();
//		RunResult retVal = mmm.getRunResult(models.size());
//		System.out.println(retVal);
//		return retVal;
//	}

//	private void runOnBuckets(int modelSplitSize, int bucketSize){
//	ArrayList<RunResult> results = new ArrayList<RunResult>();
//	ArrayList<MergeDescriptor> descriptors = getDescriptors();
//	for(MergeDescriptor md:descriptors){
//		results.add(runOn(md, modelSplitSize, bucketSize));
//	}
//	String sheetName = "b="+bucketSize+", m="+modelSplitSize;
//	writeResults(results, sheetName);
//}
//	private ArrayList<MergeDescriptor> getDescriptors() {
//	ArrayList<MergeDescriptor> retVal = new ArrayList<MergeDescriptor>();
//	retVal.addAll(allPermOnAlg(N_WAY.ALG_POLICY.PAIR_WISE));
//	retVal.addAll(allPermOnAlg(N_WAY.ALG_POLICY.GREEDY));
//	retVal.addAll(allPermOnAlg(N_WAY.ALG_POLICY.REPLACE_FIRST));
//	retVal.addAll(allPermOnAlg(N_WAY.ALG_POLICY.REPLACE_BEST));
//	return retVal;
//}

	public static abstract class RunnerThread extends Thread {
		
		private Runner rnr;

		public RunnerThread(Runner rnr){
			this.rnr = rnr;
		}
		
	    public void run() {
	    	perform(rnr);
	    }
	    
	    protected abstract void perform(Runner rnr);
    }
	

}


