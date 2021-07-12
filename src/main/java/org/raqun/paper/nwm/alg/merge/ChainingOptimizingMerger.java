package org.raqun.paper.nwm.alg.merge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class ChainingOptimizingMerger extends MultiModelMerger {
	private boolean contineouesImprove = true;

	public ChainingOptimizingMerger(ArrayList<Model> models) {
		super(models);
	}
	
	public ChainingOptimizingMerger(ArrayList<Model> models, boolean contineouesImprove) {
		this(models);
		this.contineouesImprove  = contineouesImprove;
	}

	public  ArrayList<Tuple> optimizePairs(ArrayList<Tuple> allPairs, boolean continuousOptimization){
		//System.out.println("pre chanining:");
		//AlgoUtil.printTuples(allPairs);
		HashSet<Tuple> usedTuples = new HashSet<Tuple>();
		HashMap<Element, Tuple> smallerModelIdElemToTuple = new HashMap<Element, Tuple>();
		HashMap<Element, Tuple> largerModelIdElemToTuple = new HashMap<Element, Tuple>();
		//HashSet<Element> usedElements = new HashSet<Element>();
		
		ArrayList<Tuple> allChains = new ArrayList<Tuple>();
		for(Tuple t:allPairs){
			smallerModelIdElemToTuple.put(getElementWithSmallerId(t), t);
			largerModelIdElemToTuple.put(getElementWithHigherId(t), t);
		}
		
		ArrayList<Tuple> resultTuples = new ArrayList<Tuple>(); 
		//ArrayList<Tuple> STAM = new ArrayList<Tuple>();
		boolean firstChain = true;
		for(int i=0;i< allPairs.size();i++){
			
			Tuple t = allPairs.get(i);
			
			if(usedTuples.contains(t))
				continue;
			
			// starting a chain
			
			HashSet<String> usedModelsInChain = new HashSet<String>();
			for(Element e:t.getRealElements()){
				usedModelsInChain.add(e.getModelId());
			}
			ArrayList<Tuple> chain = new ArrayList<Tuple>();
			addTupleToChain(chain, t, firstChain);
			firstChain = false;
			//Tuple newTuple = new Tuple(t.getElements());
			usedTuples.add(t);
			Element largeSideConnectingElem = getElementWithHigherId(t);
			//Element smallSideConnectionElem = getElementWithSmallerModelId(t);
			while(true){// chaining by adding to the element with the larger size
				Tuple currentTuple = smallerModelIdElemToTuple.get(largeSideConnectingElem);
				if(currentTuple == null)
					break;
				if(usedTuples.contains(currentTuple))
					break;

				if(currentTuple.getElements().size() == 1){
					if(!haveUsedModel(currentTuple.getOrderedElements().get(0), usedModelsInChain)){
						chain.add(currentTuple);				
						usedTuples.add(currentTuple);
						break;

					}
				}
				
				if(haveUsedModel(getElementWithHigherId(currentTuple), usedModelsInChain)){
					i--;
					break;
				}
				addTupleToChain(chain, currentTuple, false);
				//chain.add(currentTuple);				
				usedTuples.add(currentTuple);
				
		//		Element nextTupleSmallerElement = getElementWithHigherId(currentTuple);
				
			//	Tuple nextNext = smallerModelIdElemToTuple.get(nextTupleSmallerElement);
			//	if(nextNext == null)
			//		break;
				
				largeSideConnectingElem =  getElementWithHigherId(currentTuple);
//				Element nextElem = getElementWithHigherModelId(nextTuple);
//				newTuple.addElement(nextElem);
//				largeSideConnectingElem = nextElem;
				
			}
			
			//optimizeChain(elementsToUse, 0, new ArrayList<Tuple>(), new HashMap<Integer, ArrayList<Tuple>>());
			allChains.addAll(optimizeChain(1, chain, 0,new ArrayList<Tuple>(),new HashMap<Integer, ArrayList<Tuple>>() ));
			
//			while(true){
//				Tuple prevTuple = largerModelIdElemToTuple.get(smallSideConnectionElem);
//				if(prevTuple == null)
//					break;
//				if(usedTuples.contains(prevTuple))
//					break;
			
//
//				usedTuples.add(prevTuple);
//				Element prevElem = getElementWithSmallerModelId(prevTuple);
//				newTuple.addElement(prevElem);
//				smallSideConnectionElem = prevElem;
//			}
//			newTuple.setWeight(newTuple.calcWeight(models));
			
//			resultTuples.addAll(optimizeTuple(newTuple,usedElements));
//			STAM.add(newTuple);
			
		}
		resultTuples = allChains;
		cleanFreeElements(resultTuples);
		cleanDuplicates(resultTuples);
		//mergeFreeElements(resultTuples);
		ArrayList<Tuple> currResult = resultTuples;
		if(currResult.size() == 0)
			return currResult;
		ArrayList<Tuple> improved = improveResults(resultTuples);
		while(AlgoUtil.calcGroupWeight(currResult).compareTo(AlgoUtil.calcGroupWeight(improved)) < 0){
			currResult = improved;
			improved = improveResults(improved);
			//System.out.println(AlgoUtil.calcGroupWeight(improved));
		}
		
		
		
//		System.out.println("Pre optimization chains are:");
//		AlgoUtil.printTuples(STAM);
		//System.out.println("Post optimization chains are:");
		//AlgoUtil.printTuples(resultTuples);
	//	return resultTuples;
		return currResult;
	}
	
	private void addTupleToChain(ArrayList<Tuple> chain, Tuple tuple, boolean all) {
		if(all){
			for(Element e:tuple.getElements()){
				addElementToChain(chain, e);
			}
		}else
			addElementToChain(chain, getElementWithHigherId(tuple));
		
	}

	private void addElementToChain(ArrayList<Tuple> chain, Element e) {
		Tuple t = new Tuple();
		t.addElement(e);
		t.setWeight(t.calcWeight(models));
		chain.add(t);
	}

	private boolean haveUsedModel(Element elem, HashSet<String> usedModels) {
		for(Element e:elem.getBasedUponElements()){
			if(usedModels.contains(e.getModelId()))
				return true;
		}
		return false;
	}

	private ArrayList<Tuple> improveResults(ArrayList<Tuple> resultTuples) {
		
		//System.out.println("\n\n\n\n\n in improve");
		
		if(!contineouesImprove)
			return resultTuples;
		ArrayList<Model> merged = buildModelFromResults(resultTuples);
		
		ChainingOptimizingMerger com = new ChainingOptimizingMerger(merged, false);
		com.run();
		ArrayList<Tuple> improved = com.extractMerge();
		
	
		
		ArrayList<Element> freeElements  = extractFreeElements(merged.get(0), improved);
		
		ArrayList<Tuple> mergedFreeElements = mergeFreeElements(freeElements);
		
		if(AlgoUtil.areThereDuplicates(mergedFreeElements)){
			System.out.println("PROBLEM - in mergedFreeElements !!!");
		}
		
		
		improved.addAll(mergedFreeElements);		
		
		ArrayList<Element> isolated = extractFreeElements(models.get(0),improved);
		ArrayList<Tuple> isolatedTuples = makeTuples(isolated);
		improved.addAll(isolatedTuples);
		if(AlgoUtil.areThereDuplicates(improved)){
			System.out.println("PROBLEM !!!");
		}
		return improved;
	}
	
	private ArrayList<Tuple> makeTuples(ArrayList<Element> isolated) {
		ArrayList<Tuple> retVal = new ArrayList<Tuple>();
		for(Element e:isolated){
			Tuple t = new Tuple();
			t.addElement(e);
			retVal.add(t);
		}
		return retVal;
	}

	private ArrayList<Element> extractFreeElements(Model m,ArrayList<Tuple> result/*, ArrayList<Tuple> resultTuples*/) {
		ArrayList<Element> retVal = new ArrayList<Element>();
		HashSet<Element> foundElems = new HashSet<Element>();
		HashSet<Element> elementsInMatch = new HashSet<Element>();
		HashSet<Element> allElements = new HashSet<Element>();
		
		for(Tuple t:result){
			elementsInMatch.addAll(t.getRealElements());
		}
		for(Element modElem:models.get(0).getElements()){
			allElements.addAll(modElem.getBasedUponElements());
		}
		for(Element resultElement:allElements){
			if(!elementsInMatch.contains(resultElement)){
				if(!foundElems.contains(resultElement)){
					retVal.add(resultElement);
					foundElems.add(resultElement);
				}
					
			}
		}
		return retVal;
//		HashMap<Element, Tuple> resultElementToTuple = new HashMap<Element, Tuple>();
//		for(Tuple t:resultTuples){
//			for(Element e:t.getRealElements()){
//				resultElementToTuple.put(e, t);
//			}
//		}
	}

//	private boolean allElementsOfTupleNotIn(Tuple t, HashSet<Element> elems){
//		for(Element e:t.getRealElements()){
//			if(!elems.contains(e)){
//				return false;
//			}
//		}
//		return true;
//	}
	

	private ArrayList<Model> buildModelFromResults(ArrayList<Tuple> resultTuples) {
		ArrayList<Element> elems = new ArrayList<Element>();
		for(Tuple t:resultTuples){
			elems.add(new Element(t));
		}
		elems.addAll(getFreeElements(resultTuples));
		Model m = new Model("-56", elems);
		m.setMergedFrom(models.size());
		ArrayList<Model> retVal = new ArrayList<Model>();
		retVal.add(m);
		return retVal;
	}

	protected  ArrayList<Tuple> optimizePairs(ArrayList<Tuple> allPairs) {
		return optimizePairs(allPairs, true);
	}
	
	
//	protected void mergeElements(HashSet<Element> elems){
//		Model md = new Model("252");
//		for(Element e:elems){
//			md.addElement(e);
//		}
//	}
	
	protected ArrayList<Tuple> mergeFreeElements(ArrayList<Element> freeElems) {
		if(freeElems.size() <2 ) return new ArrayList<Tuple>();

		Model mdl  = new Model("-252",freeElems);
		mdl.setMergedFrom(models.size());
		ArrayList<Model> mdls  = new ArrayList<Model>();
		mdls.add(mdl);
		
		ChainingOptimizingMerger com = new ChainingOptimizingMerger(mdls, false);
		com.run();
		ArrayList<Tuple> tuplesOfFee = com.extractMerge();
		cleanDuplicates(tuplesOfFee);
		return tuplesOfFee;
		
//		MultiModelHungarian mmh = new MultiModelHungarian(mdl, models.size());
//		mmh.runPairing();
//		ArrayList<Tuple> otherPairs = mmh.extractMerge();
//		resultTuples.addAll(otherPairs);
//		}
	}


	private void cleanDuplicates(ArrayList<Tuple> tpls) {
		Collections.sort(tpls, new AlgoUtil.TupleComparator(true));
		HashSet<Element> usedElements = new HashSet<Element>();
		for(int i=tpls.size()-1;i>=0;i--){
			Tuple t = tpls.get(i);
			boolean containsUsed = false;
			for(Element e:t.getRealElements()){
				if(usedElements.contains(e)){
					containsUsed = true;
					break;
				}
				else{
					usedElements.add(e);
				}
			}
			if(containsUsed){
				tpls.remove(t);
			}
		}
	}

	private ArrayList<Tuple> optimizeTuple(Tuple newTuple, HashSet<Element> usedElements) {
		//System.out.println("\n\nGoing to optimize the Tuple:\n"+newTuple);
		ArrayList<Element> elementsToUse = new ArrayList<Element>();
		for(Element e:newTuple.getOrderedElements()){
			if(usedElements.contains(e))
				continue;
			elementsToUse.add(e);
			usedElements.add(e);
		}
		 ArrayList<Tuple> retVal =  optimizeChain(elementsToUse, 0, new ArrayList<Tuple>(), new HashMap<Integer, ArrayList<Tuple>>());
		// System.out.println("Optimized to:");
		 //AlgoUtil.printTuples(retVal);
		 usedElements.addAll(elementsToUse);
		 return retVal;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Tuple> optimizeChain(ArrayList<Element> elems, int currConnectionIndex, ArrayList<Tuple> optimalSoFar, HashMap<Integer, ArrayList<Tuple>> memo){
		// there are  elems.size()-1 connections in a chain
		if(currConnectionIndex == elems.size())
			return new ArrayList<Tuple>();
		if(memo.get(currConnectionIndex) != null) // no need to go down that path from this point onward the problem is solved
			return memo.get(currConnectionIndex);
		if(currConnectionIndex == elems.size()-1) {// reached to the end - building an deficient tuple from the last element
			addDeficientTuple(elems.get(currConnectionIndex), optimalSoFar);
			memo.put(currConnectionIndex, (ArrayList<Tuple>) optimalSoFar.clone());
			return optimalSoFar;
		}
		
		ArrayList<ArrayList<Tuple>> chains = new ArrayList<ArrayList<Tuple>>();
		for(int i=currConnectionIndex; i<elems.size();i++){
			Tuple t = buildTupleBasedOn(elems, currConnectionIndex,i+1); // build a tuple constructed from the subset of elements, startIndex - inclusive, endIndex - exclusive
			ArrayList<Tuple> rest = optimizeChain(elems,i+1, optimalSoFar, memo);
			rest = (ArrayList<Tuple>) rest.clone();
			rest.add(t);
			chains.add(rest);
		}
		ArrayList<Tuple> optimal = getBestChain(chains);
		memo.put(currConnectionIndex, (ArrayList<Tuple>) optimal.clone());
		return optimal;
	}
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private ArrayList<Tuple> optimizeChain(int stam,ArrayList<Tuple> chain, int currConnectionIndex, ArrayList<Tuple> optimalSoFar, HashMap<Integer, ArrayList<Tuple>> memo){
		// there are  elems.size()-1 connections in a chain
		if(currConnectionIndex == chain.size())
			return new ArrayList<Tuple>();
		if(memo.get(currConnectionIndex) != null) // no need to go down that path from this point onward the problem is solved
			return memo.get(currConnectionIndex);
		if(currConnectionIndex == chain.size()-1) {// reached to the end - building an deficient tuple from the last element
			//addDeficientTuple(elems.get(currConnectionIndex), optimalSoFar);
			Tuple last = chain.get(currConnectionIndex);
			optimalSoFar.add(last);
			memo.put(currConnectionIndex, (ArrayList<Tuple>) optimalSoFar.clone());
			return optimalSoFar;
		}
		
		ArrayList<ArrayList<Tuple>> chains = new ArrayList<ArrayList<Tuple>>();
		for(int i=currConnectionIndex; i<chain.size();i++){
			//Tuple t = buildTupleBasedOn(chain, currConnectionIndex,i+1); // build a tuple constructed from the subset of elements, startIndex - inclusive, endIndex - exclusive
			Tuple chainHeader = extractSubChain(chain,currConnectionIndex, i+1);
			ArrayList<Tuple> rest = optimizeChain(stam, chain,i+1, optimalSoFar, memo);
			rest = (ArrayList<Tuple>) rest.clone();
			rest.add(chainHeader);
			chains.add(rest);
		}
		ArrayList<Tuple> optimal = getBestChain(chains);
		memo.put(currConnectionIndex, (ArrayList<Tuple>) optimal.clone());
		return optimal;
	}
	
	
	
	
	private Tuple extractSubChain(ArrayList<Tuple> chain, int start, int end) {
		Tuple retVal = new Tuple();
		for(int i=start;i<end;i++){
			retVal.addElements(chain.get(i).getElements());
		}
		return retVal;
	}

	private ArrayList<Tuple> getBestChain(ArrayList<ArrayList<Tuple>> chains) {
		ArrayList<Tuple> bestChain = null;
		BigDecimal maxWeight = new BigDecimal(-1);
		for(ArrayList<Tuple> chain: chains){
			BigDecimal chainWeight = getChainWeight(chain); 
			//if(maxWeight.compareTo(chainWeight) == 0){
			//	System.out.println("here");
			//}
			if(maxWeight.compareTo(chainWeight) <= 0 ){
				bestChain = chain;
				maxWeight = chainWeight;
			}
		}
		return bestChain;
	}

	private BigDecimal getChainWeight(ArrayList<Tuple> chain) {
		BigDecimal result = BigDecimal.ZERO;
		for(Tuple t:chain){
			t.setWeight(t.calcWeight(models));
			result = result.add(t.getWeight(), N_WAY.MATH_CTX);
		}
		return result;
	}

	private Tuple buildTupleBasedOn(ArrayList<Element> elems, int startIndex, int endIndex) {
		Tuple t = new Tuple();
		for(int i=startIndex;i<endIndex;i++){
			t.addElement(elems.get(i));
		}
		if(endIndex - startIndex == 1)
			t.setWeight(BigDecimal.ZERO);
		else{
			if(haveTwoElementsFromSameModel(t)){
				t.setWeight(new BigDecimal(-50));
			}
			else
				t.setWeight(t.calcWeight(models));
		}
		return t;
	}

	private boolean haveTwoElementsFromSameModel(Tuple t) {
		HashSet<String> usedModels = new HashSet<String>();
		for(Element e:t.getRealElements()){
			if(usedModels.contains(e.getModelId()))
				return true;
			else
				usedModels.add(e.getModelId());
		}
		return false;
	}

	private Tuple addDeficientTuple(Element e, ArrayList<Tuple> tpls){
		Tuple t = new Tuple();
		t.addElement(e);
		t.setWeight(BigDecimal.ZERO);
		tpls.add(0, t);
		return t;
	}
}
