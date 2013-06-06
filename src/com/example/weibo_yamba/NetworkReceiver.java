package com.example.weibo_yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	public final static String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isNetworkDown = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (isNetworkDown) {
			Log.d(TAG, "Not connected");
			context.stopService(new Intent(context, UpdaterService.class));
		} else {
			Log.d(TAG, "Connected");
			context.startService(new Intent(context, UpdaterService.class));
		}
	}

}
