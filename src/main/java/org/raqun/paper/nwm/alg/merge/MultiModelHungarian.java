package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class MultiModelHungarian extends HungarianMerger {
	ArrayList<Model> oneModel = new ArrayList<Model>();
	
	public MultiModelHungarian(Model m, int numOfModels){
		super(m,m, numOfModels);
		m.setMergedFrom(numOfModels);
		oneModel.add(m);
	}
	
	protected Tuple buildTuple(Element e1, Element e2){
		Tuple t = super.buildTuple(e1, e2, true);
		ArrayList<Model> tmp = new ArrayList<Model>();
		tmp.add(models.get(0));
		t.setWeight(t.calcWeight(tmp));
		Tuple e1Container = e1.getContaingTuple();
		Tuple e2Container = e2.getContaingTuple();
		if(		AlgoUtil.areNeighbours(e1Container, e2Container) ||
				e1Container.haveCommonModelWith(e2Container)||
				areInOrder(e1, e2)||
				!AlgoUtil.shouldCompose(t, e1, e2, tmp)
				
				){ // forcing directionness to prevent a solution that includes both ways 
			t.setWeight(BigDecimal.ZERO);
		}
		return t;
	}

	public static boolean areInOrder(Element e1, Element e2) {
		//return AlgoUtil.getLexicographicRepresentation(e1).compareTo(AlgoUtil.getLexicographicRepresentation(e2)) >= 0;
	//	return e1.getModelId().compareTo(e2.getModelId()) >= 0;
	//	return e1.getId() < e2.getId();
//		int cmp = e1.getId() - e2.getId();//e1.getIdentifyingLabel().compareTo(e2.getIdentifyingLabel());
//		if( cmp < 0)
//			return true;
//		if(cmp > 0){
//			return false;
//		}
		return AlgoUtil.getLexicographicRepresentation(e1).compareTo(AlgoUtil.getLexicographicRepresentation(e2)) <= 0;
	}
	
	
	protected void sumTuplesWeight(){
		weight = BigDecimal.ZERO;
		for(Tuple t:tuplesInMatch){
			weight = weight.add(t.calcWeight(oneModel), N_WAY.MATH_CTX);
		}
	}

//	public Tuple getBestTupleInSolution(){
//		runPairing();
//		mergeMatchedModels();
//		ArrayList<Tuple> tuples = getTuplesInMatch();
//		if(tuples.size() == 0)
//			return null;
//		Collections.sort(tuples, new AlgoUtil.TupleComparator(false));
//		return tuples.get(0);
//	}

}
