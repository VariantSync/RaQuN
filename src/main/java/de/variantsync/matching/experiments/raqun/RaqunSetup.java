package de.variantsync.matching.experiments.raqun;

import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.raqun.vectorization.IVectorization;

/**
 * An ExperimentSetup that provides the required setup data for RaQuN.
 */
public class RaqunSetup extends ExperimentSetup {

    public final ISimilarityFunction similarityFunction;
    public final int startK;
    public final int maxK;
    public final IVectorization vectorization;
    public final IValidityConstraint validityConstraint;

    /**
     * Initialize a new setup for an experiment with RaQuN
     * @param name The name of the experiment, usually RaQuN plus a postfix
     * @param numberOfRepeats how often the experiment is repeated
     * @param resultDir where to save the results
     * @param datasetDir where to load the dataset from
     * @param dataset the dataset that is to be loaded
     * @param chunkSize The size of chunks in which the dataset is to be split. Set INTEGER.MAX_VALUE for no splitting
     * @param startK The k' to start with if multiple k' are considered
     * @param maxK The k' to end wth if multiple k' are considered
     * @param printVerbose Print the matches after completion
     * @param vectorization The vectorization function that is applied
     * @param similarityFunction The similarity function that is applied
     * @param validityConstraint The validity constraint that is applied
     */
    public RaqunSetup(String name, int numberOfRepeats,
                      String resultDir, String datasetDir,
                      String dataset, int chunkSize,
                      int startK, int maxK, boolean printVerbose, IVectorization vectorization, ISimilarityFunction similarityFunction,
                      IValidityConstraint validityConstraint) {
        super(name, numberOfRepeats, resultDir, datasetDir, dataset, chunkSize, printVerbose);
        this.similarityFunction = similarityFunction;
        this.startK = startK;
        this.maxK = maxK;
        this.vectorization = vectorization;
        this.validityConstraint = validityConstraint;
    }
}