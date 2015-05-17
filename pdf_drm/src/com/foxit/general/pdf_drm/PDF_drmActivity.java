package com.foxit.general.pdf_drm;

import com.foxit.general.PdfBaseDef;
import com.foxit.general.PdfDocNative;
import com.foxit.general.PdfPageNative;
import com.foxit.general.RtBaseDef;
import com.foxit.general.RtNative;
import com.foxit.general.ObjectRef;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class PDF_drmActivity extends Activity {
	
	private static String TAG = "PDF_DRM";
	
	protected ObjectRef document = null;
	protected ObjectRef page = null;
	protected ObjectRef renderer = null;
	protected PDF_Foac	m_foac = null;
	private int pageCount = 0;
	private int pageWidth = 0;
	private int pageHeight = 0;
	private Matrix matrix = null;
	private Rect diplayRect = null;
	private MainView mainView = null;
//	private String fileName = "/sdcard/tmp/Foxitdrm_online.pdf";//多页
//	private String fileName = "/sdcard/tmp/FoxitDRM.pdf"; //单页
//	private String fileName = "/sdcard/tmp/穿越玛雅.pdf";   
//	private String fileName = "/sdcard/tmp/高原狼.pdf"; 
//	private String fileName = "/sdcard/tmp/解密300种食物养生妙方.pdf"; 
	private String fileName = "/sdcard/tmp/温柔的力量.pdf";  
	private int CurPageIndex = 0;
	static{
		System.loadLibrary("fsdk");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mainView = new MainView(getApplicationContext());
		setContentView(mainView);

		try {
			
			Display display = getWindowManager().getDefaultDisplay();
			pageWidth = display.getWidth();
			pageHeight = display.getHeight();
			
			RtNative.initLibrary();	
			
			int initialMemory = 4* 10 * 1024 * 1024;
			RtNative.initFixedMemoryManager(initialMemory, RtBaseDef.MEMORYINIT_SCALABLE);
			
			String sn = "XXXXXXXXXXX";	
			String password = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
			RtNative.unlockLibrary(sn.getBytes(), password.getBytes());
			
			document = new ObjectRef();
			int ret = PdfDocNative.loadDoc(fileName, null, document);
			if (ret != RtBaseDef.ERR_SUCCESS) {
				
				Bitmap bmp = Bitmap.createBitmap(pageWidth, pageHeight,	Bitmap.Config.ARGB_8888);
				bmp.eraseColor(Color.WHITE);
				mainView.setImageBitmap(bmp);
				mainView.invalidate();
				
				switch (ret) {
				case RtBaseDef.ERR_ERROR:
					Toast.makeText(getApplicationContext(), "Error of any kind, without specific reason!", Toast.LENGTH_SHORT).show();
					break;
				case RtBaseDef.ERR_FORMAT:
					Toast.makeText(getApplicationContext(), "File not exist or format error!", Toast.LENGTH_SHORT).show();
					break;
				case RtBaseDef.ERR_PARAM:
					Toast.makeText(getApplicationContext(), "Parameter error!", Toast.LENGTH_SHORT).show();
					break;
				case RtBaseDef.ERR_INVALID_LICENSE:
					Toast.makeText(getApplicationContext(), "License error!", Toast.LENGTH_SHORT).show();
					break;
				}
				return;
			}

			m_foac = new PDF_Foac();
			if (m_foac.isFoxitDRM(document))
			{
				m_foac.setDRMFileName(fileName);
				if (m_foac.decrypt(document))
				{
					
				}
			}
			
			pageCount  = PdfPageNative.getPageCount(document);
			
			matrix = new Matrix();
			diplayRect = new Rect();
			diplayRect.set(0, 0, pageWidth, pageHeight);
			displayPage(CurPageIndex);
		} 
		catch (Exception e) {
			/* In this demo, we decide do nothing for exceptions
			 * however, you will want to handle exceptions in some way*/
			postToLog(e.getMessage());
		} 
	}
    
	void displayPage(int index) {
		if (index < 0 && index >= pageCount)
			return;
		
		int ret = RtBaseDef.ERR_ERROR;
	   	if(page != null)
    		ret = PdfPageNative.closePage(page);
	   	
		page = PdfPageNative.loadPage(document, index);
		if(page == null) return;
		
		ret = PdfPageNative.startParsingPage(page, false, -1);
		while (ret == RtBaseDef.ERR_TOBECONTINUED)
			ret = PdfPageNative.continueParsingPage(page, -1);

		PdfPageNative.getPageDisplayingMatrix(page, diplayRect, 0, matrix);

		Bitmap bmp = Bitmap.createBitmap(pageWidth, pageHeight,	Bitmap.Config.ARGB_8888);
		bmp.eraseColor(Color.WHITE);

		renderer = new ObjectRef();
		ret = PdfPageNative.createPageRenderer(page, bmp, renderer);
		ret = PdfPageNative.startRenderingPage(renderer, matrix,
				PdfBaseDef.RENDER_LCD_TEXT, null, -1);
		while (ret == RtBaseDef.ERR_TOBECONTINUED)
			ret = PdfPageNative.continueRenderingPage(page, -1);

		PdfPageNative.stopRenderingPage(renderer);

		mainView.setImageBitmap(bmp);
		mainView.invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		int eventaction = event.getAction();
		float x = event.getX();
		float width = (float) pageWidth;

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			if (x <= width / 2)
				CurPageIndex--;
			else
				CurPageIndex++;
			displayPage(CurPageIndex);
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		try {
			if (page != null)
				PdfPageNative.closePage(page);
			
			if (document != null)
				PdfDocNative.closeDoc(document);
			
			RtNative.destroyMemoryManager();
			
			RtNative.destroyLibrary();
			
		} catch (Exception e){
			System.exit(0);
		}

		super.onDestroy();
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
	
}