package de.variantsync.matching;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;
import de.variantsync.matching.raqun.validity.OneToOneValidity;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.common.MethodAdapter;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
import de.variantsync.matching.experiments.raqun.RaqunSetup;
import de.variantsync.matching.experiments.baseline.EBaselineImplementation;
import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.raqun.vectorization.PropertyBasedVectorization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentRunner {
    public static final File DEFAULT_PROPERTIES_FILE = new File("src/main/resources/experiment.properties");

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // No configuration to be done below this line
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String... args) {
        ExperimentConfiguration configuration;
        if (args.length == 0) {
            configuration = new ExperimentConfiguration(DEFAULT_PROPERTIES_FILE);
        } else if (args.length == 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            throw new IllegalArgumentException("Illegal number of arguments: " + args.length);
        }

        List<String> datasetsRQ1 = new ArrayList<>(datasetsRQ1);

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
            datasetsRQ1.addAll(argoSets);
        }
        if (runArgoUMLFull) {
            datasetsRQ1.add("argouml");
        }

        ISimilarityFunction weightMetric = new WeightMetric();
        // Flag through which we set that nwm had a timeout for an ArgoUML Subset size
        // If set to true, NwM will no longer be executed on the ArgoUML subsets
        boolean nwmTimeout = false;
        for (String dataset : datasetsRQ1) {
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
                    resultsDir, datasetDir, dataset, chunkSize, weightMetric, 0, 0,
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


}