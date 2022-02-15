package de.variantsync.matching.raqun.vectorization;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.tree.RVector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A factory for calculating vectors that represent the absolute frequency of specific characters in an element's
 * properties. This vectorization functions was not presented in our paper, but will be included in a future paper extension.
 */
public class CharacterBasedVectorization extends PropertyBasedVectorization {
    private Map<Character, Integer> characterDimensions;

    @Override
    protected int fillLexicalIndex(final Set<RElement> elements) {
        final Set<String> propertyNames = elements.stream().flatMap(element -> element.getProperties().stream()).collect(Collectors.toSet());

        final AtomicInteger i = new AtomicInteger(2);

        final Set<Character> characters = new HashSet<>();

        for (final String propertyName : propertyNames) {
            for (final char c : propertyName.toCharArray()) {
                characters.add(Character.toLowerCase(c));
            }
        }

        characterDimensions = characters.stream().collect(Collectors.toMap(c -> c, c -> i.getAndIncrement()));
        return i.get();
    }

    @Override
    public RVector vectorFor(final RElement element) {
        if (element.getProperties().isEmpty()) {
            throw new IllegalArgumentException("Elements must have at least one property!");
        }
        if (elementToVectorMap.containsKey(element)) {
            return elementToVectorMap.get(element);
        } else {
            final RVector vector = new RVector(dimensions);

            int dim = 0;

            // First Dimension: Number of properties
            vector.setCoord(dim++, element.getProperties().size());

            // Second Dimension: Average length of a property name
            double avgLength = 0;
            for (final String name : element.getProperties()) {
                avgLength += name.length();
            }
            avgLength /= element.getProperties().size();
            vector.setCoord(dim, avgLength);

            // Other Dimensions: Occurrences of characters in property names
            for (final String name : element.getProperties()) {
                for (char c : name.toCharArray()) {
                    c = Character.toLowerCase(c);
                    dim = characterDimensions.get(c);
                    vector.setCoord(dim, vector.getCoord(dim) + 1);
                }
            }

            elementToVectorMap.put(element, vector);

            return vector;
        }
    }

    /**
     *
     * @return A map of characters to dimension indices
     */
    public Map<Character, Integer> getCharacterDimensions() {
        return this.characterDimensions;
    }
}