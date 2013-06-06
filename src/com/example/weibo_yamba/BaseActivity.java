package com.example.weibo_yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*
 * The base activity with common features shared by TimelineActivity and
 * StatusActivity
 */

public class BaseActivity extends Activity {
	YambaApplication yambaApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		yambaApplication = (YambaApplication) getApplication();
	}

	// Called only once first time menu is clicked on
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemToggleService:
			if (yambaApplication.isServiceRunning()) {
				stopService(new Intent(this, UpdaterService.class));
				yambaApplication.setServiceRunning();
			} else {
				startService(new Intent(this, UpdaterService.class));
				yambaApplication.setServiceRunning();
			}
			break;
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class).addFlags(
					Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
					Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemStatus:
			startActivity(new Intent(this, StatusActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;

		case R.id.itemPurge:
			
		default:
			break;
		}
		return true;
	}

	// Called every time menu is opened
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) { //
		MenuItem toggleItem = menu.findItem(R.id.itemToggleService); //
		if (yambaApplication.isServiceRunning()) { //
			toggleItem.setTitle(R.string.titleStopService);
		} else { //
			toggleItem.setTitle(R.string.titleStartService);
		}
		return true;
	}
}
