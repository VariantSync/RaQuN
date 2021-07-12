package de.variantsync.matching.raqun.vectorization;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.tree.RVector;

import java.util.Collection;

/**
 * A factory for points in a k-d-tree that are represented by a vector.
 *
 * You can use the implementation of the simple vectorization used in our paper's motivating example as a simple template
 * for your own vectorization. MotivatingExampleVectorFactory
 */
public interface IVectorization {
    /**
     * Initialize the vectorization for the given input models.
     */
    void initialize(Collection<RModel> inputModels);

    /**
     * Return a vector representation for the given element.
     * @param element The element for which a vector representation is to be calculated
     * @return The vector that represents the given element in a k-d-trees vector space
     */
    RVector vectorFor(RElement element);

    /**
     *
     * @return the number of dimensions that the vector space created by this vectorization has
     */
    int getNumberOfDimension();
}