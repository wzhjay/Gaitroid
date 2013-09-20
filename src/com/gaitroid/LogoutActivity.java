package com.gaitroid;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class LogoutActivity extends Activity{
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		        
		setContentView(R.layout.logout);
		final Context ctx = this;
		final UserDBHandler userDBHandler = new UserDBHandler(ctx); 
		
		final Button logout_dialog_btn = (Button) findViewById(R.id.logout_dialog_btn);
		logout_dialog_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.v("Gaitroid", "confirm logout");
            	userDBHandler.deleteUser();
            	Intent i = new Intent(ctx, LoginActivity.class);
            	i.addFlags(i.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(i);
            	finish();
            }
        });
		
		final Button cancel_dialog_btn = (Button) findViewById(R.id.cancel_dialog_btn);
		cancel_dialog_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	userDBHandler.deleteUser();
            	Intent i = new Intent(ctx, LoginActivity.class);
            	startActivity(i);
            	finish();
            }
        });
	}
}
