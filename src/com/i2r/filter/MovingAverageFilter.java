/**
* Moving Average Filter
* X^k = X^k-1 + 1/N[Xk - Xk-N]
*
*/
package com.i2r.filter;
/**
* @brief
*/
public class MovingAverageFilter{

	private double accumPoints = 0.0;
	private double prevData = 0.0;
	private boolean first_block = true;
	private long pktNum = 0L;
	private int total_Points;
	private CircularBuffer avgPoints;
	public MovingAverageFilter(){
		accumPoints = 0.0;
		prevData = 0.0;
		first_block = true;
		pktNum = 0;
		total_Points = 11;
	}
	public MovingAverageFilter(int block_size){
		total_Points = block_size;
		accumPoints = 0.0;
		prevData = 0.0;
		first_block = true;
		pktNum = 0;
		avgPoints = new CircularBuffer(total_Points);
	}
	public void setBlockSize(int block_size){
		total_Points = block_size;
	}
	public int getBlockSize(){
		return total_Points;
	}
	public double filterData(double inData){
		double outData = 0.0;
		pktNum++;
		if(first_block){
			if( pktNum < (long)total_Points){
				avgPoints.insertData(inData);
				outData = inData;
				//System.out.println("First Block unfilter: inData ["+pktNum+"] = "+inData+"\toutData["+pktNum+"] = "+outData);
			}else if(pktNum == total_Points){
				
				avgPoints.insertData(inData);
				double[] pointsData = avgPoints.getData();
				//for(int i=0;i < total_Points;i++)
				//	System.out.print(pointsData[i]+"\t");
				for(int i=0;i<total_Points;i++)
					accumPoints += pointsData[i];
				outData = accumPoints/total_Points;
				first_block = false;
				//System.out.println("Firt block first filter data inData ["+pktNum+"] = "+inData+"\toutData["+pktNum+"] = "+outData);				
				//System.out.println(inData+"\t"+outData);
			}			
		}else{
			double prevData = avgPoints.insertData(inData);
			//double[] pointsData = avgPoints.getData();
			//for(int i=0;i < total_Points;i++)
			//		System.out.print(pointsData[i]+"\t");
			accumPoints = accumPoints + inData - prevData;
			outData = accumPoints/total_Points;
			//System.out.println("Filtered Data inData ["+pktNum+"] = "+inData+"\toutData["+pktNum+"] = "+outData);
			//System.out.println(inData+"\t"+outData);
		}
		return outData;
	}
	
}