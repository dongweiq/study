package com.base.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.base.bean.BaseBean;

public class IssDBFactory {
	private static final String TAG = IssDBFactory.class.getSimpleName();

	private DBConfig mConfig;
	private SQLiteDatabase mSQLiteDB;

	private IssDBOpenHelper mDBOpenHelper;

	private final Context mContext;
	
	private static IssDBFactory instance ;

	private IssDBFactory(Context context) {
		mContext = context;
	}
	
	public static void init(Context context,DBConfig dbConfig){
	    if(instance==null){
	        instance = new IssDBFactory(context.getApplicationContext());
	        instance.setDBConfig(dbConfig);
	    }
	}
	
	public static IssDBFactory getInstance(){
	    return instance;
	}
	
	
	public void setDBConfig(DBConfig dbConfig){
	    mConfig = dbConfig;
	}
	
	public DBConfig getDBConfig(){
	    return mConfig;
	}

	public SQLiteDatabase open() {
	    if(mSQLiteDB==null){
	        mDBOpenHelper = new IssDBOpenHelper(mContext, mConfig.dbName, null, mConfig.dbVersion);
	        mSQLiteDB = mDBOpenHelper.getWritableDatabase();
	    }
		return mSQLiteDB;
	}

	public void close() {
	    if(mDBOpenHelper!=null){
	        mDBOpenHelper.close();
	    }
	}

	public void beginTransaction() {
	    if(mSQLiteDB==null){
	        mSQLiteDB.beginTransaction();
	    }
	}

	public void endTransaction() {
		if (mSQLiteDB==null&&mSQLiteDB.inTransaction()) {
			mSQLiteDB.endTransaction();
		}
	}

	public void setTransactionSuccessful() {
	    if (mSQLiteDB==null){
	        mSQLiteDB.setTransactionSuccessful();
	    }
	}

	private final class IssDBOpenHelper extends SQLiteOpenHelper {

		public IssDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (Class<? extends BaseBean<?>> table : mConfig.tableList) {
				try {
					for (String statment : TableUtil.getCreateStatments(table)) {
						Log.d(TAG, statment);
						db.execSQL(statment);
					}
				} catch (Throwable e) {
					Log.e(TAG, "Can't create table " + table.getSimpleName());
				}
			}

			/**
			 * 初始化数据
			 */
			// initData();

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "onUpgrade: " + oldVersion + " >> " + newVersion);
			for (Class<? extends BaseBean<?>> table : mConfig.tableList) {
				try {
					db.execSQL("DROP TABLE IF EXISTS " + TableUtil.getTableName(table));
				} catch (Throwable e) {
					Log.e(TAG, "Can't create table " + table.getSimpleName());
				}
			}
			onCreate(db);
		}

	}

	public void cleanTable(String tableName, int maxSize, int batchSize) {
		Cursor cursor = mSQLiteDB.rawQuery("select count(_id) from " + tableName, null);
		if (cursor.getCount() != 0 && cursor.moveToFirst() && !cursor.isAfterLast()) {
			if (cursor.getInt(0) >= maxSize) {
				int deleteSize = maxSize - batchSize;
				mSQLiteDB.execSQL("delete from " + tableName + " where _id in (" + "select _id from " + tableName
						+ " order by _id " + "  limit " + deleteSize + " )");
			}
		}
		cursor.close();
	}

}
