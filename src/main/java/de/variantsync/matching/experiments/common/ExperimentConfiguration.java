package de.variantsync.matching.experiments.common;

import de.variantsync.matching.experiments.EAlgorithm;
import de.variantsync.matching.experiments.baseline.BaselineAlgoAdapter;
import de.variantsync.matching.experiments.raqun.RaQuNAdapter;
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
import java.util.concurrent.TimeUnit;

/**
 * Utility class for loading values from the properties files that define the experimental setups.
 */
public class ExperimentConfiguration {
    public static final File DEFAULT_PROPERTIES_FILE = new File("src/main/resources/experiment.properties");
    private static final String EXPERIMENTS_TIMEOUT_DURATION = "experiments.timeout.duration";
    private static final String EXPERIMENTS_TIMEOUT_UNIT = "experiments.timeout.unit";
    private static final String EXPERIMENTS_MATCHERS_ALGORITHM = "experiments.matchers.algorithm.";
    private static final String EXPERIMENTS_MATCHERS_NAME = "experiments.matchers.name.";
    private static final String EXPERIMENTS_MATCHERS_VECTORIZATION = "experiments.matchers.vectorization.";
    private static final String EXPERIMENTS_MATCHERS_VALIDITY = "experiments.matchers.validity.";
    private static final String EXPERIMENTS_MATCHERS_SIMILARITY = "experiments.matchers.similarity.";
    private static final String EXPERIMENTS_MATCHERS_SIMILARITY_PARAMETERS = "experiments.matchers.similarity.parameters.";
    private static final String EXPERIMENTS_DATASETS_FOLDER = "experiments.dir.datasets";
    private static final String EXPERIMENTS_RESULTS_FOLDER = "experiments.dir.results";
    private static final String EXPERIMENTS_EXECUTION_VERBOSE = "experiments.execution.verbose";
    private static final String EXPERIMENTS_EXECUTION_REPEATS_RQ1 = "experiments.execution.repetitions.rq1";
    private static final String EXPERIMENTS_EXECUTION_REPEATS_RQ2 = "experiments.execution.repetitions.rq2";
    private static final String EXPERIMENTS_EXECUTION_REPEATS_RQ3 = "experiments.execution.repetitions.rq3";
    private static final String EXPERIMENTS_RQ1_DATASETS = "experiments.rq1.datasets";
    private static final String EXPERIMENTS_RQ1_MATCHERS = "experiments.rq1.matchers";
    private static final String EXPERIMENTS_RQ2_MATCHER = "experiments.rq2.matcher";
    private static final String EXPERIMENTS_RQ2_DATASETS = "experiments.rq2.datasets";
    private static final String EXPERIMENTS_RQ2_START_k = "experiments.rq2.start-k";
    private static final String EXPERIMENTS_RQ2_MAX_K = "experiments.rq2.max-k";
    private static final String EXPERIMENTS_RQ2_MAX_K_ARGOUML = "experiments.rq2.max-k-argouml";
    private static final String EXPERIMENTS_RQ3_MATCHERS = "experiments.rq3.matchers";
    private static final String EXPERIMENTS_RQ3_LARGEST_SUBSET = "experiments.rq3.largest-dataset";
    private static final String EXPERIMENTS_RQ4_DATASETS = "experiments.rq4.datasets";
    private static final String EXPERIMENTS_RQ4_MATCHERS = "experiments.rq4.matchers";
    private static final String EXPERIMENTS_RQ5_MATCHERS = "experiments.rq5.matchers";
    private static final String EXPERIMENTS_RQ5_LARGEST_SUBSET = "experiments.rq5.largest-dataset";
    private final Configuration config;

    public ExperimentConfiguration() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public ExperimentConfiguration(final File propertiesFile) {
        final Parameters params = new Parameters();
        try {
            final var builder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setFile(propertiesFile)
                                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
            this.config = builder.getConfiguration();
        } catch (final ConfigurationException e) {
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

    public List<String> matchersRQ1() {
        return config.getList(String.class, EXPERIMENTS_RQ1_MATCHERS);
    }

    public String matcherDisplayName(final String matcherName) {
        return config.getString(EXPERIMENTS_MATCHERS_NAME + matcherName);
    }

    public MatcherAdapter loadMatcher(final String matcherName) {
        final EAlgorithm matcherAlgorithm = config.get(EAlgorithm.class, EXPERIMENTS_MATCHERS_ALGORITHM + matcherName);
        if (matcherAlgorithm == EAlgorithm.RaQuN) {
            final ClassLoader classLoader = ExperimentConfiguration.class.getClassLoader();
            final ISimilarityFunction similarityFunction;
            final IValidityConstraint validityConstraint;
            final IVectorization vectorization;
            try {
                similarityFunction = (ISimilarityFunction) classLoader.loadClass(config.getString(EXPERIMENTS_MATCHERS_SIMILARITY + matcherName)).getConstructor().newInstance();
                similarityFunction.setParameters(config.getList(String.class, EXPERIMENTS_MATCHERS_SIMILARITY_PARAMETERS + matcherName));
                vectorization = (IVectorization) classLoader.loadClass(config.getString(EXPERIMENTS_MATCHERS_VECTORIZATION + matcherName)).getConstructor().newInstance();
                validityConstraint = (IValidityConstraint) classLoader.loadClass(config.getString(EXPERIMENTS_MATCHERS_VALIDITY + matcherName)).getConstructor().newInstance();
            } catch (final ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException("Was not able to load matcher configuration for " + matcherName);
            }
            return new RaQuNAdapter(similarityFunction, vectorization, validityConstraint);
        } else {
            return new BaselineAlgoAdapter(matcherAlgorithm);
        }
    }

    public long timeoutDuration() {
        return config.getLong(EXPERIMENTS_TIMEOUT_DURATION);}

    public TimeUnit timeoutUnit() {
        final String unit = config.getString(EXPERIMENTS_TIMEOUT_UNIT).trim();
        switch (unit) {
            case "SECONDS":
                return TimeUnit.SECONDS;
            case "MINUTES":
                return TimeUnit.MINUTES;
            case "HOURS":
                return TimeUnit.HOURS;
            case "DAYS":
                return TimeUnit.DAYS;
            default:
                throw new IllegalArgumentException(unit + " is not a valid time unit for the timeout");
        }
    }
    public boolean verboseResults() {
        return config.getBoolean(EXPERIMENTS_EXECUTION_VERBOSE);
    }

    public int repetitionsRQ1() {
        return config.getInt(EXPERIMENTS_EXECUTION_REPEATS_RQ1);
    }

    public int repetitionsRQ2() {
        return config.getInt(EXPERIMENTS_EXECUTION_REPEATS_RQ2);
    }

    public int repetitionsRQ3() {
        return config.getInt(EXPERIMENTS_EXECUTION_REPEATS_RQ3);
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

    public String getExperimentsRq3LargestSubset() {
        return config.getString(EXPERIMENTS_RQ3_LARGEST_SUBSET);
    }

    public MatcherAdapter matcherRQ2() {
        return loadMatcher(config.getString(EXPERIMENTS_RQ2_MATCHER));
    }

    public List<String> matchersRQ3() {
        return config.getList(String.class, EXPERIMENTS_RQ3_MATCHERS);
    }

    public List<String> datasetsRQ4() {
        return config.getList(String.class, EXPERIMENTS_RQ4_DATASETS);
    }

    public List<String> matchersRQ4() {
        return config.getList(String.class, EXPERIMENTS_RQ4_MATCHERS);
    }

    public List<String> matchersRQ5() {
        return config.getList(String.class, EXPERIMENTS_RQ5_MATCHERS);
    }

    public String getExperimentsRq5LargestSubset() {
        return config.getString(EXPERIMENTS_RQ5_LARGEST_SUBSET);
    }
}
