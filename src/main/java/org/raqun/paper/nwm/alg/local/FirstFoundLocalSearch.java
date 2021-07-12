package org.raqun.paper.nwm.alg.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.merge.GreedyMerger;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public class FirstFoundLocalSearch extends LocalSearch {


	public FirstFoundLocalSearch(String nm,ArrayList<Model> mdls,  SwapBeneficiallityDecider dec) {
		super(nm,mdls, dec);
	}
	
	
	public FirstFoundLocalSearch(String nm,  SwapBeneficiallityDecider dec, ArrayList<Tuple> tpls) {
		super(nm, dec, tpls);
	}

	public FirstFoundLocalSearch(ArrayList<Model> mdls,boolean doSquaring) {
		this("Local Search", mdls, new WeightBasedBeneficiallityDecider(doSquaring));
		this.maxSubgroupSize = mdls.size();
	}
	
	public FirstFoundLocalSearch(boolean doSquaring, ArrayList<Tuple> tpls) {
		this("Local Search", new WeightBasedBeneficiallityDecider(doSquaring), tpls);
	}
	
	protected ArrayList<Tuple> getInitialSolution(){
		GreedyMerger gm = new GreedyMerger((ArrayList<Model>) models.clone());
		ArrayList<Tuple> retVal =  gm.extractMerge();
		return retVal;
	}
	
	protected boolean optimizeSolution(ArrayList<Tuple> tuples2,
			ArrayList<Tuple> solution, HashSet<Tuple> hashSet, int i) {
		// TODO Auto-generated method stub
		System.out.println("Replace first, curr solution value: " + AlgoUtil.calcGroupWeight(solution) + new Date());
		boolean optimized = replaceFirstValidSubgroupInSolution(tuples, solution, new ArrayList<Tuple> (), 0);
		//System.out.println("Replace first, curr solution value: " + AlgoUtil.calcGroupWeight(solution) + new Date());
		return optimized;
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
	
	public boolean replaceFirstValidSubgroupInSolution(ArrayList<Tuple> all, 
			ArrayList<Tuple> solution, 
			ArrayList<Tuple> subgroup, 
			                                           int indexInAll){
		int currentSubgroupSize = subgroup.size();
		int remainingTuplesToCheck = all.size() - indexInAll;
		int remainngTuplesToFillInSubgroup = maxSubgroupSize - currentSubgroupSize;
		if( remainngTuplesToFillInSubgroup > remainingTuplesToCheck ) //we cannot feel the subgroup with the remaining tuples at *all* - returning without success
			return false;
		
		
		if(currentSubgroupSize <= maxSubgroupSize){ 
			// at this point we know that the subgroup is a valid one, need to check if it is also beneficial
			ArrayList<Tuple> neighbors = getNeighboursInSolution(subgroup, solution);
			SwapDelta sd = decider.calcBeneficiallity(solution, subgroup, neighbors);
			if(sd != SwapDelta.NOT_USEFUL){
				// found a beneficial subgroup - so let's swap and restart the recursion
				AlgoUtil.trace("\n------\nTook out of the solution the tuples with weight: "+AlgoUtil.calcGroupWeight(neighbors, false) +
						"\nThe removed tuples are:\n"+neighbors+"\nAnd added a group with weight: "+AlgoUtil.calcGroupWeight(subgroup, false)+
						"\nThe added tuples are:\n"+subgroup);
				swapNeighboursWithSubgroup(neighbors, solution, subgroup, all);
				return true;
			}
		}
		if(currentSubgroupSize == maxSubgroupSize){ // we cannot continue working with this subgroup and it is not beneficial - returning without success
				return false;
		}
		// at this point we have a subgroup that can still be extended, so we try to do it
		for(int i=indexInAll;i<all.size();i++){
			Tuple tpl = all.get(i);
			if(!canAddTupleToGroup(tpl, subgroup))
				continue;
			subgroup.add(tpl); // subgroup is not filled yet, need to add and check.
			if(replaceFirstValidSubgroupInSolution(all, solution, subgroup, ++indexInAll))
				return true;
			else
				subgroup.remove(tpl); // the addition was not beneficial, removing it
		}
		return false;
	}
}
