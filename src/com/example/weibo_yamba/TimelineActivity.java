package com.example.weibo_yamba;

import java.text.ParseException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	TimelineReceiver receiver;
	IntentFilter filter;
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.example.weibo_yamba.SEND_TIMELINE_NOTIFICATIONS";
	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER,
			StatusData.C_TEXT, StatusData.C_SOURCE };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText,
			R.id.textSource };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		// get textView
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		filter = new IntentFilter("com.example.weibo_yamba.NEW_STATUS");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		yambaApplication.getStatusData().close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		cursor = yambaApplication.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);
		receiver = new TimelineReceiver();
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
		registerReceiver(receiver, filter, SEND_TIMELINE_NOTIFICATIONS, null);
		Toast.makeText(TimelineActivity.this,
				"更新了" + YambaApplication.getStatesCount() + "条微博！",
				Toast.LENGTH_SHORT).show();
		YambaApplication.setStatesCount(0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// TODO Auto-generated method stub
			if (view.getId() == R.id.textCreatedAt) {

				long timestamp = 0;
				long now = System.currentTimeMillis();
				try {
					timestamp = YambaApplication.str2Date2long(cursor
							.getString(columnIndex));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CharSequence reltime = DateUtils.getRelativeTimeSpanString(
						timestamp, now, DateUtils.SECOND_IN_MILLIS,
						DateUtils.FORMAT_ABBREV_ALL);
				((TextView) view).setText(reltime);
				return true;
			} else if (view.getId() == R.id.textSource) {
				Spanned textSource = Html.fromHtml(cursor.getString(cursor
						.getColumnIndex(StatusData.C_SOURCE)));
				((TextView) view).setText(textSource.toString());
				return true;
			} else if (view.getId() == R.id.yamba_icon) {
				String userimgString = cursor.getString(cursor
						.getColumnIndex(StatusData.C_USER_IMG));
				AsyncImageLoader.setImageViewFromUrl(userimgString,
						(ImageView) view);
				return true;
			} else {
				return false;
			}
		}

	};

	class TimelineReceiver extends BroadcastReceiver {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceive");
		}
	}

}
