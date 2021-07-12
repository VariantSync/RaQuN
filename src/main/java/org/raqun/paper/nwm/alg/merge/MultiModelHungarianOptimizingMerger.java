package org.raqun.paper.nwm.alg.merge;

import java.util.ArrayList;
import java.util.HashSet;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class MultiModelHungarianOptimizingMerger extends MultiModelMerger {

	public MultiModelHungarianOptimizingMerger(ArrayList<Model> models) {
		super(models);
	}

	@Override
	protected ArrayList<Tuple> optimizePairs(ArrayList<Tuple> pairs) {
		
		ArrayList<Tuple> nextPairs = extractNextPairs(pairs);
		ArrayList<Tuple> currPairs = getValidPairs(pairs);
		while(AlgoUtil.calcGroupWeight(nextPairs).compareTo(AlgoUtil.calcGroupWeight(currPairs)) > 0){
			currPairs = nextPairs;
			nextPairs = extractNextPairs(nextPairs);
		}
		
		return currPairs;
	}
	
	private ArrayList<Tuple> extractNextPairs(ArrayList<Tuple> pairs){
		ArrayList<Tuple> validPairs = getValidPairs(pairs);
		ArrayList<Element> elems = new ArrayList<Element>();
		for(Tuple t:validPairs){
			elems.add(new Element(t));
		}
		elems.addAll(getFreeElements(validPairs));
		Model m = new Model("2000", elems);
		MultiModelHungarian mmh = new MultiModelHungarian(m, models.size());
		mmh.runPairing();
		ArrayList<Tuple> nextPairs = mmh.extractMerge();
		return getValidPairs(nextPairs);
	}

	private ArrayList<Tuple> getValidPairs(ArrayList<Tuple> pairs) {
		ArrayList<Tuple> validTuples = new ArrayList<Tuple>();
		//Collections.sort(pairs, new AlgoUtil.TupleComparator(true));
		HashSet<Element> usedElems = new HashSet<Element>();
		for(Tuple t:pairs){
			boolean usefulTuple = true;
			for(Element e:t.getRealElements()){
				if(usedElems.contains(e)){
					usefulTuple = false;
					break;
				}
			}
			if(usefulTuple){
				validTuples.add(t);
				usedElems.addAll(t.getRealElements());
			}
		}
		return validTuples;
	}

}
