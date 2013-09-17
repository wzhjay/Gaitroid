package com.gaitroid;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
        
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final EditText uText = (EditText) findViewById(R.id.username);
        final EditText pText = (EditText) findViewById(R.id.password);
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Log.v("Gaitroid", uText.getText().toString());
        		Log.v("Gaitroid", pText.getText().toString());
        		
        		String username = uText.getText().toString();
        		String password = pText.getText().toString();
        		
        		MongoClient mongoClient = null;
				try {
					mongoClient = new MongoClient("192.168.1.101", 27017);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		DB db = mongoClient.getDB("gaitroid_database");
        		
        		Set<String> collectionNames = db.getCollectionNames();
        		for (String s : collectionNames) {
        			Log.v("Gaitroid", s);
        		}
        		DBCollection patients = db.getCollection("patients");
        		DBCollection doctors = db.getCollection("doctors");
        		BasicDBObject searchQuery = new BasicDBObject();
        		searchQuery.put("username", username);
        		DBCursor cursor = patients.find(searchQuery);
        		while (cursor.hasNext()) {
        			Log.v("Gaitroid", cursor.next().toString());
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
}
