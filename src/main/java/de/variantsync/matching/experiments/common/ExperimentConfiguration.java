package de.variantsync.matching.experiments.common;

import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ExperimentConfiguration {
    private final Configuration config;
    public static final File DEFAULT_PROPERTIES_FILE = new File("src/main/resources/experiment.properties");
    private static final String EXPERIMENTS_DATASETS_FOLDER = "experiments.dir.datasets";
    private static final String EXPERIMENTS_RESULTS_FOLDER = "experiments.dir.results";
    private static final String EXPERIMENTS_MATCHERS_NWM = "experiments.matchers.nwm";
    private static final String EXPERIMENTS_MATCHERS_PAIRWISE_ASCENDING = "experiments.matchers.pairwise-ascending";
    private static final String EXPERIMENTS_MATCHERS_PAIRWISE_DESCENDING = "experiments.matchers.pairwise-descending";
    private static final String EXPERIMENTS_MATCHERS_RAQUN = "experiments.matchers.raqun";
    private static final String EXPERIMENTS_EXECUTION_VERBOSE = "experiments.execution.verbose";
    private static final String EXPERIMENTS_EXECUTION_REPEATS = "experiments.execution.repetitions";
    private static final String EXPERIMENTS_RQ2_START_k = "experiments.rq2.start-k";
    private static final String EXPERIMENTS_RQ2_MAX_K = "experiments.rq2.max-k";
    private static final String EXPERIMENTS_RQ2_MAX_K_ARGOUML = "experiments.rq2.max-k-argouml";
    private static final String EXPERIMENTS_RQ1_DATASETS = "experiments.rq1.datasets";
    private static final String EXPERIMENTS_RQ2_DATASETS = "experiments.rq2.datasets";
    private static final String RAQUN_VECTORIZATION = "raqun.vectorization";
    private static final String RAQUN_VALIDITY = "raqun.validity";
    private static final String RAQUN_SIMILARITY = "raqun.similarity";

    public ExperimentConfiguration() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public ExperimentConfiguration(File propertiesFile) {
        Parameters params = new Parameters();
        try {
            var builder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setFile(propertiesFile)
                                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
            this.config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            System.err.println("Was not able to load properties file " + propertiesFile);
            throw new RuntimeException(e);
        }
    }

    public String datasetsFolder() {
        return config.getString(EXPERIMENTS_DATASETS_FOLDER);
    }

    public String resultsFolder() {
        return config.getString(EXPERIMENTS_RESULTS_FOLDER);
    }

    public boolean shouldRunNwM() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_NWM);
    }

    public boolean shouldRunPairwiseDesc() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_PAIRWISE_DESCENDING);
    }

    public boolean shouldRunPairwiseAsc() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_PAIRWISE_ASCENDING);
    }

    public boolean shouldRunRaQuN() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_RAQUN);
    }

    public boolean verboseResults() {
        return config.getBoolean(EXPERIMENTS_EXECUTION_VERBOSE);
    }

    public int numberOfRepeats() {
        return config.getInt(EXPERIMENTS_EXECUTION_REPEATS);
    }

    public int startK() {
        return config.getInt(EXPERIMENTS_RQ2_START_k);
    }

    public int maxK() {
        return config.getInt(EXPERIMENTS_RQ2_MAX_K);
    }

    public int maxKArgoUML() {
        return config.getInt(EXPERIMENTS_RQ2_MAX_K_ARGOUML);
    }

    public List<String> datasetsRQ1() {
        return config.getList(String.class, EXPERIMENTS_RQ1_DATASETS);
    }

    public List<String> datasetsRQ2() {
        return config.getList(String.class, EXPERIMENTS_RQ2_DATASETS);
    }

    public ISimilarityFunction similarityFunction() {
        ClassLoader classLoader = ExperimentConfiguration.class.getClassLoader();
        try {
            return (ISimilarityFunction) classLoader.loadClass(config.getString(RAQUN_SIMILARITY)).getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Was not able to load similarity function " + config.getString(RAQUN_SIMILARITY));
        }
    }

    public IValidityConstraint validityConstraint() {
        ClassLoader classLoader = ExperimentConfiguration.class.getClassLoader();
        try {
            return (IValidityConstraint) classLoader.loadClass(config.getString(RAQUN_VALIDITY)).getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Was not able to load validity constraint " + config.getString(RAQUN_SIMILARITY));
        }
    }

    public IVectorization vectorization() {
        ClassLoader classLoader = ExperimentConfiguration.class.getClassLoader();
        try {
            return (IVectorization) classLoader.loadClass(config.getString(RAQUN_VECTORIZATION)).getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Was not able to load vectorization function " + config.getString(RAQUN_SIMILARITY));
        }
    }
}
