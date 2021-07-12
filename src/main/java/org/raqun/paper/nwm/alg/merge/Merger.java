package org.raqun.paper.nwm.alg.merge;

import java.util.ArrayList;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;

public abstract class Merger {

	protected ArrayList<Model> models;
	private Model mergedModel = null;
	private ArrayList<Tuple> mergedTuples;

	public Merger(ArrayList<Model> models) {
		this.models = models;
	}
	
	public  Merger(){
		this.models = new ArrayList<Model>();
	}
	
	protected abstract Matchable getMatch();
	
	public abstract RunResult getRunResult(int numOfModels);
	
	private ArrayList<Element> makeElementsFromMatchTuples(ArrayList<Tuple> match){
		ArrayList<Element> retVal = new ArrayList<Element>();
		for(Tuple t:match){
			retVal.add(new Element(t));
		}
		return retVal;	
	}
	
	private ArrayList<Element> getNotMatchedElementsFromModel(Model m, HashSet<Element> ignoreList){
		ArrayList<Element> nonIgnored = new ArrayList<Element>();
		for(Element elem:m.getElements()){
			boolean canBeAdded = true;
			for(Element bue:elem.getBasedUponElements()){
				if(ignoreList.contains(bue)) {
					canBeAdded = false;
					break;
				}
			}
			if(canBeAdded)
				nonIgnored.add(elem);
		}
		return nonIgnored;
	} 
	
	private ArrayList<Element> getNonMatchedElements(){
		ArrayList<Tuple> match = getMatch().getTuplesInMatch();
		ArrayList<Element> retVal = new ArrayList<Element>();
		HashSet<Element> matchedElements = new HashSet<Element>();
		for(Tuple t:match){
			for(Element elem:t.sortedElements())
				matchedElements.add(elem);
		}
		for(Model m: getMatch().getModels()){
			retVal.addAll(getNotMatchedElementsFromModel(m, matchedElements));
		}
		return retVal;
	}
	
	
	public void refreshResultTuplesWeight(Model merged) {
	//	int numOfSourceModels = 0;

		ArrayList<Model> srcModels = getMatch().getModels();
	//	for(Model m: getMatch().getModels()){
	//		numOfSourceModels += m.getMergedFrom();
	//	}
		Tuple t = null;
		for(Element e:merged.getElements()){
			if(e.getBasedUponElements().size() > 1){
				t = e.getContaingTuple();
				t.setWeight(t.calcWeight(srcModels));
			}
		}
	}
	
	public Model mergeMatchedModels(){
		//System.out.println("distro before:");
		//RunResult rr = new RunResult(0, null, null, getMatch().getTuplesInMatch());
		//System.out.println(rr);
		if(this.mergedModel != null)
			return this.mergedModel ;
		
		ArrayList<Tuple> matchTuples = getMatch().getTuplesInMatch();
		
		ArrayList<Element> elements = makeElementsFromMatchTuples(matchTuples);
		elements.addAll(getNonMatchedElements());
//		Collections.sort(elements, new Comparator<Element>() {
//			@Override
//			public int compare(Element e1, Element e2) {
//				// TODO Auto-generated method stub
//				return e1.toPrint().compareTo(e2.toPrint());
//			}
//		});
		StringBuilder sb = new StringBuilder();
		sb.append("merged models: ");
		String modelId = "";
		int numOfSourceModels = 0;
		for(Model m: getMatch().getModels()){
			modelId = modelId+m.getId();
			sb.append(m.getId()).append(",");
			numOfSourceModels += m.getMergedFrom();
		}
		//System.out.println(sb.toString());

		Model merged = new  Model(modelId,elements);
		for(Element e:elements){
			e.setModelId(modelId);
		}
		merged.setMergedFrom(numOfSourceModels);
		//refreshResultTuplesWeight(numOfSourceModels, merged);
		this.mergedModel = merged;
		
		return merged;
	}
	
	public ArrayList<Tuple> extractMerge(){
		if(this.mergedTuples != null)
			return this.mergedTuples;
		ArrayList<Tuple> tpls = new ArrayList<Tuple>();
		Model m = mergeMatchedModels();
		for(Element e:m.getElements()){
			if(e.getBasedUponElements().size() > 1 || AlgoUtil.COMPUTE_RESULTS_CLASSICALLY){
				tpls.add(e.getContaingTuple());
			}
		}
		this.mergedTuples = tpls;
		return tpls;
	}

}
