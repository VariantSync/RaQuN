package de.variantsync.matching.raqun.vectorization;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.tree.RVector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A factory for calculating vector representations of elements by mapping each unique property to one specific dimension
 * in the tree's vector space. Hereby, each element is represented by a (probably) sparse vector. A specific property's
 * dimension is set to '1' iff the element has that property, otherwise, the dimension is set to '0'.
 *
 * We presented this vectorization in our paper.
 */
public class PropertyBasedVectorization implements IVectorization {
    private Map<String, Integer> propertyNamesDimension;
    protected Map<RElement, RVector> elementToVectorMap;
    protected int dimensions;

    protected int fillLexicalIndex(Set<RElement> elements) {
        Set<String> childrenNames = elements.stream().flatMap(element -> element.getProperties().stream()).collect(Collectors.toSet());
        AtomicInteger i = new AtomicInteger(0);
        propertyNamesDimension = childrenNames.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
        return i.get();

    }

    @Override
    public void initialize(Collection<RModel> inputModels) {
        Set<RElement> elements = new HashSet<>();
        inputModels.forEach((m) -> elements.addAll(m.getElements()));
        if (elements.size() == 0) {
            throw new IllegalArgumentException("There should be at least one element!");
        }
        this.dimensions = fillLexicalIndex(elements);
        this.elementToVectorMap = new HashMap<>();
    }

    @Override
    public RVector vectorFor(RElement element) {
        if (element.getProperties().isEmpty()) {
            throw new IllegalArgumentException("Elements must have at least one property!");
        }
        if (elementToVectorMap.containsKey(element)) {
            return elementToVectorMap.get(element);
        } else {
            RVector vector = new RVector(dimensions);

            element.getProperties().forEach(n -> vector.setCoord(propertyNamesDimension.get(n), 1));

            elementToVectorMap.put(element, vector);
            return vector;
        }
    }

    /**
     * @return A map of property names to dimension indices
     */
    public Map<String, Integer> getPropertyNamesDimension() {
        return this.propertyNamesDimension;
    }

    @Override
    public int getNumberOfDimension() {
        return dimensions;
    }

}