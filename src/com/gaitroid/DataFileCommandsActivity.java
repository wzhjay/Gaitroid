package com.gaitroid;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;






import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
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
							// delete the file
					  		AlertDialog diaBox = AskOption(fileName);
							diaBox.show();
										
					  }

					  if (position==1){
						  // summit and delete file
						  AlertDialog diaBox = AskSubmitOption(fileName);
						  diaBox.show();
					  }
				  }
				});
	 }
	 
	private AlertDialog AskOption(String fn)
	{
		final String fileName = fn;
	    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this) 
	        //set message, title, and icon
	        .setTitle("Delete") 
	        .setMessage("Do you want to Delete") 
	        .setIcon(R.drawable.delete)

	        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) { 
	            	String filePath = Environment.getExternalStorageDirectory()+File.separator+"Gaitroid"+File.separator+fileName;
	            	File f = new File(filePath);
	            	boolean deleted = f.delete();
	            	AlertDialog deleteFeedbackDialogBox = deleteFeedback(deleted);
	            	deleteFeedbackDialogBox.show();
	                dialog.dismiss();
	            }   

	        })

	        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {

	                dialog.dismiss();

	            }
	        })
	        .create();
	        return myQuittingDialogBox;

	}
	
	private AlertDialog AskSubmitOption(String fn)
	{
		final String fileName = fn;
	    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this) 
	        //set message, title, and icon
	        .setTitle("Submit") 
	        .setMessage("Do you want to submit and delete this data log?") 
	        .setIcon(R.drawable.submit)

	        .setPositiveButton("Summit", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) { 
	            	String filePath = Environment.getExternalStorageDirectory()+File.separator+"Gaitroid"+File.separator+fileName;
	            	boolean uploaded = FileManager.uploadFile(filePath, getApplicationContext(), fileName);
					// delete file
	            	AlertDialog submitFeedbackDialogBox = submitFeedback(filePath, uploaded);
	            	submitFeedbackDialogBox.show();
	            }   

	        })

	        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {

	                dialog.dismiss();

	            }
	        })
	        .create();
	        return myQuittingDialogBox;

	}

	private AlertDialog deleteFeedback(boolean deleted)
	{
		String alertMsg = deleted ? "You have deleted the data log file successfully!" : "deleted failed!";

	    AlertDialog deleteFeedbackDialogBox =new AlertDialog.Builder(this) 
	        .setMessage(alertMsg) 
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) { 

	                dialog.dismiss();
	            }   

	        })
	        .create();
	    return deleteFeedbackDialogBox;

	}
	
	private AlertDialog submitFeedback(String filePath, boolean submitted)
	{
		boolean deleted = false;
		if(submitted){
			File f = new File(filePath);
	    	deleted = f.delete();
		}
		
		String alertMsg = deleted ? "You have submitted the data log file successfully!" : "submitted failed!";

	    AlertDialog submitFeedbackDialogBox =new AlertDialog.Builder(this) 
	        .setMessage(alertMsg) 
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) { 

	                dialog.dismiss();
	            }   

	        })
	        .create();
	    return submitFeedbackDialogBox;

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
	      	}

	      	public void onServiceDisconnected(ComponentName arg0) {
	      		// TODO Auto-generated method stub
	      		mServiceBind=false;
	      	}
	        };

}
