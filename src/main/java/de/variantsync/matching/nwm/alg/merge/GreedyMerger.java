package de.variantsync.matching.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.variantsync.matching.experiments.common.Stopped;
import de.variantsync.matching.nwm.alg.greedy.GreedyStepper;
import de.variantsync.matching.nwm.alg.Matchable;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.common.N_WAY;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;
import de.variantsync.matching.nwm.execution.RunResult;

/**
 * Undocumented code by Rubin and Chechik
 */
public class GreedyMerger extends Merger {
	
	private GreedyStepper alg;
	private boolean executed = false;

	public GreedyMerger(ArrayList<Model> models, Stopped stopped) {
		super(models, stopped);
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