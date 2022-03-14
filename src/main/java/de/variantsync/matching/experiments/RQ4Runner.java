package de.variantsync.matching.experiments;

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
        System.out.println("Running RQ4");
        final List<String> datasets = configuration.datasetsRQ4();
        final List<String> matchers = configuration.matchersRQ4();
        final int repetitions = configuration.repetitionsRQ4();
        final BasicExperimentsRunner runner = new BasicExperimentsRunner(datasets, matchers, repetitions);
        runner.run(configuration, baseResultsDir, baseDatasetDir, verbose);
    }
}
