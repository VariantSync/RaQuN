package org.raqun.paper.nwm.common;

import java.math.MathContext;
import java.math.RoundingMode;

public interface N_WAY {

	public static final int PAIR_WISE = 1;
	public static final int GREEDY = 2;
	public static final int BEST_LOCAL_SEARCH = 4;
	public static final int FIRST_LOCAL_SEARCH = 8;
	
	
	public static MathContext MATH_CTX = new MathContext(7, RoundingMode.HALF_EVEN);
	
	public static final int ALL_ALGOS = PAIR_WISE | GREEDY | BEST_LOCAL_SEARCH | FIRST_LOCAL_SEARCH;
	
	public enum Strategy {
		MODEL_TRIPLETS, ENTIRE_INPUT
	}
	
	public enum ALG_POLICY{
		REPLACE_FIRST, REPLACE_BEST, GREEDY, PAIR_WISE,REPLACE_FIRST_BY_SQUARES, REPLACE_BEST_BY_SQUARES
	}
	
	public enum ORDER_BY{
		COHESIVENESS,
		MODEL_SIZE,
		SPARSITY,
		MATCH_QUALITY,
		MODEL_ID,
	}
	
	

}