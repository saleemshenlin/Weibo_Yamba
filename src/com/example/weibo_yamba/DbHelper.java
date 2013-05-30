package com.example.weibo_yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "create_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	Context context;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE + " (" + C_ID
				+ " int primary key, " + C_SOURCE + " text, " + C_CREATED_AT
				+ " text, " + C_USER + " text, " + C_TEXT + " text)";
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql:" + sql);
	}

	// if(newVersion != oldVersion)
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exist" + TABLE);
		Log.d(TAG, "onUpgraded");
		onCreate(db);
	}

}
