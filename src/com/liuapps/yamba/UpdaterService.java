package com.liuapps.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service{
	private static final String TAG = "UpdaterService";
	public static final String NEW_STATUS_INTENT = "com.liuapps.yamba.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "com.liuapps.yamba.NEW_STATUS_EXTRA_COUNT";
	public static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.liuapps.yamba.RECEIVE_TIMELINE_NOTIFICATIONS";
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
		Intent intent;
		public Updater() {
			super("UpdaterService-Updater");
		}
		
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater thread running");
				try {
					//-------- Begin Work -----------
					int newStatusCount = yamba.fetchStatusUpdates();
					if (newStatusCount > 0) {
						Log.d(TAG, "We have new statuses");
						intent = new Intent(NEW_STATUS_INTENT);
						intent.putExtra(NEW_STATUS_EXTRA_COUNT, newStatusCount);
						updaterService.sendBroadcast(intent, RECEIVE_TIMELINE_NOTIFICATIONS);
					}
					//-------- End work -------------
					Log.d(TAG, "Updater thread finished, sleeping thread...");
					Thread.sleep(DELAY);
				} catch (InterruptedException e ) {
					updaterService.runFlag = false;
				}
			}
		}
		
	}

}
