package com.liuapps.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
	private static final String TAG = "StatusProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://com.liuapps.yamba.statusprovider");
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.liuapps.yamba.status";
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.liuapps.yamba.mstatus";
	StatusData statusData;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri); // 

		if (id < 0) {
			return statusData.deleteStatuses(selection, selectionArgs);
		} else {
			return statusData.deleteStatuses(DbHelper.C_ID + "=" + id, null);
		}

	}

	@Override
	public String getType(Uri uri) {
		return getId(uri) < 0 ? MULTIPLE_RECORDS_MIME_TYPE : SINGLE_RECORD_MIME_TYPE;
	}

	private long getId(Uri uri) {
		String lastPathSegment = uri.getLastPathSegment();
		if (lastPathSegment != null) {
			try {
				return Long.parseLong(lastPathSegment);
			} catch (NumberFormatException e) {
				// at least we tried
			}
		}
		return -1;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = -1;
		id = statusData.insertOrIgnore(values);
		if (id == -1) {
			throw new RuntimeException(String.format(
					"%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG,
					values, uri));  // 
		} else {
			return ContentUris.withAppendedId(uri, id); // 
		}
	}

	@Override
	public boolean onCreate() {
		statusData = new StatusData(getContext());
		Log.d(TAG, "onCreate");
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query");
		long id = this.getId(uri); // 
		if (id < 0) {
			return statusData.queryStatuses(projection, selection, selectionArgs, null, null, sortOrder); // 
		} else {
			return statusData.queryStatuses(projection, DbHelper.C_ID + "=" + id, null, null, null, null); // 
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		if (id < 0) {
			return statusData.updateStatuses(values, selection, selectionArgs);
		} else {
			return statusData.updateStatuses(values, DbHelper.C_ID + "=" + id, null);
		}

	}

}
