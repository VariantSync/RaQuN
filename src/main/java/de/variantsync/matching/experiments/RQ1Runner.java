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
        System.out.println("Running RQ1");
        final List<String> datasets = configuration.datasetsRQ1();
        final List<String> matchers = configuration.matchersRQ1();
        final BasicExperimentsRunner runner = new BasicExperimentsRunner(datasets, matchers);
        runner.run(configuration, baseResultsDir, baseDatasetDir, verbose);
    }

}
