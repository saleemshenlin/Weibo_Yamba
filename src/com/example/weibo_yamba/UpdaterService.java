package com.example.weibo_yamba;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";

	private final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	DbHelper dbHelper;
	SQLiteDatabase db;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.updater = new Updater();
		dbHelper = new DbHelper(this);
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
			YambaApplication yambaApplication = (YambaApplication) updaterService
					.getApplication();
			updaterService.runFlag = true;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					yambaApplication.fetchStatusUpdates();
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
	}

}
