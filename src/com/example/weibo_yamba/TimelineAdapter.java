package com.example.weibo_yamba;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
			DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	public TimelineAdapter(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.row, cursor, FROM, TO);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(row, context, cursor);
		long timestamp = 0;
		try {
			timestamp = str2Date2long(cursor.getString(cursor
					.getColumnIndex(DbHelper.C_CREATED_AT)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView textCreateAt = (TextView) row.findViewById(R.id.textCreatedAt);
		textCreateAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
		// bindWiew
	}

	public static long str2Date2long(String dateString) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long dateLong = simpleDateFormat.parse(dateString).getTime();
		return dateLong;
	}

}
