package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CosineSimilarity implements ISimilarityFunction {
    @Override
    public boolean shouldMatch(Set<RMatch> tuples) {
        List<RElement> allElements = new ArrayList<>();
        tuples.forEach(t -> allElements.addAll(t.getElements()));
        return confidence(allElements.toArray(new RElement[0])) > 0.5;
    }

    @Override
    public void setNumberOfModels(int numberOfModels) {

    }

    @Override
    public double getMatchConfidence(RElement elementA, RElement elementB) {
        return confidence(elementA, elementB);
    }

    private double confidence(RElement... elements) {
        Set<String> intersectionProperties = null;
        Set<String> unionProperties = new HashSet<>();
        for (RElement element : elements) {
            unionProperties.addAll(element.getProperties());
            if (intersectionProperties == null) {
                intersectionProperties = new HashSet<>(element.getProperties());
            } else {
                element.getProperties().forEach(intersectionProperties::remove);
            }
        }
        double commonSum = intersectionProperties == null ? 0.0 : intersectionProperties.size();
        return commonSum / (double) unionProperties.size();
    }
}
