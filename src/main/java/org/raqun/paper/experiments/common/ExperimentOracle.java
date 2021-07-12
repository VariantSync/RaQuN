package org.raqun.paper.experiments.common;

import org.raqun.paper.raqun.data.RElement;
import org.raqun.paper.raqun.data.RMatch;

import java.util.*;

public class ExperimentOracle {

    private double tp;
    private double fp;
    private double fn;
    private double precision;
    private double recall;
    private double fMeasure;

    public ExperimentOracle(Set<RMatch> mergedModel) {
        calculateVersionTwo(mergedModel);
    }


    private void calculateVersionTwo(Set<RMatch> mergedModel) {
        tp = 0.0;
        fp = 0.0;
        fn = 0.0;
        Map<String, Integer> numberOfClassOccurrencesTotal = countClassOccurrences(mergedModel);

        for (RMatch tuple : mergedModel) {
            Collection<RElement> nodesInTuple = tuple.getElements();
            // Count the number of times each class appears in the tuple
            Map<String, Integer> numberOfClassOccurrences = new HashMap<>();
            for (RElement node : nodesInTuple) {
                String id = node.getUUID();
                if (numberOfClassOccurrences.containsKey(id)) {
                    int oldNumber = numberOfClassOccurrences.get(id);
                    numberOfClassOccurrences.put(id, oldNumber+1);
                } else {
                    numberOfClassOccurrences.put(id, 1);
                }
            }

            // Now we count the number of TP, FP, FN for the current tuple
            for (String id : numberOfClassOccurrences.keySet()) {
                int numberCurrent = numberOfClassOccurrences.get(id);
                int numberOther = nodesInTuple.size() - numberCurrent;

                // Count the tp, one for each correct match between members of a class
                for (int i = numberCurrent-1; i > 0; i--) {
                    tp += i;
                }

                // Count the fp, one for each incorrect match between members of different classes
                fp += numberCurrent * numberOther;

                // Count the fn, one for each missing match with a member of the current class
                int numberMissing = numberOfClassOccurrencesTotal.get(id) - numberCurrent;
                fn += numberCurrent * numberMissing;
                // We have to set the new value so that every missing match is only counted once
                numberOfClassOccurrencesTotal.put(id, numberMissing);
            }
        }
        // We have to halve the number of fp, because we counted them twice
        fp /= 2;
        calculateStats();
    }

    private void calculateStats() {
        if (tp == 0) {
            precision = 0.0d;
            recall = 0.0d;
            fMeasure = 0.0d;
        } else {
            precision = tp / (tp + fp);
            recall = tp / (tp + fn);
            fMeasure = (2 * (precision * recall)) / (precision + recall);
        }
    }

    public double getTp() {
        return tp;
    }

    public double getFp() {
        return fp;
    }

    public double getFn() {
        return fn;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getFMeasure() {
        return fMeasure;
    }

    private Map<String, Integer> countClassOccurrences(Set<RMatch> mergedModel) {
        Map<String, Integer> numberOfClassOccurrences = new HashMap<>();

        for (RMatch tuple : mergedModel) {
            countClassOccurrencesInTuple(tuple, numberOfClassOccurrences);
        }
        return numberOfClassOccurrences;
    }

    private void countClassOccurrencesInTuple(RMatch tuple, Map<String, Integer> classOccurrences) {
        // Counts the number of times each class appears in the given tuple and updates the given map that holds previous
        // counts of class occurrences
        for (RElement node : tuple.getElements()) {
            String id = node.getUUID();
            if (classOccurrences.containsKey(id)) {
                Integer count = classOccurrences.get(id) ;
                classOccurrences.put(id, count + 1);
            } else {
                classOccurrences.put(id, 1);
            }
        }
    }


}
