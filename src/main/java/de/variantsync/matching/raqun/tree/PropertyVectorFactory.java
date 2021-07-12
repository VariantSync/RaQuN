package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A factory for calculating vector representations of elements by mapping each unique property to one specific dimension
 * in the tree's vector space. Hereby, each element is represented by a (probably) sparse vector. A specific property's
 * dimension is set to '1' iff the element has that property, otherwise, the dimension is set to '0'.
 */
public class PropertyVectorFactory implements IVectorFactory<RVector> {
    private Map<String, Integer> childrenNamesDimension;
    protected Map<RElement, RVector> elementToVectorMap;
    protected final int dimensions;

    /**
     * Initialize the factory by collecting all properties of the given elements and creating one dimension for each unique property.
     * @param elements The elements for which the factory and therefore the vector space of the tree is to be initialized.
     */
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
    public RVector vectorFor(RElement element) {
        if (element.getProperties().isEmpty()) {
            throw new IllegalArgumentException("Elements must have at least one property!");
        }
        if (elementToVectorMap.containsKey(element)) {
            return elementToVectorMap.get(element);
        } else {
            RVector vector = new RVector(dimensions);

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