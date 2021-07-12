package org.raqun.paper.raqun.data;

import org.raqun.paper.raqun.similarity.SimilarityFunction;

import java.util.*;

public class RMatch {
    private final Set<RElement> elements;
    private final SimilarityFunction similarityFunction;
    private final MatchValidityConstraint validityConstraint;

    public RMatch(SimilarityFunction similarityFunction, MatchValidityConstraint validityConstraint, RElement... elements) {
        this(similarityFunction, validityConstraint, Arrays.asList(elements));
    }

    public RMatch(SimilarityFunction similarityFunction, MatchValidityConstraint validityConstraint, Collection<RElement> elements) {
        this.similarityFunction = similarityFunction;
        this.validityConstraint = validityConstraint;
        this.elements = new HashSet<>(elements);
    }

    public boolean isValid() {
        switch (this.validityConstraint) {
            case ONE_TO_ONE:
                HashSet<String> modelSet = new HashSet<>();
                for (RElement element : elements) {
                    if (modelSet.contains(element.getModelID())) {
                        return false;
                    } else {
                        modelSet.add(element.getModelID());
                    }
                }
                return true;
            case N_TO_M:
                return true;
            default:
                throw new UnsupportedOperationException("The validity value " + this.validityConstraint + " has not been implemented.");
        }

    }

    public Collection<RElement> getElements() {
        return elements;
    }

    public boolean contains(RElement element) {
        return elements.contains(element);
    }

    public static RMatch getMergedTuple(Set<RMatch> tuples) {
        RMatch resultTuple = null;

        for (RMatch tuple : tuples) {
            if (resultTuple == null) {
                resultTuple = tuple;
            } else {
                Set<RElement> elements = new HashSet<>(resultTuple.elements);
                elements.addAll(tuple.elements);
                resultTuple = new RMatch(tuple.similarityFunction, tuple.validityConstraint, elements);
            }
        }

        return resultTuple;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RElement element : elements) {
            sb.append(element);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getLongString() {
        StringBuilder sb = new StringBuilder();
        for (RElement element : elements) {
            sb.append(element);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("::{{");
        Set<String> properties = new HashSet<>();
        elements.forEach(e -> properties.addAll(e.getProperties()));
        for (String property : properties) {
            sb.append(property);
            sb.append(";");
        }
        sb.append("}}");
        return sb.toString();
    }

}
