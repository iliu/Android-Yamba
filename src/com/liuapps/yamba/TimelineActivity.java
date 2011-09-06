package com.liuapps.yamba;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	private static final String TAG = "StatusActivity";
	TextView textTimeline;
	Cursor cursor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.timeline);

		textTimeline = (TextView) findViewById(R.id.textTimeline);
		
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
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		
		String user, text, output;
		while (cursor.moveToNext()) {
			user = cursor.getString(cursor.getColumnIndex(DbHelper.C_USER));
			text = cursor.getString(cursor.getColumnIndex(DbHelper.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			textTimeline.append(output);
		}
	}
}
