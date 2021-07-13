package de.variantsync.matching;

import de.variantsync.matching.experiments.common.ExperimentConfiguration;

import java.io.File;

public abstract class RQRunner {
    protected ExperimentConfiguration configuration;

    protected RQRunner(String... args) {
        if (args.length == 0) {
            configuration = new ExperimentConfiguration();
        } else if (args.length == 1) {
            configuration = new ExperimentConfiguration(new File(args[0]));
        } else {
            throw new IllegalArgumentException("Illegal number of arguments: " + args.length);
        }
    }

    public abstract void run();
}
