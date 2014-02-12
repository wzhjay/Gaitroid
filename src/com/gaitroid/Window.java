package com.gaitroid;

public class Window {
	
	// define window size
	public final static int windowSize = 100;
	public final static int windowOverlapSize = 75;  // 25% overlap, 6 windows per buffer (475)

	// all accelerometer sensor
	public double[] acel_left_x;
	public double[] acel_left_y;
	public double[] acel_left_z;
	
	public double[] acel_right_x;
	public double[] acel_right_y;
	public double[] acel_right_z;
	
	// all gyroscope sensors
	public double[] gyro_left_x;
	public double[] gyro_left_y;
	public double[] gyro_left_z;
	
	public double[] gyro_right_x;
	public double[] gyro_right_y;
	public double[] gyro_right_z;

	// all Magnetometer sensors
	public double[] mag_left_x;
	public double[] mag_left_y;
	public double[] mag_left_z;

	public double[] mag_right_x;
	public double[] mag_right_y;
	public double[] mag_right_z;

	// all FSR sensors
	public double[] fsr_left_front;
	public double[] fsr_left_back;

	public double[] fsr_right_front;
	public double[] fsr_right_back;

	public Window() {
		// all accelerometer sensors
		acel_left_x = new double[windowSize];
		acel_left_y = new double[windowSize];
		acel_left_z = new double[windowSize];
		
		acel_right_x = new double[windowSize];
		acel_right_y = new double[windowSize];
		acel_right_z = new double[windowSize];
		
		// all gyroscope sensors
		gyro_left_x = new double[windowSize];
		gyro_left_y = new double[windowSize];
		gyro_left_z = new double[windowSize];
		
		gyro_right_x = new double[windowSize];
		gyro_right_y = new double[windowSize];
		gyro_right_z = new double[windowSize];

		// all Magnetometer sensors
		mag_left_x = new double[windowSize];
		mag_left_y = new double[windowSize];
		mag_left_z = new double[windowSize];

		mag_right_x = new double[windowSize];
		mag_right_y = new double[windowSize];
		mag_right_z = new double[windowSize];

		// all FSR sensors
		fsr_left_front = new double[windowSize];
		fsr_left_back = new double[windowSize];

		fsr_right_front = new double[windowSize];
		fsr_right_back = new double[windowSize];
	}
}
