package org.raqun.paper.pairwise;

import org.raqun.paper.experiments.baseline.EBaselineImplementation;
import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

import java.util.*;

import static org.raqun.paper.nwm.alg.pair.PairWiseMatch.filterTuplesByTreshold;

public class HungarianPairwiseMatcher extends AlgoBase {
    private final ArrayList<Model> models;
    private final EBaselineImplementation sortMode;
    private int numberOfComparisons;

    public HungarianPairwiseMatcher(ArrayList<Model> models, EBaselineImplementation sortMode){
        super("Hungarian Pairwise Fast");
        this.models = models;
        this.sortMode = sortMode;
    }

    @Override
    protected ArrayList<Tuple> doRun() {
        // Sort models by size ascending or descending
        if (sortMode == EBaselineImplementation.PairwiseAsc) {
            models.sort(Comparator.comparingInt(Model::size));
        } else if (sortMode == EBaselineImplementation.PairwiseDesc) {
            models.sort((m1, m2) -> Integer.compare(m2.size(), m1.size()));
        } else {
            throw new UnsupportedOperationException("This sort mode has not been implemented yet!");
        }

        // Iterate over the sorted list of models and match them iteratively
        Model mergedModel = models.get(0);
        HungarianMerger merger = null;
        numberOfComparisons = 0;
        for (int i = 1; i < models.size(); i++) {
            numberOfComparisons += (mergedModel.size() * models.get(i).size());
            merger = new HungarianMerger(mergedModel, models.get(i), 2);
            merger.runPairing();
            mergedModel = merger.mergeMatchedModels();
        }

        boolean storedVal = AlgoUtil.COMPUTE_RESULTS_CLASSICALLY;

        ArrayList<Tuple> realMerge = Objects.requireNonNull(merger).extractMerge();
        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
        if(storedVal){
            for(Tuple t:realMerge){
                t.recomputeSelf(this.models);
            }
        }
        ArrayList<Tuple> retVal = filterTuplesByTreshold(realMerge, models);

        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;
        return retVal;
    }

    public int getNumberOfComparisons() {
        return this.numberOfComparisons;
    }

    @Override
    public ArrayList<Model> getModels() {
        return null;
    }
}
