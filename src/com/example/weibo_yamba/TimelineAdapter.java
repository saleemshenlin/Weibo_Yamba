package com.example.weibo_yamba;

import java.text.ParseException;

import com.example.weibo_yamba.AsyncImageLoader.AsyncGetImg;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER,
			StatusData.C_TEXT, StatusData.C_SOURCE };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText,
			R.id.textSource };

	public TimelineAdapter(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.row, cursor, FROM, TO);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(row, context, cursor);
		long timestamp = 0;
		Spanned textSource = Html.fromHtml("Sina Weibo");
		try {
			timestamp = YambaApplication.str2Date2long(cursor.getString(cursor
					.getColumnIndex(StatusData.C_CREATED_AT)));
			textSource = Html.fromHtml(cursor.getString(cursor
					.getColumnIndex(StatusData.C_SOURCE)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView textCreateAt = (TextView) row.findViewById(R.id.textCreatedAt);
		TextView textSourceTextView = (TextView) row
				.findViewById(R.id.textSource);
		ImageView imageView = (ImageView) row.findViewById(R.id.yamba_icon);
		textCreateAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
		textSourceTextView.setText(textSource.toString());
		String userimgString = cursor.getString(cursor
				.getColumnIndex(StatusData.C_USER_IMG));
		// AsyncImageLoader.setImageViewFromUrl(userimgString, imageView);
		// bindWiew
		asyncloadImage(imageView, userimgString);
	}

	private void asyncloadImage(ImageView iv_header, String path) {
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
		AsyncGetImg task = asyncImageLoader.new AsyncGetImg(iv_header);
		task.execute(path);

	}

}
