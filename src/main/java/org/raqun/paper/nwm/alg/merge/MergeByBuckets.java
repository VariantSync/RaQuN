package org.raqun.paper.nwm.alg.merge;

import java.util.ArrayList;
import java.util.HashSet;

import org.raqun.paper.nwm.alg.Matchable;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;

public class MergeByBuckets extends Merger{

	ArrayList<Model> models;
	
	private int maxBucketCapacity ;
	private MergeDescriptor desc;

	private ArrayList<HungarianMerger> allPairs;

	public MergeByBuckets(ArrayList<Model> models, int maxBucketCapacity, MergeDescriptor desc) {
		this.models = new ArrayList<Model>();
		for(Model m:models){
			this.models.add(m.clone());
		}
		this.maxBucketCapacity = maxBucketCapacity;
		this.desc = desc;
	}
	
	
	private HungarianMerger getPairOf(String id1, String id2){
		for(HungarianMerger pair:allPairs){
			if(pair.isOf(id1, id2))
				return pair;
		}
		return null;
	}
	
	private Model extractAndMergeBucketLine(){
		ArrayList<Model> bucketLine = createInitialBucketLine();
		for(int i=0;i<models.size();i++){
			moveElementsToBucketLineBasedOn(bucketLine, i);
		}
		return mergeBucketLine(bucketLine);
	}

	private Model mergeBucketLine(ArrayList<Model> bucketLine) {
		ArrayList<Model> relevantBuckets = new ArrayList<Model>();
		for(Model bucket:bucketLine){
			if(bucket.size() != 0){
				relevantBuckets.add(bucket);
			}
		}
		
		if(relevantBuckets.size() == 0){
			return null;
		}
		
		if(relevantBuckets.size() == 1){
			return relevantBuckets.get(0);
		}
		
		if(relevantBuckets.size() == 2){
			return mergedTwoBuckets(relevantBuckets);
		}
		
		Merger merger = null;
		if(desc.algPolicy == N_WAY.ALG_POLICY.GREEDY)
			merger = new GreedyMerger(relevantBuckets);
		else if(desc.algPolicy == N_WAY.ALG_POLICY.PAIR_WISE)
			merger = new PairWiseMerger(relevantBuckets,desc, true);
		else
			merger = new LocalSearchMerger(relevantBuckets,  desc.algPolicy, false);
		return merger.mergeMatchedModels();
		
	}

	private Model mergedTwoBuckets(ArrayList<Model> relevantBuckets) {
		@SuppressWarnings("unchecked")
		PairWiseMerger pwm = new PairWiseMerger((ArrayList<Model>) relevantBuckets.clone(), desc, true);
		pwm.run();
		return pwm.mergeMatchedModels();
	}

	private boolean moveElementsToBucketLineBasedOn(ArrayList<Model> bucketLine, int indexOfBucketToBaseOn) {
		boolean addToBucket = false;
		Model bucketToBaseOn = bucketLine.get(indexOfBucketToBaseOn);
		HashSet<Element> usedElements = fillBucketFromOwnModel(bucketToBaseOn,indexOfBucketToBaseOn);
		if(usedElements.size() == 0)
			return false;
		for(int i=indexOfBucketToBaseOn+1;i<models.size();i++){
			addToBucket = fillBucket(bucketLine.get(i), usedElements, i, indexOfBucketToBaseOn) || addToBucket ;
		}
		return addToBucket;
	}

	private boolean fillBucket(Model bucketToFill, HashSet<Element> usedElementsAtBasedOn, int indexOfBucketToFill, int indexOfBucketToBaseOn) {
		int bucketSize = bucketToFill.size();
		if(bucketSize == maxBucketCapacity)
			return false;
		ArrayList<Element> freeElements = models.get(indexOfBucketToFill).getElementsNotFoundIn(bucketToFill);
		if(freeElements.size() == 0)
			return false;
		return fillBucketBasedOnPairing(bucketToFill, indexOfBucketToFill, indexOfBucketToBaseOn, usedElementsAtBasedOn, freeElements);
	}

	private boolean fillBucketBasedOnPairing(Model bucketToFill, 
											int indexOfBucketToFill, int indexOfBucketToBaseOn, 
											HashSet<Element> usedElementsAtBasedOn, ArrayList<Element> freeElements) {
		boolean added = false;
		HungarianMerger pair = getPairOf(models.get(indexOfBucketToBaseOn).getId(), models.get(indexOfBucketToFill).getId());
		int openSlotsAtBucket = maxBucketCapacity - bucketToFill.size();
		Model fromModel = models.get(indexOfBucketToFill);
		for(int i=0;i<freeElements.size() && openSlotsAtBucket > 0;i++){
			Element e = freeElements.get(i);
			if(getTupleOfElementTargetOfMatchedElements(e, pair, usedElementsAtBasedOn) != null){
				bucketToFill.addElement(e);
				fromModel.removeElement(e);
				openSlotsAtBucket--;
				added = true;
			}
		}
		return added;
	}

	private Tuple getTupleOfElementTargetOfMatchedElements(Element e, HungarianMerger pair, HashSet<Element> usedElementsAtBasedOn) {
		ArrayList<Tuple> tuples = pair.getTuplesInMatch();
		for(Tuple t:tuples){
			if(t.hasElement(e)){
				for(Element usedAtBase:usedElementsAtBasedOn){
					if(t.hasElement(usedAtBase))
						return t;
				}
				return null;
			}
		}
		return null;
	}

	private HashSet<Element> fillBucketFromOwnModel(Model bucket,int indexOfBucketToFill) {
		ArrayList<Element> freeElements = models.get(indexOfBucketToFill).getElementsNotFoundIn(bucket);
		int emptySlotsAtBucket = maxBucketCapacity - bucket.size();
		HashSet<Element> elementsUsed = new HashSet<Element>();
		Model fromModel = models.get(indexOfBucketToFill);
		for(int i=0; i<freeElements.size() && emptySlotsAtBucket > 0 ; i++, emptySlotsAtBucket--){
			Element e = freeElements.get(i);
			bucket.addElement(e);
			elementsUsed.add(e);
			fromModel.removeElement(e);
		}
		return elementsUsed;
	}

	private ArrayList<Model> createInitialBucketLine() {
		ArrayList<Model> line = new ArrayList<Model>();
		for(int i=0;i<models.size();i++){
			Model bucket = new Model("-"+models.get(i).getId());
			line.add(bucket);
		}
		return line;
	}

	public Model run(String resModelId){
		allPairs = AlgoUtil.generateAllModelPairs(models);
		Model result = new Model(resModelId);
		//Model seed = extractSeed(models);
		while(true){
			Model merged = extractAndMergeBucketLine();
			if(merged == null)
				break;
			result.absorb(merged);
		}
		return result;
	}


	@Override
	protected Matchable getMatch() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RunResult getRunResult(int numOfModels) {
		// TODO Auto-generated method stub
		return null;
	}
}
