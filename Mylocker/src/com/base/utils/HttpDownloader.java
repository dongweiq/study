package com.base.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;

public class HttpDownloader {
	private URL url = null;

	FileUtils fileUtils = new FileUtils();

	public int downfile(String urlStr, String path, String fileName,Handler handler) {
		if (fileUtils.isFileExist(path + "/" + fileName)) {
			return ConsUtil.what_exitfile;
		} else {
			try {
				return getInputStream(urlStr, path, fileName, handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ConsUtil.what_loadfail;
	}

	// 由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
	public int getInputStream(String urlStr, String path, String fileName,Handler handler) throws IOException {
		InputStream is = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("connection", "Keep-Alive");
			// 设置时长
			urlConn.setConnectTimeout(60 * 1000);
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				is = urlConn.getInputStream();
				int filelenth = urlConn.getContentLength();
				File resultFile = fileUtils.write2SDFromInput(path, fileName,is, filelenth, handler);
				LogUtil.d("下载文件长度======="+filelenth);
				if (resultFile == null) {
					return ConsUtil.what_loadfail;
				} else {
					return ConsUtil.what_loadsuccessful;
				}
			} else {
				return ConsUtil.what_loadfail;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			File file = fileUtils.creatSDFile(path + "/" + fileName);
			if (file != null && file.exists()) {
				file.delete();
			}
			return ConsUtil.what_loadfail;
		}
	}
}
