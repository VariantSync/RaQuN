package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.common.MatcherAdapter;

import java.nio.file.Paths;
import java.util.List;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

/**
 * RQ2Runner executes the experiments related to RQ1 in our paper.
 */
public class RQ2Runner extends AbstractRQRunner {

    public RQ2Runner(final String... args) {
        super(args);
    }

    public static void main(final String... args) {
        new RQ2Runner(args).run();
    }

    @Override
    public void run() {
        System.out.println("Running RQ2");
        final List<String> datasets = configuration.datasetsRQ2();
        final MatcherAdapter matcher = configuration.matcherRQ2();

        for (final String dataset : datasets) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            String resultsDir = baseResultsDir;
            final int startK = configuration.startK();
            int maxK = configuration.maxK();
            if (dataset.startsWith("argouml")) {
                maxK = configuration.maxKArgoUML();
                resultsDir = Paths.get(baseResultsDir, "argouml").toString();
            }

            // The random subsets are matched in subsets of size 10 in accordance with Rubin and Chechik, ESEC-FSE13
            final int chunkSize = Integer.MAX_VALUE;
            final int numberOfRepeats = configuration.repetitionsRQ2();

            final ExperimentSetup raqunSetup = new ExperimentSetup(
                    "RaQuN_k",
                    numberOfRepeats,
                    resultsDir,
                    baseDatasetDir,
                    dataset,
                    chunkSize, verbose,
                    startK, maxK,
                    configuration.timeoutDuration(), configuration.timeoutUnit());

            runExperiment(matcher, raqunSetup, baseResultsDir, dataset);
        }
    }
}
