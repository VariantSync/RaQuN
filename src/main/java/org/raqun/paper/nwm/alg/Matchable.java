package org.raqun.paper.nwm.alg;

import java.util.ArrayList;

import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public interface Matchable {
	 
	 ArrayList<Tuple> getTuplesInMatch();
	 ArrayList<Model> getModels();
}
