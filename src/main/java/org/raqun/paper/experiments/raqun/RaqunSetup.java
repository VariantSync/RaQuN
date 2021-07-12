package org.raqun.paper.experiments.raqun;

import org.raqun.paper.experiments.common.ExperimentSetup;
import org.raqun.paper.raqun.data.MatchValidityConstraint;
import org.raqun.paper.raqun.similarity.SimilarityFunction;
import org.raqun.paper.raqun.tree.KDTree;

public class RaqunSetup extends ExperimentSetup {

    public final SimilarityFunction similarityFunction;
    public final int startK;
    public final int maxK;
    public final KDTree.EVectorization vectorization;

    public RaqunSetup(String name, int numberOfRepeats,
                      String resultDir, String datasetDir,
                      String dataset, int chunkSize,
                      SimilarityFunction similarityFunction,
                      int startK, int maxK,
                      KDTree.EVectorization vectorization,
                      MatchValidityConstraint validityConstraint) {
        super(name, numberOfRepeats, resultDir, datasetDir, dataset, chunkSize, validityConstraint);
        this.similarityFunction = similarityFunction;
        this.startK = startK;
        this.maxK = maxK;
        this.vectorization = vectorization;
    }
}