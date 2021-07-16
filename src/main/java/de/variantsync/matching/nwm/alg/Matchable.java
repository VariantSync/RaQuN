package de.variantsync.matching.nwm.alg;

import java.util.ArrayList;

import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;

/**
 * Undocumented code by Rubin and Chechik
 */
public interface Matchable {
	 
	 ArrayList<Tuple> getTuplesInMatch();
	 ArrayList<Model> getModels();
}