package com.honghe.navdemo.view;

import java.util.List;

import com.amap.api.navi.model.NaviLatLng;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PathView extends View {
	private List<NaviLatLng> navilatArray;
	private Paint paint = new Paint();

	public PathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setColor(Color.GREEN);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		drawLine(canvas);
	}

	private void drawLine(Canvas canvas) {
		if (null != navilatArray) {
			for (NaviLatLng navilatlng : navilatArray) {
				// 转换坐标，免得看不到
				float y = ((float) navilatlng.getLatitude() * 1000f - 39870f);
				float x = ((float) navilatlng.getLongitude() * 1000f - 116250f);
				canvas.drawPoint(x, y, paint);
				Log.e("test", "x:" + x + "y+" + y);
			}
		}
	}

	public void setData(List<NaviLatLng> navilatArray) {
		this.navilatArray = navilatArray;
		invalidate();
	}
}
