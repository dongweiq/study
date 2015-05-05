package com.honghe.MyLockers.bean;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.base.bean.BaseBean;

public class ClassificationBean extends BaseBean<ClassificationBean> {
	private static final long serialVersionUID = 1L;
	public String Id;
	public String ImageUri;
	public String ClassificationName;
	public String count;

	@Override
	public ClassificationBean parseJSON(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassificationBean cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
