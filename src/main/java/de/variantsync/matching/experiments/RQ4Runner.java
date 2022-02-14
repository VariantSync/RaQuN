package de.variantsync.matching.experiments;

import de.variantsync.matching.BasicExperimentsRunner;

import java.util.List;

public class RQ4Runner extends AbstractRQRunner{

    public RQ4Runner(final String... args) {
        super(args);
    }

    public static void main(final String... args) {
        new RQ4Runner(args).run();
    }

    @Override
    public void run() {
        List<String> datasets = configuration.datasetsRQ4();
        List<String> matchers = configuration.matchersRQ4();
        BasicExperimentsRunner runner = new BasicExperimentsRunner(datasets, matchers);
        runner.run(configuration, baseResultsDir, baseDatasetDir, verbose);
    }
}
