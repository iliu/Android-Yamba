package com.liuapps.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener{
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	TextView textCount;
	Twitter twitter;
	SharedPreferences prefs; 
	
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
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
       
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
		break;
		}
		return true;
		
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

	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		twitter = null;
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

	private Twitter getTwitter() {
		if (twitter == null) {
			twitter = new Twitter(prefs.getString("username", "student"), prefs.getString("password", "password"));
			twitter.setAPIRootUrl(prefs.getString("apiroot", "http://yamba.marakana.com/api"));
		}
		return twitter;
	}
	
	private class PostToTwitter extends AsyncTask<String, Integer, String> {
	
		@Override
		protected String doInBackground(String... statuses) {
			try {
				Twitter.Status status = getTwitter().updateStatus(statuses[0]);
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
}
