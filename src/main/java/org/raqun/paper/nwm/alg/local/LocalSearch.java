package org.raqun.paper.nwm.alg.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.raqun.paper.nwm.alg.AlgoBase;
import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.common.NeighborhoodGraph;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;

public abstract class LocalSearch extends AlgoBase implements Matchable{
	
	protected int maxSubgroupSize;
	protected ArrayList<Tuple> tuples;
	protected ArrayList<Model> models;
	protected SwapBeneficiallityDecider decider;
	private NeighborhoodGraph neighborhoodGraph;
	public static BigDecimal MIN_DELTA = new BigDecimal("0.001",N_WAY.MATH_CTX);
	
	private HashMap<Tuple, Integer> tupleToIndex = new HashMap<Tuple, Integer>();

	public LocalSearch(String name, SwapBeneficiallityDecider dec) {
		super(name);
		maxSubgroupSize = 2;
		decider = dec;
	}
	public LocalSearch(String name,ArrayList<Model> mdls, SwapBeneficiallityDecider dec) {
		this(name, dec);
		models = mdls;
		
	}
	
	public LocalSearch(String name,SwapBeneficiallityDecider dec, ArrayList<Tuple> tpls) {
		this(name, dec);
		tuples = tpls;
	}
	
	

	protected void setupTuples(){
		if(tuples != null)
			return;
		tuples = AlgoUtil.generateAllTuples(models,false);
		//System.out.println(tuples);

		
		
	}

	@Override
	protected ArrayList<Tuple> doRun() {

		ArrayList<Tuple> solution = getInitialSolution();
		buildNeighborhood(solution);
		AlgoUtil.trace("------------\nStarting "+name+"\n");
		AlgoUtil.trace("initial solution weight is: "+AlgoUtil.calcGroupWeight(solution)+".\nThe solution is:\n"+solution+"\n");
		while(optimizeSolution(tuples, solution, new HashSet<Tuple> (), 0));
		AlgoUtil.trace("Finished "+ name+"\n------------");
		cleanupTuples();
		return solution;
	}
	
	private void buildNeighborhood(ArrayList<Tuple> solution) {
		long startTime = System.currentTimeMillis();
		int numOfTuples = 0;
		for(;numOfTuples<tuples.size();numOfTuples++){
			tupleToIndex.put(tuples.get(numOfTuples), numOfTuples);
		}
		for(Tuple t:solution){
			if(tupleToIndex.get(t) == null){
				tupleToIndex.put(t, numOfTuples++);
			}
		}
		this.neighborhoodGraph = new NeighborhoodGraph(numOfTuples);
		ArrayList<Tuple> tmpTuples = new ArrayList<Tuple>(tupleToIndex.keySet());
		for(int i=0;i<numOfTuples; i++){	
			for(int j=i+1;j<numOfTuples;j++){
				Tuple t1 = tmpTuples.get(i);
				Tuple t2 =  tmpTuples.get(j);
				int indOfT1 = tupleToIndex.get(t1);
				int indOfT2 = tupleToIndex.get(t2);
				boolean areNeighbors = AlgoUtil.areNeighbours(t1, t2);
				neighborhoodGraph.setConnection(indOfT1, indOfT2, areNeighbors);
				
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("time to build graph: "+(endTime-startTime));
	}

	protected abstract boolean optimizeSolution(ArrayList<Tuple> tuples2, ArrayList<Tuple> solution, HashSet<Tuple> hashSet, int i);

	protected abstract ArrayList<Tuple> getInitialSolution();
	
	protected void cleanupTuples(){
		for(Tuple t:tuples){
			t.cleanup();
		}
	}
	
	public ArrayList<Model> getModels(){
		return models;
	}
	
	protected void swapNeighboursWithSubgroup(ArrayList<Tuple> neighbours, ArrayList<Tuple> solution, ArrayList<Tuple> subgroup, ArrayList<Tuple> all) {
		takeSubgroupOutOfGroup(subgroup, all);
		takeSubgroupOutOfGroup(neighbours, solution);
		addSubgroupIntoGroup(subgroup, solution);
		addSubgroupIntoGroup(neighbours, all);
	}

	protected void addSubgroupIntoGroup(ArrayList<Tuple> neighbours, ArrayList<Tuple> all) {
		for (Iterator<Tuple> iterator = neighbours.iterator(); iterator.hasNext();) {
			Tuple t = (Tuple) iterator.next();
			all.add(t);
		}
	}
	
	protected void takeSubgroupOutOfGroup(ArrayList<Tuple> subgroup,
			ArrayList<Tuple> all) {
		for(int i=all.size()-1;i>=0;i--){
			Tuple t = all.get(i);
			if(subgroup.contains(t)){
				all.remove(i);
			}
		}
	}

	protected void addSubgroupIntoGroup(Set<Tuple> subgroup, Set<Tuple> solution) {
		 solution.addAll(subgroup);
	}
	
	protected void takeSubgroupOutOfGroup(Set<Tuple> neighbours, Set<Tuple> solution) {
		solution.removeAll(neighbours);
		
	}

	protected boolean canAddTupleToGroup(Tuple t, ArrayList<Tuple> group) {
		for (Tuple tuple : group) {
			if(areConnected(t,tuple))
				return false;
		}
		return true;
	}
	
	protected boolean areConnected(Tuple t1, Tuple t2){
//		return AlgoUtil.areNeighbours(t1, t2);
		int ind1= tupleToIndex.get(t1);
		int ind2= tupleToIndex.get(t2);
		return neighborhoodGraph.areConnected(ind1, ind2);
	}

	protected ArrayList<Tuple> getNeighboursInSolution(ArrayList<Tuple> subgroup,
			ArrayList<Tuple> solution) {
		HashSet<Tuple> neighbours = new HashSet<Tuple>();
		for (Tuple groupT : subgroup) {
			for (Tuple solT : solution) {
				//boolean tmpFromUtil = AlgoUtil.areNeighbours(solT, groupT);
				//boolean local = areConnected(solT, groupT);
				if(areConnected(solT, groupT))
					neighbours.add(solT);
			}
		}
		return new ArrayList<Tuple>(neighbours);
	}
}