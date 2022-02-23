package de.variantsync.matching.experiments;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RQAllRunner extends AbstractRQRunner{
    private static final int NUM_THREADS = 5;

    public RQAllRunner() {
        super();
    }

    public static void main(final String... args) {
        new RQAllRunner().run();
    }

    private static void waitAndClear(final List<Future<?>> futures) {
        futures.forEach(f -> {
            try {
                f.wait();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        futures.clear();
    }

    @Override
    public void run() {
        final List<Future<?>> futures = new LinkedList<>();
        final String configPath = "src/main/resources/experiment.properties";
        final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        // Run RQ1
        for (int i = 0; i < configuration.repetitionsRQ1(); i++) {
            futures.add(executorService.submit(() -> new RQ1Runner(configPath).run()));
        }
        waitAndClear(futures);

        // Run RQ2
        for (int i = 0; i < configuration.repetitionsRQ2(); i++) {
            futures.add(executorService.submit(() -> new RQ2Runner(configPath).run()));
        }
        waitAndClear(futures);

        // Run RQ3
        // For each ArgoUML subset slice
        for (int i = 1; i <= 30; i++) {
            for (int j = 0; j < configuration.repetitionsRQ3(); j++) {
                final String subsetId = String.valueOf(i);
                futures.add(executorService.submit(() -> new RQ3Runner(configPath, subsetId).run()));
            }
        }
        waitAndClear(futures);

        // Run RQ4
        for (int i = 0; i < configuration.repetitionsRQ3(); i++) {
            futures.add(executorService.submit(() -> new RQ4Runner(configPath).run()));
        }
        waitAndClear(futures);

        // Run RQ5
        // For each ArgoUML subset slice
        for (int i = 1; i <= 30; i++) {
            for (int j = 0; j < configuration.repetitionsRQ5(); j++) {
                final String subsetId = String.valueOf(i);
                futures.add(executorService.submit(() -> new RQ5Runner(configPath, subsetId).run()));
            }
        }
        waitAndClear(futures);
    }
}
