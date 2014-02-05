
/**
*	EWMA - Exponential Weighted Moving Average Filter Implementation
*
*
*/
package com.i2r.filter;
/**
* @brief
*/
public class ExpWeightedMovingAvgFilter{
	private int NumSamples;
	private long seqNum;
	private double meanData;
	private boolean accumInData;
	private double alpha;
	private double one_alpha;
	private double filterData;
	public ExpWeightedMovingAvgFilter(){
		NumSamples = 10;
		seqNum = 0L;
		accumInData = true;
		meanData = 0.0;
		alpha = 0.0;
		one_alpha = 0.0;
		filterData = 0.0;
	}
	public ExpWeightedMovingAvgFilter(int samples){
		NumSamples = samples;
		seqNum = 0L;
		accumInData = true;
		meanData = 0.0;
		alpha = 0.0;
		one_alpha = 0.0;
		filterData = 0.0;
	}
	//Sample Size
	public void setSamplesSize(int block_size){	NumSamples = block_size;	}
	public int getSamplesSize(){	return NumSamples;	}
	public double getAlpha(){return alpha;}
	public double getOne_Alpha(){	return one_alpha;	}
	//find alpha and (1 - alpha)
	public void findWeights(){
		alpha = NumSamples/(NumSamples + 1.0);
		one_alpha = 1.0/(NumSamples + 1.0);
	}
	public double filterData(double inData){
		double outData = inData;
		seqNum++;
		if(accumInData){
			if(seqNum < NumSamples){
				meanData += inData;
				filterData = 0.0;
			}else if(seqNum == NumSamples){
				meanData += inData;
				meanData /= NumSamples;
				findWeights();
				filterData = alpha*meanData + one_alpha*inData;
				accumInData = false;
			}
		}else{
			filterData = alpha*filterData + one_alpha*inData;
			outData = filterData;
		}
		
		return outData;
	}
}