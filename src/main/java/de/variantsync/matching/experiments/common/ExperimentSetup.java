package de.variantsync.matching.experiments.common;

import java.nio.file.Paths;

/**
 * An ExperimentSetup holds the parameters that configure a specific experiment.
 */
public class ExperimentSetup {
    public final String name;
    public final int numberOfRepeats;
    public final String datasetName;
    public final String resultFile;
    public final String mergeResultFile;
    public final String datasetFile;
    public final int chunkSize;
    public final boolean printVerbose;
    public final int startK;
    public final int maxK;

    public ExperimentSetup(final String name, final int numberOfRepeats,
                           final String resultDir, final String datasetDir,
                           final String datasetName, final int chunkSize,
                           final boolean printVerbose,
                           final int startK,
                           final int maxK) {
        this.name = name;
        this.numberOfRepeats = numberOfRepeats;
        this.datasetName = datasetName;
        // Adjust name for ArgoUML subsets
        if (datasetName.startsWith("argouml_p")) {
            final String[] parts = datasetName.split("_");
            final String tempName = parts[0] + "_" + parts[1];
            this.resultFile = Paths.get(resultDir, name, name + "_" + tempName + "_stats.json").toString();
        } else {
            this.resultFile = Paths.get(resultDir, name, name + "_" + datasetName + "_stats.json").toString();
        }
        this.mergeResultFile = Paths.get(resultDir, name, name + "_" + datasetName + "_model.csv").toString();
        this.datasetFile = Paths.get(datasetDir, datasetName + ".csv").toString();
        this.chunkSize = chunkSize;
        this.printVerbose = printVerbose;
        this.startK = startK;
        this.maxK = maxK;
    }

    public ExperimentSetup(final String name, final int numberOfRepeats,
                           final String resultDir, final String datasetDir,
                           final String datasetName, final int chunkSize,
                           final boolean printVerbose) {
        this(name, numberOfRepeats, resultDir, datasetDir, datasetName, chunkSize, printVerbose, 0, 0);
    }

}