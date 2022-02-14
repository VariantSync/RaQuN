package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.common.MatcherAdapter;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

/**
 * RQ3Runner executes the experiments related to RQ1 in our paper.
 */
public class RQ3Runner extends AbstractRQRunner {
    private String subsetID = null;
    private static final int FIRST_SUBSET = 1;
    private static final int LAST_SUBSET = 30;

    public RQ3Runner(final String... args) {
        super(args);
        if (args.length > 1) {
            this.subsetID = args[1].trim();
        }
    }

    public static void main(final String... args) {
        new RQ3Runner(args).run();
    }

    @Override
    public void run() {
        // Add all argouml subsets
        final File folder = new File(Paths.get(baseDatasetDir).toString(), "argouml");
        final File[] listOfFiles = folder.listFiles();
        final List<String> argoSets = new ArrayList<>();
        if (listOfFiles != null) {
            for (final File argoFile : listOfFiles) {
                if (argoFile.toString().endsWith(".csv")) {
                    final String name = argoFile.getName().replace(".csv", "");
                    argoSets.add(name);
                }
            }
        }
        List<String> datasets = new ArrayList<>(argoSets);
        if (subsetID != null) {
            System.out.println("Filtering by SUBSET_ID: " + subsetID);
            final int id = Integer.parseInt(subsetID);
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

        boolean reachedLargestSubset = false;
        final String timeoutSubset;
        if (subsetID == null) {
            timeoutSubset = "p" + configuration.getExperimentsRq3LargestSubset() + "_s030";
        } else {
            timeoutSubset = "p" + configuration.getExperimentsRq3LargestSubset() + "_" + subsetID;
        }

        final Map<String, MatcherAdapter> matchers = new HashMap<>();
        final Map<String, Boolean> successMap = new HashMap<>();
        for (final String name : configuration.matchersRQ3()) {
            matchers.put(configuration.matcherDisplayName(name), configuration.loadMatcher(name));
            successMap.put(configuration.matcherDisplayName(name), Boolean.TRUE);
        }

        for (final String dataset : datasets) {
            if (reachedLargestSubset) {
                break;
            }
            if (dataset.contains(timeoutSubset)) {
                reachedLargestSubset = true;
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            final String resultsDir = Paths.get(baseResultsDir, "argouml").toString();
            String datasetDir = baseDatasetDir;

            if (dataset.startsWith("argouml_")) {
                datasetDir = Paths.get(baseDatasetDir, "argouml").toString();
            }

            // The random subsets are matched in subsets of size 10 in accordance with Rubin and Chechik, ESEC-FSE13
            final int chunkSize = Integer.MAX_VALUE;
            final int numberOfRepeats = configuration.repetitionsRQ3();

            for (final Map.Entry<String, MatcherAdapter> entry : matchers.entrySet()) {
                if (successMap.get(entry.getKey())) {
                    final ExperimentSetup experimentSetup = new ExperimentSetup(entry.getKey(), numberOfRepeats,
                            resultsDir, datasetDir, dataset, chunkSize, verbose, 0, 0,
                            configuration.timeoutDuration(), configuration.timeoutUnit());
                    final boolean success = runExperiment(entry.getValue(),
                            experimentSetup,
                            datasetDir,
                            dataset);
                    if (!success) {
                        successMap.put(entry.getKey(), Boolean.FALSE);
                    }
                }
            }
        }
    }
}