package com.example.weibo_yamba;

import java.io.IOException;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";
	private final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.updater = new Updater();
		Log.e(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		Log.e(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();

		Log.e(TAG, "onStart");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread {
		public Updater() {
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			UpdaterService updaterService = UpdaterService.this;
			updaterService.runFlag = true;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					Log.d(TAG, "Updater ran");
					try {
						// TODO weibo friendsTimeline
						YambaApplication yamApplication = (YambaApplication) getApplication();
						StatusesAPI api = null;
						api = yamApplication.getStatusesAPI();
						api.friendsTimeline((long) 0, (long) 0, 20, 1, false,
								FEATURE.ALL, false, new RequestListener() {

									@Override
									public void onIOException(IOException arg0) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onError(WeiboException arg0) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onComplete(String arg0) {
										// TODO Auto-generated method stub
										Log.d(TAG, "onComplete");
									}
								});

						Log.d(TAG, "Updater ran");
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						updaterService.runFlag = false;
					}
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}

	}

}
