package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.raqun.paper.nwm.alg.greedy.GreedyStepper;
import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;

public class GreedyMerger extends Merger {
	
	private GreedyStepper alg;
	private boolean executed = false;

	public GreedyMerger(ArrayList<Model> models) {
		super(models);
		alg = new GreedyStepper(models);
		//AlgoUtil.printTuples(AlgoUtil.generateAllTuples(models));
	}

	@Override
	protected Matchable getMatch() {
		if(!executed){
			alg.run();
			executed = true;
		}
		return alg;
	}
	
	public RunResult getRunResult(int numOfModels){
		ArrayList<Tuple> res = extractMerge();
	//	if(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY)
	//		res = getMatch().getTuplesInMatch();
		//System.out.println(mergeMatchedModels());
		BigDecimal weight = AlgoUtil.calcGroupWeight(res);
		BigDecimal avgWeight = getAverageTupleWeight(res, weight);
		RunResult rr = alg.getRunResult();
		rr.avgTupleWeight = avgWeight;
		rr.weight = weight;
		rr.updateBins(res);
		AlgoUtil.printTuples(res);
		return rr;
	}
	
	public void refreshTuplesElements() {
		
		for(Tuple t:extractMerge()){
			t.recomputeSelf(this.models);
		}
	}
	
	public BigDecimal getAverageTupleWeight(ArrayList<Tuple> res, BigDecimal weight){
		if(res.size() == 0)
			return BigDecimal.ZERO;
		return AlgoUtil.truncateWeight(weight.divide(new BigDecimal(res.size(), N_WAY.MATH_CTX), N_WAY.MATH_CTX));
	}

}
