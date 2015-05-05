package com.yangyueyue.demo.luyin.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ListView;

/**
 * 
 * @author yang_yueyue
 * 显示对话框的类
 */
public class ShowDialog {
	
	/** ***************************************************
	 * 
	 * 是否保持录音
	 */
	public static void showSaveAudioDialog(Context context){
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle("提示");
		dialog.setMessage("是否保存该录音文件");
		dialog.setButton("保存", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.setButton("不保存", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}
	
	
	/** ***************************************************
	 * 
	 * 提醒对话框
	 */
	public static void showTheAlertDialog(Context context,String text){
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle("提示");
		dialog.setMessage(text);
		dialog.setButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		dialog.show();
	}
}
