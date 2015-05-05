package com.yangyueyue.demo.luyin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sixin.speex.SpeexTool;
import com.yangyueyue.demo.luyin.tools.GetSystemDateTime;
import com.yangyueyue.demo.luyin.tools.SDcardTools;
import com.yangyueyue.demo.luyin.tools.ShowDialog;
import com.yangyueyue.demo.luyin.tools.StringTools;

public class LuYinActivity extends Activity {
	private Button buttonStart; // 开始按钮
	private Button buttonStop; // 停止按钮
	private Button buttonDeleted; // 删除按钮
	private TextView textViewLuYinState; // 录音状态
	private ListView listViewAudio; // 显示录音文件的list
	private ArrayAdapter<String> adaperListAudio; // 列表

	private String fileAudioName; // 保存的音频文件的名字
	private MediaRecorder mediaRecorder; // 录音控制
	private String filePath; // 音频保存的文件路径
	private List<String> listAudioFileName; // 音频文件列表
	private boolean isLuYin; // 是否在录音 true 是 false否
	private File fileAudio; // 录音文件
	private File fileAudioList; //列表中的 录音文件
	File dir; //录音文件
	private boolean isSpeex = true;
	private boolean isOpenEdit = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 初始化组件
		initView();
		// 初始化数据
		initData();
		// 设置组件
		setView();
		// 设置事件
		setEvent();

	}

	/* **********************************************************************
	 * 
	 * 初始化组件
	 */
	private void initView() {
		// 开始
		buttonStart = (Button) findViewById(R.id.button_start);
		// 停止
		buttonStop = (Button) findViewById(R.id.button_stop);
		// 删除
		buttonDeleted = (Button) findViewById(R.id.button_delete);
		// 录音状态
		textViewLuYinState = (TextView) findViewById(R.id.text_luyin_state);
		// 显示录音文件的列表
		listViewAudio = (ListView) findViewById(R.id.listViewAudioFile);

	}

	/* ******************************************************************
	 * 
	 * 初始化数据
	 */
	private void initData() {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(LuYinActivity.this, "请插入SD卡以便存储录音", Toast.LENGTH_LONG).show();
			return;
		}

		// 要保存的文件的路径
		filePath = SDcardTools.getSDPath() + "/" + "myAudio";
		// 实例化文件夹
		dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		Log.i("test", "要保存的录音的文件名为" + fileAudioName + "路径为" + filePath);
		if (isSpeex) {
			listAudioFileName = SDcardTools.getFileFormSDcard(dir, ".spx");
		} else {
			listAudioFileName = SDcardTools.getFileFormSDcard(dir, ".mp3");
		}
		adaperListAudio = new ArrayAdapter<String>(LuYinActivity.this, android.R.layout.simple_list_item_1, listAudioFileName);
	}

	/* **************************************************************
	 * 
	 * 设置组件
	 */
	private void setView() {
		buttonStart.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonDeleted.setEnabled(false);
		listViewAudio.setAdapter(adaperListAudio);

	}

	/* ***********************************************************************
	 * 
	 * 设置事件
	 */
	private void setEvent() {

		// 开始按钮
		buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSpeex) {
					startSpeexAudio();
				} else {
					startAudio();
				}
			}
		});

		// 停止按钮
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSpeex) {
					stopSpeexAudio();
				} else {
					stopAudion();
				}
			}
		});

		// 删除按钮
		buttonDeleted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fileAudio != null) {
					showDeleteAudioDialog("是否删除" + fileAudioName + "文件", "不删除", "删除", false);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "该文件不存在");
				}
			}
		});

		//文件列表点击事件
		listViewAudio.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String fileAudioNameList = ((TextView) arg1).getText().toString();
				fileAudioList = new File(filePath + "/" + fileAudioNameList);
				String name = fileAudioList.getAbsolutePath();
				if (!isOpenEdit) {
					if (isSpeex) {
						SpeexTool.playMusic(LuYinActivity.this, name);
					} else {
						openFile(fileAudioList);
					}
				} else {
					try {
						Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(name));
						//			            intent.putExtra("was_get_content_intent",
						//			                    mWasGetContentIntent);
						intent.setClassName("com.ringdroid", "com.ringdroid.RingdroidEditActivity");
						startActivityForResult(intent, 1);
					} catch (Exception e) {
						Log.e("Ringdroid", "Couldn't start editor");
					}
				}

			}
		});
		//文件列表的长按删除事件
		listViewAudio.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.i("test", "长按事件执行了");
				String fileAudioNameList = ((TextView) arg1).getText().toString();
				fileAudioList = new File(filePath + "/" + fileAudioNameList);
				openFile(fileAudioList);
				if (fileAudioList != null) {
					fileAudio = fileAudioList;
					fileAudioName = fileAudioNameList;
					showDeleteAudioDialog("是否删除" + fileAudioName + "文件", "不删除", "删除", false);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "该文件不存在");
				}
				return false;
			}
		});

	}

	/* ****************************************************************
	 * 
	 * 开始录音
	 */
	private void startAudio() {
		// 创建录音频文件
		// 这种创建方式生成的文件名是随机的
		fileAudioName = "audio" + GetSystemDateTime.now() + StringTools.getRandomString(2) + ".mp3";
		mediaRecorder = new MediaRecorder();
		//MediaRecorder.AudioSource.CAMCORDER 外部mic录音
		// 设置录音的来源为麦克风
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(filePath + "/" + fileAudioName);
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			textViewLuYinState.setText("录音中。。。");

			fileAudio = new File(filePath + "/" + fileAudioName);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonDeleted.setEnabled(false);
			listViewAudio.setEnabled(false);
			isLuYin = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* ****************************************************************
	 * 
	 * 开始录音
	 */
	private void startSpeexAudio() {
		// 创建录音频文件
		// 这种创建方式生成的文件名是随机的
		fileAudioName = "audio" + GetSystemDateTime.now() + StringTools.getRandomString(2) + ".spx";
		String name = filePath + "/" + fileAudioName;
		try {
			SpeexTool.start(name);
			textViewLuYinState.setText("录音中。。。");
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonDeleted.setEnabled(false);
			listViewAudio.setEnabled(false);
			isLuYin = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != adaperListAudio) {
			adaperListAudio.notifyDataSetChanged();
		}
	}

	/* ******************************************************
	 * 
	 * 停止录制
	 */
	private void stopAudion() {
		if (null != mediaRecorder) {
			// 停止录音
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			textViewLuYinState.setText("录音停止了");

			// 开始键能够按下
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			// 删除键能按下
			buttonDeleted.setEnabled(true);
			adaperListAudio.add(fileAudioName);

		}
	}

	/* ******************************************************
	 * 
	 * 停止录制
	 */
	private void stopSpeexAudio() {
		if (null != SpeexTool.recorderInstance) {
			// 停止录音
			SpeexTool.stop();
			textViewLuYinState.setText("录音停止了");

			// 开始键能够按下
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			// 删除键能按下
			buttonDeleted.setEnabled(true);
			adaperListAudio.add(fileAudioName);

		}
	}

	/*******************************************************************************************************
	 * 
	 * 是否删除录音文件
	 * 
	 * @param messageString
	 *            //对话框标题
	 * @param button1Title
	 *            //第一个按钮的内容
	 * @param button2Title
	 *            //第二个按钮的内容
	 * @param isExit
	 *            //是否是退出程序
	 */
	public void showDeleteAudioDialog(String messageString, String button1Title, String button2Title, final boolean isExit) {
		AlertDialog dialog = new AlertDialog.Builder(LuYinActivity.this).create();
		dialog.setTitle("提示");
		dialog.setMessage(messageString);
		dialog.setButton(button1Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});
		dialog.setButton2(button2Title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				fileAudio.delete();
				adaperListAudio.remove(fileAudioName);
				fileAudio = null;
				buttonDeleted.setEnabled(false);

				if (isExit) {
					dialog.dismiss();
					System.exit(0);
				}
			}
		});

		dialog.show();
	}

	/*** *************************************************************************************
	 * 
	 * 打开播放录音文件的程序
	 * @param f
	 */
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	private String getMIMEType(File f) {
		String end = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length()).toLowerCase();
		String type = "";
		if (end.equals("mp3") || end.equals("aac") || end.equals("aac") || end.equals("amr") || end.equals("mpeg") || end.equals("mp4")) {
			type = "audio";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/**
	 * ********************************************************
	 * 
	 * 当程序停止的时候
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (null != mediaRecorder && isLuYin) {
			// 停止录音
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;

			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			listViewAudio.setEnabled(true);
			buttonDeleted.setEnabled(false);
		}
		super.onStop();
	}

	/**
	 * 
	 * 
	 *********************************************************************** 
	 * 点击退出按钮时
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mediaRecorder && isLuYin) {
				if (fileAudio != null) {
					showDeleteAudioDialog("是否保存" + fileAudioName + "文件", "保存", "不保存", true);
				} else {
					ShowDialog.showTheAlertDialog(LuYinActivity.this, "该文件不存在");
				}
			} else {
				System.exit(0);
			}
		}
		return true;
	}
}