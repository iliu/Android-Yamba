package com.liuapps.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	private static final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private SharedPreferences prefs;
	private boolean serviceRunning;
	private StatusData statusData;
	public static final String LOCATION_PROVIDER_NONE = "NONE";
	private AlarmManager alarmManager;
	PendingIntent pendingIntent;
	
	public void pauseAlarm() {
		if (pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
		Log.d(TAG, "pauseAlarm");
	}

	public void setAlarm() {
		long interval = getUpdateInterval();
		Intent intent = new Intent(this, UpdaterIntentService.class);
		pendingIntent = PendingIntent.getService(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (interval != 0) {
			Log.d(TAG, "setAlarm: Setting Alarm to interval " + interval);
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent);
		} else {
			Log.d(TAG, "setAlarm: Stopping Alarm");
			alarmManager.cancel(pendingIntent);
		}
	}
	
	public synchronized int fetchStatusUpdates() {
		List<Twitter.Status> timeline = null;
		Twitter twitter = getTwitter();
		
		if (twitter == null) {
			Log.d(TAG, "Twitter connection not initialized");
			return 0;
		}
		
		try {
			timeline = twitter.getFriendsTimeline();
			ContentValues values = new ContentValues();
			long lastUpdateTime = getStatusData().getLatestStatusCreatedAtTime();
			int newStatusCount = 0;
			
			for (Twitter.Status status : timeline) {
				//Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
				values.clear();
				values.put(DbHelper.C_ID, status.id);
				long statusCreatedAt = status.createdAt.getTime();
				values.put(DbHelper.C_CREATED_AT, statusCreatedAt);
				values.put(DbHelper.C_SOURCE, status.source);
				values.put(DbHelper.C_TEXT, status.text);
				values.put(DbHelper.C_USER, status.user.name);

				statusData.insertOrIgnore(values);
				if (statusCreatedAt > lastUpdateTime)
					newStatusCount++;
			}
			Log.d(TAG, newStatusCount > 0 ? "Got " + newStatusCount + " status updates"
				        : "No new status updates");
			return newStatusCount;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			 Log.e(TAG, "Failed to fetch status updates", e);
			 return 0;
		}
	}

	public SharedPreferences getPrefs() {
		return prefs;
	}
	
	public String getProvider() {
	    return prefs.getString("locationProvider", LOCATION_PROVIDER_NONE);
	  }

	public long getUpdateInterval() {
		return Long.parseLong(prefs.getString("interval", "0"));
	}
	
	public StatusData getStatusData() {
		return statusData;
	}
	
	public synchronized Twitter getTwitter() { // 
	    if (this.twitter == null) {
	      String username = this.prefs.getString("username", "");
	      String password = this.prefs.getString("password", "");
	      String apiRoot = prefs.getString("apiRoot",
	          "http://yamba.marakana.com/api");
	      if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
	          && !TextUtils.isEmpty(apiRoot)) {
	        this.twitter = new Twitter(username, password);
	        this.twitter.setAPIRootUrl(apiRoot);
	      }
	    }
	    return this.twitter;
	  }

	public boolean isServiceRunning () {
		return serviceRunning;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    this.prefs.registerOnSharedPreferenceChangeListener(this);
	    
	    statusData = new StatusData(this);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    
	    Log.i(TAG, "onCreated");
	}

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		this.twitter = null;
		this.setAlarm();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	
		Log.d(TAG, "onTerminate()");
	}

	public void setServiceRunning (boolean serviceRunning){
		this.serviceRunning = serviceRunning;
	}
	
}
