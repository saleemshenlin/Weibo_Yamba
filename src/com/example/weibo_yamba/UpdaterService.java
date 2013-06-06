package com.example.weibo_yamba;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

public class UpdaterService extends Service {
	public static final String NEW_STATUS_INTENT = "com.example.weibo_yamba.NEW_STATUS";
	public static final String NEW_STATUS_EXTRA_COUNT = "NEW_STATUS_EXTRA_COUNT";
	static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.example.weibo_yamba.RECEIVE_TIMELINE_NOTIFICATIONS";
	static final String TAG = "UpdaterService";

	private final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	// private YambaApplication = (YambaApplication
	YambaApplication yambaApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		yambaApplication = (YambaApplication) getApplication();
		Log.e(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		Log.e(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();

		Log.e(TAG, "onStart");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread {

		Intent intent;
		UpdaterService updaterService = UpdaterService.this;

		public Updater() {
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			updaterService.runFlag = true;

			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					StatusesAPI api = yambaApplication
							.getStatusesAPI(updaterService
									.getApplicationContext());
					api.friendsTimeline((long) 0, (long) 0, 20, 1, false,
							FEATURE.ALL, false, new RequestListener() {

								@Override
								public void onIOException(IOException arg0) {
									Log.d(TAG, arg0.toString());
								}

								@Override
								public void onError(WeiboException arg0) {
									Log.d(TAG, arg0.toString());
								}

								@Override
								public void onComplete(String json) {
									Log.d(TAG, "onComplete");
									try {
										int num = 0;
										String latestStatusCreatedAtTime = yambaApplication
												.getStatusData()
												.getLatestStatusCreatedAtTime();
										JSONObject jsonObject = new JSONObject(
												json);
										JSONArray statusesJSONArray = jsonObject
												.getJSONArray("statuses");
										ContentValues values = new ContentValues();
										for (int i = 0; i < statusesJSONArray
												.length(); i++) {
											values.clear();
											JSONObject statusesItem = (JSONObject) statusesJSONArray
													.opt(i);
											JSONObject userItem = statusesItem
													.getJSONObject("user");
											String createAtDate = YambaApplication.str2Date(statusesItem
													.getString("created_at"));
											values.put(StatusData.C_ID,
													statusesItem.getInt("id"));
											values.put(
													StatusData.C_SOURCE,
													statusesItem
															.getString("source"));
											values.put(
													StatusData.C_CREATED_AT,
													YambaApplication
															.str2Date(statusesItem
																	.getString("created_at")));
											values.put(StatusData.C_USER,
													userItem.getString("name"));
											values.put(
													StatusData.C_USER_IMG,
													userItem.getString("avatar_large"));
											values.put(StatusData.C_TEXT,
													statusesItem
															.getString("text"));
											Log.d(TAG, "Got update with id"
													+ statusesItem.getInt("id")
													+ ".Saving");
											yambaApplication.getStatusData()
													.insertOrIgnore(values);
											if (latestStatusCreatedAtTime != null) {
												int result = latestStatusCreatedAtTime
														.compareTo(createAtDate);
												if (result < 0) {
													num++;
												}
											}

										}
										if (num > 0) {
											YambaApplication
													.setStatesCount(num);
											intent = new Intent(
													NEW_STATUS_INTENT);
											intent.putExtra(
													NEW_STATUS_EXTRA_COUNT, num);
											updaterService
													.sendBroadcast(intent,
															RECEIVE_TIMELINE_NOTIFICATIONS);
											Log.d(TAG, "Fetched status updates");
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});

					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

}
