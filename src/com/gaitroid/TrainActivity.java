package com.gaitroid;

import java.util.Collection;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import root.gast.speech.SpeechRecognizingAndSpeakingActivity;

public class TrainActivity extends SpeechRecognizingAndSpeakingActivity {
	private static String mSensorView = "";
	int mEnabledSensors=0;
	
	String BluetoothAddress="";
	String BluetoothAddress0="";
	String BluetoothAddress1="";
	boolean mServiceBind=false;
	MultiShimmerPlayService mService;
	/** The OpenGL View */
	private GLSurfaceView glSurface;
	Button buttonConnect;
	Button buttonSet;
	Button buttonReset;
	public static MyGLSurfaceView t;
	TextView mTVQ1;
	TextView mTVQ2;
	TextView mTVQ3;
	TextView mTVQ4;
	Matrix3d invm3d = new Matrix3d();
	Matrix3d fm3d = new Matrix3d();
	Matrix3d m3d = new Matrix3d();
    private Shimmer mShimmerDevice1 = null;
	static final int REQUEST_CONNECT_SHIMMER = 2;
	static final int REQUEST_CONFIGURE_SHIMMER = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent=new Intent(this, MultiShimmerPlayService.class);
		getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	    
		Bundle extras = getIntent().getExtras();
	    BluetoothAddress = extras.getString("BluetoothAddress");
		BluetoothAddress0 = ((MyApplication) this.getApplication()).getBluetoothAddress0();
		BluetoothAddress1 = ((MyApplication) this.getApplication()).getBluetoothAddress1();
		
		setContentView(R.layout.training);
		t= new MyGLSurfaceView(this);
		//Create an Instance with this Activity
		glSurface = (GLSurfaceView)findViewById(R.id.graphics_glsurfaceview);

		//Set our own Renderer
		glSurface.setRenderer(t);
		//Set the GLSurface as View to this Activity
		//setContentView(glSurface);
		invm3d = new Matrix3d();
		fm3d = new Matrix3d();
		m3d = new Matrix3d();
		invm3d.setIdentity();
		//mShimmerDevice1 = new Shimmer(this, mHandler,"RightArm",false); 
		//mShimmerDevice1.enableOnTheFlyGyroCal(true, 102, 1.2);		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glSurface.onResume();
		
		Intent intent=new Intent(this, MultiShimmerPlayService.class);
  	  	Log.d("ShimmerH","Graph on Resume");
  	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Also pause the glSurface
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glSurface.onPause();
		
		Log.d("ShimmerH","Graph on Pause");
	  	  if(mServiceBind == true){
	  		  mService.enableGraphingHandler(false);
	  		  getApplicationContext().unbindService(mTestServiceConnection);
	  	  }
	}
	
	private ServiceConnection mTestServiceConnection = new ServiceConnection() {

      	public void onServiceConnected(ComponentName arg0, IBinder service) {
      		// TODO Auto-generated method stub
      
      		LocalBinder binder = (LocalBinder) service;
      		mService = binder.getService();
      		Log.d("Shimmer","Connected on Graph" + " " +BluetoothAddress);
      		//update the view
      		mServiceBind=true;
      		mService.setGraphHandler(mHandler,BluetoothAddress);
      		mService.enableGraphingHandler(true);
      		mEnabledSensors=mService.getEnabledSensors(BluetoothAddress);

      	}

      	public void onServiceDisconnected(ComponentName arg0) {
      		
      		Log.d("Shimmer","Service Disconnected on Graph" + " " +BluetoothAddress);// TODO Auto-generated method stub
      		mServiceBind=false;
      	}
    };
        
	private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
            case Shimmer.MESSAGE_READ:
            	if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj;
            	    
            	    if (objectCluster.mMyName=="0"){
                	    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Axis Angle A");  // first retrieve all the possible formats for the current sensor device
			 	    	float angle = 0,x = 0,y=0,z=0;
                	    if (accelXFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
			 	    		angle = (float) formatCluster.mData;
			 	    	}
			 	    	Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Axis Angle X");  // first retrieve all the possible formats for the current sensor device
			 	    	if (accelYFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
			 	    		x=(float) formatCluster.mData;
			 	    	}
			 	    	Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Axis Angle Y");  // first retrieve all the possible formats for the current sensor device
			 	    	if (accelZFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
			 	    		y=(float) formatCluster.mData;
			 	    	}
			 	    	Collection<FormatCluster> aaFormats = objectCluster.mPropertyCluster.get("Axis Angle Z");  // first retrieve all the possible formats for the current sensor device
			 	    	if (aaFormats != null){
			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(aaFormats,"CAL")); // retrieve the calibrated data
			 	    		z=(float) formatCluster.mData;
			 	    		AxisAngle4d aa=new AxisAngle4d(x,y,z,angle);
			 	    		m3d.set(aa);
			 	    		//flip the rotation matrix (mat = flipMat * mat * flipMat;)
			 	    		fm3d.setIdentity();
			 	    		fm3d.m11=-1;
			 	    		fm3d.m22=-1;
			 	    		Matrix3d fm3dtemp = new Matrix3d();
			 	    		fm3dtemp.setIdentity();
			 	    		fm3d.m11=-1;
			 	    		fm3d.m22=-1;
			 	    		fm3d.mul(m3d);
			 	    		fm3d.mul(fm3dtemp);
			 	    		
			 	    		//set function
			 	    		fm3dtemp.set(invm3d);
			 	    		fm3dtemp.mul(fm3d);
			 	    		aa.set(fm3dtemp);
			 	    		t.setAngleAxis((float) (aa.angle*180/Math.PI), (float)aa.x, (float)aa.y, (float)aa.z);
			 	    	}
			 	    	
			 	    	
			 	    	int[] dataArray = new int[0];
                		double[] calibratedDataArray = new double[0];
                		String[] sensorName = new String[0];
                		String units="";
                		String calibratedUnits="";
                		//mSensorView determines which sensor to graph
                		if (mSensorView.equals("Accelerometer")){
                			sensorName = new String[3]; // for x y and z axis
                			dataArray = new int[3];
                			calibratedDataArray = new double[3];
                			sensorName[0] = "Accelerometer X";
                			sensorName[1] = "Accelerometer Y";
                			sensorName[2] = "Accelerometer Z";
                			Log.d("ShimmerGraph","Received1");
                		}
                		if (mSensorView.equals("Gyroscope")){
                			sensorName = new String[3]; // for x y and z axis
                			dataArray = new int[3];
                			calibratedDataArray = new double[3];
                			sensorName[0] = "Gyroscope X";
                			sensorName[1] = "Gyroscope Y";
                			sensorName[2] = "Gyroscope Z";
                		}
                		if (mSensorView.equals("Magnetometer")){
                			sensorName = new String[3]; // for x y and z axis
                			dataArray = new int[3];
                			calibratedDataArray = new double[3];
                			sensorName[0] = "Magnetometer X";
                			sensorName[1] = "Magnetometer Y";
                			sensorName[2] = "Magnetometer Z";
                		}
                		if (mSensorView.equals("GSR")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "GSR";
                		}
                		if (mSensorView.equals("EMG")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "EMG";
                		}
                		if (mSensorView.equals("ECG")){
                			sensorName = new String[2]; 
                			dataArray = new int[2];
                			calibratedDataArray = new double[2];
                			sensorName[0] = "ECG RA-LL";
                			sensorName[1] = "ECG LA-LL";
                		}
                		if (mSensorView.equals("StrainGauge")){
                			sensorName = new String[2]; 
                			dataArray = new int[2];
                			calibratedDataArray = new double[2];
                			sensorName[0] = "Strain Gauge High";
                			sensorName[1] = "Strain Gauge Low";
                		}
                		if (mSensorView.equals("HeartRate")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "Heart Rate";
                		}
                		if (mSensorView.equals("ExpBoardA0")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "ExpBoard A0";
                		}
                		if (mSensorView.equals("ExpBoardA7")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "ExpBoard A7";
                		}
                		if (mSensorView.equals("TimeStamp")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "Timestamp";
                		}
                		
                		String deviceName = objectCluster.mBluetoothAddress;
                		//log data
                		
                		if (sensorName.length!=0){  // Device 1 is the assigned user id, see constructor of the Shimmer
    				 	    if (sensorName.length>0){
    				 	    	Log.d("ShimmerGraph","Received2");
    				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[0]);  // first retrieve all the possible formats for the current sensor device
    				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
    				 	    	if (formatCluster != null) {
    				 	    		//Obtain data for text view
    				 	    		calibratedDataArray[0] = formatCluster.mData;
    				 	    		calibratedUnits = formatCluster.mUnits;
    				 	    		
    				 	    		//Obtain data for graph
    					 	    	if (sensorName[0]=="Heart Rate"){ // Heart Rate has no uncalibrated data 
    					 	    		dataArray[0] = (int)((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")).mData; 
    					 	    		units = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")).mUnits; 
    						 	 	} else {
    						 	 		dataArray[0] = (int)((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
    							 	}
    					 	    	units = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mUnits; //TODO: Update data structure to include Max and Min values. This is to allow easy graph adjustments for the length and width
    					 	    }
    				 	    }
    				 	    if (sensorName.length>1) {
    				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[1]);  // first retrieve all the possible formats for the current sensor device
    				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL"));
    				 	    	if (formatCluster != null ) {
    					 	    	calibratedDataArray[1] = formatCluster.mData;
    					 	    	//Obtain data for text view
    					 	    	
    					 	    	//Obtain data for graph
    					 	    	dataArray[1] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
    					 	    	units = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mUnits; //TODO: Update data structure to include Max and Min values. This is to allow easy graph adjustments for the length and width

    				 	    	}
    				 	    }
    				 	    if (sensorName.length>2){
    				 	    
    				 	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(sensorName[2]);  // first retrieve all the possible formats for the current sensor device
    				 	    	FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"CAL")); 
    				 	    	if (formatCluster != null) {
    				 	    		calibratedDataArray[2] = formatCluster.mData;
    					 	    	
    					 	   	    
    				 	    		//Obtain data for graph
    				 	    		dataArray[2] =(int) ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mData; 
    					 	    	units = ((FormatCluster)ObjectCluster.returnFormatCluster(ofFormats,"RAW")).mUnits; //TODO: Update data structure to include Max and Min values. This is to allow easy graph adjustments for the length and width
    				 	    	}
    				 	    	
    			            }
    				 	   Log.d("ShimmerSensor", sensorName.toString());
    				 	   Log.d("ShimmerDate", dataArray.toString());
//    				 	   Log.d("ShimmerGraph","Received3");
//    				 	   if (sensorName[0].equals("Magnetometer X")){
//    				 		  mGraphDisplay.setDataWithAdjustment(dataArray,"","i16");
//    				 	   } else {
//    				 		   mGraphDisplay.setDataWithAdjustment(dataArray,"","u16");
//    				 	   }
    					}
		 	    	}
            	}
                break;
                 case Shimmer.MESSAGE_TOAST:
                	Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),Toast.LENGTH_SHORT).show();
                break;

                 case Shimmer.MESSAGE_STATE_CHANGE:
                	 switch (msg.arg1) {
                     	case Shimmer.MSG_STATE_FULLY_INITIALIZED:
                    	    if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED){
                    	        Log.d("ConnectionStatus","Successful");
                    	        mShimmerDevice1.enableOnTheFlyGyroCal(true, 100, 1.2);
                    	        mShimmerDevice1.enable3DOrientation(true);
                    	        mShimmerDevice1.writeSamplingRate(51.2);
                    	        mShimmerDevice1.startStreaming();
                    	     }
                    	    break;
	                    case Shimmer.STATE_CONNECTING:
	                    	Log.d("ConnectionStatus","Connecting");
                	        break;
	                    case Shimmer.STATE_NONE:
	                    	Log.d("ConnectionStatus","No State");
	                    	break;
                     }
                break;
                
            }
        }
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
    	switch (requestCode) {

    	case REQUEST_CONNECT_SHIMMER:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	if (mShimmerDevice1.getStreamingStatus()==true){
					mShimmerDevice1.stop();
				} else {
					String bluetoothAddress= data.getExtras()
	                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);;
					mShimmerDevice1.connect(bluetoothAddress,"default"); 
					invm3d = new Matrix3d();
					invm3d.setIdentity();
					fm3d = new Matrix3d();
					m3d = new Matrix3d();
				}
            }
            break;
            
    	case REQUEST_CONFIGURE_SHIMMER:
    		if (resultCode == Activity.RESULT_OK) {
    			if (data.getExtras().getString("Command").equals("Mag")){
    				if (mShimmerDevice1.getStreamingStatus()){
    					mShimmerDevice1.stopStreaming();
    					mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
    					mShimmerDevice1.startStreaming();
    				} else {
    					mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
    				}
    			} else if (data.getExtras().getString("Command").equals("Gyro")) {
    				mShimmerDevice1.enableOnTheFlyGyroCal(data.getExtras().getBoolean("Enable"), 100, 1.2);
    			}
            }
            break;
    	}
    }

	@Override
	protected void receiveWhatWasHeard(List<String> arg0, float[] arg1) {
		// TODO Auto-generated method stub
		
	}
}
