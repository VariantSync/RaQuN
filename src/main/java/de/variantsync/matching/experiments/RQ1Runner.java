package de.variantsync.matching.experiments;

import java.util.List;

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
        List<String> matchers = configuration.matchersRQ1();
        BasicExperimentsRunner runner = new BasicExperimentsRunner(datasets, matchers);
        runner.run(configuration, baseResultsDir, baseDatasetDir, verbose);
    }

}
