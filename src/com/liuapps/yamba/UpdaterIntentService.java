package com.liuapps.yamba;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class UpdaterIntentService extends IntentService {
	private static final String TAG = UpdaterIntentService.class.getSimpleName();
	private NotificationManager notificationManager;
	private Notification notification;

	public UpdaterIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent inIntent) {
		Intent intent;

		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // 
		this.notification = new Notification(android.R.drawable.stat_notify_chat,
				"", 0); // 

		Log.d(TAG, "onHandleIntent");
		int newUpdates = 0;
		YambaApplication yamba = (YambaApplication) getApplication();
		newUpdates = yamba.fetchStatusUpdates();

		if (newUpdates > 0) {
			intent = new Intent(UpdaterService.NEW_STATUS_INTENT);
			intent.putExtra(UpdaterService.NEW_STATUS_EXTRA_COUNT, newUpdates);
			this.sendBroadcast(intent, UpdaterService.RECEIVE_TIMELINE_NOTIFICATIONS);
			sendTimelineNotification(newUpdates);
		}
	}

	/**
	 * Creates a notification in the notification bar telling user there are new
	 * messages
	 *
	 * @param timelineUpdateCount
	 *          Number of new statuses
	 */
	private void sendTimelineNotification(int timelineUpdateCount) {
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, -1, new Intent(this, TimelineActivity.class), PendingIntent.FLAG_UPDATE_CURRENT); // 
		this.notification.when = System.currentTimeMillis();  
		this.notification.flags |= Notification.FLAG_AUTO_CANCEL;  
		CharSequence notificationTitle = this.getText(R.string.msgNotificationTitle); 
		CharSequence notificationSummary = this.getString(R.string.msgNotificationMessage, timelineUpdateCount);
		this.notification.setLatestEventInfo(this, notificationTitle,notificationSummary, pendingIntent); // 
		this.notificationManager.notify(0, this.notification);
		
		Log.d(TAG, "sendTimelineNotificationed");
	}

}
