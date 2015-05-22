package com.honghe.record;

import java.io.File;

import android.media.MediaRecorder;
import android.os.Environment;

public class AudioFileFunc {
	//音频输入-麦克风
	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

	//采用频率
	//44100是目前的标准，但是某些设备仍然支持22050，16000，11025
//	public final static int AUDIO_SAMPLE_RATE = 44100; //44.1KHz,普遍使用的频率   
	public final static int AUDIO_SAMPLE_RATE = 8000; //44.1KHz,普遍使用的频率   
	//录音输出文件
	private  static String AUDIO_RAW_FILENAME = "RawAudio.raw";
	private  static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
	public  static String AUDIO_AMR_FILENAME = "FinalAudio.amr";

	/**
	 * 判断是否有外部存储设备sdcard
	 * @return true | false
	 */
	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 获取麦克风输入的原始音频流文件路径
	 * @return
	 */
	public static String getRawFilePath() {
		String mAudioRawPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioRawPath = fileBasePath + "/" + AUDIO_RAW_FILENAME;
		}

		return mAudioRawPath;
	}
	
	public static void setRawFileName(String fileName) {
		AUDIO_RAW_FILENAME = fileName;
	}
	public static void setWavFileName(String fileName) {
		AUDIO_WAV_FILENAME = fileName;
	}
	public static void setAmrFileName(String fileName) {
		AUDIO_AMR_FILENAME = fileName;
	}

	/**
	 * 获取编码后的WAV格式音频文件路径
	 * @return
	 */
	public static String getWavFilePath() {
		String mAudioWavPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioWavPath = fileBasePath + "/" + AUDIO_WAV_FILENAME;
		}
		return mAudioWavPath;
	}

	/**
	 * 根据文件名获取文件的真是路径
	 * @return
	 */
	public static String getFilePathByName(String fileName) {
		String path = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			path = fileBasePath + "/" + fileName;
		}
		return path;
	}

	/**
	 * 获取编码后的AMR格式音频文件路径
	 * @return
	 */
	public static String getAMRFilePath() {
		String mAudioAMRPath = "";
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mAudioAMRPath = fileBasePath + "/" + AUDIO_AMR_FILENAME;
		}
		return mAudioAMRPath;
	}

	/**
	 * 获取文件大小
	 * @param path,文件的绝对路径
	 * @return
	 */
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists())
			return -1;
		return mFile.length();
	}
}
