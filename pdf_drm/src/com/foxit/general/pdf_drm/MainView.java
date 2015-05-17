package com.foxit.general.pdf_drm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MainView extends View {

	private Bitmap bitmap;

	public MainView(Context context)
	{
		super(context);
	}

	public MainView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setImageBitmap(Bitmap b) {
		bitmap = b;
	}

	@Override
	protected void onDraw(Canvas canvas) {
	//	super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}
}