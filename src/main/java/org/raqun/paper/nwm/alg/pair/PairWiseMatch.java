package org.raqun.paper.nwm.alg.pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public abstract class PairWiseMatch extends AlgoBase {

	private ArrayList<Model> models;
	private HashSet<Model> modelsInUse;
	private Comparator<HungarianMerger> comparator;
	
	public PairWiseMatch(String name,ArrayList<Model> lst, boolean largerFirst){
		super(name);
		models = lst;
		modelsInUse = new HashSet<Model>(lst);
		comparator = getPolicyComperator(largerFirst);
	}
	
	public ArrayList<Model> getModels(){
		return models;
	}
	
	public abstract Comparator<HungarianMerger> getPolicyComperator(boolean largerFirst);
	@Override
	
	protected ArrayList<Tuple> doRun() {
		//System.out.println("-----------------\nstarting "+name+"\n");
		
		ArrayList<HungarianMerger> pairs = AlgoUtil.generateAllModelPairs(models);
		HungarianMerger best = null;
		Model mergedBest = null;
		
		
		while(modelsInUse.size() > 1){
			Collections.sort(pairs, comparator);
			best = pairs.get(0);
			mergedBest = best.mergeMatchedModels();
			//System.out.println(mergedBest);
			removeUsedModels(pairs, best.getModel1(), best.getModel2());
			ArrayList<HungarianMerger> newPairs = generatePairing(mergedBest, modelsInUse);
			addMergedModel(mergedBest);
			pairs.addAll(0,newPairs);
			//System.out.println(mergedBest);
		} 
		
		boolean storedVal = AlgoUtil.COMPUTE_RESULTS_CLASSICALLY;
		

		//System.out.println("finished "+name+"\n-----------------\n");
		ArrayList<Tuple> realMerge =// AlgoUtil.COMPUTE_RESULTS_CLASSICALLY?
															//best.getTuplesInMatch():
																best.extractMerge();
		AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
		if(storedVal == true){
			refreshTuplesElements(realMerge);
		}
		//System.out.println(realMerge);
		ArrayList<Tuple> retVal = filterTuplesByTreshold(	realMerge);
		
		AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;
		return retVal;
	}
	
	
	private void refreshTuplesElements(ArrayList<Tuple> tuples) {
		for(Tuple t:tuples){
			t.recomputeSelf(this.models);
		}
	}

	public static ArrayList<Tuple> filterTuplesByTreshold(ArrayList<Tuple> tuplesInMatch, ArrayList<Model> mdls) {
		ArrayList<Tuple> retVal = new ArrayList<Tuple>();
		for(Tuple t:tuplesInMatch){
			t.setWeight(t.calcWeight(mdls));
			if(!AlgoUtil.isNonValidTuple(t)){
				retVal.add(t);
			}
		}
		
		//AlgoUtil.printTuples(retVal);
		
		return retVal;
	}
	
	
	private ArrayList<Tuple> filterTuplesByTreshold(ArrayList<Tuple> tuplesInMatch) {
		return filterTuplesByTreshold(tuplesInMatch, models);
	}

	private void addMergedModel(Model mergedBest) {
		modelsInUse.add(mergedBest);
	}
	
	private ArrayList<HungarianMerger> generatePairing(Model merged, HashSet<Model> models) {
		ArrayList<HungarianMerger> newPairs = new ArrayList<HungarianMerger>();
		for(Model m:models){
			HungarianMerger mp = new HungarianMerger(merged, m,models.size());
			mp.runPairing();
			newPairs.add(mp);
		}
		return newPairs;
	}
	
	private void removeUsedModels(ArrayList<HungarianMerger> pairs, Model model1, Model model2) {
		for(int i=pairs.size()-1;i>=0 ;i--){
			HungarianMerger mp = pairs.get(i);
			if(mp.has(model1) || mp.has(model2))
				pairs.remove(i);
		}
		modelsInUse.remove(model1);
		modelsInUse.remove(model2);
		
	}
	
	@Override
	protected void refreshResultTuplesWeight() {
		for(Tuple t:result){
			t.setWeight(t.calcWeight(models));
		}
	}


}
