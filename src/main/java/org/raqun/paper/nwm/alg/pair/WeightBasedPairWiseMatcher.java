package org.raqun.paper.nwm.alg.pair;

import java.util.ArrayList;
import java.util.Comparator;

import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.domain.Model;

public class WeightBasedPairWiseMatcher extends PairWiseMatch {
	
	private boolean bestFirst = true;
	
	public WeightBasedPairWiseMatcher(ArrayList<Model> lst, boolean useBestFirst) {
		super(useBestFirst?"Pairwise Best Match First":"Pairwise Worst Match First",lst, useBestFirst);
		bestFirst = useBestFirst;
	}

	@Override
	public Comparator<HungarianMerger> getPolicyComperator(final boolean useBestFirst) {
		// TODO Auto-generated method stub

		/*
        Note by RaQuN authors: We had to fix a bug in this method, therefore it is no longer working exactly as
        implemented by Rubin and Chechik.
         */
		return new Comparator<HungarianMerger>() {
			@Override
			public int compare(HungarianMerger mp1, HungarianMerger mp2) {
				int comparisonResult = mp1.getWeight().compareTo(mp2.getWeight());

				return bestFirst ? -comparisonResult : comparisonResult;
			}
		};
	}

}
