package de.variantsync.matching.pairwise;

import de.variantsync.matching.experiments.EAlgorithm;
import de.variantsync.matching.experiments.common.IKillableLongTask;
import de.variantsync.matching.nwm.alg.merge.HungarianMerger;
import de.variantsync.matching.nwm.alg.AlgoBase;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;

import java.util.*;

import static de.variantsync.matching.nwm.alg.pair.PairWiseMatch.filterTuplesByTreshold;

/**
 * Wrapper for calling the Hungarian-pairwise implementation considered by Rubin and Chechik, ESEC/FSE 2013
 * https://dl.acm.org/doi/10.1145/2491411.2491446
 */
public class HungarianPairwiseMatcher extends AlgoBase implements IKillableLongTask {
    private final ArrayList<Model> models;
    private final EAlgorithm sortMode;
    private int numberOfComparisons;
    private boolean isStopped = false;

    public HungarianPairwiseMatcher(ArrayList<Model> models, EAlgorithm sortMode){
        super("Hungarian Pairwise Fast");
        this.models = models;
        this.sortMode = sortMode;
    }

    @Override
    protected ArrayList<Tuple> doRun() {
        // Sort models by size ascending or descending
        if (sortMode == EAlgorithm.PairwiseAsc) {
            models.sort(Comparator.comparingInt(Model::size));
        } else if (sortMode == EAlgorithm.PairwiseDesc) {
            models.sort((m1, m2) -> Integer.compare(m2.size(), m1.size()));
        } else {
            throw new UnsupportedOperationException("This sort mode has not been implemented yet!");
        }

        if (logKilled()) {
            return null;
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
            if (logKilled()) {
                merger.kill();
                return null;
            }
        }

        boolean storedVal = AlgoUtil.COMPUTE_RESULTS_CLASSICALLY;

        ArrayList<Tuple> realMerge = Objects.requireNonNull(merger).extractMerge();
        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
        if(storedVal){
            for(Tuple t:realMerge){
                t.recomputeSelf(this.models);
            }
        }
        if (logKilled()) {
            return null;
        }
        ArrayList<Tuple> retVal = filterTuplesByTreshold(realMerge, models);

        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;
        return retVal;
    }

    /**
     *
     * @return How many comparisons were required to compute the last matching?
     */
    public int getNumberOfComparisons() {
        return this.numberOfComparisons;
    }

    @Override
    public ArrayList<Model> getModels() {
        return null;
    }

    @Override
    public void kill() {
        this.isStopped = true;
    }

    @Override
    public boolean killed() {
        return this.isStopped;
    }
}