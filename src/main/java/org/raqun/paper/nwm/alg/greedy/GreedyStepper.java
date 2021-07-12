package org.raqun.paper.nwm.alg.greedy;

import java.util.ArrayList;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.alg.TupleExaminer;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class GreedyStepper extends AlgoBase implements TupleExaminer {
	
	private ArrayList<Model> models;
	private boolean continueExploring = true;
	private HashSet<Element> collectedElements = new HashSet<Element>();
	ArrayList<Tuple> resultTuples = new ArrayList<Tuple>();
	
	private Tuple bestInCycle;
//	private int stamCnt;
	//private int actualModelCount;
	
	/* the idea 
	 1) each created tuple is measured vs currenlyExamined, if it is better and it improves the solution, than we update currenlyExamined
	 2) at the end of the tuple generation cycle: 
	 	2.1) if there's a currenlyExamined, we add it to the solution and start a new cycle
	    2.2) if there's no currenlyExamined, we do not restart another cycle, and the solution we have is the one that is best
	 */
	public GreedyStepper(ArrayList<Model> models) {
		super("Greedy one by one");
		this.models = models;
//		int modelCount = 0;
//		for(Model m:models){
//			modelCount +=m.getMergedFrom();
//		}
//		this.actualModelCount = modelCount;
	}

	@Override
	protected ArrayList<Tuple> doRun() {
		//ArrayList<Tuple> solution = new ArrayList<Tuple>();
		
		
	//	this.stamCnt = 0;
		while(continueExploring){
			AlgoUtil.generateTuplePartially(models, this);
			doneWithTupleCreation();
		}
		//System.out.println("num of result tuples: " +resultTuples.size());
		//System.out.println(resultTuples);
		//AlgoUtil.getTuplesOfNonMatchedCompositeElementsFromModel(m, elementsUsedInMatch));
		return resultTuples;
	}
	
	protected void updateResultWithTuplesOfNonMatchedCompositeElements() {
		HashSet<Element> elementsUsedInMatch = new HashSet<Element>();
		for(Tuple t:result){
			elementsUsedInMatch.addAll(t.getElements());
		}
		for(Model m:getModels()){		
			result.addAll(AlgoUtil.getTuplesOfNonMatchedCompositeElementsFromModel(m, elementsUsedInMatch));
		}
	}

	
	@Override
	public boolean examine(Tuple t) {
		//stamCnt++;
		handleReceivedTuple(t);
		return false; // we do not want to create any list of tuples!!!!
	}

	@Override
	public boolean proceedWithTupleExpansion(Tuple t) { 
		return canBeAddedToSolution(t);
	}
	
	private void handleReceivedTuple(Tuple t) {
		t.setWeight(t.calcWeight(models));
		if(AlgoUtil.isNonValidTuple(t))
			return;
		if(bestInCycle == null ||  (t.getWeight().compareTo(bestInCycle.getWeight()) >0)){
			if(canBeAddedToSolution(t))
				bestInCycle = t;
		}
	}
	
	private void addComposites(Tuple t){
		
		// add the composite tuple to the ignore list
		// the ignore is the tuple(or tuples with equal weight) with the lowest weight that should be ignored
		
		// once we do a run that does not yield a bestInCycle
		// we take the solution, go over all of its elements, taking each such element from its original model, the elements that remained at the models are
		// to be added as tuples to the solution
		
		
		for(Element e:t.getElements()){
			if(!AlgoUtil.isNonValidTuple(e.getContaingTuple())){
				addTupleToSolution(e.getContaingTuple());
			}
		}
	}

	private boolean canBeAddedToSolution(Tuple t) {
		for(Element e:t.getConstructingElements()){
			if(collectedElements.contains(e))
				return false;
		}
		return true;
	}

	@Override
	public void doneWithTupleCreation() {
		if(bestInCycle != null){
			addBestFoundTupleToSolution();		
		}
		else
			continueExploring = false;
	}

	private void addBestFoundTupleToSolution() {
		if(!AlgoUtil.COMPUTE_RESULTS_CLASSICALLY && !AlgoUtil.shouldCompose(bestInCycle, models))
			addComposites(bestInCycle);
		else
			addTupleToSolution(bestInCycle);
		bestInCycle = null;
	}
	
	private void addTupleToSolution(Tuple t){
		resultTuples.add(t);
		collectedElements.addAll(t.getConstructingElements());
	}

	@Override
	public ArrayList<Model> getModels() { return models;}
}
