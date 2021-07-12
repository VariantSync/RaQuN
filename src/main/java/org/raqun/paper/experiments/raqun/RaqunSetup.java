package org.raqun.paper.experiments.raqun;

import org.raqun.paper.experiments.common.ExperimentSetup;
import org.raqun.paper.raqun.data.MatchValidityConstraint;
import org.raqun.paper.raqun.similarity.SimilarityFunction;
import org.raqun.paper.raqun.tree.RKDTree;

public class RaqunSetup extends ExperimentSetup {

    public final SimilarityFunction similarityFunction;
    public final int startK;
    public final int maxK;
    public final RKDTree.EVectorization vectorization;

    public RaqunSetup(String name, int numberOfRepeats,
                      String resultDir, String datasetDir,
                      String dataset, int chunkSize,
                      SimilarityFunction similarityFunction,
                      int startK, int maxK,
                      RKDTree.EVectorization vectorization,
                      MatchValidityConstraint validityConstraint) {
        super(name, numberOfRepeats, resultDir, datasetDir, dataset, chunkSize, validityConstraint);
        this.similarityFunction = similarityFunction;
        this.startK = startK;
        this.maxK = maxK;
        this.vectorization = vectorization;
    }
}