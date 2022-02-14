package de.variantsync.matching.experiments.common;

public interface IKillableLongTask {

    /**
     * Stop any ongoing execution.
     */
    void kill();

    boolean killed();

    default boolean logKilled() {
        if (killed()) {
            System.err.println("Successfully stopped " + this);
            return true;
        }
        return false;
    }
}
