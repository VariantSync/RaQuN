package org.raqun.paper.nwm.common;

public class NeighborhoodGraph {
	
	private byte[] connectionBitMap;
	
	private static final byte[] bitsTurnedOnAtIndex = new byte[]{1,2, 4,8, 16,  32, 64, -128}; // based on doing OR of the right int and the element
	private static final byte[] bitsTurnedOffAtIndex = new byte[]{-2, -3, -5, -9, -17, -33, -65, 127}; // based on doing AND of the right int and the element

	public NeighborhoodGraph(int size){
		int numOfConnections = size*(size+1)/2;
		int numOfBytesNeeded = numOfConnections/8;
		numOfBytesNeeded = (numOfConnections % 8 == 0)?numOfBytesNeeded:numOfBytesNeeded+1;
		this.connectionBitMap = new byte[numOfBytesNeeded];
	}
	
	// the connection between two elements is marked at the cell
	// the larger index of the pair is the row number, the smaller number is the column number

	private int bitIndexOf(int ind1, int ind2){
		int row = (ind1 > ind2)?ind1:ind2;
		int col = (ind1 < ind2)?ind1:ind2;
		int positionOfBit = row*(row-1)/2 + col;
		return positionOfBit;
	}
	
	public static void main(String args[]){
		int size = 4;
		NeighborhoodGraph ng = new NeighborhoodGraph(size);
		
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				ng.setConnection(i, j, true);
				if(!ng.areConnected(i, j)){
					System.out.println("i=" +i +"  , j="+j+ "are not connected !!!");
				}
			}
		}
		
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				ng.setConnection(i, j, false);
				if(ng.areConnected(i, j)){
					System.out.println("i=" +i +"  , j="+j+ "are connected !!!");
				}
			}
		}
		
		System.out.println("done");
		
//		byte b = 1;
//		for(int i=0;i<8;i++){
//			System.out.println("for i=" + i+" , val is "+(~b));
//			b = (byte) (b << 1);
//		}
	}
	
	public void setConnection(int ind1, int ind2, boolean areConnected){
		if(ind1 == ind2)
			return;
		int bitIndex = bitIndexOf(ind1, ind2);
		byte b = connectionBitMap[bitIndex / 8];
		int indexInsideByte = bitIndex % 8;
		if(areConnected)
			connectionBitMap[bitIndex / 8]  = (byte) (b | bitsTurnedOnAtIndex[indexInsideByte]);
		else
			connectionBitMap[bitIndex / 8] =  (byte) (b & bitsTurnedOffAtIndex[indexInsideByte]);
		if(areConnected != this.areConnected(ind1, ind2))
			System.out.println("PROBLEM!!");
	}
	
	public boolean areConnected(int ind1, int ind2){
		if (ind1 == ind2) return true;
		int bitIndex = bitIndexOf(ind1, ind2);
		byte b = connectionBitMap[bitIndex / 8];
		int indexInsideByte = bitIndex % 8;
		boolean isOn = ((b & bitsTurnedOnAtIndex[indexInsideByte]) != 0);
		return isOn;
		
	}

}
