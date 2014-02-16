package com.gaitroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

@SuppressWarnings("deprecation")
public class WindowProcess {
	
	private final int windowSize = Window.windowSize; 
	private FastVector<Attribute> fvWekaAttributes;
	private final int instanceSize = 22;
	private ArrayList<Instances> windowsTobeProcessedQueue = new ArrayList<Instances>();
	PearsonsCorrelation correlation;
	private Evaluation eval;
	private Classifier cls;
	
	// weka attributes definition
    //Left
    static Attribute sensor_acl_0_x = new Attribute("Left Accelerometer X"); 
    static Attribute sensor_acl_0_y = new Attribute("Left Accelerometer Y"); 
    static Attribute sensor_acl_0_z = new Attribute("Left Accelerometer Z"); 
    static Attribute sensor_gyr_0_x = new Attribute("Left Gyroscope X"); 
    static Attribute sensor_gyr_0_y = new Attribute("Left Gyroscope Y"); 
    static Attribute sensor_gyr_0_z = new Attribute("Left Gyroscope Z"); 
    static Attribute sensor_exp_0_a7 = new Attribute("Left FSR B"); 
    static Attribute sensor_exp_0_a0 = new Attribute("Left FSR F"); 
    static Attribute sensor_mag_0_x = new Attribute("Left Magnetometer X"); 
    static Attribute sensor_mag_0_y = new Attribute("Left Magnetometer Y"); 
    static Attribute sensor_mag_0_z = new Attribute("Left Magnetometer Z");
    
    // Right
    static Attribute sensor_acl_1_x = new Attribute("Right Accelerometer X"); 
    static Attribute sensor_acl_1_y = new Attribute("Right Accelerometer Y"); 
    static Attribute sensor_acl_1_z = new Attribute("Right Accelerometer Z"); 
    static Attribute sensor_gyr_1_x = new Attribute("Right Gyroscope X"); 
    static Attribute sensor_gyr_1_y = new Attribute("Right Gyroscope Y"); 
    static Attribute sensor_gyr_1_z = new Attribute("Right Gyroscope Z"); 
    static Attribute sensor_exp_1_a7 = new Attribute("Right FSR B");
    static Attribute sensor_exp_1_a0 = new Attribute("Right FSR F"); 
    static Attribute sensor_mag_1_x = new Attribute("Right Magnetometer X"); 
    static Attribute sensor_mag_1_y = new Attribute("Right Magnetometer Y"); 
    static Attribute sensor_mag_1_z = new Attribute("Right Magnetometer Z"); 
    
    
	public WindowProcess(Context ctx) throws Exception {
		correlation = new PearsonsCorrelation();
		// weka initialization
		// Declare the class attribute along with its values
		FastVector<String> fvClassVal = new FastVector<String>(8);
		fvClassVal.addElement("Right_heel_strike");
		fvClassVal.addElement("Right_toe_contact");
		fvClassVal.addElement("Left_toe_off");
		fvClassVal.addElement("Right_heel_off");
		fvClassVal.addElement("Left_heel_strike");
		fvClassVal.addElement("Left_toe_contact");
		fvClassVal.addElement("Right_toe_off");
		fvClassVal.addElement("Left_heel_off");
		Attribute ClassAttribute = new Attribute("class", fvClassVal);
			       
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
		
		// build classifier
		buildClassifier(ctx);
	}
	
	// build classifier	
	public void buildClassifier(Context ctx) throws Exception {
		Log.v("Classifier", "buildClassifier");
		ArffLoader loader = new ArffLoader();
		File f = new File(ctx.getFilesDir(), "train.arff");
		InputStream inputStream = ctx.getResources().openRawResource(R.raw.train);
        FileOutputStream fileOutputStream = new FileOutputStream(f);

        byte buf[]=new byte[1024];
        int len;
        while((len=inputStream.read(buf))>0) {
            fileOutputStream.write(buf,0,len);
        }

        fileOutputStream.close();
        inputStream.close();
        
		Log.v("Classifier", f.length() + "");
		loader.setFile(f);
		Instances train = loader.getStructure();
		Log.v("Classifier", train.toString());
		
		// train classifier
		cls = new J48();
		cls.buildClassifier(train);
				
		// evaluate classifier and print some statistics
		eval = new Evaluation(train);
	}
	
	public void processWindows(ArrayList<Window> windows) {
		for(int i=0; i<windows.size(); i++) {
			processIndividualWindow(windows.get(i));
		}
	}
	
	public void processIndividualWindow(Window w) {
		
		// extract the features before building Instance
		featureExtract(w);
		
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
            Log.v("Instance", inst.toString());
		}
		// append to Queue wiat for classification
		windowsTobeProcessedQueue.add(insts);
	}
	
	public void featureExtract(Window w) {
		// left acel
		double mean_acel_left_x = StatUtils.mean(w.acel_left_x);
		double std_acel_left_x = StatUtils.variance(w.acel_left_x);
		double median_acel_left_x = StatUtils.percentile(w.acel_left_x, 50);
		
		Log.v("Features", "mean_acel_left_x: " + mean_acel_left_x + 
						" std_acel_left_x: " + std_acel_left_x + 
						" median_acel_left_x: " + median_acel_left_x);
		
		double mean_acel_left_y = StatUtils.mean(w.acel_left_y);
		double std_acel_left_y = StatUtils.variance(w.acel_left_y);
		double median_acel_left_y = StatUtils.percentile(w.acel_left_y, 50);

		double mean_acel_left_z = StatUtils.mean(w.acel_left_z);
		double std_acel_left_z = StatUtils.variance(w.acel_left_z);
		double median_acel_left_z = StatUtils.percentile(w.acel_left_z, 50);

		// left gyro
		double mean_gyro_left_x = StatUtils.mean(w.gyro_left_x);
		double std_gyro_left_x = StatUtils.variance(w.gyro_left_x);
		double median_gyro_left_x = StatUtils.percentile(w.gyro_left_x, 50);
		
		double mean_gyro_left_y = StatUtils.mean(w.gyro_left_y);
		double std_gyro_left_y = StatUtils.variance(w.gyro_left_y);
		double median_gyro_left_y = StatUtils.percentile(w.gyro_left_y, 50);

		double mean_gyro_left_z = StatUtils.mean(w.gyro_left_z);
		double std_gyro_left_z = StatUtils.variance(w.gyro_left_z);
		double median_gyro_left_z = StatUtils.percentile(w.gyro_left_z, 50);

		// left mag
		double mean_mag_left_x = StatUtils.mean(w.mag_left_x);
		double std_mag_left_x = StatUtils.variance(w.mag_left_x);
		double median_mag_left_x = StatUtils.percentile(w.mag_left_x, 50);
		
		double mean_mag_left_y = StatUtils.mean(w.mag_left_y);
		double std_mag_left_y = StatUtils.variance(w.mag_left_y);
		double median_mag_left_y = StatUtils.percentile(w.mag_left_y, 50);

		double mean_mag_left_z = StatUtils.mean(w.mag_left_z);
		double std_mag_left_z = StatUtils.variance(w.mag_left_z);
		double median_mag_left_z = StatUtils.percentile(w.mag_left_z, 50);
		
		// left correlation
		double correlation_acel_left_x_y = correlation.correlation(w.acel_left_x, w.acel_left_y);
		double correlation_acel_left_x_z = correlation.correlation(w.acel_left_x, w.acel_left_z);
		double correlation_acel_left_y_z = correlation.correlation(w.acel_left_y, w.acel_left_z);

		double correlation_gyro_left_x_y = correlation.correlation(w.gyro_left_x, w.gyro_left_y);
		double correlation_gyro_left_x_z = correlation.correlation(w.gyro_left_x, w.gyro_left_z);
		double correlation_gyro_left_y_z = correlation.correlation(w.gyro_left_y, w.gyro_left_z);

		double correlation_mag_left_x_y = correlation.correlation(w.mag_left_x, w.mag_left_y);
		double correlation_mag_left_x_z = correlation.correlation(w.mag_left_x, w.mag_left_z);
		double correlation_mag_left_y_z = correlation.correlation(w.mag_left_y, w.mag_left_z);
		
		Log.v("Features", "correlation_acel_left_x_y: " + correlation_acel_left_x_y + 
						 " correlation_acel_left_x_z: " + correlation_acel_left_x_z + 
						 " correlation_acel_left_y_z: " + correlation_acel_left_y_z);
	}

	public ArrayList<Instances> getWindowsTobeProcessedQueue() {
		return windowsTobeProcessedQueue;
	}

	public void setWindowsTobeProcessedQueue(
			ArrayList<Instances> windowsTobeProcessedQueue) {
		this.windowsTobeProcessedQueue = windowsTobeProcessedQueue;
	}
	
	public void WekaClassify(Instances insts) throws Exception {
		Log.v("Classify", "start classify");
		Instances test = insts;
		eval.evaluateModel(cls, test);
		Log.v("Classify", eval.toSummaryString("Results", false));
	}
}
