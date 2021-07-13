package de.variantsync.matching.experiments;

import de.variantsync.matching.RQRunner;
import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.experiments.baseline.EBaselineImplementation;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import java.nio.file.Paths;
import java.util.List;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

public class RQ1Runner extends RQRunner {

    public RQ1Runner(String... args) {
        super(args);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // No configuration to be done below this line
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String... args) {
        new RQ1Runner(args).run();
    }

    @Override
    public void run() {
        List<String> datasets = configuration.datasetsRQ1();
        String baseResultsDir = configuration.resultsFolder();
        String baseDatasetDir = configuration.datasetsFolder();
        int numberOfRepeats = configuration.numberOfRepeats();
        boolean verbose = configuration.verboseResults();

        ISimilarityFunction similarityFunction = configuration.similarityFunction();
        IValidityConstraint validityConstraint = configuration.validityConstraint();
        IVectorization vectorization = configuration.vectorization();

        for (String dataset : datasets) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            String resultsDir = baseResultsDir;

            if (dataset.startsWith("argouml")) {
                resultsDir = Paths.get(baseResultsDir, "argouml").toString();
            }

            // The random subsets are matched in subsets of size 10 in accordance with Rubin and Chechik, ESEC-FSE13
            int chunkSize = Integer.MAX_VALUE;
            if (dataset.startsWith("random")) {
                chunkSize = 10;
            }

            RaqunSetup raqunSetup = new RaqunSetup("RaQuN", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, 0, 0, verbose, vectorization, similarityFunction,
                    validityConstraint);

            ExperimentSetup nwmSetup = new ExperimentSetup("NwM", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, verbose);

            ExperimentSetup pairwiseAscSetup = new ExperimentSetup("PairwiseAsc", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, verbose);

            ExperimentSetup pairwiseDescSetup = new ExperimentSetup("PairwiseDesc", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, verbose);

            // Run Pairwise
            if (configuration.shouldRunPairwiseAsc()) {
                runExperiment(new BaselineAlgoAdapter(pairwiseAscSetup, EBaselineImplementation.PairwiseAsc),
                        baseResultsDir,
                        pairwiseAscSetup.name,
                        dataset);
            }
            if (configuration.shouldRunPairwiseDesc()) {
                runExperiment(new BaselineAlgoAdapter(pairwiseDescSetup, EBaselineImplementation.PairwiseDesc),
                        baseResultsDir,
                        pairwiseDescSetup.name,
                        dataset);
            }

            // Raqun
            if (configuration.shouldRunRaQuN()) {
                runExperiment(new RaQuNAdapter(raqunSetup), baseResultsDir, raqunSetup.name, dataset);
            }

            // NwM
            // argouml is too big for NwM, therefore we exclude it
            if (!dataset.equals("argouml")) {
                if (configuration.shouldRunNwM()) {
                    runExperiment(new BaselineAlgoAdapter(nwmSetup, EBaselineImplementation.NwM),
                            baseResultsDir,
                            nwmSetup.name,
                            dataset);
                }
            }
        }
    }
}
