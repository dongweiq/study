/**
 * 
 */
package com.sixin.speex;

import java.io.File;

import android.os.Handler;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
	private OnSpeexCompletionListener speexListener = null;
	private static final int speexplay_completion = 1001;
	private static final int speexplay_error = 1002;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case speexplay_completion:
				if (speexListener != null) {
					speexListener.onCompletion(speexdec);
				} else {
					System.out.println("司信---------null===speexListener");
				}
				break;
			case speexplay_error:
				if (speexListener != null) {
					File file = new File(SpeexPlayer.this.fileName);
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

	public SpeexPlayer(String fileName, OnSpeexCompletionListener splistener) {
		this.speexListener = splistener;
		this.fileName = fileName;
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
			File file = new File(SpeexPlayer.this.fileName);
			if (null != file && file.exists()) {
				file.delete();
			}
		}
	}

	public void startPlay() {
		RecordPlayThread rpt = new RecordPlayThread();
		Thread th = new Thread(rpt);
		th.start();
	}

	public boolean isPlay = false;

	class RecordPlayThread extends Thread {

		public void run() {
			try {
				if (speexdec != null) {
					isPlay = true;
					speexdec.decode();
					if (null != speexdec.getErrmsg()) {
						throw new Exception(speexdec.getErrmsg());
					}
				}
				System.out.println("RecordPlayThread   播放完成");
				if (isPlay) {
					handler.sendEmptyMessage(speexplay_completion);
				} else {
					if (speexdec != null) {
						speexdec.setPaused(true);
					}
					//handler.sendEmptyMessage(speexplay_error);
				}
				isPlay = false;
			} catch (Exception t) {
				t.printStackTrace();
				System.out.println("RecordPlayThread   播放出错");
				speexdec.setPaused(true);
				handler.sendEmptyMessage(speexplay_error);
				isPlay = false;
			}
		}
	}

	/**
	 * 结束播放
	 */
	public void stopPlay() {
		if (speexdec != null) {
			speexdec.setPaused(true);
		}
		isPlay = false;
	}

	public String getSpxFileName() {
		return this.fileName;
	};
}
