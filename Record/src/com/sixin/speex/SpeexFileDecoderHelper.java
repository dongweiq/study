/**
 * 
 */
package com.sixin.speex;

import java.io.File;

import android.os.Handler;

/**
 * @author honghe
 * 
 */
public class SpeexFileDecoderHelper {
	private String srcName = null;
	private String dstName = null;
	private SpeexFileDecoder speexdec = null;
	private OnSpeexFileCompletionListener speexListener = null;
	private static final int speexdecode_completion = 1001;
	private static final int speexdecode_error = 1002;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case speexdecode_completion:
				if (speexListener != null) {
					speexListener.onCompletion(speexdec);
				} else {
					System.out.println("司信---------null===speexListener");
				}
				break;
			case speexdecode_error:
				if (speexListener != null) {
					File file = new File(SpeexFileDecoderHelper.this.srcName);
					if (null != file && file.exists()) {
						file.delete();
					}
					speexListener.onError(null);
				}
				break;
			default:
				break;
			}
		};
	};

	public SpeexFileDecoderHelper(String fileName,String dstName, OnSpeexFileCompletionListener splistener) {
		this.speexListener = splistener;
		this.srcName = fileName;
		this.dstName = dstName;
		try {
			speexdec = new SpeexFileDecoder(new File(this.srcName),new File(this.dstName));
		} catch (Exception e) {
			e.printStackTrace();
			File file = new File(SpeexFileDecoderHelper.this.srcName);
			if (null != file && file.exists()) {
				file.delete();
			}
		}
	}

	public void startDecode() {
		RecordDecodeThread rpt = new RecordDecodeThread();
		Thread th = new Thread(rpt);
		th.start();
	}

	public boolean isDecoding = false;

	class RecordDecodeThread extends Thread {

		public void run() {
			try {
				if (speexdec != null) {
					isDecoding = true;
					speexdec.decode();
					if (null != speexdec.getErrmsg()) {
						throw new Exception(speexdec.getErrmsg());
					}
				}
				System.out.println("RecordPlayThread   文件转换完成");
				if (isDecoding) {
					handler.sendEmptyMessage(speexdecode_completion);
				} 
				isDecoding = false;
			} catch (Exception t) {
				t.printStackTrace();
				System.out.println("RecordPlayThread   文件转换出错");
				handler.sendEmptyMessage(speexdecode_error);
				isDecoding = false;
			}
		}
	}

	/**
	 * 结束播放
	 */
	public void stopDecode() {
		isDecoding = false;
	}

	public String getSpxFileName() {
		return this.srcName;
	};
}
