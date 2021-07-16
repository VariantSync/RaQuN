package de.variantsync.matching.nwm.alg.local;

import java.util.ArrayList;

import de.variantsync.matching.nwm.domain.Tuple;

/**
 * Undocumented code by Rubin and Chechik
 */
public interface SwapBeneficiallityDecider{
	// for computation caching - it is up to the caller to provide the neighbors, as it may be reused by it
	SwapDelta calcBeneficiallity(ArrayList<Tuple> solution, ArrayList<Tuple> proposedGroup, ArrayList<Tuple> neighborsInSolution);
}