package com.gaitroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
		
		Intent service1 = new Intent(context, MyAlarmService.class);
	       context.startService(service1);
	       
	}

}
