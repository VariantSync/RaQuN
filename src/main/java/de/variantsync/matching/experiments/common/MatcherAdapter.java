package de.variantsync.matching.experiments.common;

/**
 * Interface definition for a Matcher
 */
public interface MatcherAdapter {
    /**
     * Run experiments with the matcher
     */
    <T extends ExperimentSetup> void run(T setup);
}