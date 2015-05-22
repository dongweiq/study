package com.honghe.record;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import com.honghe.drawwav.DrawWaveActivity;
import com.sixin.speex.OnSpeexFileCompletionListener;
import com.sixin.speex.SpeexFileDecoder;
import com.sixin.speex.SpeexFileDecoderHelper;
import com.sixin.speex.SpeexTool;

import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private final static int FLAG_WAV = 0;
	private final static int FLAG_AMR = 1;
	private final static int FLAG_SPX = 2;
	private int mState = -1; //-1:没再录制，0：录制wav，1：录制amr
	private Button btn_record_wav;
	private Button btn_play_raw;
	private Button btn_play_wav;
	private Button btn_play_raw2;
	private Button btn_play_wav2;
	private Button btn_record_wav2;
	private Button btn_record_amr;
	private Button btn_stop;
	private Button btn_wave_join;
	private Button btn_wave_join_play;
	private Button btn_wave2spx;
	private Button btn_spx2wave;
	private Button btn_playspx;
	private Button btn_record_spx;
	private Button btn_sixin_spx2wave;
	private Button btn_sixin_wave_play;
	private Button btn_sixin_show_wave;
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
		btn_play_raw2 = (Button) this.findViewById(R.id.btn_play_raw2);
		btn_play_wav2 = (Button) this.findViewById(R.id.btn_play_wav2);
		btn_record_wav2 = (Button) this.findViewById(R.id.btn_record_wav2);
		btn_record_amr = (Button) this.findViewById(R.id.btn_record_amr);
		btn_stop = (Button) this.findViewById(R.id.btn_stop);
		btn_wave_join = (Button) this.findViewById(R.id.btn_wave_join);
		btn_wave_join_play = (Button) this.findViewById(R.id.btn_wave_join_play);
		btn_wave2spx = (Button) this.findViewById(R.id.btn_wave2spx);
		btn_spx2wave = (Button) this.findViewById(R.id.btn_spx2wave);
		btn_playspx = (Button) this.findViewById(R.id.btn_playspx);
		btn_record_spx = (Button) this.findViewById(R.id.btn_record_spx);
		btn_sixin_spx2wave = (Button) this.findViewById(R.id.btn_sixin_spx2wave);
		btn_sixin_wave_play = (Button) this.findViewById(R.id.btn_sixin_wave_play);
		btn_sixin_show_wave = (Button) this.findViewById(R.id.btn_sixin_show_wave);
		txt = (TextView) this.findViewById(R.id.text);
	}

	private void setListeners() {
		btn_record_wav.setOnClickListener(this);
		btn_play_raw.setOnClickListener(this);
		btn_play_wav.setOnClickListener(this);
		btn_play_raw2.setOnClickListener(this);
		btn_play_wav2.setOnClickListener(this);
		btn_record_wav2.setOnClickListener(this);
		btn_record_amr.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_wave_join.setOnClickListener(this);
		btn_wave_join_play.setOnClickListener(this);
		btn_wave2spx.setOnClickListener(this);
		btn_spx2wave.setOnClickListener(this);
		btn_playspx.setOnClickListener(this);
		btn_record_spx.setOnClickListener(this);
		btn_sixin_spx2wave.setOnClickListener(this);
		btn_sixin_wave_play.setOnClickListener(this);
		btn_sixin_show_wave.setOnClickListener(this);
	}

	private void init() {
		uiHandler = new UIHandler();
	}

	private void audioTrackPlay(String fileName) {
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
			track = new AudioTrack(AudioManager.STREAM_MUSIC, AudioFileFunc.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, WaveJoin.bufferSizeInBytes,
					AudioTrack.MODE_STREAM);
			raf = new RandomAccessFile(AudioFileFunc.getFilePathByName(fileName), "r");
			raf.seek(0);
			byte[] decoded = new byte[320];
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
			case FLAG_SPX:
				SpeexTool.stop();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_record_wav:
			AudioFileFunc.setRawFileName(WaveJoin.rawName);
			AudioFileFunc.setWavFileName(WaveJoin.waveName);
			record(FLAG_WAV);
			break;
		case R.id.btn_record_wav2:
			AudioFileFunc.setRawFileName(WaveJoin.rawName2);
			AudioFileFunc.setWavFileName(WaveJoin.waveName2);
			record(FLAG_WAV);
			break;
		case R.id.btn_play_wav:
			mediaPlayerPlay(WaveJoin.waveName);
			break;
		case R.id.btn_play_wav2:
			mediaPlayerPlay(WaveJoin.waveName2);
			break;
		case R.id.btn_play_raw:
			new Thread(new Runnable() {
				@Override
				public void run() {
					audioTrackPlay(WaveJoin.rawName);
				}
			}).start();
			break;
		case R.id.btn_play_raw2:
			new Thread(new Runnable() {
				@Override
				public void run() {
					audioTrackPlay(WaveJoin.rawName2);
				}
			}).start();
			break;
		case R.id.btn_record_amr:
			record(FLAG_AMR);
			break;
		case R.id.btn_stop:
			stop();
			break;
		case R.id.btn_wave_join:
			join();
			break;
		case R.id.btn_wave_join_play:
			mediaPlayerPlay(WaveJoin.joinWaveName);
			break;
		case R.id.btn_wave2spx:
			waveSpeex.wave2spx();
			break;
		case R.id.btn_spx2wave:
			waveSpeex.spx2wav();
			break;
		case R.id.btn_playspx:
			mediaPlayerPlay(WaveSpeex.decodedSpxNamewav);
			break;
		case R.id.btn_record_spx:
			SpeexTool.start(AudioFileFunc.getFilePathByName(SpeexTool.fileName));
			mState = FLAG_SPX;
			break;
		case R.id.btn_sixin_spx2wave:
			SpeexTool.decodeSpx(MainActivity.this, AudioFileFunc.getFilePathByName(SpeexTool.fileName), AudioFileFunc.getFilePathByName(SpeexTool.dstName));
			break;
		case R.id.btn_sixin_wave_play:
			mediaPlayerPlay(SpeexTool.dstName);
			break;
		case R.id.btn_sixin_show_wave:
			startActivity(new Intent(MainActivity.this, DrawWaveActivity.class));
			break;

		default:
			break;
		}
	}
}
