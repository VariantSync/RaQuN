package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.alg.local.BestFoundLocalSearch;
import org.raqun.paper.nwm.alg.local.FirstFoundLocalSearch;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;

public class LocalSearchMerger extends Merger{

	private N_WAY.ALG_POLICY policy;
	private AlgoBase alg;
	private boolean executed;
	private boolean doSquaring;

	public LocalSearchMerger(ArrayList<Model> models,  N_WAY.ALG_POLICY policy, boolean doSquaring) {
		super(models);
		this.policy = policy;
		this.doSquaring = doSquaring;
		
		if(policy == N_WAY.ALG_POLICY.REPLACE_FIRST){
			alg = new FirstFoundLocalSearch(models, doSquaring);
		}
		if(policy == N_WAY.ALG_POLICY.REPLACE_BEST){
			alg = new BestFoundLocalSearch(models, doSquaring);
		}
	}
	
	protected Matchable getMatch() {
		if(!executed){
			runAlg();
			executed = true;
		}
		return alg;
	}
	
	private void runAlg() {
		System.out.println("starting to run "+policy +" , doing squaring is "+doSquaring);
		alg.run();
	}

	public Model run(){
		return mergeMatchedModels();
	}
	
	
	public ArrayList<Tuple> getResult(){
		return alg.getResult();
	}
	
//	public RunResult getRunResult(int numOfModels){
//		refreshResultTuplesWeight(numOfModels);
//		return alg.getRunResult();
//	}
	
	
	
	
	
	public RunResult getRunResult(int numOfModels){
		ArrayList<Tuple> res = extractMerge();
		//System.out.println(mergeMatchedModels());
		 BigDecimal weight = AlgoUtil.calcGroupWeight(res);
		BigDecimal avgWeight = getAverageTupleWeight(res, weight);
		RunResult rr = alg.getRunResult();;
		rr.avgTupleWeight = avgWeight;
		rr.weight = weight;
		rr.updateBins(res);
		return rr;
	}
	
	public BigDecimal getAverageTupleWeight(ArrayList<Tuple> res, BigDecimal weight){
		if(res.size() == 0)
			return BigDecimal.ZERO;
		return AlgoUtil.truncateWeight(weight.divide(new BigDecimal(res.size(), N_WAY.MATH_CTX), N_WAY.MATH_CTX));
	}

}
