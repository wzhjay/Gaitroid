package com.gaitroid;

import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.gaitroid.*;
import com.gaitroid.R;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;


public class LeftRightCommandsActivity extends Activity{
	String mCurrentDevice=null;
	Button mButtonConnect;
	Button mButtonCommand;
	int mCurrentSlot=-1;
	MultiShimmerPlayService mService;
	private boolean mServiceBind=false;
	private String[] commands = new String [] {"Connect"};
	private double mSamplingRate=-1;
	private int mAccelRange=-1;
	private int mGSRRange=-1;
	
	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.main_commands);
		    
		    Intent intent=new Intent(this, MultiShimmerPlayService.class);
			getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
		    Intent sender=getIntent();
		    String extraData=sender.getExtras().getString("LocalDeviceID");
		    mCurrentDevice=extraData;
		    setTitle("CMD: " + mCurrentDevice);
			mCurrentSlot=sender.getExtras().getInt("CurrentSlot");
		    Log.d("Shimmer","Create MC:  " + extraData);
		    
		    
		    final ListView listViewCommands = (ListView) findViewById(R.id.listView1);

		    
			ArrayList<String> commandsList = new ArrayList<String>();  
			commandsList.addAll( Arrays.asList(commands) );  
		    ArrayAdapter<String> sR = new ArrayAdapter<String>(this, R.layout.sensor_commands_name,commandsList);
			listViewCommands.setAdapter(sR);
		    
		    
			listViewCommands.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					  if (position==0){
								// TODO Auto-generated method stub
								Intent mainCommandIntent=new Intent(LeftRightCommandsActivity.this,DeviceListActivity.class);
					     		startActivityForResult(mainCommandIntent, MultiShimmerPlayActivity.REQUEST_CONNECT_SHIMMER);
					  }
				  }
				});
			
		    
		    
		    
		   
		    
	 }
	 
	 	
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	switch (requestCode) {	
	    	case MultiShimmerPlayActivity.REQUEST_CONNECT_SHIMMER:
	        // When DeviceListActivity returns with a device to connect
	        if (resultCode == Activity.RESULT_OK) {
	            String address = data.getExtras()
	                    .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	           Log.d("Shimmer",address);
	           Log.d("Shimmer",mCurrentDevice);
	           
	           Intent intent = new Intent();
	           intent.putExtra("CurrentDevice", mCurrentDevice);
	           intent.putExtra("Address", address);
	           intent.putExtra("CurrentSlot", mCurrentSlot);
	           setResult(Activity.RESULT_OK, intent);

	           finish();
	        }
	        break;
	    	case MultiShimmerPlayActivity.REQUEST_COMMANDS_SHIMMER:
		    	if (resultCode == Activity.RESULT_OK) {
		    		 Log.d("Shimmer","COmmands Received");
		    		 Log.d("Shimmer","iam");
		     		if (resultCode == Activity.RESULT_OK) {
		 	    		if(data.getExtras().getBoolean("ToggleLED",false) == true)
		 	    		{
		 	    			if (mCurrentDevice.equals("All Devices")){
		 	    				Log.d("Shimmer","Toggle ALL LEDS");
		 	    				mService.toggleAllLEDS();
		 	    			} else {
		 	    				mService.toggleLED(mCurrentDevice);
		 	    			}
		 	    				
		 	    		}
		 	    		
		 	    		if(data.getExtras().getDouble("SamplingRate",-1) != -1)
		 	    		{
		 	    			if (mCurrentDevice.equals("All Devices")){
		 	    				Log.d("Shimmer","Set Sampling Rate ALL LEDS");
		 	    				mService.setAllSampingRate((data.getExtras().getDouble("SamplingRate",-1)));
		 	    			} else {
		 	    				mService.writeSamplingRate(mCurrentDevice, (data.getExtras().getDouble("SamplingRate",-1)));
		 	    			}
		 	    		}
		 	    		
		 	    		if(data.getExtras().getInt("AccelRange",-1) != -1)
		 	    		{
		 	    			if (mCurrentDevice.equals("All Devices")){
		 	    				Log.d("Shimmer","Set AccelRange ALL LEDS");
		 	    				mService.setAllAccelRange(data.getExtras().getInt("AccelRange",-1));
		 	    			} else {
		 	    				mService.writeAccelRange(mCurrentDevice, data.getExtras().getInt("AccelRange",-1));
		 	    			}
		 	    		}
		 	    		
		 	    		if(data.getExtras().getInt("GSRRange",-1) != -1)
		 	    		{
		 	    			if (mCurrentDevice.equals("All Devices")){
		 	    				Log.d("Shimmer","Set ALL GSRRange");
		 	    				mService.setAllGSRRange(data.getExtras().getInt("GSRRange",-1));
		 	    			} else {
		 	    				mService.writeGSRRange(mCurrentDevice, data.getExtras().getInt("GSRRange",-1));
		 	    			}
		 	    		}
		 	    		
		     		}
		    	}
		    	break;
	    	case MultiShimmerPlayActivity.REQUEST_CONFIGURE_SHIMMER:
	    		if (resultCode == Activity.RESULT_OK) {
	    			if (mCurrentDevice.equals("All Devices")){
 	    				Log.d("Shimmer","Configure Sensors ALL Devices");
 	    				mService.setAllEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone));
 	    				
 	    			} else {
 	    				mService.setEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone),mCurrentDevice);
 	    			}
	    		}
	    		break;
	    	case MultiShimmerPlayActivity.REQUEST_LOGGING_SHIMMER:
	    		if (resultCode == Activity.RESULT_OK) {
	    			data.getExtras().getBoolean("EnableLogging");
	    			mService.setEnableLogging(data.getExtras().getBoolean("EnableLogging"));
	    		}
	    		break;
	    		
	    		
	    	}
	    	}
	    
	    public void onPause(){
	  	  super.onPause();
	  	  
	  	Log.d("ShimmerH","MCA on Pause");
	  	  if(mServiceBind == true){
	  		  getApplicationContext().unbindService(mTestServiceConnection);
	  	  }
	  	 }
	    
	  public void onResume(){
	  	super.onResume();

	  	Intent intent=new Intent(LeftRightCommandsActivity.this, MultiShimmerPlayService.class);
	  	Log.d("ShimmerH","MCA on Resume");
	  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	  }
	    
	    
	    private ServiceConnection mTestServiceConnection = new ServiceConnection() {

	      	public void onServiceConnected(ComponentName arg0, IBinder service) {
	      		// TODO Auto-generated method stub
	      
	      		LocalBinder binder = (LocalBinder) service;
	      		mService = binder.getService();
	      		Log.d("Shimmer","Connected on COmmands");
	      		//update the view
	      		mServiceBind=true;
	      		mSamplingRate=mService.getSamplingRate(mCurrentDevice);
	      		mAccelRange=mService.getAccelRange(mCurrentDevice);
	      		mGSRRange=mService.getGSRRange(mCurrentDevice);
	      	}

	      	public void onServiceDisconnected(ComponentName arg0) {
	      		// TODO Auto-generated method stub
	      		mServiceBind=false;
	      	}
	        };

}
