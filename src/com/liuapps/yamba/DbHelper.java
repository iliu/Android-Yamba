package com.liuapps.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	  static final String TAG = "DbHelper";
	  static final String DB_NAME = "timeline.db"; // 
	  static final int DB_VERSION = 2; // 
	  static final String TABLE = "timeline"; // 
	  static final String C_ID = BaseColumns._ID;
	  static final String C_CREATED_AT = "created_at";
	  static final String C_SOURCE = "source";
	  static final String C_TEXT = "txt";
	  static final String C_USER = "user";
	  Context context;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, "
	    + C_CREATED_AT + " int, " + C_SOURCE + " text, " + C_USER + " text, " + C_TEXT + " text)";
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("drop table if exists " + TABLE); // drops the old database
	    Log.d(TAG, "onUpdated");
	    onCreate(db); // run onCreate to get new database
	}

}
