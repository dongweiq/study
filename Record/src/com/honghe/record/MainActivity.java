package com.honghe.record;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	private final static int FLAG_WAV = 0;
	private final static int FLAG_AMR = 1;
	private int mState = -1; //-1:没再录制，0：录制wav，1：录制amr
	private Button btn_record_wav;
	private Button btn_play_raw;
	private Button btn_play_wav;
	private Button btn_record_wav2;
	private Button btn_record_amr;
	private Button btn_stop;
	private Button btn_wave_join;
	private Button btn_wave_join_play;
	private Button btn_wave2spx;
	private Button btn_spx2wave;
	private Button btn_playspx;
	private TextView txt;
	private UIHandler uiHandler;
	private UIThread uiThread;
	private AudioTrack track = null;
	private RandomAccessFile raf = null;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private WaveSpeex waveSpeex = new WaveSpeex();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewByIds();
		setListeners();
		init();
	}

	private void findViewByIds() {
		btn_record_wav = (Button) this.findViewById(R.id.btn_record_wav);
		btn_play_raw = (Button) this.findViewById(R.id.btn_play_raw);
		btn_play_wav = (Button) this.findViewById(R.id.btn_play_wav);
		btn_record_wav2 = (Button) this.findViewById(R.id.btn_record_wav2);
		btn_record_amr = (Button) this.findViewById(R.id.btn_record_amr);
		btn_stop = (Button) this.findViewById(R.id.btn_stop);
		btn_wave_join = (Button) this.findViewById(R.id.btn_wave_join);
		btn_wave_join_play = (Button) this.findViewById(R.id.btn_wave_join_play);
		btn_wave2spx = (Button) this.findViewById(R.id.btn_wave2spx);
		btn_spx2wave = (Button) this.findViewById(R.id.btn_spx2wave);
		btn_playspx = (Button) this.findViewById(R.id.btn_playspx);
		txt = (TextView) this.findViewById(R.id.text);
	}

	private void setListeners() {
		btn_record_wav.setOnClickListener(btn_record_wav_clickListener);
		btn_play_raw.setOnClickListener(btn_play_raw_clickListener);
		btn_play_wav.setOnClickListener(btn_play_wav_clickListener);
		btn_record_wav2.setOnClickListener(btn_record_wav_clickListener);
		btn_record_amr.setOnClickListener(btn_record_amr_clickListener);
		btn_stop.setOnClickListener(btn_stop_clickListener);
		btn_wave_join.setOnClickListener(btn_join_clickListener);
		btn_wave_join_play.setOnClickListener(btn_wave_join_play_clickListener);
		btn_wave2spx.setOnClickListener(btn_wave2spx_clickListener);
		btn_spx2wave.setOnClickListener(btn_spx2wave_clickListener);
		btn_playspx.setOnClickListener(btn_playspx_clickListener);
	}

	private void init() {
		uiHandler = new UIHandler();
	}

	private Button.OnClickListener btn_record_wav_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_record_wav:
				AudioFileFunc.setRawFileName(WaveJoin.rawName);
				AudioFileFunc.setWavFileName(WaveJoin.waveName);
				break;
			case R.id.btn_record_wav2:
				AudioFileFunc.setRawFileName(WaveJoin.rawName2);
				AudioFileFunc.setWavFileName(WaveJoin.waveName2);
				break;
			default:
				break;
			}
			record(FLAG_WAV);
		}
	};
	private Button.OnClickListener btn_play_raw_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					audioTrackPlay();
				}
			}).start();
		}
	};
	private Button.OnClickListener btn_play_wav_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {

			mediaPlayerPlay(WaveJoin.waveName);

		}
	};

	private void audioTrackPlay() {
		if (null != track) {
			track.stop();
			track.release();
		}
		if (null != raf) {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			track = new AudioTrack(AudioManager.STREAM_MUSIC, AudioFileFunc.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, WaveJoin.bufferSizeInBytes,
					AudioTrack.MODE_STREAM);
			raf = new RandomAccessFile(AudioFileFunc.getFilePathByName(WaveJoin.rawName), "r");
			raf.seek(0);
			byte[] decoded = new byte[160];
			int length = 0;
			while ((length = raf.read(decoded)) != -1) {
				track.write(decoded, 0, length);
				float maxVol = AudioTrack.getMaxVolume();
				track.setStereoVolume(maxVol, maxVol);// 
				track.play();
			}
		} catch (Exception e) {
		} finally {

		}
	}

	private void mediaPlayerPlay(String fileName) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(AudioFileFunc.getFilePathByName(fileName));
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Button.OnClickListener btn_record_amr_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			record(FLAG_AMR);
		}
	};
	private Button.OnClickListener btn_stop_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			stop();
		}
	};
	private Button.OnClickListener btn_join_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			join();
		}
	};
	private Button.OnClickListener btn_wave_join_play_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			mediaPlayerPlay(WaveJoin.joinWaveName);
		}
	};
	private Button.OnClickListener btn_wave2spx_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			waveSpeex.wave2spx();
		}
	};
	private Button.OnClickListener btn_spx2wave_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			waveSpeex.spx2wav();
		}
	};
	private Button.OnClickListener btn_playspx_clickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			mediaPlayerPlay(WaveSpeex.decodedSpxNamewav);
		}
	};

	/**
	 * 音频拼接
	 */
	private void join() {
		WaveJoin.Join();
	}

	/**
	 * 开始录音
	 * @param mFlag，0：录制wav格式，1：录音amr格式
	 */
	private void record(int mFlag) {
		if (mState != -1) {
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", ErrorCode.E_STATE_RECODING);
			msg.setData(b);

			uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			return;
		}
		int mResult = -1;
		switch (mFlag) {
		case FLAG_WAV:
			AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
			mResult = mRecord_1.startRecordAndFile();
			break;
		case FLAG_AMR:
			MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
			mResult = mRecord_2.startRecordAndFile();
			break;
		}
		if (mResult == ErrorCode.SUCCESS) {
			uiThread = new UIThread();
			new Thread(uiThread).start();
			mState = mFlag;
		} else {
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_RECORDFAIL);
			b.putInt("msg", mResult);
			msg.setData(b);

			uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
		}
	}

	/**
	 * 停止录音
	 */
	private void stop() {
		if (mState != -1) {
			switch (mState) {
			case FLAG_WAV:
				AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
				mRecord_1.stopRecordAndFile();
				break;
			case FLAG_AMR:
				MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
				mRecord_2.stopRecordAndFile();
				break;
			}
			if (uiThread != null) {
				uiThread.stopThread();
			}
			if (uiHandler != null)
				uiHandler.removeCallbacks(uiThread);
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putInt("cmd", CMD_STOP);
			b.putInt("msg", mState);
			msg.setData(b);
			uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI 
			mState = -1;
		}
	}

	private final static int CMD_RECORDING_TIME = 2000;
	private final static int CMD_RECORDFAIL = 2001;
	private final static int CMD_STOP = 2002;

	class UIHandler extends Handler {
		public UIHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.d("MyHandler", "handleMessage......");
			super.handleMessage(msg);
			Bundle b = msg.getData();
			int vCmd = b.getInt("cmd");
			switch (vCmd) {
			case CMD_RECORDING_TIME:
				int vTime = b.getInt("msg");
				MainActivity.this.txt.setText("正在录音中，已录制：" + vTime + " s");
				break;
			case CMD_RECORDFAIL:
				int vErrorCode = b.getInt("msg");
				String vMsg = ErrorCode.getErrorInfo(MainActivity.this, vErrorCode);
				MainActivity.this.txt.setText("录音失败：" + vMsg);
				break;
			case CMD_STOP:
				int vFileType = b.getInt("msg");
				switch (vFileType) {
				case FLAG_WAV:
					AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
					long mSize = mRecord_1.getRecordFileSize();
					MainActivity.this.txt.setText("录音已停止.录音文件:" + AudioFileFunc.getWavFilePath() + "\n文件大小：" + mSize);
					break;
				case FLAG_AMR:
					MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
					mSize = mRecord_2.getRecordFileSize();
					MainActivity.this.txt.setText("录音已停止.录音文件:" + AudioFileFunc.getAMRFilePath() + "\n文件大小：" + mSize);
					break;
				}
				break;
			default:
				break;
			}
		}
	};

	class UIThread implements Runnable {
		int mTimeMill = 0;
		boolean vRun = true;

		public void stopThread() {
			vRun = false;
		}

		public void run() {
			while (vRun) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mTimeMill++;
				Log.d("thread", "mThread........" + mTimeMill);
				Message msg = new Message();
				Bundle b = new Bundle();// 存放数据
				b.putInt("cmd", CMD_RECORDING_TIME);
				b.putInt("msg", mTimeMill);
				msg.setData(b);

				MainActivity.this.uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			}

		}
	}
}
