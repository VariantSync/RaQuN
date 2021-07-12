package org.raqun.paper.nwm.alg.local;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Tuple;

public class SwapDelta{
	private ArrayList<Tuple> neighbours;
	private ArrayList<Tuple> proposedGroup;
	private BigDecimal value;
	
	public static SwapDelta NOT_USEFUL = new SwapDelta(null, null, new BigDecimal(-1, N_WAY.MATH_CTX));
	
	public ArrayList<Tuple> getNeighbours() {
		return neighbours;
	}

	public ArrayList<Tuple> getProposedGroup() {
		return proposedGroup;
	}

	public BigDecimal getValue() {
		return value;
	}


	
	public SwapDelta(ArrayList<Tuple> n, ArrayList<Tuple> g, BigDecimal v){
		neighbours = n;
		proposedGroup = g;
		value = v;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+value;
	}
}