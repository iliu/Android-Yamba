package com.liuapps.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {
	protected YambaApplication yamba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yamba = (YambaApplication) getApplication();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemStatus:
			startActivity(new Intent(this, StatusActivity.class));
			break;
		case R.id.itemPurge:
			((YambaApplication) getApplication()).getStatusData().delete();
			Toast.makeText(this, R.string.msgAllDataPurged, Toast.LENGTH_LONG).show();
			break;
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class).addFlags(
			          Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
			                  Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;			
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemToggleService:
			if (yamba.isServiceRunning()) {
				stopService(new Intent(this, UpdaterService.class));
			} else {
				startService(new Intent(this, UpdaterService.class));
			}
			break;

		}
		return true;

	}
	
	 // Called every time menu is opened
	  @Override
	  public boolean onMenuOpened(int featureId, Menu menu) { // 
	    MenuItem toggleItem = menu.findItem(R.id.itemToggleService); // 
	    if (yamba.isServiceRunning()) { // 
	      toggleItem.setTitle(R.string.menuStopService);
	      toggleItem.setIcon(android.R.drawable.ic_media_pause);
	    } else { // 
	      toggleItem.setTitle(R.string.menuStartService);
	      toggleItem.setIcon(android.R.drawable.ic_media_play);
	    }
	    return true;
	  }
}
