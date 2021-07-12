package org.raqun.paper.nwm.alg.pair;

import java.util.ArrayList;
import java.util.Comparator;

import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.domain.Model;

public class ModelSizeBasedPairWiseMatcher extends PairWiseMatch {

    private boolean largestFirst;

    public ModelSizeBasedPairWiseMatcher(ArrayList<Model> lst, boolean useLargestFirst) {
        super(useLargestFirst ? "Pairwise Largest Models First" : "Pairwise Smallest Models First", lst, useLargestFirst);
        largestFirst = useLargestFirst;
    }

    @Override
    public Comparator<HungarianMerger> getPolicyComperator(final boolean largerFrst) {
        // TODO Auto-generated method stub
        /*
        Note by RaQuN authors: We had to fix a bug in this method, therefore it is no longer working exactly as
        implemented by Rubin and Chechik.
         */
        return (mp1, mp2) -> {
            int large1 = mp1.getLargerModel().size();
            int small1 = mp1.getSmallerModel().size();
            int large2 = mp2.getLargerModel().size();
            int small2 = mp2.getSmallerModel().size();

            int compResult;

            if (large1 < large2) {
                compResult = -1;
            } else if (large1 > large2) {
				compResult = 1;
            } else {
                if (small1 < small2) {
					compResult = -1;
                } else if (small1 > small2) {
					compResult = 1;
                } else {
					compResult = 0;
                }
            }

			return largerFrst ? -compResult : compResult;
        };
    }
}
