package org.raqun.paper.nwm.common;

import java.util.Comparator;

import org.raqun.paper.nwm.alg.merge.HungarianMerger;
import org.raqun.paper.nwm.domain.Model;

public class ModelIdComparator implements Comparator<HungarianMerger> {
	
	private  boolean largerFirst;

	public ModelIdComparator(boolean largerFirst) {
		this.largerFirst = largerFirst;
	}

	@Override
	public int compare(HungarianMerger hm1, HungarianMerger hm2) {
		 Model big1 =getLargerModel(hm1);
		 Model big2 = getLargerModel(hm2);
		 Model small1 =getSmallerModel(hm1);
		 Model small2 = getSmallerModel(hm2);

		 if(largerFirst){
			 if(big1.getId().compareTo(big2.getId()) >0)
				 return -1;
			if(big1.getId().equals(big2.getId())){
				if(small1.getId().compareTo(small2.getId())>0)
					return -1;
				return 1;
			}
			return 1;
		 }
		 else{
			 if(small1.getId().compareTo(small2.getId()) <0)
				 return -1;
			if(small1.getId() == small2.getId()){
				if(big1.getId().compareTo(big2.getId()) <0)
					return -1;
				return 1;
			}
			return 1;
		 }
	}

	private Model getSmallerModel(HungarianMerger hm){
		if(hm.getModel1().getId().compareTo(hm.getModel2().getId()) <0)
			return hm.getModel1();
		return hm.getModel2();
	}

	private Model getLargerModel(HungarianMerger hm){
		if(hm.getModel1().getId().compareTo(hm.getModel2().getId()) >0)
			return hm.getModel1();
		return hm.getModel2();
	}
}