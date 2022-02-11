package de.variantsync.matching.experiments.common;

/**
 * Interface definition for a Matcher
 */
public interface MatcherAdapter {
    /**
     * Run experiments with the matcher
     */
    boolean run(ExperimentSetup setup);
}