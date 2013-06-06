package com.example.weibo_yamba;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.api.StatusesAPI;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private static String CONSUMER_KEY = "1663244227";// 替换为开发者的appkey，例如"1646212860";
	private static String REDIRECT_URL = "http://weibo.com/saleemshenlin";
	public static Oauth2AccessToken accessToken;
	private boolean serviceRunning;
	private static int statesCount = 0;
	StatusData tStatusData = new StatusData(this);
	public static int DPI = 0;

	Weibo mWeibo;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		DisplayMetrics dpi = this.getApplicationContext().getResources()
				.getDisplayMetrics();
		DPI = dpi.densityDpi;
		Log.e(TAG, "onCreate" + DPI);
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

	public void setServiceRunning() {
		if (this.serviceRunning) {
			this.serviceRunning = false;
		} else {
			this.serviceRunning = true;
		}
	}

	public StatusesAPI getStatusesAPI(Context context) {
		Oauth2AccessToken accesstoken = AccessTokenKeeper
				.readAccessToken(context);
		StatusesAPI api = new StatusesAPI(accesstoken);
		return api;
	}

	public StatusData getStatusData() {
		StatusData tStatusData = new StatusData(this);
		return tStatusData;
	}

	public static String str2Date(String str) {
		String date = null;
		String dateArr[] = str.split(" ");
		switch (monthEnum.toMonth(dateArr[1])) {
		case Jan:
			dateArr[1] = "1";
			break;
		case Feb:
			dateArr[1] = "2";
			break;
		case Mar:
			dateArr[1] = "3";
			break;
		case Apr:
			dateArr[1] = "4";
			break;
		case May:
			dateArr[1] = "5";
			break;
		case Jun:
			dateArr[1] = "6";
			break;
		case Jul:
			dateArr[1] = "7";
			break;
		case Aug:
			dateArr[1] = "8";
			break;
		case Sep:
			dateArr[1] = "9";
			break;
		case Oct:
			dateArr[1] = "10";
			break;
		case Nov:
			dateArr[1] = "11";
			break;
		case Dec:
			dateArr[1] = "12";
			break;
		default:
			break;
		}
		// date YYYY-MM-DDDD HH:MM:SS
		date = dateArr[5] + "-" + dateArr[1] + "-" + dateArr[2] + " "
				+ dateArr[3];
		return date;
	}

	public static long str2Date2long(String dateString) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long dateLong = simpleDateFormat.parse(dateString).getTime();
		return dateLong;
	}

	public static int getStatesCount() {
		return statesCount;
	}

	public static void setStatesCount(int statesCount) {
		YambaApplication.statesCount = statesCount;
	}

	public enum monthEnum {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec;
		public static monthEnum toMonth(String str) {
			try {
				return valueOf(str);
			} catch (Exception e) {
				return Jan;
			}
		}
	}

	public static Bitmap getImageBitmap(String url) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(url)
					.getContent());
		} catch (IOException e) {
			Log.e(TAG, "Error getting bitmap", e);
		}
		return bitmap;
	}

}
