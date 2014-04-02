package com.gaitroid;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import weka.core.Instances;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.beardedhen.bbutton.BootstrapButton;
import com.gaitroid.R;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import android.speech.tts.TextToSpeech;

@SuppressWarnings("deprecation")
public class GraphActivity extends Activity implements TextToSpeech.OnInitListener{
	   
	   int mEnabledSensors=0;
	   String BluetoothAddress="";
	   String BluetoothAddress0="";
	   String BluetoothAddress1="";
	   static String socketConnectPath = "";
	   static IOCallback io;
	   static SocketIO socket;
	   
	   private static GraphView mGraphDisplay;
	   private static String mSensorView = ""; //The sensor device which should be viewed on the graph
	   
	   // services
	   boolean mServiceBind=false;
	   static MultiShimmerPlayService mService;
	   
	   // tts
	   private static TextToSpeech tts;
	   
	   // gait images flow
	   private ImageView image;
	   
	   public String speedCheck = "NORMAL";
	   public int speed = 0;
	   
	   static String[] sensor_acl = {"Accelerometer X", "Accelerometer Y", "Accelerometer Z"};
	   static String[] sensor_gyr = {"Gyroscope X", "Gyroscope Y", "Gyroscope Z"};
	   static String[] sensor_mag = {"Magnetometer X", "Magnetometer Y", "Magnetometer Z"};
	   static String sensor_exp_a0 = "ExpBoard A0";
	   static String sensor_exp_a7 = "ExpBoard A7";
       static double[] cal_acl_0 = new double[3];
       static double[] cal_gyr_0 = new double[3];
       static double[] cal_mag_0 = new double[3];
       static double[] cal_acl_1 = new double[3];
       static double[] cal_gyr_1 = new double[3];
       static double[] cal_mag_1 = new double[3];
       static double cal_exp_a0_0 = 0.0;
       static double cal_exp_a7_0 = 0.0;
       static double cal_exp_a0_1 = 0.0;
       static double cal_exp_a7_1 = 0.0;
       
       // buffer and window processing
       static int bufferSize = DataStreamBuffer.bufferSize; 
       static int bufferSizeCounter = -500;
       static DataStreamBuffer myDataStreamBuffer;
       static WindowProcess wp;
       
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_view);
		
		BluetoothAddress0 = ((MyApplication) this.getApplication()).getBluetoothAddress0();
		BluetoothAddress1 = ((MyApplication) this.getApplication()).getBluetoothAddress1();
		socketConnectPath = ((MyApplication) this.getApplication()).getSocketConnectPath();
		io = new BasicExample();
		socket = null ;
		
		try {
			socket = new SocketIO(socketConnectPath);
			socket.connect(io);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent=new Intent(this, MultiShimmerPlayService.class);
		getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	    // tts
	    tts = new TextToSpeech(this, this);
	    
	    // image
	    Intent sender = getIntent();
	    speedCheck = sender.getExtras().getString("speed");
	    Log.v("speed", speedCheck);
	    image = (ImageView) findViewById(R.id.gait_image);
	    setSpeed();
	    
	    // stop button
	 	final BootstrapButton button_stop = (BootstrapButton) findViewById(R.id.stop_graph);
	 	button_stop.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	         	// start streaming for all devices
	         	mService.stopStreamingAllDevices();
	         	finish();
 	        }
 	    });
	 	
	 	// buffer and window processing
	 	myDataStreamBuffer = new DataStreamBuffer();
	 	try {
			wp = new WindowProcess(getBaseContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
      		// modify the service, the BluetoothAddress will not affect the handler
      		mService.setGraphHandler(mHandler,BluetoothAddress0);
      		mService.enableGraphingHandler(true);
      		mEnabledSensors=mService.getEnabledSensors(BluetoothAddress0);

      	}

      	public void onServiceDisconnected(ComponentName arg0) {
      		
      		//Log.d("Shimmer","Service Disconnected on Graph" + " " +BluetoothAddress);// TODO Auto-generated method stub
      		mServiceBind=false;
      	}
        };

        public void onPause(){
  	  	  super.onPause();
	  	  	//glSurface.onPause();
	  	  	Log.d("ShimmerH","Graph on Pause");
	  	  	if(mServiceBind == true){
	  	  		mService.enableGraphingHandler(false);
	  	  		getApplicationContext().unbindService(mTestServiceConnection);
	  	  	}
  	  	 }
  	    
  	  public void onResume(){
  	  	super.onResume();
  	  	
  	  	//glSurface.onResume();
  	  	Intent intent=new Intent(this, MultiShimmerPlayService.class);
  	  	Log.d("ShimmerH","Graph on Resume");
  	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
  	  }
        
        
        private static Handler mHandler = new Handler() {
        	   
        	
    		@SuppressWarnings("null")
			public void handleMessage(Message msg) {
    			
                switch (msg.what) {
                case Shimmer.MESSAGE_READ:
                		Log.d("ShimmerGraph","Received");
                	    if ((msg.obj instanceof ObjectCluster)){
                	    	
                	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 

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
    				 	   
    				 	 
    					}

                		
                        // gaitroid, get all the sensor data
                        if(objectCluster.mMyName.equals("0")) {
                        	Log.d("gaitroid_data", "deveice: 0");
                            // acl
                            Collection<FormatCluster> gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[0]);
                            FormatCluster gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_0[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_0[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_0[2] = gaitroid_formatCluster.mData;
                            }

                            // gyro
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_0[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_0[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_0[2] = gaitroid_formatCluster.mData;
                            }

                            // mag
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_0[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_0[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_0[2] = gaitroid_formatCluster.mData;
                            }
                            
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_exp_a0);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_exp_a0_0 = gaitroid_formatCluster.mData;
                            }
                            
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_exp_a7);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_exp_a7_0 = gaitroid_formatCluster.mData;
                            }
                            
                            try {
                            	JSONObject job = new JSONObject();
                            	JSONObject acl = new JSONObject();
                            	acl.put("acl_x", cal_acl_0[0]);
                            	acl.put("acl_y", cal_acl_0[1]);
                            	acl.put("acl_z", cal_acl_0[2]);
                            	JSONObject gyr = new JSONObject();
                            	gyr.put("gyr_x", cal_gyr_0[0]);
                            	gyr.put("gyr_y", cal_gyr_0[1]);
                            	gyr.put("gyr_z", cal_gyr_0[2]);
                            	JSONObject mag = new JSONObject();
                            	mag.put("mag_x", cal_mag_0[0]);
                            	mag.put("mag_y", cal_mag_0[1]);
                            	mag.put("mag_z", cal_mag_0[2]);
                            	job.put("acl", acl);
                            	job.put("gyr", gyr);
                            	job.put("mag", mag);
                            	job.put("exp_a0_0", cal_exp_a0_0); // LEFT FRONT
                            	job.put("exp_a7_0", cal_exp_a7_0); // LEFT BACK
                            	Log.d("job 0", job.toString());
                				socket.emit("gaitroid_data_0", job);
                			} catch (JSONException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}

                            Log.d("gaitroid_data", "acl: " + Arrays.toString(cal_acl_0) + " gyr: " + Arrays.toString(cal_gyr_0) + " mag: " + Arrays.toString(cal_mag_0) + " exp_a0_0: " + cal_exp_a0_0 + " exp_a7_0: " + cal_exp_a7_0);
                        
                        }
                        
                        // gaitroid, get all the sensor data
                        if(objectCluster.mMyName.equals("1")) {
                        	Log.d("gaitroid_data", "deveice: 1");
                            // acl
                            Collection<FormatCluster> gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[0]);
                            FormatCluster gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_1[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_1[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_acl[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_acl_1[2] = gaitroid_formatCluster.mData;
                            }

                            // gyro
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_1[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_1[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_gyr[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_gyr_1[2] = gaitroid_formatCluster.mData;
                            }

                            // mag
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[0]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_1[0] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[1]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_1[1] = gaitroid_formatCluster.mData;
                            }
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_mag[2]);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_mag_1[2] = gaitroid_formatCluster.mData;
                            }
                            
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_exp_a0);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_exp_a0_1 = gaitroid_formatCluster.mData;
                            }
                            
                            gaitroid_ofFormats = objectCluster.mPropertyCluster.get(sensor_exp_a7);
                            gaitroid_formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gaitroid_ofFormats,"CAL"));
                            if (gaitroid_formatCluster != null ) {
                                cal_exp_a7_1 = gaitroid_formatCluster.mData;
                            }
                            
                            try {
                            	JSONObject job = new JSONObject();
                            	JSONObject acl = new JSONObject();
                            	acl.put("acl_x", cal_acl_1[0]);
                            	acl.put("acl_y", cal_acl_1[1]);
                            	acl.put("acl_z", cal_acl_1[2]);
                            	JSONObject gyr = new JSONObject();
                            	gyr.put("gyr_x", cal_gyr_1[0]);
                            	gyr.put("gyr_y", cal_gyr_1[1]);
                            	gyr.put("gyr_z", cal_gyr_1[2]);
                            	JSONObject mag = new JSONObject();
                            	mag.put("mag_x", cal_mag_1[0]);
                            	mag.put("mag_y", cal_mag_1[1]);
                            	mag.put("mag_z", cal_mag_1[2]);
                            	job.put("acl", acl);
                            	job.put("gyr", gyr);
                            	job.put("mag", mag);
                            	job.put("exp_a0_1", cal_exp_a0_1); // RIGHT FRONT
                            	job.put("exp_a7_1", cal_exp_a7_1); // RIGHT back
                            	Log.d("job 1", job.toString());
                				socket.emit("gaitroid_data_1", job);
                			} catch (JSONException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}
                            Log.d("gaitroid_data", "acl: " + Arrays.toString(cal_acl_1) + " gyr: " + Arrays.toString(cal_gyr_1) + " mag: " + Arrays.toString(cal_mag_1) + " exp_a0_1: " + cal_exp_a0_1 + " exp_a7_1: " + cal_exp_a7_1);
                        }
                     // store data into circuler buffer
                        // if buffer full, reset the bufferSizeCounter, overwrite new data into buffer
                        // create temp buffer, create windows of buffer data
                        // after done window processing, clear windows
                        if(bufferSizeCounter < bufferSize) {
                        	Log.v("bufferSizeCounter", bufferSizeCounter + "");
                        	bufferSizeCounter += 1;
                        	
                        	myDataStreamBuffer.Acel_Left_X.insertData(cal_acl_0[0]);
                        	myDataStreamBuffer.Acel_Left_Y.insertData(cal_acl_0[1]);
                        	myDataStreamBuffer.Acel_Left_Z.insertData(cal_acl_0[2]);
                        	
                        	myDataStreamBuffer.Gyro_Left_X.insertData(cal_gyr_0[0]);
                        	myDataStreamBuffer.Gyro_Left_Y.insertData(cal_gyr_0[1]);
                        	myDataStreamBuffer.Gyro_Left_Z.insertData(cal_gyr_0[2]);
                        	
                        	myDataStreamBuffer.Mag_Left_X.insertData(cal_mag_0[0]);
                        	myDataStreamBuffer.Mag_Left_Y.insertData(cal_mag_0[1]);
                        	myDataStreamBuffer.Mag_Left_Z.insertData(cal_mag_0[2]);
                        	
                        	myDataStreamBuffer.FSR_Left_F.insertData(cal_exp_a0_0);
                        	myDataStreamBuffer.FSR_Left_B.insertData(cal_exp_a7_0);
                        	
                        	myDataStreamBuffer.Acel_Right_X.insertData(cal_acl_1[0]);
                        	myDataStreamBuffer.Acel_Right_Y.insertData(cal_acl_1[1]);
                        	myDataStreamBuffer.Acel_Right_Z.insertData(cal_acl_1[2]);
                        	
                        	myDataStreamBuffer.Gyro_Right_X.insertData(cal_gyr_1[0]);
                        	myDataStreamBuffer.Gyro_Right_Y.insertData(cal_gyr_1[1]);
                        	myDataStreamBuffer.Gyro_Right_Z.insertData(cal_gyr_1[2]);
                        	
                        	myDataStreamBuffer.Mag_Right_X.insertData(cal_mag_1[0]);
                        	myDataStreamBuffer.Mag_Right_Y.insertData(cal_mag_1[1]);
                        	myDataStreamBuffer.Mag_Right_Z.insertData(cal_mag_1[2]);
                        	
                        	myDataStreamBuffer.FSR_Right_F.insertData(cal_exp_a0_1);
                        	myDataStreamBuffer.FSR_Right_B.insertData(cal_exp_a7_1);
                        }
                        
                        if(bufferSizeCounter >= bufferSize) {
                    		bufferSizeCounter = 0;
                    		myDataStreamBuffer.cretaeWindows();
                    		Log.v("myWindows", myDataStreamBuffer.myWindows.size() + "");
                    		
                    		try {
								wp.processWindows(myDataStreamBuffer.myWindows);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    		myDataStreamBuffer.clearWindows();
                    		Log.v("myWindows", myDataStreamBuffer.myWindows.size() + "");
                    		Log.v("windowsTobeProcessedQueue", wp.getWindowsTobeProcessedQueue().size() + "");
                    		
                    		// test on classification
    			        	if(wp.getWindowsTobeProcessedQueue().size() > 0) {
    				        	Instances insts = wp.getWindowsTobeProcessedQueue().remove(0);
    				        	Log.v("windowsTobeProcessedQueue", "get instances from head of queue");
    				        	try {
    								wp.WekaClassify(insts);
    							} catch (Exception e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    			        	}
                    	}
                        
                        //PhaseDetect(cal_exp_a0_0, cal_exp_a7_0, cal_exp_a0_1, cal_exp_a7_1);
                	}
    				
                    break;
                }
            }
        };
        
//        @SuppressWarnings("unchecked")
//		public static void formWindows(Instance inst) {
//        	if(counterPrev < windowSize) {
//        		prev.add(inst);
//        		counterPrev += 1;
//        	} else { // when prev window full, i.e. counterPrev == windowSize
//        		Instances window = new Instances(prev); // copy prev Instances which full
//        		windowsTobeProcessedQueue.add(window);
//        		prev = next;	// point to next Instances
//        		counterPrev = counterNext;	// map counter from next to prev
//        		counterNext = 0;	// reset next counter
//        	}
//        	
//        	if(counterPrev == windowOverlapSize) {
//        		next = new Instances("GaitroidTest", fvWekaAttributes, windowSize);
//        	}
//        	
//        	if(counterPrev > windowOverlapSize) {
//        		next.add(inst);
//        		counterNext += 1;
//        	}
//        }
        
        public static void PhaseDetect(double left_front, double left_back, double right_front, double right_back) {
        	Log.v("sensor", "left_front: " + left_front + " left_back: " + left_back + " right_front: " + right_front +  " right_back: " + right_back);
        	if( (left_front < 3000) && 
        		(left_back < 3000) &&
        		(right_front < 1000) &&
        		(right_back > 3500)) { // Left toe off
        		speakOut("left toe off");
        	}
        }
        
        @Override
        public void onDestroy() {
            // Don't forget to shutdown tts!
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
            super.onDestroy();
        }
        
        // ==================================== tts =======================================
		@Override
		public void onInit(int status) {
			// TODO Auto-generated method stub
			if (status == TextToSpeech.SUCCESS) {
				 
	            int result = tts.setLanguage(Locale.US);
	 
	            if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	                Log.e("TTS", "This Language is not supported");
	            } else {
	            	//preparation();
	            	start();
	            }
	 
	        } else {
	            Log.e("TTS", "Initilization Failed!");
	        }
		}
		
		private static void speakOut(String text) {
	        String tt = text;
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	    }
		
		// loop the procedures
		private void start() {
			starting();
			fiveSecondPrepare();
			stratSignal();
			walking();
		}
        
		// ======================================== image content =======================================
		private void setSpeed() {
			if(speedCheck.equals("NORMAL")) {
				speed = 180;
			}
			else if (speedCheck.equals("SLOW")) {
				speed = 250;
			}
			else if (speedCheck.equals("FAST")) {
				speed = 120;
			}
			else
				speed = 200;
		}
		
		public void starting() {
			final String str[] = {"test start in five seconds", "please stand straight and get ready"};
			final Handler startHandler = new Handler();
			startHandler.postDelayed(new Runnable()
		    {
				private int flag = 0;
		        @Override
		        public void run()
		        {
		        	if(flag < 2) {
		        		speakOut(str[flag]);
		        		startHandler.postDelayed(this, 2000);
		        		flag++;
		        	}
		        	else
		        		startHandler.removeCallbacks(this);
		        }
		    }, 200); // 0.2 second delay (takes millis)
		}
		
		public void fiveSecondPrepare(){
			final Handler fiveSecondPrepareHandler = new Handler();
			final MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.buzz);
			fiveSecondPrepareHandler.postDelayed(new Runnable()
		    {
				private int time = 0;
		        @Override
		        public void run()
		        {
		        	if(time < 5) {
		        		mPlayer.start();
		        		fiveSecondPrepareHandler.postDelayed(this, 1000);
		        		time++;
		        	}
		        	else
		        		fiveSecondPrepareHandler.removeCallbacks(this);
		        }
		    }, 5000); // 1 second delay (takes millis)
		}
		
		public void stratSignal() {
			final Handler stratSignalHandler = new Handler();
			stratSignalHandler.postDelayed(new Runnable(){
				private int flag = 0;
				@Override
		        public void run()
		        {
		        	if(flag < 1) {
		        		speakOut("start");
		        		stratSignalHandler.postDelayed(this, 1000);
		        		flag++;
		        	}
		        	else
		        		stratSignalHandler.removeCallbacks(this);
		        }
			}, 10000);
		}
		
		
		public void walking() {
			
			final int v[]={R.drawable.gaitroid_1, R.drawable.gaitroid_2, R.drawable.gaitroid_3, R.drawable.gaitroid_4, R.drawable.gaitroid_5};
			final Handler imgHandler = new Handler();
			
			imgHandler.postDelayed(new Runnable()
			{
				private int flag = 0;
				private boolean left = true;
				public void run(){
					if(flag>1000)
			        	imgHandler.removeCallbacks(this);
			        else{
			        	
			        	if(flag%4 == 0){
//			        		if(left)
//			        			speakOut("left"); 
//			        		else
//			        			speakOut("right");
//			        		left = !left;
			        	}
			        	image.setImageResource(v[flag%4]);
			        	imgHandler.postDelayed(this, speed);
			        	flag++;
			        }
				}
			}, 11000);
		}

}
