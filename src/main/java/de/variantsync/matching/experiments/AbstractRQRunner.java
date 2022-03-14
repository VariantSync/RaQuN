package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;

import java.io.File;

/**
 * Abstract base class for RQRunners
 */
public abstract class AbstractRQRunner {
    protected ExperimentConfiguration configuration;
    protected String baseResultsDir;
    protected String baseDatasetDir;
    protected boolean verbose;

    protected AbstractRQRunner(final String... args) {
        if (args.length >= 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            configuration = new ExperimentConfiguration();
        }

        baseResultsDir = configuration.resultsFolder();
        baseDatasetDir = configuration.datasetsFolder();
        verbose = configuration.verboseResults();
    }

    /**
     * Run the experiment related to a research question (RQ)
     */
    public abstract void run();
}