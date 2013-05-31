package com.example.weibo_yamba;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends BaseActivity {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
			DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		// get textView
		listTimeline = (ListView) findViewById(R.id.listTimeline);
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
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}

	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// TODO Auto-generated method stub
			if (view.getId() != R.id.textCreatedAt)
				return false;
			long timestamp = 0;
			try {
				timestamp = str2Date2long(cursor.getString(columnIndex));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CharSequence reltime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(reltime);
			return true;
		}

	};

	public static long str2Date2long(String dateString) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long dateLong = simpleDateFormat.parse(dateString).getTime();
		return dateLong;
	}
}
