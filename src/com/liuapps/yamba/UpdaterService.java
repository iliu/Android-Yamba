package com.liuapps.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service{
	private static final String TAG = "UpdaterService";
	static final int DELAY = 60000; 
	private boolean runFlag = false;
	private Updater updater;
	YambaApplication yamba;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "onCreated");
		updater = new Updater();
		yamba = (YambaApplication) getApplication();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		updater = null;
		yamba.setServiceRunning(false);
		
		Log.d(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStartCommand");
		
		this.runFlag = true;
		this.updater.start();
		yamba.setServiceRunning(true);
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onBind");
		return null;
	}
	
	private class Updater extends Thread {
		List<Twitter.Status> timeline;
		
		public Updater() {
			super("UpdaterService-Updater");
		}
		
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater thread running");
				try {
					try {
						timeline = yamba.getTwitter().getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					
					for (Twitter.Status status : timeline) {
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
					}
					
					Log.d(TAG, "Updater finished, sleeping thread...");
					Thread.sleep(DELAY);
				} catch (InterruptedException e ) {
					updaterService.runFlag = false;
				}
			}
		}
		
	}

}
