
package com.base.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.base.bean.BaseBean;

public abstract class IssContentProvider extends ContentProvider {
    public static String CONTENT_TYPE = "vnd.android.cursor.dir/iss.db";

    protected SQLiteDatabase mDB;

    public static String AUTHORITY = "com.iss.mobile";

    @Override
    public boolean onCreate() {
        Log.i("liaowenxin", "contentprovider onCreate");
        init();
        IssDBFactory issDBFactory = IssDBFactory.getInstance();
        DBConfig config = IssDBFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not init");
        }
        AUTHORITY = config.authority;
        CONTENT_TYPE = "vnd.android.cursor.dir/" + config.dbName;
        mDB = issDBFactory.open();
        return true;
    }

    public abstract void init();

    public static final String SCHEME = "content";

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableName(uri);
        long result = mDB.insert(tableName, null, values);
        if (result != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return buildResultUri(tableName, result);
    }
    
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        mDB.beginTransaction();
        String tableName = getTableName(uri);
        for(ContentValues value:values){
            mDB.insert(tableName, null, value);
        }
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        return values.length;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        String tableName = getTableName(uri);
        return mDB.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return CONTENT_TYPE;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        int result = mDB.delete(tableName, selection, selectionArgs);
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        int result = mDB.update(tableName, values, selection, selectionArgs);
        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    private Uri buildResultUri(String tableName, long result) {
        final Uri.Builder builder = new Uri.Builder();
        DBConfig config = IssDBFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not init");
        }
        builder.scheme(SCHEME);
        builder.authority(config.authority);
        builder.path(tableName);
        builder.appendPath(String.valueOf(result));
        return builder.build();
    }

    private String getTableName(Uri uri) {
        DBConfig config = IssDBFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not init");
        }
        String path = uri.getLastPathSegment();
        if (!config.tableNameList.contains(path)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return path;
    }

    public static Uri buildUri(String path, String id) {
        final Uri.Builder builder = new Uri.Builder();
        DBConfig config = IssDBFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not init");
        }
        builder.scheme(SCHEME);
        builder.authority(config.authority);
        builder.path(path);
        builder.appendPath(id);
        return builder.build();
    }

    public static Uri buildUri(String path) {
        final Uri.Builder builder = new Uri.Builder();
        DBConfig config = IssDBFactory.getInstance().getDBConfig();
        if (config == null) {
            throw new RuntimeException("db factory not init");
        }
        builder.scheme(SCHEME);
        builder.authority(config.authority);
        builder.path(path);
        return builder.build();
    }

    public static Uri buildUri(Class<? extends BaseBean<?>> c) {
        final String tableName = TableUtil.getTableName(c);
        return buildUri(tableName);
    }

}
