package com.honghe.MyLockers.bean;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.base.bean.BaseBean;
import com.base.db.TableColumn;

public class LockersDetailBean extends BaseBean<LockersDetailBean> {
	private static final long serialVersionUID = 1L;
	@TableColumn(type = TableColumn.Types.TEXT, isNotNull = true)
	public String lockerdetailsid;
	@TableColumn(type = TableColumn.Types.TEXT)
	public String ImageUri;
	@TableColumn(type = TableColumn.Types.TEXT)
	public String LockersDetailName;
	@TableColumn(type = TableColumn.Types.TEXT)
	public String BelongsLockersId;

	@Override
	public LockersDetailBean parseJSON(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockersDetailBean cursorToBean(Cursor cursor) {
		lockerdetailsid = cursor.getString(cursor
				.getColumnIndex("lockerdetailsid"));
		ImageUri = cursor.getString(cursor.getColumnIndex("ImageUri"));
		LockersDetailName = cursor.getString(cursor
				.getColumnIndex("LockersDetailName"));
		BelongsLockersId = cursor.getString(cursor
				.getColumnIndex("BelongsLockersId"));
		return this;
	}

	@Override
	public ContentValues beanToValues() {
		ContentValues values = new ContentValues();
		values.put("lockerdetailsid", lockerdetailsid);
		values.put("ImageUri", ImageUri);
		values.put("LockersDetailName", LockersDetailName);
		values.put("BelongsLockersId", BelongsLockersId);
		return values;
	}

	public String toString() {
		return "lockersid" + lockerdetailsid + "\n ImageUri" + ImageUri
				+ "\n LockersDetailName" + LockersDetailName
				+ "\n BelongsLockersId" + BelongsLockersId;

	}

}
