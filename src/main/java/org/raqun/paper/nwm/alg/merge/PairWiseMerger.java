package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.alg.pair.ModelIdbasedPairWiseMatcher;
import org.raqun.paper.nwm.alg.pair.ModelSizeBasedPairWiseMatcher;
import org.raqun.paper.nwm.alg.pair.WeightBasedPairWiseMatcher;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.ModelComparator;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;

public class PairWiseMerger extends Merger{

	private ArrayList<Model> models;
	private MergeDescriptor desc;
	private Matchable match;
	private boolean keepOrder;
	private String resultName = null;
	long execTime = 0;
	private boolean alreadyRun = false;

	public PairWiseMerger(ArrayList<Model> models, MergeDescriptor md, boolean keepOrder) {
		this.models = models;
		this.desc = md;
		this.keepOrder = keepOrder;
	}
	
	public RunResult getRunResult(int numOfModels){
		
		ArrayList<Tuple> res = extractMerge();
		BigDecimal weight = AlgoUtil.calcGroupWeight(res);
		BigDecimal avgWeight =(res.size() == 0)?BigDecimal.ZERO: weight.divide(new BigDecimal(res.size(), N_WAY.MATH_CTX), N_WAY.MATH_CTX);
		RunResult rr = new RunResult(execTime , weight ,avgWeight ,res);
		rr.setTitle(resultName);
	//	AlgoUtil.printTuples(res);
		return rr;
	}
	
	public void run(){
		if(alreadyRun)
			return;
		
		long startTime = System.currentTimeMillis();
		if(desc.orderBy == N_WAY.ORDER_BY.MODEL_SIZE){
			ModelSizeBasedPairWiseMatcher alg = new ModelSizeBasedPairWiseMatcher(models, !desc.asc);
			alg.run();
			match = alg;
			resultName = alg.getName();
		}
		else if(desc.orderBy == N_WAY.ORDER_BY.MODEL_ID){
			ModelIdbasedPairWiseMatcher alg = new ModelIdbasedPairWiseMatcher(models, !desc.asc);
			alg.run();
			match = alg;
			resultName = alg.getName();
		}
		else if(desc.orderBy == N_WAY.ORDER_BY.MATCH_QUALITY){
			WeightBasedPairWiseMatcher alg = new WeightBasedPairWiseMatcher(models, !desc.asc);
			alg.run();
			match = alg;
			resultName = alg.getName();
		}
		
		else{
			 doPairsByAvg(desc.asc);
		}
		long endTime = System.currentTimeMillis();
		execTime = endTime - startTime;
		alreadyRun  = true;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Model> sortBy(ArrayList<Model> mdls ,boolean  byModelSize, final boolean  asc){
		if(keepOrder)
			return (ArrayList<Model>) mdls.clone();
		if(byModelSize){
			Collections.sort(mdls, new ModelComparator(asc));
			return mdls;
		}
		else{
			return AlgoUtil.getModelsByCohesiveness(mdls, asc);
		}
	}
	
	private void doPairsByAvg(boolean asc) {
		@SuppressWarnings("unchecked")
		ArrayList<Model> ms = (ArrayList<Model>) models.clone();
		HungarianMerger hm = null;
		
		while(ms.size() > 1){
			ms =  sortBy(ms, false, asc);
			hm = new HungarianMerger(ms.get(0), ms.get(1), models.size());
			hm.runPairing();
			Model m = hm.mergeMatchedModels();
			//System.out.println(m);
			ms.remove(1);
			ms.remove(0);
			ms.add(0, m);
		}
		hm.fitlerResultBasedOnThreshold();
		match = hm;
		
		resultName = "Pairwise by cohesiveness "+((desc.asc)?"Weaker First":"Stronger First");
	}

	@Override
	protected Matchable getMatch() {
		// TODO Auto-generated method stub
		run();
		return match;
	}

}
