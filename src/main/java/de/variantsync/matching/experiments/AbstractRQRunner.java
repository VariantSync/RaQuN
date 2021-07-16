package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import java.io.File;

/**
 * Abstract base class for RQRunners
 */
public abstract class AbstractRQRunner {
    protected ExperimentConfiguration configuration;
    protected String baseResultsDir;
    protected String baseDatasetDir;
    protected boolean verbose;
    protected ISimilarityFunction similarityFunction;
    protected IValidityConstraint validityConstraint;
    protected IVectorization vectorization;

    protected AbstractRQRunner(String... args) {
        if (args.length == 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            configuration = new ExperimentConfiguration();
        }

        baseResultsDir = configuration.resultsFolder();
        baseDatasetDir = configuration.datasetsFolder();
        verbose = configuration.verboseResults();

        similarityFunction = configuration.similarityFunction();
        validityConstraint = configuration.validityConstraint();
        vectorization = configuration.vectorization();
    }

    public abstract void run();
}