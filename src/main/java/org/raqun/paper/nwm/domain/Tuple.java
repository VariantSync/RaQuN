package org.raqun.paper.nwm.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.raqun.similarity.WeightMetric;


public class Tuple {
	private ArrayList<Element> elements;
	private ArrayList<Element> realElements = null;
	private int numberOfModels = -1;
	
	private long scaledWeight;
	
	private ArrayList<Element> sortedElems = null;
	private BigDecimal weight = BigDecimal.ZERO;
	
	public static int cnt = 0;
	
	public static BigDecimal relativeThresholdCoefficient = BigDecimal.ONE;//AlgoUtil.ratio("4", "3");
	
	private HashSet<Tuple> neighbors = new HashSet<Tuple>();
	private BigDecimal relativeThreshold = null;
	private int elemsCount = -1;
	private int propertiesUnionSize;
	private int sumOfAllElementSizes;
	private HashSet<String> myModels;
	
	private ArrayList<Element> orderedElements = new ArrayList<Element>();
	
	public Tuple(){
		elements = new ArrayList<Element>();
	}
	
	public Tuple(ArrayList<Element> elems){
		elements = elems;
		orderedElements.addAll(elems);
	}
	
	public void setScaledWeight(long l){
		scaledWeight = l;
	}
	
	public void cleanup(){
		neighbors = null;
	}
	
	public long getScaledWeight(){
		return scaledWeight;
	}
	
	public void addNeighbour(Tuple t){
		this.neighbors.add(t);
	}
	
	public HashSet<Tuple> getNeighbors(){
		return neighbors;
	}
	
	public boolean haveCommonModelWith(Tuple t){
		if(this.myModels == null){
			this.myModels = new HashSet<String>();
			for(Element e:getRealElements()){
				myModels.add(e.getModelId());
			}
		}
		for(Element e:t.getRealElements()){
			if(myModels.contains(e.getModelId()))
				return true;
		}
		return false;
	}
	
	public boolean isNeighborOf(Tuple t){
		if(neighbors.size() != 0)
			return neighbors.contains(t);
		for(Element e:t.getRealElements()){
			if(getRealElements().contains(e))
				return true;
		}
		return false;
	}
	
	public ArrayList<Element> getElements(){
		return elements;
	}
	
	public ArrayList<Element> sortedElements(){
		if(sortedElems == null){
			sortedElems = new ArrayList<Element>();
			for(Element e:elements){
				sortedElems.addAll(e.getBasedUponElements());
			}
			
			Collections.sort(sortedElems, new Comparator<Element>() {
				@Override
				public int compare(Element e1, Element e2) {
					return  (e1.getModelId().compareTo(e2.getModelId())> 0)?1:-1;
				}
			});
		}
		return sortedElems;
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Tuple){
			return getRealElements().equals(((Tuple)o).getRealElements());
		}
		return false;
	}
	
	
	public ArrayList<Element> getRealElements(){
		if(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY)
			return getElements();
		if(realElements == null){
			realElements = new ArrayList<Element>();
			for(Element e:elements){
				realElements.addAll(e.getBasedUponElements());
			}
		}
		return realElements;
	}
	
	public void addElement(Element e){
		elements.add(e);
		orderedElements.add(e);
	}
	
	public void addElements(Collection<Element> c){
		elements.addAll(c);
		orderedElements.addAll(c);
	}
	
	public ArrayList<Element> getOrderedElements(){
		return orderedElements;
	}
	
	
	public BigDecimal getWeight(){
		return weight;
	}
	
	public void setWeight(BigDecimal w){
		weight = w;
	}
	
	public int getSize(){
		return getRealElements().size();
	}
	
	
	
	public Tuple newExpanded(Element e, ArrayList<Model> mdls){
		@SuppressWarnings("unchecked")
		Tuple tuple = new Tuple(((ArrayList<Element>) elements.clone()));
		tuple.addElement(e);
		tuple.setWeight(tuple.calcWeight(mdls));
		return tuple;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nTUPLE "+" : ").append("weight:").append(weight).append("\t");
		for (Iterator<Element> iter = sortedElements().iterator(); iter.hasNext();) {
			Element e = (Element) iter.next();
			sb.append(e).append("\t");
		}
		return sb.
				
				append("\t(scaled = ").append(AlgoUtil.truncateWeight(new BigDecimal(scaledWeight, N_WAY.MATH_CTX))).
				append(")\t").
				append(collectElements()).
				toString();
	}
	
	
	@SuppressWarnings("unchecked")
	private String collectElements(){
		StringBuilder sb = new StringBuilder();
		HashMap<String, ArrayList<String>> allPropertiesModels = new HashMap<String,  ArrayList<String>>();
		HashSet<String> allProps = new HashSet<String>();
		for(Element e:getRealElements()){
			for(String prop:e.getProperties()){
				allProps.add(prop);
				 ArrayList<String> propModels = allPropertiesModels.get(prop);
				if(propModels == null)
					propModels = new  ArrayList<String>();
				propModels.add(e.getModelId());
				allPropertiesModels.put(prop, propModels);
			}
		}
		//final HashMap<String, ArrayList<Long>> finalCnt = (HashMap<String, ArrayList<Long>>) allPropertiesModels.clone();
		ArrayList<String> orderedProps = new ArrayList<String>(allProps);
		Collections.sort(orderedProps);
		
		for(String prp:orderedProps){
			sb.append(makePrintOfProperty(prp, allPropertiesModels.get(prp))).append(" ,");
		}
		sb.delete(sb.length()-2,sb.length()); // removing the last comma
		
		return sb.toString();
	}
	
	private String makePrintOfProperty(String prp, ArrayList<String> propModels) {
		StringBuilder sb = new StringBuilder();
		sb.append(prp);
		if(propModels.size() < numberOfModels){
			Collections.sort(propModels);
			sb.append(propModels);
		}
		return sb.toString();
			
	}

	public BigDecimal weight1Func(int numOfModels){
//		if(this.elemsCount != -1){
//			return (propertiesUnionSize == 0)? BigDecimal.ZERO:
//				new BigDecimal(
//					     this.sumOfAllElementSizes*this.elemsCount /*elemsCnt*/                     , N_WAY.MATH_CTX).
//		                            divide
//		                      (new BigDecimal(        propertiesUnionSize*(numOfModels*numOfModels)  /*sqModelSize */                              , N_WAY.MATH_CTX), N_WAY.MATH_CTX);
//		}
//		
		int sumOfElementSizes = 0;
		HashSet<String> propertiesUnion = new HashSet<String>();
		
		int elemsCnt = 0;
		
	//	HashSet<String> actualModels = new HashSet<String>(); 
		
		for(Element elem:getRealElements()){
				propertiesUnion.addAll(elem.getProperties());
				sumOfElementSizes += elem.getSize();
		//		actualModels.add(elem.getModelId());
				elemsCnt++;
		}
		this.elemsCount = elemsCnt;
		this.propertiesUnionSize = propertiesUnion.size();
		this.sumOfAllElementSizes = sumOfElementSizes;
		
		
		this.relativeThreshold  = new BigDecimal(1, N_WAY.MATH_CTX).divide(new BigDecimal(numOfModels, N_WAY.MATH_CTX), N_WAY.MATH_CTX);

		BigDecimal tooAccurateWeight = (propertiesUnionSize == 0)? BigDecimal.ZERO:
			new BigDecimal(
							                 sumOfElementSizes                      , N_WAY.MATH_CTX).
				                                     divide
		(new BigDecimal(        propertiesUnionSize*numOfModels  /*sqModelSize */                              , N_WAY.MATH_CTX), N_WAY.MATH_CTX);
			//	sumOfElementSizes / (propertiesUnionSize*numOfModels); //sumOfElementSizes*elemsCnt / (propertiesUnionSize*sqModelSize);
		
		
//		int sqModelSize = numOfModels*numOfModels;
//		this.relativeThreshold  = new BigDecimal(elemsCnt, N_WAY.MATH_CTX).divide(new BigDecimal(sqModelSize, N_WAY.MATH_CTX), N_WAY.MATH_CTX);
//
//		BigDecimal tooAccurateWeight = (propertiesUnionSize == 0)? BigDecimal.ZERO:
//			new BigDecimal(
//							     sumOfElementSizes*elemsCnt /*elemsCnt*/                     , N_WAY.MATH_CTX).
//				                            divide
//		(new BigDecimal(        propertiesUnionSize*sqModelSize  /*sqModelSize */                              , N_WAY.MATH_CTX), N_WAY.MATH_CTX);
//			//	sumOfElementSizes / (propertiesUnionSize*numOfModels); //sumOfElementSizes*elemsCnt / (propertiesUnionSize*sqModelSize);
							     
		return tooAccurateWeight;		
	}

	public BigDecimal weight2Func(int numOfModels){
		double sumOfElementSizes = 0;
		HashSet<String> propertiesUnion = new HashSet<String>();
		HashMap<String, Integer> propCount = new HashMap<String, Integer>();
		
		for(Element e:getRealElements()){
			propertiesUnion.addAll(e.getProperties());
			sumOfElementSizes += e.getSize();
			for(String prop:e.getProperties()){
				Integer cnt = propCount.get(prop);
				if(cnt == null)
					cnt = 0;
				cnt += 1;
				propCount.put(prop, cnt);
			}
		}
		
		HashMap<Integer, Integer> countersBins = new HashMap<Integer, Integer>();
		
		for(String prop:propCount.keySet()){
			Integer propCnt = propCount.get(prop);
			Integer cntBin = countersBins.get(propCnt);
			if(cntBin == null) cntBin = new Integer(0);
			cntBin += 1;
			countersBins.put(propCnt,cntBin);
		}
		
		double sum = 0.0;
		for(Integer amount:countersBins.keySet()){
			if(amount < 2)
				continue;
			int times = countersBins.get(amount);
			sum = sum + amount*amount*times;
		}
		this.relativeThreshold  = BigDecimal.ZERO;// new BigDecimal(1, N_WAY.MATH_CTX).divide(new BigDecimal(numOfModels, N_WAY.MATH_CTX), N_WAY.MATH_CTX);
		BigDecimal tooAccurateWeight = new BigDecimal(   sum                  ).
				                                                                  divide        (
			   new BigDecimal(     propertiesUnion.size() *numOfModels*numOfModels                               ), N_WAY.MATH_CTX);
		//		(propertiesUnion.size() *numOfModels*numOfModels);
				//(numOfModels*sumOfElementSizes);
		return tooAccurateWeight;
	}

	/*
	// TODO: Changed weight calculation here, change back if necessary
	// HAD TO CHANGE THIS SO THAT ALL ALGORITHMS USE THE SAME IMPLEMENTATION
	// WE ARE USING OUR OWN IMPLEMENTATION OF THE WEIGHT CALCULATION INSTEAD
	public BigDecimal calcWeight(ArrayList<Model> mdls) {
		int mdCount = 0;
		for(Model m:mdls){
			mdCount += m.getMergedFrom();
		}
		numberOfModels = mdCount;
		return weight2Func(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY?mdls.size():mdCount);
	}*/
	public BigDecimal calcWeight(ArrayList<Model> mdls) {
		int mdCount = 0;
		for(Model m:mdls){
			mdCount += m.getMergedFrom();
		}
		numberOfModels = mdCount;
		double weight = WeightMetric.weightForElements(this.getRealElements(),
				AlgoUtil.COMPUTE_RESULTS_CLASSICALLY ? mdls.size() : mdCount);
		this.relativeThreshold  = BigDecimal.ZERO;
		try {
			return new BigDecimal(weight);
		}catch(Exception e) {
			System.out.println("ERROR");
			throw e;
		}
	}

	public boolean hasElement(Element e) {
		return elements.contains(e);
	}

	public BigDecimal getRelativeThreshold() {
		return relativeThreshold.multiply(relativeThresholdCoefficient, N_WAY.MATH_CTX);
	}
	

	public void recomputeSelf(ArrayList<Model> mdls) {
		ArrayList<Element> elems  = new ArrayList<Element>();
		
		for(Element e:getElements()){
			elems.addAll(decomposeElement(e));
		}
		this.elements = elems;
		this.realElements = null;
		this.sortedElems = null;
		this.myModels = null;
		this.orderedElements = elems;
		this.setWeight(this.calcWeight(mdls));
	}
	
	public ArrayList<Element> decompose(){
		ArrayList<Element> retVal = new ArrayList<Element>();
		for(Element e:getElements()){
			retVal.addAll(decomposeElement(e));
		}
		return retVal;
	}
	
	public ArrayList<Element> getConstructingElements(){
		ArrayList<Element> retVal = new ArrayList<Element>();
		for(Element e:getElements()){
			retVal.addAll(getConstructingElements(e));
		}
		return retVal;
	}

	private  ArrayList<Element> getConstructingElements(Element e) {
		ArrayList<Element> retVal= new ArrayList<Element>();
		ArrayList<Element> cons = e.getConstructingElements();
		if(cons.size() == 1){
			if(cons.get(0).isRaw()){
				retVal.add(e);
				return retVal;
			}
			for(Element elem:cons.get(0).getBasedUponElements()){
				retVal.addAll(getConstructingElements(elem));
			}
		}
		for(Element el:cons){
			retVal.addAll(getConstructingElements(el));
		}
		return retVal;
	}

	private ArrayList<Element> decomposeElement(Element e) {
		ArrayList<Element> retVal= new ArrayList<Element>();
		ArrayList<Element> bue = e.getBasedUponElements();
		if(bue.size() == 1){
			if(bue.get(0).isRaw()){
				retVal.add(e);
				return retVal;
			}
			for(Element elem:bue.get(0).getBasedUponElements()){
				retVal.addAll(decomposeElement(elem));
			}
		}
		for(Element el:bue){
			retVal.addAll(decomposeElement(el));
		}
		return retVal;
	}
}
