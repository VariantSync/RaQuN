package de.variantsync.matching.raqun.data;

import java.util.*;

/**
 * Representation of a match (aka. tuple of matched elements).
 */
public class RMatch {
    // The elements that have been matched to each other
    private final Set<RElement> elements;

    /**
     * Initialize a match of the given elements
     * @param elements that are matched
     */
    public RMatch(RElement... elements) {
        this(Arrays.asList(elements));
    }

    /**
     * Initialize a match of the given elements
     * @param elements that are matched
     */
    public RMatch(Collection<RElement> elements) {
        this.elements = new HashSet<>(elements);
    }

    /**
     *
     * @return elements that are part of this match
     */
    public Collection<RElement> getElements() {
        return elements;
    }

    /**
     *
     * @param element which is checked
     * @return true if the element is part of this match, false otherwise
     */
    public boolean contains(RElement element) {
        return elements.contains(element);
    }

    /**
     * Create a new match by merging the given matches
     * @param matches the matches that are to be merged
     * @return a match containing all elements in the given matches
     */
    public static RMatch getMergedMatch(Set<RMatch> matches) {
        RMatch resultTuple = null;

        for (RMatch tuple : matches) {
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

    /**
     * @return String for pretty printing
     */
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