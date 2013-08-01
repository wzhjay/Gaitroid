package com.gaitroid;

import java.net.MalformedURLException;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;

import io.socket.IOCallback;
import io.socket.SocketIO;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class GaitroidMain extends Activity {
	
	static final int REQUEST_ENABLE_BT = 1;
	public final static int REQUEST_MAIN_COMMAND_SHIMMER=3;
	public final static int REQUEST_CONNECT_SHIMMER=2;
	public final static int REQUEST_COMMANDS_SHIMMER=4;
	public static final int REQUEST_CONFIGURE_SHIMMER = 5;
	public static final int REQUEST_LOGGING_SHIMMER = 6;
	public static final int REQUEST_GRAPH_SHIMMER = 7;
	public static final int REQUEST_CONFIGURE_GRAPH = 8;
	String mCurrentDevice=null;
	int mCurrentSlot=-1;
	MultiShimmerPlayService mService;
	private boolean mServiceBind=false;
	private double mSamplingRate=-1;
	private int mAccelRange=-1;
	private int mGSRRange=-1;
	private boolean mServiceFirstTime=true;
	private Context mCtx;
	private static String mSensorView = ""; //The sensor device which should be viewed on the graph
	
	String BluetoothAddress="";	
	int mEnabledSensors=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gaitroid_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (!isMyServiceRunning())
	    {
	    	Log.d("ShimmerH","Oncreate2");
	    	Intent intent=new Intent(this, MultiShimmerPlayService.class);
	    	startService(intent);
	    	if (mServiceFirstTime==true){
	    		Log.d("ShimmerH","Oncreate3");
				getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
				mServiceFirstTime=false;
			}
	    	
	    }
		
		mCtx=this;
	    registerReceiver(myReceiver,new IntentFilter("com.shimmerresearch.service.MultiShimmerService"));
	    
	    Intent intent=new Intent(this, MultiShimmerPlayService.class);
		getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
//	    Intent sender=getIntent();
//	    String extraData=sender.getExtras().getString("LocalDeviceID");
//	    mCurrentDevice=extraData;
//	    setTitle("CMD: " + mCurrentDevice);
//		mCurrentSlot=sender.getExtras().getInt("CurrentSlot");
//	    Log.d("Shimmer","Create MC:  " + extraData);
		final Button button_socket = (Button) findViewById(R.id.connect_socket);
		button_socket.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Perform action on click
            	Log.d("Gaitroid", "click button_socket");
            	IOCallback io = new BasicExample();
        		
        		SocketIO socket = null ;
        		try {
        			socket = new SocketIO("http://192.168.237.150:3000/");
        			socket.connect(io);
        		} catch (MalformedURLException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		try {
					socket.emit("msg", new JSONObject().put("key", "hello world"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		final Button button_bluetooth = (Button) findViewById(R.id.connect_bluetooth);
		button_bluetooth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Log.d("Gaitroid", "click button_bluetooth");
            	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter == null) {
                	Toast.makeText(context, "Device does not support Bluetooth\nExiting...", Toast.LENGTH_LONG).show();
                	finish();
                }
                    
            	if(!mBluetoothAdapter.isEnabled()) {     	
                	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            	}
            	
            	
            	// Bind service
            	//Intent intent=new Intent(GaitroidMain.this, MultiShimmerPlayService.class);
    			//getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
    		    
    		    Intent mainCommandIntent=new Intent(GaitroidMain.this,DeviceListActivity.class);
    	 		startActivityForResult(mainCommandIntent, GaitroidMain.REQUEST_CONNECT_SHIMMER);
            }
        });
		
		final Button button_disconnect = (Button) findViewById(R.id.button_disconnect);
		button_disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	mService.disconnectAllDevices();
            }
        });
		
		final Button button_start_streaming = (Button) findViewById(R.id.button_start_streaming);
		button_start_streaming.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Intent mainCommandIntent=new Intent(GaitroidMain.this,ConfigureActivity.class);
				  startActivityForResult(mainCommandIntent, GaitroidMain.REQUEST_CONFIGURE_SHIMMER);
            }
        });
		
		final Button button_start = (Button) findViewById(R.id.button_start);
		button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	// Bind service
            	
            	mService.startStreamingAllDevicesGetSensorNames();

        	    Intent mainCommandIntent=new Intent(GaitroidMain.this,GraphActivity.class);
        	    mainCommandIntent.putExtra("BluetoothAddress",mCurrentDevice);
				startActivityForResult(mainCommandIntent, GaitroidMain.REQUEST_GRAPH_SHIMMER);
            }
        });
		
		final Button button_stop = (Button) findViewById(R.id.button_stop);
		button_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	// Bind service
            	mService.stopStreamingAllDevices();
            }
        });
		// Sends a string to the server.
		//socket.send("Hello Server");

		// Sends a JSON object to the server.
		//socket.send(new JSONObject().put("key", "value").put("key2",
			//	"another value"));

		// Emits an event to the server.
		//socket.emit("msg", new JSONObject().put("key", "hello world"));
		//socket.on("wzhjay", );
		/*
		try {
			new BasicExample();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gaitroid_main, menu);
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {	
	    	case GaitroidMain.REQUEST_CONNECT_SHIMMER:
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
	        
	    	case GaitroidMain.REQUEST_CONFIGURE_SHIMMER:
	    	if (resultCode == Activity.RESULT_OK) {
//	    		if (mCurrentDevice.equals("All Devices")){
	    			Log.d("Shimmer","Configure Sensors ALL Devices");
 	    			mService.setAllEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone));	
// 	    		} else {
// 	    			mService.setEnabledSensors(data.getExtras().getInt(ConfigureActivity.mDone),mCurrentDevice);
// 	    		}
	    	}
	    	break;
    	}
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
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.shimmerresearch.service.MultiShimmerPlayService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    private BroadcastReceiver myReceiver= new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getIntExtra("ShimmerState", -1)!=-1){		
				//updateListView();
			}
			
		}
    	
    };
    
    public void onPause(){
  	  super.onPause();
  	  
  	  unregisterReceiver(myReceiver);
  	  if(mServiceBind == true){
  		  getApplicationContext().unbindService(mTestServiceConnection);
  	  }
  	 }
    
  public void onResume(){
  	super.onResume();

  	Intent intent=new Intent(GaitroidMain.this, MultiShimmerPlayService.class);
  	Log.d("ShimmerH","on Resume");
  	registerReceiver(myReceiver,new IntentFilter("com.shimmerresearch.service.MultiShimmerPlayService"));
  	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
  }
  
  
  public static void sendDataToServer(int[] dataArray) {
	// Perform action on click
  	Log.d("Gaitroid", "click button_socket");
  	IOCallback io = new BasicExample();
		
		SocketIO socket = null ;
		try {
			socket = new SocketIO("http://192.168.237.150:3000/");
			socket.connect(io);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	  try {
			socket.emit("msg", new JSONObject().put("key", dataArray.toString()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}
