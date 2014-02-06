package com.gaitroid;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

@SuppressWarnings("deprecation")
public class WindowProcess {
	
	private final int windowSize = Window.windowSize; 
	private FastVector<Attribute> fvWekaAttributes;
	private final int instanceSize = 22;
	private ArrayList<Instances> windowsTobeProcessedQueue = new ArrayList<Instances>();
    
	// weka attributes definition
    //Left
    static Attribute sensor_acl_0_x = new Attribute("Left Accelerometer X"); 
    static Attribute sensor_acl_0_y = new Attribute("Left Accelerometer Y"); 
    static Attribute sensor_acl_0_z = new Attribute("Left Accelerometer Z"); 
    static Attribute sensor_gyr_0_x = new Attribute("Left Gyroscope X"); 
    static Attribute sensor_gyr_0_y = new Attribute("Left Gyroscope Y"); 
    static Attribute sensor_gyr_0_z = new Attribute("Left Gyroscope Z"); 
    static Attribute sensor_mag_0_x = new Attribute("Left Magnetometer X"); 
    static Attribute sensor_mag_0_y = new Attribute("Left Magnetometer Y"); 
    static Attribute sensor_mag_0_z = new Attribute("Left Magnetometer Z"); 
    static Attribute sensor_exp_0_a0 = new Attribute("Left FSR F"); 
    static Attribute sensor_exp_0_a7 = new Attribute("Left FSR B"); 
    
    // Right
    static Attribute sensor_acl_1_x = new Attribute("Right Accelerometer X"); 
    static Attribute sensor_acl_1_y = new Attribute("Right Accelerometer Y"); 
    static Attribute sensor_acl_1_z = new Attribute("Right Accelerometer Z"); 
    static Attribute sensor_gyr_1_x = new Attribute("Right Gyroscope X"); 
    static Attribute sensor_gyr_1_y = new Attribute("Right Gyroscope Y"); 
    static Attribute sensor_gyr_1_z = new Attribute("Right Gyroscope Z"); 
    static Attribute sensor_mag_1_x = new Attribute("Right Magnetometer X"); 
    static Attribute sensor_mag_1_y = new Attribute("Right Magnetometer Y"); 
    static Attribute sensor_mag_1_z = new Attribute("Right Magnetometer Z"); 
    static Attribute sensor_exp_1_a0 = new Attribute("Right FSR F"); 
    static Attribute sensor_exp_1_a7 = new Attribute("Right FSR B");
    
    
	public WindowProcess(ArrayList<Window> windows) {
		
		// weka initialization
		// Declare the class attribute along with its values
		FastVector<String> fvClassVal = new FastVector<String>(2);
		fvClassVal.addElement("normal");
		fvClassVal.addElement("abnormal");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
			       
		fvWekaAttributes = new FastVector<Attribute>(22);
		fvWekaAttributes.addElement(sensor_acl_0_x);    
		fvWekaAttributes.addElement(sensor_acl_0_y);    
		fvWekaAttributes.addElement(sensor_acl_0_z);
		fvWekaAttributes.addElement(sensor_gyr_0_x);    
		fvWekaAttributes.addElement(sensor_gyr_0_y);    
		fvWekaAttributes.addElement(sensor_gyr_0_z);
		fvWekaAttributes.addElement(sensor_mag_0_x);    
		fvWekaAttributes.addElement(sensor_mag_0_y);    
		fvWekaAttributes.addElement(sensor_mag_0_z);
		fvWekaAttributes.addElement(sensor_exp_0_a0);    
		fvWekaAttributes.addElement(sensor_exp_0_a7);
			    
		fvWekaAttributes.addElement(sensor_acl_1_x);    
		fvWekaAttributes.addElement(sensor_acl_1_y);    
		fvWekaAttributes.addElement(sensor_acl_1_z);
		fvWekaAttributes.addElement(sensor_gyr_1_x);    
		fvWekaAttributes.addElement(sensor_gyr_1_y);    
		fvWekaAttributes.addElement(sensor_gyr_1_z);
		fvWekaAttributes.addElement(sensor_mag_1_x);    
		fvWekaAttributes.addElement(sensor_mag_1_y);    
		fvWekaAttributes.addElement(sensor_mag_1_z);
		fvWekaAttributes.addElement(sensor_exp_1_a0);    
		fvWekaAttributes.addElement(sensor_exp_1_a7);
			    
		fvWekaAttributes.addElement(ClassAttribute);
		
		
		for(int i=0; i<windows.size(); i++) {
			processIndividualWindow(windows.get(i));
		}
	}
	
	public void processIndividualWindow(Window w) {
		
		Instances insts = new Instances("GaitroidTest", fvWekaAttributes, windowSize);  // "GaitroidTest" is relaition name
		// form instance
		for(int i=0; i<windowSize; i++) {
			// left
            Instance inst = new DenseInstance(instanceSize);
            inst.setValue(sensor_acl_0_x, w.acel_left_x[i]);
            inst.setValue(sensor_acl_0_y, w.acel_left_y[i]);
            inst.setValue(sensor_acl_0_z, w.acel_left_z[i]);
            
            inst.setValue(sensor_gyr_0_x, w.gyro_left_x[i]);
            inst.setValue(sensor_gyr_0_y, w.gyro_left_y[i]);
            inst.setValue(sensor_gyr_0_z, w.gyro_left_z[i]);
            
            inst.setValue(sensor_mag_0_x, w.mag_left_x[i]);
            inst.setValue(sensor_mag_0_y, w.mag_left_y[i]);
            inst.setValue(sensor_mag_0_z, w.mag_left_z[i]);
            
            inst.setValue(sensor_exp_0_a0, w.fsr_left_front[i]);
            inst.setValue(sensor_exp_0_a7, w.fsr_left_back[i]);
            
            // right
            inst.setValue(sensor_acl_1_x, w.acel_right_x[i]);
            inst.setValue(sensor_acl_1_y, w.acel_right_y[i]);
            inst.setValue(sensor_acl_1_z, w.acel_right_z[i]);
            
            inst.setValue(sensor_gyr_1_x, w.gyro_right_x[i]);
            inst.setValue(sensor_gyr_1_y, w.gyro_right_y[i]);
            inst.setValue(sensor_gyr_1_z, w.gyro_right_z[i]);
            
            inst.setValue(sensor_mag_1_x, w.mag_right_x[i]);
            inst.setValue(sensor_mag_1_y, w.mag_right_y[i]);
            inst.setValue(sensor_mag_1_z, w.mag_right_z[i]);
            
            inst.setValue(sensor_exp_1_a0, w.fsr_right_front[i]);
            inst.setValue(sensor_exp_1_a7, w.fsr_right_back[i]);
            
            // add into instances
            insts.add(inst);
		}
		// append to Queue wiat for classification
		windowsTobeProcessedQueue.add(insts);
	}

	public ArrayList<Instances> getWindowsTobeProcessedQueue() {
		return windowsTobeProcessedQueue;
	}

	public void setWindowsTobeProcessedQueue(
			ArrayList<Instances> windowsTobeProcessedQueue) {
		this.windowsTobeProcessedQueue = windowsTobeProcessedQueue;
	}
}
