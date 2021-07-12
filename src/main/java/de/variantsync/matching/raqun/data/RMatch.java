package de.variantsync.matching.raqun.data;

import java.util.*;

/**
 * Representation of a match (aka. tuple of matched elements).
 */
public class RMatch {
    // The elements that have been matched to each other
    private final Set<RElement> elements;

    public RMatch(RElement... elements) {
        this(Arrays.asList(elements));
    }

    public RMatch(Collection<RElement> elements) {
        this.elements = new HashSet<>(elements);
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
                resultTuple = new RMatch(elements);
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