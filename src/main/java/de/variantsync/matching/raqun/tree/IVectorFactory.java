package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;

/**
 * A factory for points in a k-d-tree that are represented by a vector.
 *
 * You can use the implementation of the simple vectorization used in our paper's motivating example as a simple template
 * for your own vectorization. MotivatingExampleVectorFactory
 */
public interface IVectorFactory<T extends PropertyVector> {
    /**
     * Return a vector representation for the given element.
     * @param element The element for which a vector representation is to be calculated
     * @return The vector that represents the given element in a k-d-trees vector space
     */
    T vectorFor(RElement element);
    int getNumberOfDimension();
}