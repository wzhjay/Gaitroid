package com.gaitroid;

public class Instance {
	
	// all accelerometer sensors
	private double acel_left_x;
	private double acel_left_y;
	private double acel_left_z;
	
	private double acel_right_x;
	private double acel_right_y;
	private double acel_right_z;
	
	// all gyroscope sensors
	private double gyro_left_x;
	private double gyro_left_y;
	private double gyro_left_z;
	
	private double gyro_right_x;
	private double gyro_right_y;
	private double gyro_right_z;

	// all Magnetometer sensors
	private double mag_left_x;
	private double mag_left_y;
	private double mag_left_z;

	private double mag_right_x;
	private double mag_right_y;
	private double mag_right_z;

	// all FSR sensors
	private double fsr_left_front;
	private double fsr_left_back;

	private double fsr_right_front;
	private double fsr_right_back;

	public Instance(double a_l_x, double a_l_y, double a_l_z,
					double a_r_x, double a_r_y, double a_r_z,
					double g_l_x, double g_l_y, double g_l_z,
					double g_r_x, double g_r_y, double g_r_z,
					double m_l_x, double m_l_y, double m_l_z,
					double m_r_x, double m_r_y, double m_r_z,
					double f_l_f, double f_l_b,
					double f_r_f, double f_r_b) {
		// acel
		this.acel_left_x = a_l_x;
		this.acel_left_y = a_l_y;
		this.acel_left_z = a_l_z;

		this.acel_right_x = a_r_x;
		this.acel_right_y = a_r_y;
		this.acel_right_z = a_r_z;

		// gyro
		this.gyro_left_x = g_l_x;
		this.gyro_left_y = g_l_y;
		this.gyro_left_z = g_l_z;

		this.gyro_right_x = g_r_x;
		this.gyro_right_y = g_r_y;
		this.gyro_right_z = g_r_z;

		// mag
		this.mag_left_x = m_l_x;
		this.mag_left_y = m_l_y;
		this.mag_left_z = m_l_z;

		this.mag_right_x = m_r_x;
		this.mag_right_y = m_r_y;
		this.mag_right_z = m_r_z;

		// fsr
		this.fsr_left_front = f_l_f;
		this.fsr_left_back = f_l_b;

		this.fsr_right_front = f_r_f;
		this.fsr_right_back = f_r_b;
	}
}
