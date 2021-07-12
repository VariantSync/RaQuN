package org.raqun.paper.nwm.alg.local;

import java.util.ArrayList;

import org.raqun.paper.nwm.domain.Tuple;

public interface SwapBeneficiallityDecider{
	// for computation caching - it is up to the caller to provide the neighbors, as it may be reused by it
	SwapDelta calcBeneficiallity(ArrayList<Tuple> solution, ArrayList<Tuple> proposedGroup, ArrayList<Tuple> neighborsInSolution);
}