package com.yangyueyue.demo.luyin.tools;

import java.util.Random;

/**
 * ************************************************
 * 
 * @author yang_yueyue
 * 
 *         字符串常用操作的工具类
 * 
 */
public class StringTools {

	/** ************************************************************
	 * 
	 * 获取随机字符串
	 * @param len 字符串的长度
	 * @return
	 */
	public static String getRandomString(int len) {
		String returnStr = "";
		char[] ch = new char[len];
		Random rd = new Random();
		for (int i = 0; i < len; i++) {
			ch[i] = (char) (rd.nextInt(9)+97);
		}
		returnStr = new String(ch);
		return returnStr;
	}

}
