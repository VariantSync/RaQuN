package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.experiments.baseline.EBaselineImplementation;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

/**
 * RQ3Runner executes the experiments related to RQ1 in our paper.
 */
public class RQ3Runner extends AbstractRQRunner {
    private String subsetID = null;
    public final int FIRST_SUBSET = 1;
    public final int LAST_SUBSET = 30;

    public RQ3Runner(String... args) {
        super(args);
        if (args.length > 1) {
            this.subsetID = args[1].trim();
        }
    }

    public static void main(String... args) {
        new RQ3Runner(args).run();
    }

    @Override
    public void run() {
        // Add all argouml subsets
        File folder = new File(Paths.get(baseDatasetDir).toString(), "argouml");
        File[] listOfFiles = folder.listFiles();
        List<String> argoSets = new ArrayList<>();
        if (listOfFiles != null) {
            for (File argoFile : listOfFiles) {
                if (argoFile.toString().endsWith(".csv")) {
                    String name = argoFile.getName().replace(".csv", "");
                    argoSets.add(name);
                }
            }
        }
        List<String> datasets = new ArrayList<>(argoSets);
        if (subsetID != null) {
            System.out.println("Filtering by SUBSET_ID: " + subsetID);
            int id = Integer.parseInt(subsetID);
            if (id < FIRST_SUBSET || id > LAST_SUBSET) {
                throw new RuntimeException("Invalid subsetID '" + subsetID + "'. expected a number in the interval [1, 30].");
            }
            // If only specific subsets are to be run
            if (subsetID.length() == 1) {
                subsetID = "s00" + subsetID;
            } else if (subsetID.length() == 2) {
                subsetID = "s0" + subsetID;
            } else {
                throw new IllegalArgumentException("Invalid subset id!");
            }
            // Filter the subsets
            datasets = datasets.stream().filter((d) -> d.contains(subsetID)).collect(Collectors.toList());
            Collections.sort(datasets);
            // Add argouml once
            datasets.add("argouml");
        } else {
            Collections.sort(datasets);
            // Add argouml once for each subsetID
            for (int i = FIRST_SUBSET; i <= LAST_SUBSET; i++) {
                datasets.add("argouml");
            }
        }

        // Flag through which we set that nwm had a timeout for an ArgoUML Subset size
        // If set to true, NwM will no longer be executed on the ArgoUML subsets
        boolean nwmTimeout = false;
        boolean reachedLargestSubset = false;
        String timeoutSubset;
        if (subsetID == null) {
            timeoutSubset = "p" + configuration.getExperimentsRq3LargestSubset() + "_s030";
        } else {
            timeoutSubset = "p" + configuration.getExperimentsRq3LargestSubset() + "_" + subsetID;
        }

        for (String dataset : datasets) {
            if (reachedLargestSubset) {
                break;
            }
            if (dataset.contains(timeoutSubset)) {
                reachedLargestSubset = true;
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            String resultsDir = Paths.get(baseResultsDir, "argouml").toString();
            String datasetDir = baseDatasetDir;

            if (dataset.startsWith("argouml_")) {
                datasetDir = Paths.get(baseDatasetDir, "argouml").toString();
            }

            // The random subsets are matched in subsets of size 10 in accordance with Rubin and Chechik, ESEC-FSE13
            int chunkSize = Integer.MAX_VALUE;
            int numberOfRepeats = configuration.repetitionsRQ3();

            RaqunSetup raqunSetup = new RaqunSetup("RaQuN", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, 0, 0, verbose, vectorization, similarityFunction,
                    validityConstraint);

            ExperimentSetup nwmSetup = new ExperimentSetup("NwM", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, verbose);

            ExperimentSetup pairwiseAscSetup = new ExperimentSetup("PairwiseAsc", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, verbose);

            ExperimentSetup pairwiseDescSetup = new ExperimentSetup("PairwiseDesc", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, verbose);

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

            // NwM
            if (!dataset.equals("argouml")) {
                // argouml is too big for NwM, therefore we stop after the calculation of the 40% subset
                // We determined through experiments that NwM exceeds the timeout of 12 hours here on all subsets
                if (dataset.startsWith("argouml_p045")) {
                    nwmTimeout = true;
                }
                if (!dataset.startsWith("argouml_") || !nwmTimeout) {
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
}
