package com.honghe.MyLockers;

import com.honghe.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class TitleActivity extends FragmentActivity {
	public LinearLayout layout_view_contenner;
	public TextView tvTitle, tvRight, tvLeft;
	public RelativeLayout rvLeft, rvRight;
	public RelativeLayout Relayout_titleact;
	public ImageView iv_back, iv_right;
	public ImageView iv_i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
		initView();
		initData();
		setListener();

	}

	private void init() {
		setContentView(R.layout.activity_title);
		rvLeft = (RelativeLayout) findViewById(R.id.rv_title_left);
		rvRight = (RelativeLayout) findViewById(R.id.rv_title_right);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tvTitle = (TextView) findViewById(R.id.title_textview);
		tvRight = (TextView) findViewById(R.id.tv_right);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		tvLeft = (TextView) findViewById(R.id.tv_back);
		iv_i = (ImageView) findViewById(R.id.iv_fileinfo);
		Relayout_titleact = (RelativeLayout) findViewById(R.id.Relayout_titleact);
		layout_view_contenner = (LinearLayout) findViewById(R.id.view_contenner);
	}

	public void addView(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.view_contenner);
		layout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
	}

	public void removeView(View view) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.view_contenner);
		layout.removeView(view);
	}

	public void setBackgroudColor(int color) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.view_contenner);
		layout.setBackgroundColor(color);
	}

	public void setBackgroudResouce(int resid) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.view_contenner);
		layout.setBackgroundResource(resid);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setTextRight(String str) {
		tvRight.setText(str);
	}

	public void hideTitleLayout() {
		Relayout_titleact.setVisibility(View.GONE);
	}

	public void ShowTitleLayout() {
		Relayout_titleact.setVisibility(View.VISIBLE);
	}

	public int getTitleLayoutVisibility() {
		return Relayout_titleact.getVisibility();
	}

	protected abstract void initView();

	protected abstract void initData();

	protected abstract void setListener();
}
