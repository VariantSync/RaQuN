package de.variantsync.matching.experiments;

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

/**
 * RQ1Runner executes the experiments related to RQ1 in our paper.
 */
public class RQ1Runner extends AbstractRQRunner {

    public RQ1Runner(final String... args) {
        super(args);
    }

    public static void main(final String... args) {
        new RQ1Runner(args).run();
    }

    @Override
    public void run() {
        List<String> datasets = configuration.datasetsRQ1();
        if (datasets.get(0).equals("ALL")) {
            datasets = retrieveDatasets();
        }

        final Map<String, MatcherAdapter> matchers = new HashMap<>();
        for (final String name : configuration.matchers()) {
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

            final int numberOfRepeats = configuration.repetitionsRQ1();

            for (final Map.Entry<String, MatcherAdapter> entry : matchers.entrySet()) {
                final ExperimentSetup experimentSetup = new ExperimentSetup(entry.getKey(), numberOfRepeats,
                        resultsDir, baseDatasetDir, dataset, chunkSize, verbose, 0, 0);
                runExperiment(entry.getValue(),
                        experimentSetup,
                        baseResultsDir,
                        dataset);
            }

        }
    }

    private List<String> retrieveDatasets() {
        return Stream.of(Objects.requireNonNull(new File(baseDatasetDir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(n -> n.endsWith(".csv"))
                .map(n -> n.substring(0, n.length()-4))
                .collect(Collectors.toList());
    }
}
