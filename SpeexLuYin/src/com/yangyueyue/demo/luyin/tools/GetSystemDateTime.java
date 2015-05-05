package com.yangyueyue.demo.luyin.tools;

import android.text.format.Time;

/**
 * 得到系统时间
 * @author yang_yueyue
 *
 */
public class GetSystemDateTime {
	
	/** *******************************************
	 * 得到系统时间 
	 */
	public static String now()
	  {
	    Time localTime = new Time();
	    localTime.setToNow();
	    return localTime.format("%Y%m%d%H%M%S");
	  }
}
