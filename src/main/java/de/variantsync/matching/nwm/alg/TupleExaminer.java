package de.variantsync.matching.nwm.alg;

import de.variantsync.matching.nwm.domain.Tuple;

public interface TupleExaminer {
	// accepts a tuple, handles it, returns true if it should be added to the list of tuples
	boolean examine(Tuple t);
	
	// returns true if the process of creating tuples should continue
	boolean proceedWithTupleExpansion(Tuple t);
	
	//incoming notification that the tuple creation process is finished
	void doneWithTupleCreation();
}