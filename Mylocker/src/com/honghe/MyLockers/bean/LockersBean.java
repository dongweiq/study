package com.honghe.MyLockers.bean;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.base.bean.BaseBean;
import com.base.db.TableColumn;

public class LockersBean extends BaseBean<LockersBean> {
	private static final long serialVersionUID = 1L;
	@TableColumn(type = TableColumn.Types.TEXT, isNotNull = true)
	public String lockersid;
	@TableColumn(type = TableColumn.Types.TEXT)
	public String ImageUri;
	@TableColumn(type = TableColumn.Types.TEXT)
	public String LockersName;

	@Override
	public LockersBean parseJSON(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockersBean cursorToBean(Cursor cursor) {
		lockersid = cursor.getString(cursor.getColumnIndex("lockersid"));
		ImageUri = cursor.getString(cursor.getColumnIndex("ImageUri"));
		LockersName = cursor.getString(cursor.getColumnIndex("LockersName"));

		return this;
	}

	@Override
	public ContentValues beanToValues() {
		ContentValues values = new ContentValues();
		values.put("lockersid", lockersid);
		values.put("ImageUri", ImageUri);
		values.put("LockersName", LockersName);
		return values;
	}

	public String toString() {
		return "lockersid" + lockersid + "\n ImageUri" + ImageUri + "\n LockersName" + LockersName;

	}

}
