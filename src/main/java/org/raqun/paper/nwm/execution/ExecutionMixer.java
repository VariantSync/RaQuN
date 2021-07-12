package org.raqun.paper.nwm.execution;

import java.util.ArrayList;

import org.raqun.paper.nwm.domain.Model;

public class ExecutionMixer {

	//private int m;
	private ArrayList<Model> models;

	public ExecutionMixer(ArrayList<Model> models) {
		this.models = models;
	}
	
//	public RunResult run(int m, MergeDescriptor desc){
//		RunResult rr = null;
////		if(m == 2){
////			PairWiseMerger pwm = new PairWiseMerger(models,desc, false);
////			pwm.run();
////			pwm.getRunResult(models.size());
////			
////		}
////		
////		if(desc.algPolicy == N_WAY.ALG_POLICY.REPLACE_BEST || desc.algPolicy == N_WAY.ALG_POLICY.REPLACE_FIRST ){
////			rr = reduceByTriplets((ArrayList<Model>) models.clone(), desc.algPolicy, desc.asc, desc.byModelSize); 
////		}
//		
////		if(desc.algPolicy == N_WAY.ALG_POLICY.GREEDY /* && !byModelSize*/)
////			rr = doGreedy( (ArrayList<Model>) models.clone(), m,desc.byModelSize, desc.asc);
////		rr.setTitle(desc.algPolicy.toString() + " "+m +"\tasc:"+desc.asc +"  , by model size: "+desc.byModelSize+"\t");
//		return rr;
//	}
	
//	private ArrayList<Model> sortBy(ArrayList<Model> mdls ,boolean  byModelSize, final boolean  asc){
//		if(byModelSize){
//			Collections.sort(mdls, new ModelComparator(asc));
//			return mdls;
//		}
//		else{
//			return AlgoUtil.getModelsByCohesiveness(mdls, asc);
//		}
//	}

//	private RunResult doGreedy(ArrayList<Model> baseModels, int m, boolean byModelSize, boolean asc) {
//		long startTime = System.currentTimeMillis();
//		ArrayList<Model> mdls = baseModels;
//		GreedyMerger gm = null;
//		while(mdls.size() > 2){
//			mdls = sortBy(mdls,byModelSize, asc);
//			ArrayList<Model> modelsToUse = new ArrayList<Model>();
//			for(int i=0;i<m && i < mdls.size();i++){
//				modelsToUse.add(mdls.get(i));
//			}
//			gm = new GreedyMerger(modelsToUse);
//			Model matched = gm.mergeMatchedModels();
//			mdls.removeAll(modelsToUse);
//			mdls.add(matched);
//		}
//		
//		PairWiseMerger pwm = new PairWiseMerger(mdls,MergeDescriptor.EMPTY, true);
//		pwm.run();
//		//GreedyMerger gm = new GreedyMerger(mdls);
//		//gm.mergeMatchedModels();
//		RunResult rr = pwm.getRunResult(models.size());//gm.getRunResult(models.size());
//		long endTime = System.currentTimeMillis();
//		rr.setExecTime(endTime-startTime);
//		return rr;
//		
//		
//	}



//	private RunResult reduceByTriplets(ArrayList<Model> remainingModels,  N_WAY.ALG_POLICY algToUse,final boolean asc, boolean byModelSize) {
//		
//		if(remainingModels.size() > 1){
//			ArrayList<Model> triplet = new ArrayList<Model>();
//			triplet.add(remainingModels.get(0));
//			triplet.add(remainingModels.get(1));
//			if(remainingModels.size() > 2)
//				triplet.add(remainingModels.get(2));
//			LocalSearchMerger merger = new LocalSearchMerger(triplet, algToUse, false);
//			Model merged = merger.mergeMatchedModels();
//			remainingModels.removeAll(triplet);
//			remainingModels.add(merged);
//			remainingModels = sortBy(remainingModels, byModelSize, asc);
//			if(remainingModels.size() == 1)
//				return merger.getRunResult(0);
//			return reduceByTriplets(remainingModels, algToUse, asc, byModelSize);
//		}
//		return null;
//		
//	}

}
