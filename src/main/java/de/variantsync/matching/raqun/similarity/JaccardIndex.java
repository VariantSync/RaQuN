package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the Jaccard similarity index, aka. ratio of intersection to union
 */
public class JaccardIndex implements ISimilarityFunction {
    // default threshold - should be set manually
    private double threshold = 0.5;

    @Override
    public boolean shouldMatch(final Set<RMatch> tuples) {
        final List<RElement> allElements = new ArrayList<>();
        tuples.forEach(t -> allElements.addAll(t.getElements()));
        return jaccard(allElements.toArray(new RElement[0])) >= threshold;
    }

    @Override
    public void setNumberOfModels(final int numberOfModels) {

    }

    @Override
    public double getMatchConfidence(final RElement elementA, final RElement elementB) {
        return jaccard(elementA, elementB);
    }

    @Override
    public void setParameters(final List<String> parameters) {
        if (parameters.size() == 1) {
            this.threshold = Double.parseDouble(parameters.get(0));
        } else if (parameters.size() > 1) {
            throw new IllegalArgumentException("Unexpected number of parameters: " + parameters.size());
        }

    }

    /**
     * The Jaccard index is defined as (|A \cap B|) / (| |A \cup B|).
     * It can thus also be calculated as (|A \cap B|) / (|A| + |B| - |A \cap B|
     * @param elements The elements for which the Jaccard index is calculated. An Element is a set of properties.
     * @return The Jaccard similarity index for the given elements
     */
    private double jaccard(final RElement... elements) {
        Set<String> intersectionProperties = null;
        final Set<String> unionProperties = new HashSet<>();
        for (final RElement element : elements) {
            if (intersectionProperties == null) {
                intersectionProperties = new HashSet<>(element.getProperties());
            } else {
                intersectionProperties.retainAll(element.getProperties());
            }
            unionProperties.addAll(element.getProperties());
        }
        final double intersectionProps = intersectionProperties == null ? 0.0 : intersectionProperties.size();
        return intersectionProps / unionProperties.size();
    }
}
