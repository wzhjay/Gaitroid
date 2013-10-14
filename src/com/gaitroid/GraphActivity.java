package com.gaitroid;

import java.util.Arrays;
import java.util.Collection;



import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.gaitroid.R;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

public class GraphActivity extends Activity{
	   boolean mServiceBind=false;
	   MultiShimmerPlayService mService;
	   int mEnabledSensors=0;
	   String BluetoothAddress="";
		private static GraphView mGraphDisplay;
	   private static String mSensorView = ""; //The sensor device which should be viewed on the graph
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.graph_view);
	
	Bundle extras = getIntent().getExtras();
    BluetoothAddress = extras.getString("BluetoothAddress");
    setTitle("Graph: " + BluetoothAddress);
	Intent intent=new Intent(this, MultiShimmerPlayService.class);
	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    View mGraph = (View) findViewById(R.id.graph);
    mGraphDisplay = (GraphView)findViewById(R.id.graph);
    
    mGraph.setOnLongClickListener(new OnLongClickListener() {
    	
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			Log.d("ShimmerGraph","on long click");

			Intent mainCommandIntent=new Intent(GraphActivity.this,SensorViewActivity.class);
			mainCommandIntent.putExtra("Enabled_Sensors",mEnabledSensors);
			startActivityForResult(mainCommandIntent, GaitroidMain.REQUEST_CONFIGURE_GRAPH);
			return false;
		}
		});
	}
	
public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
    	switch (requestCode) {
    	
    	case GaitroidMain.REQUEST_CONFIGURE_GRAPH:
    		if (resultCode == Activity.RESULT_OK) {
    			mSensorView=data.getExtras().getString(ConfigureActivity.mDone);
    			Log.d("Shimmer","Request Configure Graph" + mSensorView);
    		}
    		break;
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

        public void onPause(){
  	  	  super.onPause();
  	  	  
  	  	Log.d("ShimmerH","Graph on Pause");
  	  	  if(mServiceBind == true){
  	  		  mService.enableGraphingHandler(false);
  	  		  getApplicationContext().unbindService(mTestServiceConnection);
  	  	  }
  	  	 }
  	    
  	  public void onResume(){
  	  	super.onResume();

  	  	Intent intent=new Intent(this, MultiShimmerPlayService.class);
  	  	Log.d("ShimmerH","Graph on Resume");
  	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
  	  }
        
        
        private static Handler mHandler = new Handler() {
        	   

    		public void handleMessage(Message msg) {
            	
                switch (msg.what) {
                case Shimmer.MESSAGE_READ:
                		Log.d("ShimmerGraph","Received");
                	    if ((msg.obj instanceof ObjectCluster)){
                	    	
                	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
                	    
                	   
                	    // for gaitroid

                        String[] sensor_acl = {"Accelerometer X", "Accelerometer Y", "Accelerometer Z"};
                        String[] sensor_gyr = {"Gyroscope X", "Gyroscope Y", "Gyroscope Z"};
                        String[] sensor_mag = {"Magnetometer X", "Magnetometer Y", "Magnetometer Z"};
                        double[] cal_acl = new double[3];
                        double[] cal_gyr = new double[3];
                        double[] cal_mag = new double[3];


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
                		if (mSensorView.equals("Timestamp")){
                			sensorName = new String[1]; 
                			dataArray = new int[1];
                			calibratedDataArray = new double[1];
                			sensorName[0] = "TimeStamp";
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
    				 	   //Log.d("deviceName", objectCluster.mBluetoothAddress);
    				 	   //Log.d("ShimmerSensor", Arrays.deepToString(sensorName));
    				 	   //Log.d("ShimmerData", Arrays.toString(dataArray));
    				 	   //Log.d("ShimmerGraph","Received3");
    				 	   //mGraphDisplay.setDataWithAdjustment(dataArray,"","u16");
    				 	 
    					}


                        // gaitroid, get all the sensor data
                        if(objectCluster.mMyName.equals("0")) {
                            //mService.setEnabledSensors(0, deviceName);
                            //mService.setEnabledSensors(1, deviceName);
                            //mService.setEnabledSensors(2, deviceName);
                            // acl
                            Collection<FormatCluster> gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[0]);
                            FormatCluster gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl[2] = gaitroid_formatCluster.mData;
                            }

                            // gyro
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr[2] = gaitroid_formatCluster.mData;
                            }

                            // mag
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag[2] = gaitroid_formatCluster.mData;
                            }

                            Log.d("gaitroid_data", "acl: " + Arrays.toString(cal_acl) + " gyr: " + Arrays.toString(cal_gyr) + " mag: " + Arrays.toString(cal_mag));
                        }
                	}
    				
                    break;
                }
            }
        };
	
}
