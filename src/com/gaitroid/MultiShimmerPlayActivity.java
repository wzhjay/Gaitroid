package com.gaitroid;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;
import com.gaitroid.R;
import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;

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
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;



public class MultiShimmerPlayActivity extends Activity {
  private static final String TAG = "ServicesDemo";
  
  Button buttonStop;

	MultiShimmerPlayService mService;
	private Context mCtx;
	private String currentSelectedBluetoothAddress;
	private String currentSelectedDeviceName;
	private int mCurrentSelectedSlot=-1;
	ArrayList<String> Devices= new ArrayList<String>();
	private String[] devices = new String [] {"Left","Right","All Devices"};

	boolean mServiceBind=false;
	static final int REQUEST_ENABLE_BT = 1;
	public final static int REQUEST_MAIN_COMMAND_SHIMMER=3;
	public final static int REQUEST_CONNECT_SHIMMER=2;
	public final static int REQUEST_COMMANDS_SHIMMER=4;
	public static final int REQUEST_CONFIGURE_SHIMMER = 5;
	public static final int REQUEST_LOGGING_SHIMMER = 6;
	public static final int REQUEST_GRAPH_SHIMMER = 7;
	public static final int REQUEST_CONFIGURE_GRAPH = 8;
	private boolean mServiceFirstTime=true;
	
	
	@Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.connect_main);
    Log.d("ShimmerH","Oncreate");
   
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
    
    
    createDirIfNotExists("multishimmerplay");
    
    
    mCtx=this;
    registerReceiver(myReceiver,new IntentFilter("com.shimmerresearch.service.MultiShimmerService"));
    //final ListView listViewDevices = (ListView) findViewById(R.id.listView1);

    
	// ArrayList<String> devicesList = new ArrayList<String>();  
	// devicesList.addAll( Arrays.asList(devices) );  
 //    ArrayAdapter<String> sR = new ArrayAdapter<String>(this, R.layout.commands_name,devicesList);
	// listViewDevices.setAdapter(sR);
	

	// gaitroid customize listview
    Model.LoadModel();
    final ListView listViewDevices = (ListView) findViewById(R.id.listView_connect_main);
    String[] ids = new String[Model.Items.size()];
    for (int i= 0; i < ids.length; i++){

        ids[i] = Integer.toString(i+1);
    }
    
    ItemAdapter adapter = new ItemAdapter(this,R.layout.commands_name, R.id.rowTextView, ids);
    listViewDevices.setAdapter(adapter);


    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if(mBluetoothAdapter == null) {
    	Toast.makeText(this, "Device does not support Bluetooth\nExiting...", Toast.LENGTH_LONG).show();
    	finish();
    }
        
	if(!mBluetoothAdapter.isEnabled()) {     	
    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	
	
	
	listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			//Object o = listViewDevices.getItemAtPosition(position);
		    //Log.d("Shimmer",o.toString());
		    mCurrentSelectedSlot = position;
		    if(position == 2) {   
		    	Intent mainCommandIntent=new Intent(MultiShimmerPlayActivity.this,MainCommandsActivity.class);
			    mainCommandIntent.putExtra("LocalDeviceID","All Devices");
			    mainCommandIntent.putExtra("CurrentSlot", position);
			    mainCommandIntent.putExtra("requestCode", REQUEST_MAIN_COMMAND_SHIMMER);
     			startActivityForResult(mainCommandIntent, REQUEST_MAIN_COMMAND_SHIMMER);
     		}
     		else {
     			String deviceName;
     			if(position == 1)
     				deviceName = "Right";
     			else
     				deviceName = "Left";
     			Intent leftRightCommandIntent=new Intent(MultiShimmerPlayActivity.this,LeftRightCommandsActivity.class);
			    leftRightCommandIntent.putExtra("LocalDeviceID", deviceName);
			    leftRightCommandIntent.putExtra("CurrentSlot", position);
			    leftRightCommandIntent.putExtra("requestCode", REQUEST_MAIN_COMMAND_SHIMMER);
     			startActivityForResult(leftRightCommandIntent, REQUEST_MAIN_COMMAND_SHIMMER);
     		}
			
		}
	});
	
	// connect socket button
//	final Button button_socket = (Button) findViewById(R.id.button_socket);
//	button_socket.setOnClickListener(new View.OnClickListener() {
//		@Override
//        public void onClick(View v) {
//			IOCallback io = new BasicExample();
//			SocketIO socket = null ;
//			
//			try {
//    			socket = new SocketIO("http://192.168.237.150:3000/");
//    			socket.connect(io);
//    		} catch (MalformedURLException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		}
//			
//			try {
//				socket.emit("msg", new JSONObject().put("key", "hello world"));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//    });
	
	// Jump button
	final Button button_start = (Button) findViewById(R.id.button_start);
	button_start.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        	Intent jumpMainIntent=new Intent(mCtx, GaitroidMain.class);
  	 		startActivityForResult(jumpMainIntent, 0);
        }
    });
  }
	
	  
  public void updateListView(){
	  devices = new String [] {"Left","Right","All Devices"};
	  HashMap<String, Object> tempHMap=mService.mMultiShimmer;
		Collection<Object> colS=tempHMap.values();
		Iterator<Object> iterator = colS.iterator();
		int i=0;
		while (iterator.hasNext()) {
			Shimmer stemp=(Shimmer) iterator.next();
			devices[Integer.parseInt(stemp.getDeviceName())]=stemp.getBluetoothAddress(); //check name which is a number sequence?
			i++;
		}
	  // final ListView listViewDevices = (ListView) findViewById(R.id.listView1);
	  // ArrayList<String> devicesList = new ArrayList<String>();  
	  // devicesList.addAll( Arrays.asList(devices) );  
	  // ArrayAdapter<String> sR = new ArrayAdapter<String>(this, R.layout.commands_name,devicesList);
	  // listViewDevices.setAdapter(sR);
		Model.LoadModel();
	    final ListView listViewDevices = (ListView) findViewById(R.id.listView_connect_main);
	    String[] ids = new String[Model.Items.size()];
	    for (int j= 0; j < ids.length; j++){

	        ids[j] = Integer.toString(j+1);
	    }
	    
	    ItemAdapter adapter = new ItemAdapter(this,R.layout.commands_name, R.id.rowTextView, ids);
	    listViewDevices.setAdapter(adapter);
  }
  
  public void onPause(){
	  super.onPause();
	  
	  unregisterReceiver(myReceiver);
	  if(mServiceBind == true){
		  getApplicationContext().unbindService(mTestServiceConnection);
	  }
	 }
  
public void onResume(){
	super.onResume();

	Intent intent=new Intent(MultiShimmerPlayActivity.this, MultiShimmerPlayService.class);
	Log.d("ShimmerH","on Resume");
	registerReceiver(myReceiver,new IntentFilter("com.shimmerresearch.service.MultiShimmerPlayService"));
	getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
}
  
  private ServiceConnection mTestServiceConnection = new ServiceConnection() {

  	public void onServiceConnected(ComponentName arg0, IBinder service) {
  		// TODO Auto-generated method stub
  		Log.d(TAG, "srvice connected");
  		LocalBinder binder = (LocalBinder) service;
  		mService = binder.getService();
  		mServiceBind = true;
  		//update the view
  		
  		
		updateListView();
  		
  	}

  	public void onServiceDisconnected(ComponentName arg0) {
  		// TODO Auto-generated method stub
  		mServiceBind = false;
  	}
    };
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {	
    	case REQUEST_MAIN_COMMAND_SHIMMER:
        // When DeviceListActivity returns with a device to connect
    		 if (resultCode == Activity.RESULT_OK) {
	            String address = data.getExtras()
	                    .getString("Address");
	            String currentDeviceName = data.getExtras().getString("CurrentDevice");
	            int currentSlot = data.getExtras().getInt("CurrentSlot");
	            Log.d("ShimmerMainActivity",address);
	            Log.d("ShimmerMainActivity",currentDeviceName);
	            Intent intent=new Intent(this, MultiShimmerPlayService.class);
	            currentSelectedBluetoothAddress=address;
	            currentSelectedDeviceName=currentDeviceName;
	            if (!isMyServiceRunning())
	            {	
	            	intent.putExtra("BluetoothAddress", address);
	            	intent.putExtra("DeviceName", currentDeviceName);
	            	intent.putExtra("CurrentSlot",currentSlot);
	            	startService(intent);
	            	//getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
	            } else {
	          		mService.connectShimmer(currentSelectedBluetoothAddress, Integer.toString(mCurrentSelectedSlot));
	          		//getApplicationContext().unbindService(mTestServiceConnection);
		            //
		            //getApplicationContext().unbindService(mTestServiceConnection);
	            }
    		 }

        break;
    	}
    }
    
    
    private BroadcastReceiver myReceiver= new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getIntExtra("ShimmerState", -1)!=-1){
				
				updateListView();
			}
			
			
			
			
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
    
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
        	Log.d("TravellerLog :: ", "creating file");
            if (!file.mkdirs()) {
                Log.d("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            } else {
            	Log.d("TravellerLog :: ", "file created");
            }
        }
        return ret;
    }
    
  
  }
