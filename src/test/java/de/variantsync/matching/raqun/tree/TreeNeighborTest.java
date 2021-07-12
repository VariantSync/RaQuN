package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.testhelper.TestDataFactory;
import de.variantsync.matching.raqun.data.RElement;
import org.junit.jupiter.api.Test;

public class TreeNeighborTest {
    @Test
    public void initializationCorrect() {
        RElement element = TestDataFactory.getSimpleRElement();
        TreeNeighbor treeNeighbor = new TreeNeighbor(element, 0.0d);

        assert treeNeighbor.getElement() == element;
        assert Double.compare(treeNeighbor.getDistance(), 0.0d) == 0;

        treeNeighbor = new TreeNeighbor(null, 1.0d);
        assert treeNeighbor.getElement() == null;
    }
}