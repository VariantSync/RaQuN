package de.variantsync.matching.experiments;

import java.util.List;

public class RQ5Runner extends ArgoUMLExperimentsRunner {
    public RQ5Runner(final String... args) {
        super(args);
    }

    @Override
    protected List<String> matcherList() {
        return configuration.matchersRQ5();
    }

    @Override
    protected String largestSubset() {
        return configuration.getExperimentsRq5LargestSubset();
    }

    public static void main(final String... args) {
        System.out.println("Running RQ5");
        new RQ5Runner(args).run();
    }

}
