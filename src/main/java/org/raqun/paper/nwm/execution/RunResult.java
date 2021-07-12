package org.raqun.paper.nwm.execution;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;

import org.raqun.paper.nwm.common.N_WAY;
import org.raqun.paper.nwm.domain.Tuple;

public class RunResult {


	
	public double execTime;
	public BigDecimal weight;
	public BigDecimal avgTupleWeight;
	public String title;
	public double[] bins = new double[20];
	

	public RunResult(double execTime, BigDecimal weight, BigDecimal avgTupleWeight, ArrayList<Tuple> sol) {
		this.execTime = execTime;
		this.weight = weight;
		this.avgTupleWeight = avgTupleWeight;
		updateBins(sol);
	}
	
	public void add(RunResult other){
		execTime+= other.execTime;
		weight = weight.add(other.weight,N_WAY.MATH_CTX);
		avgTupleWeight = avgTupleWeight.add(other.avgTupleWeight,N_WAY.MATH_CTX);
		addBins(other);
	}
	
	private void addBins(RunResult other) {
		for(int i=0;i<20;i++){
			this.bins[i]+= other.bins[i];
		}
		
	}

	public void setTitle(String ttl){
		this.title = ttl;
	}
	
	public void setExecTime(double et){
		this.execTime = et;
	}
	
	public void divideBy(double n){
		execTime /= n;
		weight = weight.divide(new BigDecimal(""+n, N_WAY.MATH_CTX), N_WAY.MATH_CTX);
		avgTupleWeight = avgTupleWeight.divide(new BigDecimal(""+n, N_WAY.MATH_CTX), N_WAY.MATH_CTX);
		for(int i=0;i<20;i++)
			bins[i] /=n;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(title != null)
			sb.append(title).append("\t");
		sb.append("execTime = ").append(execTime).append("\t");
		sb.append("weight = ").append(weight).append("\t");
		sb.append("avgTupleWeight = ").append(avgTupleWeight).append("\n");
		for(int i=2;i<11;i++){
			sb.append(i).append(" -> ").append(bins[i]).append(" , ");
		}
		sb.append("\n");
		return sb.toString();
				
	}
	
	public String toCSV(){
		StringBuilder sb = new StringBuilder();
		if(title!= null)
			sb.append(title).append(",");
		sb.append(weight).append(",").append(avgTupleWeight).append(",").append(execTime).append("\n");
		return sb.toString();
	}
	
	public static void writeExcelHeaderLine(Row row){
		row.createCell(0).setCellValue("Name");
		row.createCell(1).setCellValue("Weight");
		row.createCell(2).setCellValue("Average tuple weight");
		row.createCell(3).setCellValue("run time");
	}
	
	public void toExcel(Row r){
		if(title != null){
			r.createCell(0).setCellValue(title);
		}
		r.createCell(1).setCellValue(weight.doubleValue());
		r.createCell(2).setCellValue(avgTupleWeight.doubleValue());
		r.createCell(3).setCellValue(execTime);
	}

	public void updateBins(ArrayList<Tuple> res) {
		this.bins = new double[100];
		for(Tuple t:res){
			bins[t.getRealElements().size()]++;
		}
		
	}
	
}
