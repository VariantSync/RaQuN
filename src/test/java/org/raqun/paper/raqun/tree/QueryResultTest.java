package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;
import org.junit.jupiter.api.Test;

import static org.raqun.paper.testhelper.TestDataFactory.getSimpleRElement;

public class QueryResultTest {
    @Test
    public void initializationCorrect() {
        RElement element = getSimpleRElement();
        QueryResult queryResult = new QueryResult(element, 0.0d);

        assert queryResult.getElement() == element;
        assert Double.compare(queryResult.getDistance(), 0.0d) == 0;

        queryResult = new QueryResult(null, 1.0d);
        assert queryResult.getElement() == null;
    }
}
