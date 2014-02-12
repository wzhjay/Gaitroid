package com.gaitroid;

import java.util.ArrayList;

import com.i2r.filter.CircularBuffer;

public class DataStreamBuffer {
	
	// define the buffer size for all buffers and sensors number
	public static final int bufferSize = 475;
	
	// windows within this Stream buffer
	public ArrayList<Window> myWindows;
	
	// left sensors data stream buffer
	public CircularBuffer Acel_Left_X;
	public CircularBuffer Acel_Left_Y;
	public CircularBuffer Acel_Left_Z;
	
	public CircularBuffer Gyro_Left_X;
	public CircularBuffer Gyro_Left_Y;
	public CircularBuffer Gyro_Left_Z;
	
	public CircularBuffer Mag_Left_X;
	public CircularBuffer Mag_Left_Y;
	public CircularBuffer Mag_Left_Z;
	
	public CircularBuffer FSR_Left_F;
	public CircularBuffer FSR_Left_B;
	
	// right sensors data stream buffer
	public CircularBuffer Acel_Right_X;
	public CircularBuffer Acel_Right_Y;
	public CircularBuffer Acel_Right_Z;
	
	public CircularBuffer Gyro_Right_X;
	public CircularBuffer Gyro_Right_Y;
	public CircularBuffer Gyro_Right_Z;
	
	public CircularBuffer Mag_Right_X;
	public CircularBuffer Mag_Right_Y;
	public CircularBuffer Mag_Right_Z;
	
	public CircularBuffer FSR_Right_F;
	public CircularBuffer FSR_Right_B;
	
	
	public DataStreamBuffer() {
		
		myWindows = new ArrayList<Window>();
		
		Acel_Left_X = new CircularBuffer(bufferSize);
		Acel_Left_Y = new CircularBuffer(bufferSize);
		Acel_Left_Z = new CircularBuffer(bufferSize);
		
		Gyro_Left_X = new CircularBuffer(bufferSize);
		Gyro_Left_Y = new CircularBuffer(bufferSize);
		Gyro_Left_Z = new CircularBuffer(bufferSize);
		
		Mag_Left_X = new CircularBuffer(bufferSize);
		Mag_Left_Y = new CircularBuffer(bufferSize);
		Mag_Left_Z = new CircularBuffer(bufferSize);
		
		FSR_Left_F = new CircularBuffer(bufferSize);
		FSR_Left_B = new CircularBuffer(bufferSize);
		
		Acel_Right_X = new CircularBuffer(bufferSize);
		Acel_Right_Y = new CircularBuffer(bufferSize);
		Acel_Right_Z = new CircularBuffer(bufferSize);
		
		Gyro_Right_X = new CircularBuffer(bufferSize);
		Gyro_Right_Y = new CircularBuffer(bufferSize);
		Gyro_Right_Z = new CircularBuffer(bufferSize);
		
		Mag_Right_X = new CircularBuffer(bufferSize);
		Mag_Right_Y = new CircularBuffer(bufferSize);
		Mag_Right_Z = new CircularBuffer(bufferSize);
		
		FSR_Right_F = new CircularBuffer(bufferSize);
		FSR_Right_B = new CircularBuffer(bufferSize);
	}
	
	// after clone a new dataStreamBuffer, call this to create windows list for this data stream
	public ArrayList<Window> cretaeWindows() {
		int windowSize = Window.windowSize;
		int windowOverlapSize = Window.windowOverlapSize;
		int i = 0;
		int newWindowPt = bufferSize - windowSize + windowOverlapSize;
		
		while(i<newWindowPt) {
			Window window = new Window();
			for(int j=0; j<windowSize; j++) {
				// Left
				window.acel_left_x[j] = Acel_Left_X.getData()[i+j];
				window.acel_left_y[j] = Acel_Left_Y.getData()[i+j];
				window.acel_left_z[j] = Acel_Left_Z.getData()[i+j];
				
				window.gyro_left_x[j] = Gyro_Left_X.getData()[i+j];
				window.gyro_left_y[j] = Gyro_Left_Y.getData()[i+j];
				window.gyro_left_z[j] = Gyro_Left_Z.getData()[i+j];
				
				window.mag_left_x[j] = Mag_Left_X.getData()[i+j];
				window.mag_left_y[j] = Mag_Left_Y.getData()[i+j];
				window.mag_left_z[j] = Mag_Left_Z.getData()[i+j];
				
				window.fsr_left_back[j] = FSR_Left_F.getData()[i+j];
				window.fsr_left_front[j] = FSR_Left_B.getData()[i+j];
				
				// Right
				window.acel_right_x[j] = Acel_Right_X.getData()[i+j];
				window.acel_right_y[j] = Acel_Right_Y.getData()[i+j];
				window.acel_right_z[j] = Acel_Right_Z.getData()[i+j];
				
				window.gyro_right_x[j] = Gyro_Right_X.getData()[i+j];
				window.gyro_right_y[j] = Gyro_Right_Y.getData()[i+j];
				window.gyro_right_z[j] = Gyro_Right_Z.getData()[i+j];
				
				window.mag_right_x[j] = Mag_Right_X.getData()[i+j];
				window.mag_right_y[j] = Mag_Right_Y.getData()[i+j];
				window.mag_right_z[j] = Mag_Right_Z.getData()[i+j];
				
				window.fsr_right_back[j] = FSR_Right_F.getData()[i+j];
				window.fsr_right_front[j] = FSR_Right_B.getData()[i+j];
			}
			myWindows.add(window);
			// shift to next window point
			i += windowOverlapSize;
		}
		
		return myWindows;
	}
	
	public void clearWindows() {
		myWindows.clear();
	}
}
