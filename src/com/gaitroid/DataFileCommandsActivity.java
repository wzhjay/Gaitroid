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
import android.widget.ListView;

import com.gaitroid.R;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;


public class DataFileCommandsActivity extends Activity{
	String fileName=null;
	int mCurrentSlot=-1;
	MultiShimmerPlayService mService;
	private boolean mServiceBind=false;
	private String[] commands = new String [] {"Delete", "Summit and Delete"};
	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.main_commands);
		    
		    Intent intent=new Intent(this, MultiShimmerPlayService.class);
			getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
		    Intent sender=getIntent();
		    String extraData=sender.getExtras().getString("fileName");
		    fileName=extraData;
		    setTitle("Log File: " + fileName);
		    
		    final ListView listViewCommands = (ListView) findViewById(R.id.listView1);

		    
			ArrayList<String> commandsList = new ArrayList<String>();  
			commandsList.addAll( Arrays.asList(commands) );  
		    ArrayAdapter<String> sR = new ArrayAdapter<String>(this, R.layout.sensor_commands_name,commandsList);
			listViewCommands.setAdapter(sR);
		    
		    
			listViewCommands.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					  if (position==0){
								// TODO Auto-generated method stub
								// Intent mainCommandIntent=new Intent(LeftRightCommandsActivity.this,DeviceListActivity.class);
					   //   		startActivityForResult(mainCommandIntent, MultiShimmerPlayActivity.REQUEST_CONNECT_SHIMMER);
					  }

					  if (position==1){
								// TODO Auto-generated method stub
								// Intent mainCommandIntent=new Intent(LeftRightCommandsActivity.this,DeviceListActivity.class);
					   //   		startActivityForResult(mainCommandIntent, MultiShimmerPlayActivity.REQUEST_CONNECT_SHIMMER);
					  }
				  }
				});
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

	  	Intent intent=new Intent(DataFileCommandsActivity.this, MultiShimmerPlayService.class);
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
//	      		mSamplingRate=mService.getSamplingRate(mCurrentDevice);
//	      		mAccelRange=mService.getAccelRange(mCurrentDevice);
//	      		mGSRRange=mService.getGSRRange(mCurrentDevice);
	      	}

	      	public void onServiceDisconnected(ComponentName arg0) {
	      		// TODO Auto-generated method stub
	      		mServiceBind=false;
	      	}
	        };

}
