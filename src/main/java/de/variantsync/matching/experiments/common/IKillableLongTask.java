package de.variantsync.matching.experiments.common;

public interface IKillableLongTask {

    /**
     * Stop any ongoing execution.
     */
    void stop();

    boolean stopped();
}
