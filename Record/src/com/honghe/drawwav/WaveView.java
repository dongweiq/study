package com.honghe.drawwav;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.honghe.record.AudioFileFunc;
import com.sin.java.waveaccess.WaveFileReader;
import com.sixin.speex.SpeexTool;

public class WaveView extends View {
	private Paint paint = new Paint();

	public WaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		drawWave(canvas, AudioFileFunc.getFilePathByName(SpeexTool.dstName));
	}

	public void drawWave(Canvas canvas, String filePath) {
		// 去锯齿
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setTextSize(20);
		WaveFileReader waveFileReader = new WaveFileReader(filePath);
		if (waveFileReader.isSuccess()) {
			String title = String.format("%s %dHZ %dBit %dCH", filePath, waveFileReader.getSampleRate(), waveFileReader.getBitPerSample(), waveFileReader.getNumChannels());
			paint.setColor(Color.RED);
			canvas.drawText(title, 100, 400, paint);
			int[] color = { Color.GREEN, Color.BLUE, Color.RED };
			canvas.translate(0, 200);
			for (int i = 0; i < waveFileReader.getNumChannels(); ++i) {
				// 获取i声道数据
				int[] data = waveFileReader.getData()[i];
				// 绘图
				drawData2(canvas, data, color[i % color.length]);
			}
		} else {
			System.err.println(filePath + "不是一个正常的wav文件");
		}

	}

	public void drawData(Canvas canvas, int[] data, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		int scale = 720;
		int length = data.length / scale;
		Path path = new Path();
		for (int i = 0; i < scale; i++) {
			Log.e("test", i + " value" + data[i] + " length" + data.length);
			if (i == 0) {
				path.moveTo(i, data[length * i]);
			} else {
				path.lineTo(i, data[length * i]);
			}
		}
		canvas.drawPath(path, paint);
	}

	public void drawData2(Canvas canvas, int[] data, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		int scalex = 720;
		int length = data.length / scalex;
		int scaley = 200;
		float lengthy = (float)200 / (float)32768;

		for (int i = 0; i < scalex; i++) {
			Log.e("test", i + " value" + data[length * i] + " length" + data.length + " lengthy" + lengthy);
			if (i == 0) {
				canvas.translate(i, data[length * i] * lengthy);
			} else {
				canvas.drawLine(i - 1, data[length * (i - 1)] * lengthy, i, data[length * i] * lengthy, paint);
			}
		}
	}
}
