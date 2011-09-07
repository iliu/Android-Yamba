package com.liuapps.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, TextWatcher, LocationListener{
	private static final String TAG = "StatusActivity";
	private static final long LOCATION_MIN_TIME = 3600000; // One hour
	private static final float LOCATION_MIN_DISTANCE = 1000; // One kilometer
	EditText editText;
	Button updateButton;
	TextView textCount;
	LocationManager locationManager;
	Location location;
	String provider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);

		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		textCount = (TextView) findViewById(R.id.textCount);

		updateButton.setOnClickListener(this);
		editText.addTextChangedListener(this);
		_setCharLeftText(140);     
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		provider = yamba.getProvider();
		Log.d(TAG, "onResume: " + provider + " v.s. " + YambaApplication.LOCATION_PROVIDER_NONE);
		
		if (!provider.equals(YambaApplication.LOCATION_PROVIDER_NONE)) {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Log.d(TAG, "onResume: Setup location manager");
		}
		else {
			locationManager = null;
			location = null;
		}

		if (locationManager != null) {
			location = locationManager.getLastKnownLocation(provider);
			if ( location != null) 
				Log.d(TAG, "onResume: Got location: " + location.getLatitude() + ", " + location.getLongitude());
			locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME,
					LOCATION_MIN_DISTANCE, this);
		}
	}

	public void onClick(View w) {
		new PostToTwitter().execute(editText.getText().toString());
		Log.d(TAG, "onClicked");
	}

	@Override
	public void afterTextChanged(Editable s) {
		_setCharLeftText(140 - s.length());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {		
	}

	private void _setCharLeftText (int count) {
		textCount.setText(Integer.toString(count));
		if (count >= 40)
			textCount.setTextColor(Color.GREEN);
		else if (count < 40 && count >= 10 )
			textCount.setTextColor(Color.YELLOW);
		else
			textCount.setTextColor(Color.RED);
	}

	private class PostToTwitter extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... statuses) {
			try {

				if (location != null) { // 
					double latlong[] = {location.getLatitude(), location.getLongitude()};
					yamba.getTwitter().setMyLocation(latlong);
					Log.d(TAG, "Posting location" + latlong[0] + " " + latlong[1]);
				}

				Twitter.Status status = yamba.getTwitter().updateStatus(statuses[0]);
				Log.d(TAG, "updated twitter status");
				return status.text;
			} catch (TwitterException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post";
			}
		}

		// Called when there's a status to be updated
		@Override
		protected void onProgressUpdate(Integer... values) { // 
			super.onProgressUpdate(values);
			// Not used in this case
		}

		// Called once the background activity has completed
		@Override
		protected void onPostExecute(String result) { // 
			Toast.makeText(StatusActivity.this, "Status updated to: " + result, Toast.LENGTH_LONG).show();
			editText.setText("");
			_setCharLeftText(140);
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
		this.location = location;

	}

	@Override
	public void onProviderDisabled(String provider) {	
		if (this.provider.equals(provider))
		      locationManager.removeUpdates(this);

	}

	@Override
	public void onProviderEnabled(String provider) {
		if (this.provider.equals(provider)) {
		      locationManager.requestLocationUpdates(this.provider, LOCATION_MIN_TIME,
		          LOCATION_MIN_DISTANCE, this);
		      Log.d(TAG, "onProviderEnabled: requested location updates");
		}
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}   
}
