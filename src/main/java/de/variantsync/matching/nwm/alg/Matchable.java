package de.variantsync.matching.nwm.alg;

import java.util.ArrayList;

import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;

public interface Matchable {
	 
	 ArrayList<Tuple> getTuplesInMatch();
	 ArrayList<Model> getModels();
}