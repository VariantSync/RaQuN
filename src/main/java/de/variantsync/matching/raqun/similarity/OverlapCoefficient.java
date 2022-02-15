package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OverlapCoefficient implements ISimilarityFunction {
    private double threshold = 0.5;

    @Override
    public boolean shouldMatch(final Set<RMatch> tuples) {
        final List<RElement> allElements = new ArrayList<>();
        tuples.forEach(t -> allElements.addAll(t.getElements()));
        return coefficient(allElements.toArray(new RElement[0])) >= threshold;
    }

    @Override
    public void setNumberOfModels(final int numberOfModels) {

    }

    @Override
    public double getMatchConfidence(final RElement elementA, final RElement elementB) {
        return coefficient(elementA, elementB);
    }

    @Override
    public void setParameters(final List<String> parameters) {
        if (parameters.size() == 1) {
            this.threshold = Double.parseDouble(parameters.get(0));
        } else if (parameters.size() > 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + parameters.size());
        }

    }

    private double coefficient(final RElement... elements) {
        Set<String> intersectionProperties = null;
        int minimumNumberOfProperties = 1;
        for (final RElement element : elements) {
            if (intersectionProperties == null) {
                intersectionProperties = new HashSet<>(element.getProperties());
                minimumNumberOfProperties = intersectionProperties.size();
            } else {
                intersectionProperties = element.getProperties().stream().filter(intersectionProperties::contains).collect(Collectors.toSet());
                final int size = element.getProperties().size();
                if (minimumNumberOfProperties > size) {
                    minimumNumberOfProperties = size;
                }
            }
        }
        final double commonSum = intersectionProperties == null ? 0.0 : intersectionProperties.size();
        return commonSum / (double) minimumNumberOfProperties;
    }
}
