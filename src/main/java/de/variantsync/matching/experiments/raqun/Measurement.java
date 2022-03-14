package de.variantsync.matching.experiments.raqun;

/**
 * A Measurement holds metadata about an experiment run with RaQuN
 */
public class Measurement {
    int runId;
    String testCase;
    int chunksCount;
    int chunkNumber;
    int k;
    double resultIndexTimeElapsedSec;
    double resultSearchTimeElapsedSec;
    double resultMatchTimeElapsedSec;

    public Measurement(final int runId, final String testCase, final int k, final int chunksCount, final int chunkNumber) {
        this.runId = runId;
        this.testCase = testCase;
        this.chunksCount = chunksCount;
        this.chunkNumber = chunkNumber;
        this.k = k;
    }

}