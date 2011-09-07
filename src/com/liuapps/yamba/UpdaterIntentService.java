package com.liuapps.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdaterIntentService extends IntentService {
	private static final String TAG = UpdaterIntentService.class.getSimpleName();
	
	public UpdaterIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		int newUpdates = 0;
		YambaApplication yamba = (YambaApplication) getApplication();
		newUpdates = yamba.fetchStatusUpdates();
		
		if (newUpdates > 0) {
			intent = new Intent(UpdaterService.NEW_STATUS_INTENT);
			intent.putExtra(UpdaterService.NEW_STATUS_EXTRA_COUNT, newUpdates);
			this.sendBroadcast(intent, UpdaterService.RECEIVE_TIMELINE_NOTIFICATIONS);
		}

	}

}
