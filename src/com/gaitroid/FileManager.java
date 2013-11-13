package com.gaitroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

public class FileManager {
	
	public static String[] getAllDataFiles() {
		File storage = new File(Environment.getExternalStorageDirectory()+File.separator+"Gaitroid");

		FilenameFilter dataFilter = new FilenameFilter() {
			File f;
			public boolean accept(File dir, String name) {

			    if(name.endsWith(".csv")){
			    	return true;
			    }

			    f = new File(dir.getAbsolutePath()+"/Gaitroid/"+name);
			    return f.isDirectory();
			  }
			};
		
		String listOfFileNames[] = storage.list(dataFilter);
		
		return listOfFileNames;
	}
	
	public static boolean uploadFile(String filePath, Context ctx, String fn){
		boolean uploaded = false;
		final UserDBHandler db = new UserDBHandler(ctx);
		User u = db.getUser();
		String userID = u.getID();
		try {
		    // Set your file path here
		    FileInputStream fstrm = new FileInputStream(filePath);
		    
		    // Set your server page url (and the file title/description)
		    HttpFileUpload hfu = new HttpFileUpload(
		    		MyApplication.getBaseAPIPath() + "dataFileUpload/" + userID,	// upload url
		    		"Gaitroid",		// title
		    		"User data log file",	// description
		    		fn
		    	);

		    hfu.Send_Now(fstrm);
		    uploaded = true;
		  } catch (FileNotFoundException e) {
		    // Error: File not found
			  AlertDialog diaBox = fileUploadFailed(ctx);
			  diaBox.show();
			  uploaded = false;
		  }
		return uploaded;
	}
	
	private static AlertDialog fileUploadFailed(Context ctx)
	{
	    AlertDialog fileUploadFailedDialogBox = new AlertDialog.Builder(ctx) 
	        //set message, title, and icon
	        .setTitle("Alert") 
	        .setMessage("Sorry, Data file upload failed!") 
	        .setIcon(R.drawable.delete)

	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int whichButton) { 
	                dialog.dismiss();
	            }   

	        })
	        .create();
	        return fileUploadFailedDialogBox;

	}
}
