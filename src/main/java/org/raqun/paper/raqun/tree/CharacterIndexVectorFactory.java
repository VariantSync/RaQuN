package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CharacterIndexVectorFactory extends IndexVectorFactory {
    private Map<Character, Integer> characterDimensions;

    public CharacterIndexVectorFactory(List<RElement> elements) {
        super(elements);
    }

    @Override
    protected int fillLexicalIndex(List<RElement> elements) {
        Set<String> propertyNames = elements.stream().flatMap(element -> element.getProperties().stream()).collect(Collectors.toSet());

        AtomicInteger i = new AtomicInteger(2);

        Set<Character> characters = new HashSet<>();

        for (String propertyName : propertyNames) {
            for (char c : propertyName.toCharArray()) {
                characters.add(Character.toLowerCase(c));
            }
        }

        characterDimensions = characters.stream().collect(Collectors.toMap(c -> c, c -> i.getAndIncrement()));
        return i.get();
    }

    @Override
    public IndexVector vectorFor(RElement element) {
        if (element.getProperties().isEmpty()) {
            throw new IllegalArgumentException("Elements must have at least one property!");
        }
        if (elementToVectorMap.containsKey(element)) {
            return elementToVectorMap.get(element);
        } else {
            IndexVector vector = new IndexVector(dimensions);

            int dim = 0;

            // First Dimension: Number of properties
            vector.setCoord(dim++, element.getProperties().size());

            // Second Dimension: Average length of a property name
            double avgLength = 0;
            for (String name : element.getProperties()) {
                avgLength += name.length();
            }
            avgLength /= element.getProperties().size();
            vector.setCoord(dim, avgLength);

            // Other Dimensions: Occurrences of characters in property names
            for (String name : element.getProperties()) {
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

    public Map<Character, Integer> getCharacterDimensions() {
        return this.characterDimensions;
    }
}
