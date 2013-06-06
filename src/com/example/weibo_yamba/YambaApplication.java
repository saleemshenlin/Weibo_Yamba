package com.example.weibo_yamba;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private SharedPreferences prefs;
	private static String CONSUMER_KEY = "1663244227";// 替换为开发者的appkey，例如"1646212860";
	private static String REDIRECT_URL = "http://weibo.com/saleemshenlin";
	private boolean serviceRunning;
	private static int statesCount = 0;
	StatusData tStatusData = new StatusData(this);

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

	public void setServiceRunning() {
		if (this.serviceRunning) {
			this.serviceRunning = false;
		} else {
			this.serviceRunning = true;
		}
	}

	public StatusesAPI getStatusesAPI() {
		StatusesAPI api = new StatusesAPI(SSO_Main.accessToken);
		return api;
	}

	public StatusData getStatusData() {
		StatusData tStatusData = new StatusData(this);
		return tStatusData;
	}

	public synchronized int fetchStatusUpdates() {
		Log.d(TAG, "Fetching status updates");
		StatusesAPI api = this.getStatusesAPI();
		api.friendsTimeline((long) 0, (long) 0, 20, 1, false, FEATURE.ALL,
				false, new RequestListener() {

					@Override
					public void onIOException(IOException arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onError(WeiboException arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onComplete(String json) {
						// TODO Auto-generated method stub
						Log.d(TAG, "onComplete");
						try {
							YambaApplication.setStatesCount(0);
							String latestStatusCreatedAtTime = tStatusData
									.getLatestStatusCreatedAtTime();
							JSONObject jsonObject = new JSONObject(json);
							JSONArray statusesJSONArray = jsonObject
									.getJSONArray("statuses");
							ContentValues values = new ContentValues();
							for (int i = 0; i < statusesJSONArray.length(); i++) {
								values.clear();
								JSONObject statusesItem = (JSONObject) statusesJSONArray
										.opt(i);
								JSONObject userItem = statusesItem
										.getJSONObject("user");
								String createAtDate = str2Date(statusesItem
										.getString("created_at"));
								values.put(StatusData.C_ID,
										statusesItem.getInt("id"));
								values.put(StatusData.C_SOURCE,
										statusesItem.getString("source"));
								values.put(StatusData.C_CREATED_AT,
										str2Date(statusesItem
												.getString("created_at")));
								values.put(StatusData.C_USER,
										userItem.getString("name"));
								values.put(StatusData.C_TEXT,
										statusesItem.getString("text"));
								Log.d(TAG,
										"Got update with id"
												+ statusesItem.getInt("id")
												+ ".Saving");
								tStatusData.insertOrIgnore(values);
								if (latestStatusCreatedAtTime != null) {
									int result = latestStatusCreatedAtTime
											.compareTo(createAtDate);
									if (result < 0) {
										YambaApplication
												.setStatesCount(YambaApplication
														.getStatesCount() + 1);
									}
								}
							}
							Log.d(TAG, "Fetched status updates");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		return YambaApplication.getStatesCount();
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
