package com.i2r.filter;

import java.io.*;

public class CircularBuffer{
	private int buffer_size;
	private int size_mask;
	private int head;
	private double[] dataArray;
	public CircularBuffer(int sample_points){
		buffer_size = sample_points;
		size_mask = buffer_size - 1;
		head = 0;
		dataArray = new double[buffer_size];
	}
	public double[] getData(){
		return dataArray;
	}
	public double insertData(double val){
		int dummy=0;
		double lastValue = 0.0;
		dummy = head;
		if(dummy+1 < buffer_size)
			head = dummy+1;
		else
			head = 0;
		//head = ((dummy+1) & size_mask);
		//System.out.println("head is "+head);
		lastValue = dataArray[dummy];
		//System.out.println("last value is "+lastValue);
		dataArray[dummy] = val;
		return lastValue;
	}
	public void displayBuffer(){
		for(int i=0;i < buffer_size; i++)
			System.out.print(dataArray[i]+"\t");
		System.out.println("\n");
	}
	public static void main(String[] args){
		CircularBuffer test = new CircularBuffer(5);
		BufferedReader stdin = new BufferedReader( new InputStreamReader( System.in ));
		int i=0;
		double number = 0.0;
		try{
		do{
			String input = stdin.readLine();
			number= Double.parseDouble(input);	
			double removedVal = test.insertData(number);
			if(++i > test.buffer_size){
				test.displayBuffer();
				System.out.println("Removing from buffer "+removedVal);
				System.out.println("Adding to Buffer "+number);
			}
		}while(number != -1.0);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}