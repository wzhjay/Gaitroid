package com.gaitroid;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import android.os.Environment;
import android.util.Log;

public class FileManager {
	
	public static String[] getAllDataFiles() {
		File storage = new File(Environment.getExternalStorageDirectory()+File.separator+"Gaitroid");

		FilenameFilter dataFilter = new FilenameFilter() {
			File f;
			public boolean accept(File dir, String name) {

			    if(name.endsWith(".dat")){
			    	return true;
			    }

			    f = new File(dir.getAbsolutePath()+"/Gaitroid/"+name);
			    return f.isDirectory();
			  }
			};
		
		String listOfFileNames[] = storage.list(dataFilter);
		
		return listOfFileNames;
	}
}
