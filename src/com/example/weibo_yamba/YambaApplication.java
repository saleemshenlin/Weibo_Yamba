package com.example.weibo_yamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.api.StatusesAPI;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private static String CONSUMER_KEY = "1663244227";// 替换为开发者的appkey，例如"1646212860";
	private static String REDIRECT_URL = "http://weibo.com/saleemshenlin";
	private boolean serviceRunning;
	Weibo mWeibo;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.e(TAG, "onCreate");
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.e(TAG, "onTerminate");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		this.mWeibo = null;
	}

	public synchronized Weibo getWeibo() {
		if (mWeibo == null) {
			mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		}
		return this.mWeibo;
	}

	public boolean isServiceRunning() {
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}

	public StatusesAPI getStatusesAPI() {
		StatusesAPI api = new StatusesAPI(SSO_Main.accessToken);
		return api;
	}
}
