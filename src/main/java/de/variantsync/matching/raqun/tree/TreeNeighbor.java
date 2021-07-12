package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;

/**
 * Representation of a neighboring element in a k-d-tree. A TreeNeighbor comprises the neighboring element and the distance
 * to it.
 */
public record TreeNeighbor(RElement element, double distance) {

	public double getDistance() {
		return distance;
	}

	public RElement getElement() {
		return element;
	}
}