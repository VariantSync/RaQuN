package de.variantsync.matching;

import de.variantsync.matching.raqun.validity.OneToOneValidity;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.common.MethodAdapter;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;
import de.variantsync.matching.experiments.baseline.EBaselineImplementation;
import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.raqun.tree.PropertyBasedVectorization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentRunner {
    // Only change this if you want to load models from a different directory
    public static final String baseDatasetDir = "experimental_subjects";
    // This directory is created in the working directory in order to store all experimental results there
    public static final String baseResultsDir = "results";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Configuration of experiments starts from here
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // Flags that determine which algorithms should be run
    public static final boolean shouldRunNwM = true;
    public static final boolean shouldRunPairwise_Ascending = true;
    public static final boolean shouldRunPairwise_Descending = true;
    public static final boolean shouldRunRaQuN = true;
    // Runs the experiment that studies the impact of an increasing number of neighbors on the performance of RaQuN
    public static final boolean shouldRunImpactOfKInvestigation = true;

    // Extra-Verbose mode, prints the tuples of each matching
    public static final boolean PRINT_MATCH = false;
    // Number of times each setup is executed, e.g., how often should RaQuN with high dimension vectorization be
    // run on the dataset Hospital?
    public static final int numberOfRepeats = 30;

    // Range of k for the evaluation of different numbers of neighbors for the candidate search
    public static final int startK = 1;
    public static final int normalMaxK = 20;
    public static final int argoMaxK = 10;

    // List of the "smaller" datasets
    // You can comment out lines of the datasets which you do not want to run (beware of commas)
    public static List<String> datasets = Arrays.asList(
            "hospitals",
            "warehouses",
            "random",
            "randomLoose",
            "randomTight",
            "ppu",
            "ppu_statem",
            "bcs",
            "bcms",
            "Apogames"
    );
    // Flags whether the subsets of ArgoUML should be run as well, warning: might take quite long
    public static boolean runArgoSubsets = true;
    public static boolean runArgoUMLFull = true;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // No configuration to be done below this line
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public static void main(String... args) {
        datasets = new ArrayList<>(datasets);

        String argoDir = "argouml";
        if (args.length == 1) {
            argoDir = args[0];
        } else if (args.length > 1) {
            throw new RuntimeException("Too many arguments");
        }

        if (runArgoSubsets) {
            File folder = new File(Paths.get(baseDatasetDir).toString(), argoDir);
            File[] listOfFiles = folder.listFiles();
            List<String> argoSets = new ArrayList<>();
            if (listOfFiles != null) {
                for (File argoFile : listOfFiles) {
                    String name = argoFile.getName().replace(".csv", "");
                    argoSets.add(name);
                }
            }
            Collections.sort(argoSets);
            datasets.addAll(argoSets);
        }
        if (runArgoUMLFull) {
            datasets.add("argouml");
        }

        ISimilarityFunction weightMetric = new WeightMetric();
        // Flag through which we set that nwm had a timeout for an ArgoUML Subset size
        // If set to true, NwM will no longer be executed on the ArgoUML subsets
        boolean nwmTimeout = false;
        for (String dataset : datasets) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            int maxK = normalMaxK;
            String resultsDir = baseResultsDir;
            String datasetDir = baseDatasetDir;
            if (dataset.startsWith("argouml")) {
                maxK = argoMaxK;
                resultsDir = Paths.get(baseResultsDir, "argouml").toString();
                if (dataset.startsWith("argouml_")) {
                    datasetDir = Paths.get(baseDatasetDir, argoDir).toString();
                }
            }

            int chunkSize = Integer.MAX_VALUE;
            if (dataset.startsWith("random")) {
                chunkSize = 10;
            }

            RaqunSetup raqunSetup = new RaqunSetup("RaQuN", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, weightMetric, -1, -1,
                    PropertyBasedVectorization.class,
                    new OneToOneValidity());

            RaqunSetup raqunKSetup = new RaqunSetup("RaQuN_k", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, weightMetric, startK, maxK,
                    PropertyBasedVectorization.class,
                    new OneToOneValidity());

            ExperimentSetup nwmSetup = new ExperimentSetup("NwM", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, new OneToOneValidity());

            ExperimentSetup pairwiseAscSetup = new ExperimentSetup("PairwiseAsc", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, new OneToOneValidity());

            ExperimentSetup pairwiseDescSetup = new ExperimentSetup("PairwiseDesc", numberOfRepeats,
                    resultsDir, datasetDir, dataset, chunkSize, new OneToOneValidity());

            // NwM
            if (!dataset.equals("argouml")) {
                // argouml is too big for NwM, therefore we stop after the calculation of the 40% subset
                // We determined through experiments that NwM exceeds the timeout of 12 hours here on all subsets
                if (dataset.startsWith("argouml_p045")) {
                    nwmTimeout = true;
                }
                if (!dataset.startsWith("argouml_") || !nwmTimeout) {
                    if (shouldRunNwM) {
                        runExperiment(new BaselineAlgoAdapter(nwmSetup, EBaselineImplementation.NwM),
                                nwmSetup.name,
                                dataset);
                    }
                }
            }

            // Run Pairwise
            if (shouldRunPairwise_Ascending) {
                runExperiment(new BaselineAlgoAdapter(pairwiseAscSetup, EBaselineImplementation.PairwiseAsc),
                        pairwiseAscSetup.name,
                        dataset);
            }
            if (shouldRunPairwise_Descending) {
                runExperiment(new BaselineAlgoAdapter(pairwiseDescSetup, EBaselineImplementation.PairwiseDesc),
                        pairwiseDescSetup.name,
                        dataset);
            }

            // Raqun with k == number of models
            if (shouldRunRaQuN) {
                runExperiment(new RaQuNAdapter(raqunSetup), raqunSetup.name, dataset);
            }

            // Raqun K step-wise
            if (!dataset.startsWith("argouml_") && !dataset.startsWith("webamp_")) {
                if (shouldRunImpactOfKInvestigation) {
                    runExperiment(new RaQuNAdapter(raqunKSetup), raqunKSetup.name, dataset);
                }
            }
        }
    }

    private static void runExperiment(MethodAdapter adapter, String name, String dataset) {
        try {
            System.out.println("Running " + name + " on " + dataset + "...");
            adapter.run();
        } catch (Error | Exception error) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String errorText = "+++++++++++++++++++++++\n"
                    + localDateTime
                    + ": ERROR for " + name + " on " + dataset + "\n"
                    + error
                    + "\n+++++++++++++++++++++++\n";

            File errorLogFile = Paths.get(baseResultsDir, "ERRORLOG.txt").toFile();
            try (FileWriter fw = new FileWriter(errorLogFile, true)) {
                fw.write(errorText);
                fw.write("\n");
            } catch (IOException e) {
                System.err.println("WARNING: Not possible to write to ERRORLOG!\n" + e);
            }

            System.err.println("ERROR for " + name + " on " + dataset + "\n" + error);
            error.printStackTrace();
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

}