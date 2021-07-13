package de.variantsync.matching.experiments;

import de.variantsync.matching.RQRunner;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import java.nio.file.Paths;
import java.util.List;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

public class RQ2Runner extends RQRunner {

    public RQ2Runner(String... args) {
        super(args);
    }

    public static void main(String... args) {

    }

    @Override
    public void run() {
        List<String> datasets = configuration.datasetsRQ2();
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
            int startK = configuration.startK();
            int maxK = configuration.maxK();
            if (dataset.startsWith("argouml")) {
                maxK = configuration.maxKArgoUML();
                resultsDir = Paths.get(baseResultsDir, "argouml").toString();
            }

            // The random subsets are matched in subsets of size 10 in accordance with Rubin and Chechik, ESEC-FSE13
            int chunkSize = Integer.MAX_VALUE;

            RaqunSetup raqunSetup = new RaqunSetup(
                    "RaQuN_k",
                    numberOfRepeats,
                    resultsDir,
                    baseDatasetDir,
                    dataset,
                    chunkSize,
                    startK, maxK, verbose, vectorization, similarityFunction,
                    validityConstraint);

            runExperiment(new RaQuNAdapter(raqunSetup), baseResultsDir, raqunSetup.name, dataset);
        }
    }
}
