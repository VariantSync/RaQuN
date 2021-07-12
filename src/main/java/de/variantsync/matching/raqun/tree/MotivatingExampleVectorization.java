package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RModel;

import java.util.Collection;

/**
 * A simple vectorization that was used in the motivating example of our paper. This vectorization can be used as a
 * template for creating custom vectorization functions.
 */
public class MotivatingExampleVectorization implements Vectorization {
    private final int NUMBER_OF_DIMENSIONS = 2;

    @Override
    public void initialize(Collection<RModel> inputModels) {
        // There is no additional initialization required for this vectorization
    }

    @Override
    public RVector vectorFor(RElement element) {
        RVector vector = new RVector(NUMBER_OF_DIMENSIONS);

        // The first dimension is the average property length
        double averageLength = 0.0;
        for (String property:element.getProperties()) {
            averageLength += property.length();
        }
        averageLength /= element.getProperties().size();
        vector.setCoord(0, averageLength);

        // The second dimension is the number of properties
        vector.setCoord(1, element.getProperties().size());
        return vector;
    }

    @Override
    public int getNumberOfDimension() {
        return NUMBER_OF_DIMENSIONS;
    }
}