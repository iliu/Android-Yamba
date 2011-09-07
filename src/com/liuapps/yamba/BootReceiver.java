package com.liuapps.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UpdaterIntentService.class));
		YambaApplication yamba = (YambaApplication) context.getApplicationContext();
		yamba.setAlarm();
		
		Log.d(TAG, "started service");
	}
}
