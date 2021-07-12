package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;

/**
 * A simple vectorization that was used in the motivating example of our paper. This vectorization can be used as a
 * template for creating custom vectorization functions.
 */
public class MotivatingExampleVectorFactory implements IVectorFactory<RVector> {
    private final int NUMBER_OF_DIMENSIONS = 2;

    @Override
    public RVector vectorFor(RElement element) {
        RVector vector = new RVector(NUMBER_OF_DIMENSIONS);
        // The first dimension is the number of properties
        vector.setCoord(0, element.getProperties().size());

        double averageLength = 0.0;
        for (String property:element.getProperties()) {
            averageLength += property.length();
        }
        averageLength /= element.getProperties().size();
        // The second dimension is the average property length
        vector.setCoord(1, averageLength);
        return vector;
    }

    @Override
    public int getNumberOfDimension() {
        return NUMBER_OF_DIMENSIONS;
    }
}