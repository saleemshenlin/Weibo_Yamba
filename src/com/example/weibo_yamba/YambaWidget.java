package com.example.weibo_yamba;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {
	private static final String TAG = YambaWidget.class.getSimpleName();
	RemoteViews views;

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String intentActionString = intent.getAction();
		Log.d(TAG, intentActionString);
		if (intentActionString.equals(UpdaterService.NEW_STATUS_INTENT)) {
			Log.d(TAG, "onReceived detected new status update");
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			this.onUpdate(context, appWidgetManager, appWidgetManager
					.getAppWidgetIds(new ComponentName(context,
							YambaWidget.class)));
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "Widget is Updating!");
		Cursor cursor = context.getContentResolver().query(
				StatusProvider.CONTENT_URI, null, null, null,
				StatusData.C_CREATED_AT + " DESC");
		try {
			if (cursor.moveToFirst()) {
				CharSequence user = cursor.getString(cursor
						.getColumnIndex(StatusData.C_USER)); //
				CharSequence createdAt = "";
				try {
					createdAt = DateUtils
							.getRelativeTimeSpanString(
									YambaApplication
											.str2Date2long(cursor.getString(cursor
													.getColumnIndex(StatusData.C_CREATED_AT))),
									System.currentTimeMillis(),
									DateUtils.SECOND_IN_MILLIS,
									DateUtils.FORMAT_ABBREV_ALL);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CharSequence message = cursor.getString(cursor
						.getColumnIndex(StatusData.C_TEXT));
				if (message.length() > 100) {
					message = message.subSequence(0, 100) + "...";
				}
				CharSequence source = Html.fromHtml(
						cursor.getString(cursor
								.getColumnIndex(StatusData.C_SOURCE)))
						.toString();
				for (int appWidgetId : appWidgetIds) {
					Log.d(TAG, "Updating widget " + appWidgetId);
					views = new RemoteViews(context.getPackageName(),
							R.layout.yamba_widget);
					views.setTextViewText(R.id.textUser, user); //
					views.setTextViewText(R.id.textCreatedAt, createdAt);
					views.setTextViewText(R.id.textText, message);
					views.setTextViewText(R.id.textSource, source);
					views.setOnClickPendingIntent(R.id.yamba_icon,
							PendingIntent.getActivity(context, 0, new Intent(
									context, TimelineActivity.class), 0));
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			} else {
				Log.d(TAG, "No data to update");
			}
		} finally {
			cursor.close();
		}
	}

	// Makes HttpURLConnection and returns InputStream
	private InputStream getHttpConnection(String urlString) throws IOException {
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stream;
	}

	/**
	 * 下载线程
	 */

	class DownloadPic extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(params[0]);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			Log.d(TAG, "get bitmap");
			// YambaWidget.bitmap = bitmap;
			// views = new RemoteViews(context.getPackageName(),
			// R.layout.yamba_widget);
		}
	}
}
