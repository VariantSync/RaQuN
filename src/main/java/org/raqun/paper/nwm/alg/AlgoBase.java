package org.raqun.paper.nwm.alg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.raqun.paper.nwm.execution.RunResult;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Tuple;

public abstract class AlgoBase implements Matchable{

	protected ArrayList<Tuple> result;
	protected long execTime;
	protected String name;
	public static long ms2s = 1000;
	public static long ms2min = ms2s*60;
	public static long ms2h = ms2min*60;
	public static long ms2day = ms2h*24;
	public static long ms2year = ms2day*365;
	private boolean executed = false;
	
	
	public AlgoBase(String nm){
		name = nm;
	}
	
	protected abstract ArrayList<Tuple> doRun();
	
	public ArrayList<Tuple> getTuplesInMatch(){
		return   this.getResult();
	}
	
	public ArrayList<Tuple> getResult(){
		return result;
	}
	
	public void addToExecTime(long addition){
		execTime += addition;
	}
	
	public String getName(){
		return name;
	}
	
	protected void setupTuples(){}
	
	public  ArrayList<Tuple> run(){
		if(executed)
			return result;
		setupTuples();
		long startTime = System.currentTimeMillis();
		result =  doRun();
		//RunResult rrr = new RunResult(0, null, null, result);
		//rrr.setTitle("at algobase 59, after doRun");
		//System.out.println(rrr);
	//	System.out.println(result);
//		ArrayList<Element> allElems = new ArrayList<Element>();
//		for(Model m:getModels()){
//			for(Element e:m.getElements()){
//				allElems.addAll(e.getBasedUponElements());
//			}
//		}
		
		//System.out.println("total elems "+allElems.size());
		
//		ArrayList<Element> coveredElements = new ArrayList<Element>();
//		int coveredCount = 0;
//		for(Tuple t:result){
//			int size = t.getRealElements().size();
//			if( size > 1){
//				coveredElements.addAll(t.getRealElements());
//			}
//		}
		//System.out.println(result);
		//RunResult rr  = new RunResult(-1, BigDecimal.ZERO, BigDecimal.ZERO, result);
		//System.out.println(rrr.toString());
		//System.out.println("covered elems "+coveredElements.size());
		
		//updateResultWithTuplesOfNonMatchedCompositeElements();
		//System.out.println("now there are "+result.size());
		long endTime = System.currentTimeMillis();
		execTime = endTime - startTime;
		executed = true;
		return result;
		
	}
	
	protected void updateResultWithTuplesOfNonMatchedCompositeElements() {
//		HashSet<Element> elementsUsedInMatch = new HashSet<Element>();
	//	for(Tuple t:result){
	//		elementsUsedInMatch.addAll(t.getElements());
	//	}
//		for(Model m:getModels()){
//			result.addAll(AlgoUtil.getTuplesOfNonMatchedCompositeElementsFromModel(m, elementsUsedInMatch));
//		}
	}

	private String formatTime(){
		StringBuilder sb = new StringBuilder();
		long reamining = execTime;
		long numOfYears = reamining / ms2year;
		sb.append("(Years:").append(numOfYears);
		
		reamining = reamining % ms2year;
		long numOfDays = reamining/ms2day;
		sb.append("\tDays:").append(numOfDays);

		reamining = reamining % ms2day;
		long numOfhours = reamining/ms2h;
		sb.append("\tHours:").append(numOfhours);
		
		reamining = reamining % ms2h;
		long numOfMins = reamining/ms2min;		
		sb.append("\tMinutes:").append(numOfMins);
		
		reamining = reamining % ms2min;
		long numOfSecs = reamining/ms2s;
		sb.append("\tSeconds:").append(numOfSecs);

		reamining = reamining % ms2s;
		long numOfMs = reamining;
		sb.append("\tms:").append(numOfMs).append(")");
		return sb.toString();
	}
	
	protected BigDecimal getSolutionWeight(){
		refreshResultTuplesWeight();
		return AlgoUtil.calcGroupWeight(result);
	}
	
	protected void refreshResultTuplesWeight() {}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		ArrayList<Tuple> sortedRes = new ArrayList<Tuple>(result);
		Collections.sort(sortedRes, new AlgoUtil.TupleComparator());
		
		sb.append("************\n").append(name).append(" --> total weight: "+
	          AlgoUtil.truncateWeight(getSolutionWeight())).
	          append(getAdditionalInfo()).append("\n************\nran in ").append(execTime).append(" ms\t").append(formatTime()).append("\n");
		sb.append("result is:\n").append(sortedRes).append("\n");
		return sb.toString();
	}
	
	public long getExecTime(){
		return execTime;
	}
	
	public BigDecimal getResultWeight(){
		return AlgoUtil.truncateWeight(getSolutionWeight());
	}
	
	public BigDecimal getAverageTupleWeight(){
		if(result.size() == 0)
			return BigDecimal.ZERO;
		return AlgoUtil.truncateWeight(getSolutionWeight().divide(new BigDecimal(result.size(), N_WAY.MATH_CTX), N_WAY.MATH_CTX));
	}

	protected String getAdditionalInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tnum of tuples: ").append(result.size());
		sb.append("\taverage tuple weight: ").append(getAverageTupleWeight());
		sb.append("\tScaled weight is: ").append(AlgoUtil.calcGroupWeight(result, true)).append("\n");
		return sb.toString();

	}
	
	public RunResult getRunResult(){
		if(!executed){
			run();
		}
		return new RunResult(execTime, getResultWeight(), getAverageTupleWeight(), result);
	}
	
}