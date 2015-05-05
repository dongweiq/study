package com.base.utils;

import android.util.Log;

public class LogUtil {

	public static boolean ISDEBUG = true;
	public static String DEFAULT_TAG = "test";

	public static void e(String tag, String msg) {
		if (ISDEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (ISDEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String msg) {
		if (ISDEBUG) {
			Log.e(DEFAULT_TAG, msg);
		}
	}

	public static void d(String msg) {
		if (ISDEBUG) {
			Log.d(DEFAULT_TAG, msg);
		}
	}

	public static void d(String message, Object... args) {
		if (ISDEBUG) {
			d(String.format(message, args));
		}
	}
}
