package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;

public interface IVectorFactory {
    PropertyVector vectorFor(RElement element);
    int getNumberOfDimension();
}