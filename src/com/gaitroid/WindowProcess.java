package com.gaitroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("deprecation")
public class WindowProcess {
	
	private final int windowSize = Window.windowSize; 
	private FastVector<Attribute> fvWekaAttributes;
	private final int instanceSize = 23;
	private ArrayList<Instances> windowsTobeProcessedQueue = new ArrayList<Instances>();
	PearsonsCorrelation correlation;
	private Evaluation eval;
	private Classifier cls;
	private Instances structure;
	private String fileAddress = Environment.getExternalStorageDirectory()+File.separator+"Gaitroid/train.arff";
	
	// weka attributes definition
    //Left
    Attribute sensor_acl_0_x = new Attribute("Left_Accelerometer_X"); 
    Attribute sensor_acl_0_y = new Attribute("Left_Accelerometer_Y"); 
    Attribute sensor_acl_0_z = new Attribute("Left_Accelerometer_Z"); 
    Attribute sensor_gyr_0_x = new Attribute("Left_Gyroscope_X"); 
    Attribute sensor_gyr_0_y = new Attribute("Left_Gyroscope_Y"); 
    Attribute sensor_gyr_0_z = new Attribute("Left_Gyroscope_Z"); 
    Attribute sensor_exp_0_a7 = new Attribute("Left_FSR_B"); 
    Attribute sensor_exp_0_a0 = new Attribute("Left_FSR_F"); 
    Attribute sensor_mag_0_x = new Attribute("Left_Magnetometer_X"); 
    Attribute sensor_mag_0_y = new Attribute("Left_Magnetometer_Y"); 
    Attribute sensor_mag_0_z = new Attribute("Left_Magnetometer_Z");
    
    // Right
    Attribute sensor_acl_1_x = new Attribute("Right_Accelerometer_X"); 
    Attribute sensor_acl_1_y = new Attribute("Right_Accelerometer_Y"); 
    Attribute sensor_acl_1_z = new Attribute("Right_Accelerometer_Z"); 
    Attribute sensor_gyr_1_x = new Attribute("Right_Gyroscope_X"); 
    Attribute sensor_gyr_1_y = new Attribute("Right_Gyroscope_Y"); 
    Attribute sensor_gyr_1_z = new Attribute("Right_Gyroscope_Z"); 
    Attribute sensor_exp_1_a7 = new Attribute("Right_FSR_B");
    Attribute sensor_exp_1_a0 = new Attribute("Right_FSR_F"); 
    Attribute sensor_mag_1_x = new Attribute("Right_Magnetometer_X"); 
    Attribute sensor_mag_1_y = new Attribute("Right_Magnetometer_Y"); 
    Attribute sensor_mag_1_z = new Attribute("Right_Magnetometer_Z"); 
    
    
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
			       
		fvWekaAttributes = new FastVector<Attribute>(23);
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
		
		// download train.arff store in local folder
		downLoadFromServer();
		// build classifier
		buildClassifier(ctx);
	}
	
	public void downLoadFromServer() {
		try {
	        //set the download URL, a url that points to a file on the internet
	        //this is the file to be downloaded
	        URL url = new URL("https://dl.dropboxusercontent.com/u/14697468/year4%20sem1/CG4001/Project/Project%20resource/semple%20data/train.arff");

	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();

	        //set the path where we want to save the file
	        //in this case, going to save it on the root directory of the
	        //sd card.
	        File SDCardRoot = Environment.getExternalStorageDirectory();
	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        File file = new File(SDCardRoot,"/Gaitroid/train.arff");
	        
	        if(!file.exists()) {
		        //this will be used to write the downloaded data into the file we created
		        FileOutputStream fileOutput = new FileOutputStream(file);
	
		        //this will be used in reading the data from the internet
		        InputStream inputStream = urlConnection.getInputStream();
	
		        //this is the total size of the file
		        int totalSize = urlConnection.getContentLength();
		        //variable to store total downloaded bytes
		        int downloadedSize = 0;
	
		        //create a buffer...
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0; //used to store a temporary size of the buffer
	
		        //now, read through the input buffer and write the contents to the file
		        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
		                //add the data in the buffer to the file in the file output stream (the file on the sd card
		                fileOutput.write(buffer, 0, bufferLength);
		                //add up the size so we know how much is downloaded
		                downloadedSize += bufferLength;
		                //this is where you would do something to report the prgress, like this maybe
	//	                updateProgress(downloadedSize, totalSize);
	
		        }
		        //close the output stream when done
		        fileOutput.close();
	        }
		//catch some possible errors...
		} catch (MalformedURLException e) {
		        e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		}
	}
	
	// build classifier	
	public void buildClassifier(Context ctx) throws Exception {
//		Log.v("Classifier", "buildClassifier");
		ArffLoader loader = new ArffLoader();
		File f = new File(fileAddress);
		loader.setFile(f);
		structure = loader.getStructure();
		BufferedReader reader = new BufferedReader(new FileReader(fileAddress));
		Instances train = new Instances(reader);
		// setting class attribute
		train.setClassIndex(train.numAttributes() - 1);
		reader.close();
		Log.v("Classifier", train.toSummaryString());
		
		// train classifier
		cls = new J48();
		cls.buildClassifier(train);		
		// evaluate classifier and print some statistics
		eval = new Evaluation(train);
		
		// test
		Instances test = train;
		eval.evaluateModel(cls, test);
		Log.v("Classify", "evaluation start");
		Log.v("Classify", eval.toSummaryString("\nResults\n=======================================================\n", false));
	}
	
	public void processWindows(ArrayList<Window> windows) throws IOException {
		for(int i=0; i<windows.size(); i++) {
			processIndividualWindow(windows.get(i));
		}
	}
	
	public void processIndividualWindow(Window w) throws IOException {
		
		// extract the features before building Instance
		featureExtract(w);
		
		//Instances insts = new Instances("Gait", fvWekaAttributes, windowSize);  // "Gait" is relaition name
		Instances insts = new Instances(structure, windowSize);
		Log.v("Instances", insts.toSummaryString());
		Log.v("Instances", "1 " + insts.attribute(1).toString());
		Log.v("Instances", "2 " + insts.attribute(2).toString());
		Log.v("Instances", "12 " + insts.attribute(12).toString());
		// form instance
		for(int i=0; i<windowSize; i++) {
			// left
            Instance inst = new DenseInstance(instanceSize);
            inst.setValue(0, w.acel_left_x[i]);
            inst.setValue(1, w.acel_left_y[i]);
            inst.setValue(2, w.acel_left_z[i]);
            
            inst.setValue(3, w.gyro_left_x[i]);
            inst.setValue(4, w.gyro_left_y[i]);
            inst.setValue(5, w.gyro_left_z[i]);
            
            inst.setValue(8, w.mag_left_x[i]);
            inst.setValue(9, w.mag_left_y[i]);
            inst.setValue(10, w.mag_left_z[i]);
            
            inst.setValue(7, w.fsr_left_front[i]);
            inst.setValue(6, w.fsr_left_back[i]);
            
            // right
            inst.setValue(11, w.acel_right_x[i]);
            inst.setValue(12, w.acel_right_y[i]);
            inst.setValue(13, w.acel_right_z[i]);
            
            inst.setValue(14, w.gyro_right_x[i]);
            inst.setValue(15, w.gyro_right_y[i]);
            inst.setValue(16, w.gyro_right_z[i]);
            
            inst.setValue(19, w.mag_right_x[i]);
            inst.setValue(20, w.mag_right_y[i]);
            inst.setValue(21, w.mag_right_z[i]);
            
            inst.setValue(18, w.fsr_right_front[i]);
            inst.setValue(17, w.fsr_right_back[i]);
           
            // test on class attribute
//            inst.setValue(22, "Right_heel_strike");
            inst.setMissing(22);
//            inst.setDataset(insts);
            try {
				double classNum = cls.classifyInstance(inst);
				Log.v("ClassNum", classNum + "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // add into instances
            insts.add(inst);
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
		Log.v("ClassifierTest", "ClassifierTest");
		Log.v("ClassifierTest", test.toSummaryString());
		eval.evaluateModel(cls, test);
		Log.v("Classify", eval.toSummaryString("\nResults\n=======================================================\n", false));
	}
}
