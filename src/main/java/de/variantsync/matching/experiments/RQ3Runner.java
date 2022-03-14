package de.variantsync.matching.experiments;

import java.util.*;

/**
 * RQ3Runner executes the experiments related to RQ1 in our paper.
 */
public class RQ3Runner extends ArgoUMLExperimentsRunner {

    public RQ3Runner(final String... args) {
        super(args);
    }

    @Override
    protected List<String> matcherList() {
        return configuration.matchersRQ3();
    }

    @Override
    protected String largestSubset() {
        return configuration.getExperimentsRq3LargestSubset();
    }

    @Override
    protected int repetitions() {
        return configuration.repetitionsRQ3();
    }

    public static void main(final String... args) {
        System.out.println("Running RQ3");
        new RQ3Runner(args).run();
    }

}