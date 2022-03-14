package de.variantsync.matching.experiments;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;
import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.experiments.common.MatcherAdapter;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.variantsync.matching.experiments.common.ExperimentHelper.runExperiment;

public class BasicExperimentsRunner {
    private List<String> datasets;
    private final List<String> matcherList;
    private final int repetitions;

    public BasicExperimentsRunner(final List<String> datasets, final List<String> matcherList, final int repetitions) {
        this.datasets = datasets;
        this.matcherList = matcherList;
        this.repetitions = repetitions;
    }


    public void run(final ExperimentConfiguration configuration, final String baseResultsDir, final String baseDatasetDir, final boolean verbose) {
        if (datasets.get(0).equals("ALL")) {
            datasets = retrieveDatasets(baseDatasetDir);
        }

        final Map<String, MatcherAdapter> matchers = new HashMap<>();
        for (final String name : matcherList) {
            matchers.put(configuration.matcherDisplayName(name), configuration.loadMatcher(name));
        }

        for (final String dataset : datasets) {
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

            for (final Map.Entry<String, MatcherAdapter> entry : matchers.entrySet()) {
                final ExperimentSetup experimentSetup = new ExperimentSetup(entry.getKey(), repetitions,
                        resultsDir, baseDatasetDir, dataset, chunkSize, verbose, 0, 0,
                        configuration.timeoutDuration(), configuration.timeoutUnit());
                runExperiment(entry.getValue(),
                        experimentSetup,
                        baseResultsDir,
                        dataset);
            }

        }
    }

    private List<String> retrieveDatasets(final String baseDatasetDir) {
        return Stream.of(Objects.requireNonNull(new File(baseDatasetDir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(n -> n.endsWith(".csv"))
                .map(n -> n.substring(0, n.length()-4))
                .collect(Collectors.toList());
    }
}
