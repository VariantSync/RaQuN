package de.variantsync.matching.experiments.raqun;

import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.raqun.vectorization.IVectorization;

public class RaqunSetup extends ExperimentSetup {

    public final ISimilarityFunction similarityFunction;
    public final int startK;
    public final int maxK;
    public final Class<? extends IVectorization> vectorization;

    public RaqunSetup(String name, int numberOfRepeats,
                      String resultDir, String datasetDir,
                      String dataset, int chunkSize,
                      ISimilarityFunction similarityFunction,
                      int startK, int maxK,
                      Class<? extends IVectorization> vectorization,
                      IValidityConstraint validityConstraint) {
        super(name, numberOfRepeats, resultDir, datasetDir, dataset, chunkSize, validityConstraint);
        this.similarityFunction = similarityFunction;
        this.startK = startK;
        this.maxK = maxK;
        this.vectorization = vectorization;
    }
}