package org.raqun.paper.nwm.alg.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.merge.GreedyMerger;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class BestFoundLocalSearch extends LocalSearch {
	
	private ArrayList<SwapDelta> goodSets;

	public BestFoundLocalSearch(ArrayList<Model> mdls, boolean doSquaring) {
		this("Greedy + Total Local Search",mdls,  new WeightBasedBeneficiallityDecider(doSquaring));
	}
	
	public BestFoundLocalSearch(String nm,ArrayList<Model> mdls, SwapBeneficiallityDecider dec) {
		super(nm,mdls, dec);
	}
	
	
	public BestFoundLocalSearch(boolean doSquaring, ArrayList<Tuple> tpls) {
		this("Greedy + Total Local Search",  new WeightBasedBeneficiallityDecider(doSquaring), tpls);
	}
	
	public BestFoundLocalSearch(String nm, SwapBeneficiallityDecider dec, ArrayList<Tuple> tpls) {
		super(nm, dec, tpls);
	}
	
	protected ArrayList<Tuple> getInitialSolution(){
		@SuppressWarnings("unchecked")
		GreedyMerger gm = new GreedyMerger((ArrayList<Model>) models.clone());
		ArrayList<Tuple> retVal =  gm.extractMerge();
		return retVal;
	}
	
	protected boolean optimizeSolution(ArrayList<Tuple> all,
			ArrayList<Tuple> solution, HashSet<Tuple> ArrayList, int i) {
		goodSets = new ArrayList<SwapDelta>();
		
		findBetterSubgroups(tuples, solution, new ArrayList<Tuple> (), 0);
		if(goodSets.size() == 0)
			return false;
		Collections.sort(goodSets, new Comparator<SwapDelta>() {
			@Override
			public int compare(SwapDelta sd1, SwapDelta sd2) {
				return sd1.getValue().compareTo(sd2.getValue()) < 0? 1: -1;
			}
		});
		ArrayList<Tuple> subgroup = goodSets.get(0).getProposedGroup();
		ArrayList<Tuple> neighbours = goodSets.get(0).getNeighbours();//getNeighboursInSolution(subgroup, solution);// neighbours;//
		swapNeighboursWithSubgroup(neighbours, solution, subgroup, all);
		//System.out.println("Replace Best, curr solution value: " + AlgoUtil.calcGroupWeight(solution) + new Date());
		//System.out.println(AlgoUtil.calcGroupWeight(solution));
		AlgoUtil.trace("\n------\nTook out of the solution the tuples with weight: "+AlgoUtil.calcGroupWeight(neighbours, false) +
				"\nThe removed tuples are:\n"+neighbours+"\nAnd added a group with weight: "+AlgoUtil.calcGroupWeight(subgroup, false)+
				"\nThe added tuples are:\n"+subgroup);
		
		return true;
	}
	
	/* Receiving a *solution* that has several tuples in it, and a list of *all* possible tuples.
	 * Recursively building a subgroup of *all*, starting from the tuple at the index *indexInAll*
	 * We need to find a subgroup of *all* that increases the weight of the solution. In order to do so we need to find the set of tuples in the solution
	 * that should be removed if we add that subgroup (this is the neighbors group). 
	 * If the built subgroup is heavier than the neighbor group, than we swap these groups (the found subgroup and the neighbors).
	 * If the build subgroup is not heavier, than we construct the next subgroup
	 * the field subgroupSize defines the recursion depth (this is the t parameter) - it indicates the size of the subgroup of tuples that is built recursively
	 * 
	 *  */
	
	@SuppressWarnings("unchecked")
	public void findBetterSubgroups(ArrayList<Tuple> all, 
			ArrayList<Tuple> solution, 
			ArrayList<Tuple> subgroup, 
			                                           int indexInAll){
		int currentSubgroupSize = subgroup.size();
		int remainingTuplesToCheck = all.size() - indexInAll;
	//	int remainngTuplesToFillInSubgroup = maxSubgroupSize - currentSubgroupSize;
		
		if(remainingTuplesToCheck == 0)
			return;
		if(currentSubgroupSize <= maxSubgroupSize){ // a fully populate subgroup - need to check if it adds value, if it does, add it to list and return;
			ArrayList<Tuple> neighbors = getNeighboursInSolution(subgroup, solution);
			SwapDelta sd = decider.calcBeneficiallity(solution, subgroup, neighbors);
			if(sd != SwapDelta.NOT_USEFUL){
				goodSets.add(sd);
			}
			if(currentSubgroupSize == maxSubgroupSize) return; // no more exploration with this subgroup
		}
		
		// here we have a subgroup that still can be filled
		
		for(int i=indexInAll;i<all.size();i++){
			Tuple tpl = all.get(i);
			if(!canAddTupleToGroup(tpl, subgroup))
				continue;
			subgroup = (ArrayList<Tuple>) subgroup.clone(); // not to affect others 
			subgroup.add(tpl); // subgroup is not filled yet, need to add and check.
			findBetterSubgroups(all, solution, subgroup, ++indexInAll);
			subgroup = (ArrayList<Tuple>) subgroup.clone(); // the addition may have been used, need to clone
			subgroup.remove(tpl); // 
		}

	}
	
	

}
