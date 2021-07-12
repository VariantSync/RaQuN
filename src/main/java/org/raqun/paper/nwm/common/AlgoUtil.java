package org.raqun.paper.nwm.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.raqun.paper.nwm.alg.TupleExaminer;
import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.alg.merge.MergeDescriptor;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class AlgoUtil {
	
	public static boolean TRACE = false;
	
	public static boolean COMPUTE_RESULTS_CLASSICALLY = false;
	
	public static final double PROPERTIES_QUALITY_THRESHOLD = 0;
	
	private static final BigDecimal tresholdRatioBase = new BigDecimal(4.0/3.0, N_WAY.MATH_CTX);
	public static BigDecimal tresholdRatio =  new BigDecimal ("" + 4.0/3.0, N_WAY.MATH_CTX); // to be later divided by k
	
	private static final BigDecimal pairWisePairingTresholdBase =  new BigDecimal(0.5, N_WAY.MATH_CTX);//.0.5;// 2.0 / 3.0;
	public static BigDecimal pairWisePairingTreshold =  new BigDecimal(""+2.0/3.0, N_WAY.MATH_CTX);//0.5;// 2.0 / 3.0;
	
	public static boolean filterGeneration = true;
	public static String NO_MODEL_ID ="-1";
	
	private static final TupleExaminer EMPTY_EXAMINER = new TupleExaminer() {
		@Override
		public boolean proceedWithTupleExpansion(Tuple t) { return true;}
		@Override
		public boolean examine(Tuple t) { return true;}
		@Override
		public void doneWithTupleCreation() {}
	};

	public static BigDecimal ratio(String up, String down){
		return bigDec(up).divide(bigDec(down), N_WAY.MATH_CTX);
	}
	
	public static BigDecimal  bigDec(String num){
		return new BigDecimal(num, N_WAY.MATH_CTX);
	}
	
	public static boolean areThereDuplicates(ArrayList<Tuple> tpls){
//		HashSet<Element> elems = new HashSet<Element>();
//		for(Tuple t:tpls){
//			for(Element e:t.getRealElements()){
//				if(elems.contains(e)){
//					return true;
//				}
//				elems.add(e);
//			}
//		}
		return false;
	}
	
	public static void useTreshold(boolean use){
		if(use){
			tresholdRatio = tresholdRatioBase;
			pairWisePairingTreshold = pairWisePairingTresholdBase; 
		}
		else{
			tresholdRatio =  BigDecimal.ZERO; 
			pairWisePairingTreshold = BigDecimal.ZERO; 
		}
	}
	
	public static ArrayList<Tuple> generateTuplePartially(ArrayList<Model> models, TupleExaminer examiner){
		ArrayList<Tuple> retVal = new ArrayList<Tuple>();
		generateTuplePartially(models, 0, examiner,retVal  , new Tuple());
		return retVal;
	}
	
	private static boolean generateTuplePartially(ArrayList<Model> models,int currModelIndex,  TupleExaminer examiner, ArrayList<Tuple> accum, Tuple t){
		int numOfModels = models.size();
		if(currModelIndex == numOfModels)
			return true;
		Model m = models.get(currModelIndex);
		ArrayList<Element> elems = m.getElementsSortedByLabel();
		Element e;
		for(int i=0;i<=elems.size();i++){
			if(i==elems.size())
				e = null;
			else
				e = elems.get(i);
			Tuple created = (e==null)?t:t.newExpanded(e, models);
			if(e!=null && examiner.examine(created))
				accum.add(created);
			if(!examiner.proceedWithTupleExpansion(created))
				continue;
			if(!generateTuplePartially(models, currModelIndex+1, examiner, accum, created))
				return false;
		}
		return true;
	}

	public static ArrayList<Tuple> generateAllTuples(ArrayList<Model> models){
		return generateAllTuples(models, true);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Tuple> generateAllTuples(ArrayList<Model> models, boolean generateGraph) {
		//if(tuplesMemo.get(models) != null){
		//	return (ArrayList<Tuple>) tuplesMemo.get(models).clone();
		//}
		ArrayList<Tuple> lst = new ArrayList<Tuple>();
		int numOfModels = 0;
		for(Model m:models){
			numOfModels += m.getMergedFrom();
		}
		lst.add(new Tuple());
	//	try {
		for(Model m: models){
			for(int i=0, l = lst.size(); i<l ;i++){
				Tuple t = lst.get(i);
				ArrayList<Element> elems = m.getElements();
				for(Element e: elems){
					Tuple created = t.newExpanded(e,models);
					lst.add(created);
				}
			}
		}
		filterAndSetScaledWeight(lst, tresholdRatio.divide(new BigDecimal(numOfModels, N_WAY.MATH_CTX),  N_WAY.MATH_CTX));
	//	long startTime = System.currentTimeMillis();
		//if(models.size() <= 3 && generateGraph) prepareNeighborhoodGrap(lst);
	//	long endTime = System.currentTimeMillis();
		//long execTime = endTime - startTime;
		//tuplesMemo.put(models, lst);
	//	}catch(Throwable t){System.out.println("num of tuples = "+lst.size());}
		return lst; 
	}
	
	public static ArrayList<Tuple> getTuplesOfNonMatchedCompositeElementsFromModel( Model m, HashSet<Element> elementsUsedInMatch) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		for(Element e:m.getElements()){
			if(elementsUsedInMatch.contains(e))
				continue;
			if(e.getBasedUponElements().size() != 1)
				tuples.add(e.getContaingTuple());
		}
		return tuples;
	}
	
	public static void printTuples(ArrayList<Tuple> tuples){
		ArrayList<Tuple> ttt = (ArrayList<Tuple>) tuples.clone();
		Collections.sort(ttt, new TupleComparator());
		System.out.println(ttt);
	}
	
	
	public static void trace(String msg){
		if(TRACE){
			System.out.println(msg);
		}
	}
	
	private static void prepareNeighborhoodGrap(ArrayList<Tuple> lst) {
//		for(int i=0;i<lst.size();i++){
//			Tuple t1 = lst.get(i);
//			Set<Element> e1 = t1.getRealElements();
//			t1.addNeighbour(t1);
//			for(int j=i+1;j<lst.size();j++){
//				Tuple t2 = lst.get(j);
//				Set<Element> e2 = t2.getRealElements();
//				for (Element element : e1) {
//					if(e2.contains(element)){
//						t1.addNeighbour(t2);
//						t2.addNeighbour(t1);
//					}
//				}
//			}
//		}
		
	}
	
	public static boolean shouldCompose(Tuple composed, Element e1, Element e2, ArrayList<Model> models){
		BigDecimal w1 = e1.getContaingTuple().calcWeight(models);
		BigDecimal w2 = e2.getContaingTuple().calcWeight(models);
		return composed.calcWeight(models).compareTo(w1.add(w2, N_WAY.MATH_CTX)) > 0;
	}
	
	// returns true if the tuple should be composed of its elements -> their sum is smaller than the tuple's weight
	public static boolean shouldCompose(Tuple t, ArrayList<Model> mdls){
		HashSet<Element> elements = new HashSet<Element> (t.getElements());
		BigDecimal sumOfComposingElements = BigDecimal.ZERO;
		for(Element e:elements){
			Tuple containing = e.getContaingTuple();
			if(e.getBasedUponElements().size() > 1){
				sumOfComposingElements = sumOfComposingElements.add(containing.calcWeight(mdls), N_WAY.MATH_CTX);
			}
		}
		return sumOfComposingElements.compareTo(t.getWeight()) < 0;
	}
	
	
	public static boolean isNonValidTuple(Tuple t){
		if(t.getWeight().compareTo(BigDecimal.ZERO) == 0)
			return true;
		if(t.getSize() < 2)
			return true;
		if (t.getWeight().compareTo(t.getRelativeThreshold()) <= 0)
			return true;	
		if(haveLowQualityElement(t, PROPERTIES_QUALITY_THRESHOLD))
			return true;
		for(Element e1:t.sortedElements()){
			boolean haveCommonPropsWithRestOfTuple = false;
			for(Element e2:t.sortedElements()){
				if(e1 == e2)
					continue;
				if(haveCommonProperty(e1,e2)){
					haveCommonPropsWithRestOfTuple = true;
					break;
				}
			}
			if(!haveCommonPropsWithRestOfTuple)
				return true;
		}
		return false;
	}

	private static boolean haveLowQualityElement(Tuple t, double elemQualityThreshold) {
		for(Element e:t.getRealElements()){
			HashSet<String> otherProps = new HashSet<String>();
			for(Element e1:t.getRealElements()){
				if(e1 != e){
					otherProps.addAll(e1.getProperties());
				}
			}
			
			double usedPropsCount = 0;
			for(String prp:e.getProperties()){
				if(otherProps.contains(prp))
					usedPropsCount++;
			}
			
			if( ( (double)e.getProperties().size()) /usedPropsCount < elemQualityThreshold  )
				return true;
			
		}
		
		return false;
	}

	private static boolean haveCommonProperty(Element e1, Element e2) {
		Set<String> e1Props = e1.getProperties();
		for(String prp:e2.getProperties()){
			if(e1Props.contains(prp))
				return true;
		}
		return false;
	}

	private static void filterAndSetScaledWeight(ArrayList<Tuple> all, BigDecimal weightTreshold) {
		int numOfTuples = all.size();
		for(int i=all.size()-1; i>=0;i--){
			Tuple t = all.get(i);
			if(t.getSize() < 2 ||(filterGeneration && isNonValidTuple(t)))
				all.remove(i);
			else
				t.setScaledWeight((t.getWeight().multiply(new BigDecimal(100*numOfTuples))).longValue());
		}
		//System.out.println(all.size());
	}

	public static BigDecimal truncateWeight(BigDecimal w){
		long tmp = w.multiply(new BigDecimal(100000, N_WAY.MATH_CTX), N_WAY.MATH_CTX).longValue();
		return new  BigDecimal(tmp).divide(new BigDecimal(100000, N_WAY.MATH_CTX), N_WAY.MATH_CTX);
	}
	
	public static void sortTuples(ArrayList<Tuple> tuples){
		Collections.sort(tuples, new TupleComparator());
	}
	
	public static class TupleComparator implements Comparator<Tuple> {
	    
	    private boolean asc = false;
	    
	    public TupleComparator(){}
	    public TupleComparator(boolean asc){this.asc = asc;}
	    @Override
	    public int compare(Tuple t1, Tuple t2) {
	    	int comparisonResult = t1.getWeight().compareTo(t2.getWeight());
	        if(!asc)
	        	return -comparisonResult;
	        else
	        	return comparisonResult;
	    }
	}
	
	public static BigDecimal calcGroupWeight(Collection<Tuple> c, boolean scaled){
		BigDecimal res = BigDecimal.ZERO;
		for (Iterator<Tuple> iterator = c.iterator(); iterator.hasNext();) {
			Tuple t = (Tuple) iterator.next();
			if(scaled)
				res = res.add(new BigDecimal(t.getScaledWeight(), N_WAY.MATH_CTX),N_WAY.MATH_CTX);
			else
				res = res.add(t.getWeight(),N_WAY.MATH_CTX);
		}
		return res;
	}
	
	public static String getLexicographicRepresentation(Element elem){
		ArrayList<String> mdls = new ArrayList<String>();
		for(Element e:elem.getBasedUponElements()){
			mdls.add(e.getModelId());
		}
		Collections.sort(mdls);
		return mdls.toString();
	}
	
	public static boolean areNeighbours(Tuple t1, Tuple t2) {
		return t1.isNeighborOf(t2);
	}

	public static BigDecimal calcGroupWeight(ArrayList<Tuple> tuples) {
		return calcGroupWeight(tuples, false);
	}

	public static ArrayList<HungarianMerger> generateAllModelPairs(ArrayList<Model> models) {
		ArrayList<HungarianMerger> pairs = new ArrayList<HungarianMerger>();
		for(int i=0;i<models.size();i++){
			for(int j=i+1;j<models.size();j++){
				HungarianMerger hm = new HungarianMerger(models.get(i), models.get(j), models.size());
				pairs.add(hm);
				hm.runPairing();
			}
		}
		return pairs;
	}
	
	
	public static ArrayList<Model> getModelsByCohesiveness(ArrayList<Model> models,final boolean asc){
		ArrayList<HungarianMerger> merges = generateAllModelPairs(models);
		HashMap<Model, BigDecimal> acccumMap = new HashMap<Model, BigDecimal>();
		HashMap<Model, BigDecimal> cntMap = new HashMap<Model, BigDecimal>();
		for(HungarianMerger hm:merges){
			//BigDecimal avgW = hm.getWeight(); // cohesiveness is the size of the edge
			BigDecimal avgW = (hm.getWeight().compareTo(BigDecimal.ZERO) == 0)?BigDecimal.ZERO: 
			                       hm.getWeight().divide(new BigDecimal(hm.getTuplesInMatch().size(), N_WAY.MATH_CTX), N_WAY.MATH_CTX  ); // cohesiveness is the avg weight of the edge

			for(Model m:hm.getModels()){
				BigDecimal accumedWeight = acccumMap.get(m);
				BigDecimal cnt = cntMap.get(m);
				if(accumedWeight == null){
					accumedWeight = BigDecimal.ZERO;
					cnt = BigDecimal.ZERO;
				}
				acccumMap.put(m,accumedWeight.add(avgW,N_WAY.MATH_CTX));
				cntMap.put(m, cnt.add(BigDecimal.ONE));
			}
		}
		@SuppressWarnings("unchecked")
		ArrayList<Model> retVal = (ArrayList<Model>) models.clone();
		HashMap<Model, BigDecimal> avgWeightOfModel = new HashMap<Model, BigDecimal>();
		for(Model m:models){
			avgWeightOfModel.put(m, acccumMap.get(m).divide(cntMap.get(m), N_WAY.MATH_CTX) );
		}
		@SuppressWarnings("unchecked")
		final HashMap<Model, BigDecimal> avgWeight = (HashMap<Model, BigDecimal>) avgWeightOfModel.clone();
		Collections.sort(retVal, new Comparator<Model>() {

			@Override
			public int compare(Model m1, Model m2) {
				if(asc){
					return (avgWeight.get(m1).compareTo(avgWeight.get(m2)) < 0)?-1:1;
				}else{
					return (avgWeight.get(m1).compareTo(avgWeight.get(m2)) <0)?1:-1;
				}
					
			}
		});
		return retVal;
	}

	public static void reset() {
	}
	
	public static String nameOfMergeDescription(MergeDescriptor md, int splitSize){
		String res = "";
		if(md.algPolicy == N_WAY.ALG_POLICY.PAIR_WISE) res = "PW, ";
		if(md.algPolicy == N_WAY.ALG_POLICY.GREEDY) res = "G";
		if(md.algPolicy == N_WAY.ALG_POLICY.REPLACE_FIRST) res = "LS, ";
		if(md.algPolicy == N_WAY.ALG_POLICY.REPLACE_BEST) res = "GTLS, ";
		if(splitSize > 2)
			res = res+splitSize+", ";
		if(md.orderBy == N_WAY.ORDER_BY.MODEL_SIZE)
			res = res+"size ";
		else if(md.orderBy == N_WAY.ORDER_BY.COHESIVENESS)
			res = res+"cohesiveness ";
		else if(md.orderBy == N_WAY.ORDER_BY.MATCH_QUALITY)
			res = res+"Best Match ";
		else if(md.orderBy == N_WAY.ORDER_BY.SPARSITY)
			res = res+"Most sparse ";
		else if(md.orderBy == N_WAY.ORDER_BY.MODEL_ID)
			res = res+"by id ";
		
		if(md.asc)
			return res+"ascending";
		else
			return res+"descending";
	}
	
}
