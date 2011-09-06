package com.liuapps.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	private static final String TAG = "TimelineActivity";
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
		DbHelper.C_TEXT };  //
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };
	private static final String SEND_TIMELINE_NOTIFICATIONS = "com.liuapps.yamba.SEND_TIMELINE_NOTIFICATIONS";
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	Cursor cursor;
	TimelineReceiver receiver;
	IntentFilter filter;

	private void setTimeline(){
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);

		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);  
		listTimeline.setAdapter(adapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.timeline);

		listTimeline = (ListView) findViewById(R.id.listTimeline);
		receiver = new TimelineReceiver();
		filter = new IntentFilter("com.liuapps.yamba.NEW_STATUS");

		// check to see if preferences is set, if not, redirect user to preference screen first
		if (yamba.getPrefs().getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class)); 
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		
		setTimeline();
		
		registerReceiver(receiver, filter, SEND_TIMELINE_NOTIFICATIONS, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	// View binder constant to inject business logic that converts a timestamp to
	// relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() { // 

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) { // 
			if (view.getId() != R.id.textCreatedAt)
				return false; // 

			// Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex); // 
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
			Log.d(TAG, relTime.toString());// 
			((TextView) view).setText(relTime); // 

			return true; // 
		}

	};

	class TimelineReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			setTimeline();

			Log.d("TimelineReceiver", "onReceived");
		}
		
	}
}
