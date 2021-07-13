package de.variantsync.matching;

import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.experiments.baseline.EBaselineImplementation;
import de.variantsync.matching.experiments.common.ExperimentConfiguration;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

public class RQ1Runner {

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // No configuration to be done below this line
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String... args) {
        ExperimentConfiguration configuration;
        if (args.length == 0) {
            configuration = new ExperimentConfiguration();
        } else if (args.length == 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            throw new IllegalArgumentException("Illegal number of arguments: " + args.length);
        }

        List<String> datasetsRQ1 = configuration.datasetsRQ1();
        WeightMetric weightMetric = new WeightMetric();
        String baseResultsDir = configuration.resultsFolder();
        String baseDatasetDir = configuration.datasetsFolder();
        int numberOfRepeats = configuration.numberOfRepeats();

        ISimilarityFunction similarityFunction = configuration.similarityFunction();
        IValidityConstraint validityConstraint = configuration.validityConstraint();
        IVectorization vectorization = configuration.vectorization();

        for (String dataset : datasetsRQ1) {
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
                    resultsDir, baseDatasetDir, dataset, chunkSize, similarityFunction, 0, 0,
                    vectorization,
                    validityConstraint);

            ExperimentSetup nwmSetup = new ExperimentSetup("NwM", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, validityConstraint);

            ExperimentSetup pairwiseAscSetup = new ExperimentSetup("PairwiseAsc", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, validityConstraint);

            ExperimentSetup pairwiseDescSetup = new ExperimentSetup("PairwiseDesc", numberOfRepeats,
                    resultsDir, baseDatasetDir, dataset, chunkSize, validityConstraint);

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

            // Raqun with k == number of models
            if (configuration.shouldRunRaQuN()) {
                runExperiment(new RaQuNAdapter(raqunSetup), baseResultsDir, raqunSetup.name, dataset);
            }

        }
    }

}
