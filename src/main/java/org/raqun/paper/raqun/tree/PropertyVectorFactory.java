package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PropertyVectorFactory implements IVectorFactory {
    private Map<String, Integer> childrenNamesDimension;
    protected Map<RElement, PropertyVector> elementToVectorMap;
    protected final int dimensions;

    public PropertyVectorFactory(List<RElement> elements) {
        if (elements.size() == 0) {
            throw new IllegalArgumentException("There should be at least one element!");
        }
        this.dimensions = fillLexicalIndex(elements);
        this.elementToVectorMap = new HashMap<>();
    }

    protected int fillLexicalIndex(List<RElement> elements) {
        Set<String> childrenNames = elements.stream().flatMap(element -> element.getProperties().stream()).collect(Collectors.toSet());
        AtomicInteger i = new AtomicInteger(0);
        childrenNamesDimension = childrenNames.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
        return i.get();

    }

    @Override
    public PropertyVector vectorFor(RElement element) {
        if (element.getProperties().isEmpty()) {
            throw new IllegalArgumentException("Elements must have at least one property!");
        }
        if (elementToVectorMap.containsKey(element)) {
            return elementToVectorMap.get(element);
        } else {
            PropertyVector vector = new PropertyVector(dimensions);

            element.getProperties().forEach(n -> vector.setCoord(childrenNamesDimension.get(n), 1));

            elementToVectorMap.put(element, vector);
            return vector;
        }
    }

    public Map<String, Integer> getChildrenNamesDimension () {
        return this.childrenNamesDimension;
    }

    @Override
    public int getNumberOfDimension() {
        return dimensions;
    }

}