package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import java.io.File;

public abstract class AbstractRQRunner {
    protected ExperimentConfiguration configuration;
    protected String baseResultsDir;
    protected String baseDatasetDir;
    protected int numberOfRepeats;
    protected boolean verbose;
    protected ISimilarityFunction similarityFunction;
    protected IValidityConstraint validityConstraint;
    protected IVectorization vectorization;

    protected AbstractRQRunner(String... args) {
        if (args.length == 0) {
            configuration = new ExperimentConfiguration();
        } else if (args.length == 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            throw new IllegalArgumentException("Illegal number of arguments: " + args.length);
        }
        baseResultsDir = configuration.resultsFolder();
        baseDatasetDir = configuration.datasetsFolder();
        numberOfRepeats = configuration.numberOfRepeats();
        verbose = configuration.verboseResults();

        similarityFunction = configuration.similarityFunction();
        validityConstraint = configuration.validityConstraint();
        vectorization = configuration.vectorization();
    }

    public abstract void run();
}
