package org.raqun.paper.experiments.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExperimentHelper {

    public static <T> List<ArrayList<T>> getDatasetChunks(ArrayList<T> inputModels, int chunkSize) {
        // Shuffle the models for randomness
        Collections.shuffle(inputModels);
        List<ArrayList<T>> chunks = new ArrayList<>();
        ArrayList<T> subList = null;
        for (int i = 0; i < inputModels.size(); i++) {
            if (i % chunkSize == 0) {
                subList = new ArrayList<>();
                chunks.add(subList);
            }
            subList.add(inputModels.get(i));
        }
        return chunks;
    }
}
