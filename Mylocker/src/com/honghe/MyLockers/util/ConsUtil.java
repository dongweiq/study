package com.honghe.MyLockers.util;

import java.io.File;

import com.honghe.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

public class ConsUtil {
	public static String appName = "Mylockers";
	public static String sdPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String appPath = sdPath + "/" + appName;
	public static String picPath = appPath + "/pic";
	public static String logPath = appPath + "/log";
	public static String cachePath = appPath + "/cache";
	public static DisplayImageOptions options;
	
	public static String LockerDetailAdd="1";
	public static String LockerDetailEdit="2";

	public static void createAppDir(Context context) {
		// 创建应用文件夹
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		} else {
			ConsUtil.appPath = context.getCacheDir().getAbsolutePath() + "/" + appName;
		}
		// 应用程序文件夹
		File file = new File(ConsUtil.appPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 应用程序本地图片文件夹
		File file0 = new File(ConsUtil.picPath);
		if (!file0.exists()) {
			file0.mkdirs();
		}
		// 应用程序本地缓存文件夹
		File file1 = new File(ConsUtil.cachePath);
		if (!file1.exists()) {
			file1.mkdirs();
		}
		// 应用程序本地log文件夹
		File file2 = new File(ConsUtil.logPath);
		if (!file2.exists()) {
			file2.mkdirs();
		}
	}

	public static void initImageloader(Context context) {
		ConsUtil.createAppDir(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(1).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024)// 50Mb
				.diskCache(new UnlimitedDiscCache(new File(cachePath)))//自定义缓存路径
				.tasksProcessingOrder(QueueProcessingType.LIFO).imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout_5s,readTimeOut_30s超时时间
				.build();
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).decodingOptions(getDecodingOptions()).showImageOnLoading(R.drawable.imageposition)
				.showImageForEmptyUri(R.drawable.imageposition).showImageOnFail(R.drawable.imageposition).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public static Options getDecodingOptions() {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		return opt;
	}
}
