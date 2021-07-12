package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;

public class QueryResult
{
	private final RElement element;
	private final double _distance;

	public QueryResult(RElement node, double distance)
	{
		element = node;
		_distance = distance;
	}

	public double getDistance()
	{
		return _distance;
	}

	public RElement getElement()
	{
		return element;
	}
}
