package org.raqun.paper.nwm.alg.local;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Tuple;

public class AverageBasedBeneficiallityDecider implements SwapBeneficiallityDecider{
	@Override
	public SwapDelta calcBeneficiallity(ArrayList<Tuple> solution,
			ArrayList<Tuple> proposedGroup, ArrayList<Tuple> neighborsInSolution) {
		// TODO Auto-generated method stub
		BigDecimal groupWeight = AlgoUtil.calcGroupWeight(proposedGroup,true);
		BigDecimal neighborsWeight = AlgoUtil.calcGroupWeight(neighborsInSolution,true);
		if(groupWeight.compareTo(neighborsWeight)<0)
			return SwapDelta.NOT_USEFUL;
		BigDecimal solWeight = AlgoUtil.calcGroupWeight(solution, true);
		BigDecimal solutionAvg = solWeight.divide( new BigDecimal(solution.size(), N_WAY.MATH_CTX),N_WAY.MATH_CTX);
		BigDecimal solutionWeightAfterChange =  solWeight.subtract(neighborsWeight,N_WAY.MATH_CTX).add(groupWeight,N_WAY.MATH_CTX);
		int solutionSizeAfterChange = solution.size() - neighborsInSolution.size() + proposedGroup.size();
		BigDecimal solutionAvgAfterChange = solutionWeightAfterChange.divide(new BigDecimal(solutionSizeAfterChange, N_WAY.MATH_CTX),N_WAY.MATH_CTX);
		if(solutionAvgAfterChange.compareTo(solutionAvg) < 0)
			return SwapDelta.NOT_USEFUL;
		return new SwapDelta(neighborsInSolution, proposedGroup , solutionAvgAfterChange.subtract(solutionAvg));
	}
}