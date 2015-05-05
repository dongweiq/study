package com.honghe.MyLockers.app;

import com.honghe.MyLockers.util.ConsUtil;

import android.app.Application;

public class Myapplicaion extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ConsUtil.initImageloader(getApplicationContext());
	}

}
