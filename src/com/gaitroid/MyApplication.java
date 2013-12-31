package com.gaitroid;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class MyApplication extends Application {
	
    private static String BaseAPIPath = "http://192.168.1.100:3000/api/";
    private String SocketConnectPath = "http://192.168.1.100:3000/";
//    private static String BaseAPIPath = "http://192.168.237.240:3000/api/";
//    private String SocketConnectPath = "http://192.168.237.240:3000/";
    private String[] BluetoothAddress = {"", ""};
    private static String userID = "";

    public static String getBaseAPIPath() {
        return BaseAPIPath;
    }
    
    public String getSocketConnectPath() {
        return SocketConnectPath;
    }
    
    public void setBluetoothAddress(String deviceName, String bluetoothAddress) {
    	if(deviceName.equals("0")) {
    		BluetoothAddress[0] = bluetoothAddress;
    		Log.v("bluetoothAddress 0", BluetoothAddress[0]);
    	}
    	else if(deviceName.equals("1")) {
    		BluetoothAddress[1] = bluetoothAddress;
    		Log.v("bluetoothAddress 1", BluetoothAddress[1]);
    	}
    }
    
    public String getBluetoothAddress0() {
    	Log.v("bluetoothAddress 0", BluetoothAddress[0]);
    	return BluetoothAddress[0];

    }
    
    public String getBluetoothAddress1() {
    	Log.v("bluetoothAddress 1", BluetoothAddress[1]);
    	return BluetoothAddress[1];

    }

    public void setUserId(String uID) {
        userID = uID;
    }

    public static String getUserId() {
        return userID;
    }

//    public void setSomeVariable(String someVariable) {
//        this.someVariable = someVariable;
//    }
}
