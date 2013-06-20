package com.gaitroid;

import java.net.MalformedURLException;

import com.shimmerresearch.service.MultiShimmerPlayService;
import com.shimmerresearch.service.MultiShimmerPlayService.LocalBinder;

import io.socket.IOCallback;
import io.socket.SocketIO;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class GaitroidMain extends Activity {
	
	static final int REQUEST_ENABLE_BT = 1;
	public final static int REQUEST_CONNECT_SHIMMER=2;
	String mCurrentDevice=null;
	int mCurrentSlot=-1;
	MultiShimmerPlayService mService;
	private boolean mServiceBind=false;
	private double mSamplingRate=-1;
	private int mAccelRange=-1;
	private int mGSRRange=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gaitroid_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		final Button button_socket = (Button) findViewById(R.id.connect_socket);
		button_socket.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Log.d("Gaitroid", "click button_socket");
            	IOCallback io = new BasicExample();
        		
        		SocketIO socket ;
        		try {
        			socket = new SocketIO("http://192.168.2.109:3000/");
        			socket.connect(io);
        		} catch (MalformedURLException e) {
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
            	Intent intent=new Intent(GaitroidMain.this, MultiShimmerPlayService.class);
    			getApplicationContext().bindService(intent,mTestServiceConnection, Context.BIND_AUTO_CREATE);
    		    
    		    Intent mainCommandIntent=new Intent(GaitroidMain.this,DeviceListActivity.class);
    	 		startActivityForResult(mainCommandIntent, GaitroidMain.REQUEST_CONNECT_SHIMMER);
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

}
