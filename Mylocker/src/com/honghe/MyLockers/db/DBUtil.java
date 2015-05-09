package com.honghe.MyLockers.db;

import java.util.ArrayList;

import com.honghe.MyLockers.bean.LockersBean;
import com.honghe.MyLockers.bean.LockersDetailBean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DBUtil {
	public static Uri URI_MYLOCKER = HongheProvider.buildUri(LockersBean.class);
	public static Uri URI_MYLOCKER_DETAIL = HongheProvider
			.buildUri(LockersDetailBean.class);

	/** lockers增删改查 */
	public static boolean addLockersBean(Context context, LockersBean bean)
			throws Exception {
		int result = updateLockersBean(context, bean);
		if (result == 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = cr.insert(URI_MYLOCKER, bean.beanToValues());
			// 通过返回的uri可以获取到当条数据插入的行号，插入失败为-1
			String path = uri.getLastPathSegment();
			if (path.equals("-1")) {
				throw new Exception("insert error");
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新locker
	 * 
	 * @param context
	 * @param bean
	 * @return
	 */
	public static int updateLockersBean(Context context, LockersBean bean) {
		ContentResolver cr = context.getContentResolver();
		int result = cr.update(URI_MYLOCKER, bean.beanToValues(),
				"lockersid=?", new String[] { bean.lockersid });
		return result;
	}

	/**
	 * 删除某一个locker
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static int delLockersBean(Context context, String id) {
		ContentResolver cr = context.getContentResolver();
		int result = cr
				.delete(URI_MYLOCKER, "lockersid=?", new String[] { id });
		return result;
	}

	/**
	 * 获取某一个locker
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static LockersBean getLockersBean(Context context, String id) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(URI_MYLOCKER, null, "lockersid=?",
				new String[] { id }, null);
		LockersBean bean = null;
		if (cursor.moveToNext()) {
			bean = new LockersBean();
			bean.cursorToBean(cursor);
		}
		cursor.close();
		return bean;
	}

	/**
	 * 获取所有lockers
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<LockersBean> getAllLockersBean(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(URI_MYLOCKER, null, null, null, null);
		ArrayList<LockersBean> beans = new ArrayList<LockersBean>();
		while (cursor.moveToNext()) {
			LockersBean bean = new LockersBean();
			bean.cursorToBean(cursor);
			beans.add(bean);
		}
		cursor.close();
		return beans;
	}

	/** lockers增删改查 结束 */

	/** lockersDetail增删改查 */
	public static boolean addLockersDetailBean(Context context,
			LockersDetailBean bean) throws Exception {
		int result = updateLockersDetailBean(context, bean);
		if (result == 0) {
			ContentResolver cr = context.getContentResolver();
			Uri uri = cr.insert(URI_MYLOCKER_DETAIL, bean.beanToValues());
			// 通过返回的uri可以获取到当条数据插入的行号，插入失败为-1
			String path = uri.getLastPathSegment();
			if (path.equals("-1")) {
				throw new Exception("insert error");
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新lockerdetail
	 * 
	 * @param context
	 * @param bean
	 * @return
	 */
	public static int updateLockersDetailBean(Context context,
			LockersDetailBean bean) {
		ContentResolver cr = context.getContentResolver();
		int result = cr.update(URI_MYLOCKER_DETAIL, bean.beanToValues(),
				"lockerdetailsid=?", new String[] { bean.lockerdetailsid });
		return result;
	}

	/**
	 * 删除某一个lockerdetail
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static int delLockersDetailBean(Context context, String id) {
		ContentResolver cr = context.getContentResolver();
		int result = cr.delete(URI_MYLOCKER_DETAIL, "lockerdetailsid=?",
				new String[] { id });
		return result;
	}

	/**
	 * 获取某一个lockerdetail
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static LockersDetailBean getLockersDetailBean(Context context,
			String id) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(URI_MYLOCKER_DETAIL, null,
				"lockerdetailsid=?", new String[] { id }, null);
		LockersDetailBean bean = null;
		if (cursor.moveToNext()) {
			bean = new LockersDetailBean();
			bean.cursorToBean(cursor);
		}
		cursor.close();
		return bean;
	}

	/**
	 * 获取所有lockersdetail
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<LockersDetailBean> getAllLockersDetailBean(
			Context context, String id) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(URI_MYLOCKER_DETAIL, null,
				"BelongsLockersId=?", new String[] { id }, null);
		ArrayList<LockersDetailBean> beans = new ArrayList<LockersDetailBean>();
		while (cursor.moveToNext()) {
			LockersDetailBean bean = new LockersDetailBean();
			bean.cursorToBean(cursor);
			beans.add(bean);
		}
		cursor.close();
		return beans;
	}
	/** lockersdetail增删改查 结束 */
}
