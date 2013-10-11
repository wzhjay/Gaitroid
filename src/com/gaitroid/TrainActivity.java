package com.gaitroid;

import java.util.Collection;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;

import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import root.gast.speech.SpeechRecognizingAndSpeakingActivity;

public class TrainActivity extends SpeechRecognizingAndSpeakingActivity {
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
		setContentView(R.layout.training);
		t= new MyGLSurfaceView(this);
		//Create an Instance with this Activity
		glSurface = (GLSurfaceView)findViewById(R.id.graphics_glsurfaceview);

		//Set our own Renderer
		glSurface.setRenderer(t);
		//Set the GLSurface as View to this Activity
		//setContentView(glSurface);
//		invm3d = new Matrix3d();
//		fm3d = new Matrix3d();
//		m3d = new Matrix3d();
//		invm3d.setIdentity();
		//mShimmerDevice1 = new Shimmer(this, mHandler,"RightArm",false); 
		//mShimmerDevice1.enableOnTheFlyGyroCal(true, 102, 1.2);		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glSurface.onResume();
	}

	/**
	 * Also pause the glSurface
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glSurface.onPause();
	}

	@Override
	protected void receiveWhatWasHeard(List<String> arg0, float[] arg1) {
		// TODO Auto-generated method stub
		
	}
	
//	private final Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
//            case Shimmer.MESSAGE_READ:
//            	if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
//            	    ObjectCluster objectCluster =  (ObjectCluster) msg.obj; 
//            	    if (objectCluster.mMyName=="0"){
//                	    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Axis Angle A");  // first retrieve all the possible formats for the current sensor device
//			 	    	float angle = 0,x = 0,y=0,z=0;
//                	    if (accelXFormats != null){
//			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
//			 	    		angle = (float) formatCluster.mData;
//			 	    	}
//			 	    	Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Axis Angle X");  // first retrieve all the possible formats for the current sensor device
//			 	    	if (accelYFormats != null){
//			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
//			 	    		x=(float) formatCluster.mData;
//			 	    	}
//			 	    	Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Axis Angle Y");  // first retrieve all the possible formats for the current sensor device
//			 	    	if (accelZFormats != null){
//			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
//			 	    		y=(float) formatCluster.mData;
//			 	    	}
//			 	    	Collection<FormatCluster> aaFormats = objectCluster.mPropertyCluster.get("Axis Angle Z");  // first retrieve all the possible formats for the current sensor device
//			 	    	if (aaFormats != null){
//			 	    		FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(aaFormats,"CAL")); // retrieve the calibrated data
//			 	    		z=(float) formatCluster.mData;
//			 	    		AxisAngle4d aa=new AxisAngle4d(x,y,z,angle);
//			 	    		m3d.set(aa);
//			 	    		//flip the rotation matrix (mat = flipMat * mat * flipMat;)
//			 	    		fm3d.setIdentity();
//			 	    		fm3d.m11=-1;
//			 	    		fm3d.m22=-1;
//			 	    		Matrix3d fm3dtemp = new Matrix3d();
//			 	    		fm3dtemp.setIdentity();
//			 	    		fm3d.m11=-1;
//			 	    		fm3d.m22=-1;
//			 	    		fm3d.mul(m3d);
//			 	    		fm3d.mul(fm3dtemp);
//			 	    		
//			 	    		//set function
//			 	    		fm3dtemp.set(invm3d);
//			 	    		fm3dtemp.mul(fm3d);
//			 	    		aa.set(fm3dtemp);
//			 	    		t.setAngleAxis((float) (aa.angle*180/Math.PI), (float)aa.x, (float)aa.y, (float)aa.z);
//			 	    	}
//		 	    	}
//            	}
//                break;
//                 case Shimmer.MESSAGE_TOAST:
//                	Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),Toast.LENGTH_SHORT).show();
//                break;
//
//                 case Shimmer.MESSAGE_STATE_CHANGE:
//                	 switch (msg.arg1) {
//                     	case Shimmer.MSG_STATE_FULLY_INITIALIZED:
//                    	    if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED){
//                    	        Log.d("ConnectionStatus","Successful");
//                    	        mShimmerDevice1.enableOnTheFlyGyroCal(true, 100, 1.2);
//                    	        mShimmerDevice1.enable3DOrientation(true);
//                    	        mShimmerDevice1.writeSamplingRate(51.2);
//                    	        mShimmerDevice1.startStreaming();
//                    	     }
//                    	    break;
//	                    case Shimmer.STATE_CONNECTING:
//	                    	Log.d("ConnectionStatus","Connecting");
//                	        break;
//	                    case Shimmer.STATE_NONE:
//	                    	Log.d("ConnectionStatus","No State");
//	                    	break;
//                     }
//                break;
//                
//            }
//        }
//    };
    
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		
//    	switch (requestCode) {
//
//    	case REQUEST_CONNECT_SHIMMER:
//            // When DeviceListActivity returns with a device to connect
//            if (resultCode == Activity.RESULT_OK) {
//            	if (mShimmerDevice1.getStreamingStatus()==true){
//					mShimmerDevice1.stop();
//				} else {
//					String bluetoothAddress= data.getExtras()
//	                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);;
//					mShimmerDevice1.connect(bluetoothAddress,"default"); 
//					invm3d = new Matrix3d();
//					invm3d.setIdentity();
//					fm3d = new Matrix3d();
//					m3d = new Matrix3d();
//				}
//            }
//            break;
//            
//    	case REQUEST_CONFIGURE_SHIMMER:
//    		if (resultCode == Activity.RESULT_OK) {
//    			if (data.getExtras().getString("Command").equals("Mag")){
//    				if (mShimmerDevice1.getStreamingStatus()){
//    					mShimmerDevice1.stopStreaming();
//    					mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
//    					mShimmerDevice1.startStreaming();
//    				} else {
//    					mShimmerDevice1.enableLowPowerMag(data.getExtras().getBoolean("Enable"));
//    				}
//    			} else if (data.getExtras().getString("Command").equals("Gyro")) {
//    				mShimmerDevice1.enableOnTheFlyGyroCal(data.getExtras().getBoolean("Enable"), 100, 1.2);
//    			}
//            }
//            break;
//    	}
//    }
//
//	@Override
//	protected void receiveWhatWasHeard(List<String> arg0, float[] arg1) {
//		// TODO Auto-generated method stub
//		
//	}
}
