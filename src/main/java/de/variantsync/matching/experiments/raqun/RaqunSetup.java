package de.variantsync.matching.experiments.raqun;

import de.variantsync.matching.raqun.data.MatchValidityConstraint;
import de.variantsync.matching.raqun.similarity.SimilarityFunction;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.raqun.tree.Vectorization;

public class RaqunSetup extends ExperimentSetup {

    public final SimilarityFunction similarityFunction;
    public final int startK;
    public final int maxK;
    public final Class<? extends Vectorization> vectorization;

    public RaqunSetup(String name, int numberOfRepeats,
                      String resultDir, String datasetDir,
                      String dataset, int chunkSize,
                      SimilarityFunction similarityFunction,
                      int startK, int maxK,
                      Class<? extends Vectorization> vectorization,
                      MatchValidityConstraint validityConstraint) {
        super(name, numberOfRepeats, resultDir, datasetDir, dataset, chunkSize, validityConstraint);
        this.similarityFunction = similarityFunction;
        this.startK = startK;
        this.maxK = maxK;
        this.vectorization = vectorization;
    }
}