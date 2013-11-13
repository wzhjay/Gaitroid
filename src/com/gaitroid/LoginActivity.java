package com.gaitroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i;
        // if user had login before and never logout, jump to home page
        
        /* save the folder in internal memory of phone */
        File storagePath = new File(Environment.getExternalStorageDirectory()+ "/Gaitroid");
        boolean success = true;
        if (!storagePath.exists()) {
            success = storagePath.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure 
        }
        
        Log.d("MyApp", storagePath.toURI().toString());
        
        StrictMode.ThreadPolicy policy = new StrictMode.
        ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        // setting default screen to login.xml
        setContentView(R.layout.login);
        final Context ctx = this;
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final EditText uText = (EditText) findViewById(R.id.username);
        final EditText pText = (EditText) findViewById(R.id.password);
        
        
        final UserDBHandler db = new UserDBHandler(ctx);
        
        if(db.hasUser()) {
        	Log.d("Gaitroid", "Jumping...");
        	i = new Intent(this, MultiShimmerPlayActivity.class);
        	startActivity(i);
            finish();
        }
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Log.v("Gaitroid", uText.getText().toString());
        		Log.v("Gaitroid", pText.getText().toString());
        		
        		String username = uText.getText().toString().trim();
        		String password = pText.getText().toString().trim();
        		String getUserPatient = getUserPatient(username, password);
        		if(getUserPatient != null){
	        		try {
	        			JSONArray jsonArray = new JSONArray(getUserPatient);
	        			Log.i("Gaitriod",
	        					"Number of entries " + jsonArray.length());
	        			for (int i = 0; i < jsonArray.length(); i++) {
	        				JSONObject patient = jsonArray.getJSONObject(i);
	        				Log.i("Gaitriod", patient.toString());
	        				Log.i("Gaitriod", patient.optString("username"));
	        				Log.i("Gaitriod", patient.optString("password"));
	        				JSONArray patientProfile = patient.getJSONArray("patient_profile");
	        				JSONArray patientProfileAddress = patientProfile.getJSONObject(0).getJSONArray("address");
	        				Log.i("Gaitriod", patientProfile.getJSONObject(0).optString("lastname"));
	        				Log.i("Gaitriod", patientProfileAddress.getJSONObject(0).optString("country"));

	        				// Inserting Contacts
	        		        Log.d("Gaitroid", "Inserting .."); 
	        		        db.addUser(new User(patient.optString("_id"),
	        		        		patient.optString("username"),
	        		        		patient.optString("password"),
	        		        		patientProfile.getJSONObject(0).optString("firstname"),
	        		        		patientProfile.getJSONObject(0).optString("lastname"),
	        		        		patientProfile.getJSONObject(0).optString("gender"),
	        		        		patientProfile.getJSONObject(0).optString("email"),
	        		        		patientProfile.getJSONObject(0).optString("phone").toString(),
	        		        		patientProfile.getJSONObject(0).optString("age").toString(),
	        		        		patientProfileAddress.getJSONObject(0).optString("country"),
	        		        		patientProfileAddress.getJSONObject(0).optString("city"),
	        		        		patientProfileAddress.getJSONObject(0).optString("street"),
	        		        		patientProfileAddress.getJSONObject(0).optString("postcode"),
	        		        		patientProfile.getJSONObject(0).optString("created_time")
	        		        		));
	        		        
	        		        Log.d("Gaitroid", "Reading user..");
		        			User u = db.getUser();
		        			String log = "Id: "+u.getID()+" ,username: " + u.getUsername() + " ,createTime: " + u.getCreateTime();
		        			((MyApplication) getApplication()).setUserId(u.getID());
		        			Log.d("Gaitroid", log);
	        			}
	        			
	        			Intent j = new Intent(LoginActivity.this, MultiShimmerPlayActivity.class);
	        			startActivity(j);
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
        		}
        	}
        });
        
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
 
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }
	
	public String getUserPatient(String username, String  password) {
	    StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(((MyApplication) this.getApplication()).getBaseAPIPath() + "patient/" + username + "/" + password);
	    try {
	      HttpResponse response = client.execute(httpGet);
	      StatusLine statusLine = response.getStatusLine();
	      int statusCode = statusLine.getStatusCode();
	      if (statusCode == 200) {
	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        String line;
	        while ((line = rd.readLine()) != null) {
	          builder.append(line);
	        }
	      } else {
	        Log.e("Gaitroid", "Failed to call api");
	      }
	    } catch (ClientProtocolException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return builder.toString();
	  }
}
