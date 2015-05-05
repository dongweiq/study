package com.honghe.MyLockers.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.honghe.R;
import com.honghe.MyLockers.TitleActivity;
import com.honghe.MyLockers.adapter.ClassificationAdapter;
import com.honghe.MyLockers.adapter.LockersAdapter;
import com.honghe.MyLockers.bean.ClassificationBean;
import com.honghe.MyLockers.bean.LockersBean;
import com.honghe.MyLockers.db.DBUtil;

public class MainActivity extends TitleActivity implements OnClickListener {
	private ViewFlipper fliper_lockers_classification;
	private GridView gridView_lockers;
	private GridView gridView_classification;
	private ClassificationAdapter classificationAdapter;
	private LockersAdapter lockersAdapter;
	private ArrayList<LockersBean> lockerBeans = new ArrayList<LockersBean>();
	private ArrayList<ClassificationBean> classificationBeans = new ArrayList<ClassificationBean>();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rv_title_right:
			if (tvRight.getText().toString().trim().equals("切换到分类视图")) {
				tvRight.setText("切换到橱柜视图");
				fliper_lockers_classification.showNext();
			} else {
				tvRight.setText("切换到分类视图");
				fliper_lockers_classification.showPrevious();
			}

			break;

		default:
			break;
		}
	}

	@Override
	protected void initView() {
		rvLeft.setVisibility(View.GONE);
		setTitle("我的储物柜");
		tvRight.setText("切换到分类视图");
		addView(View.inflate(this, R.layout.activity_main, null));
		fliper_lockers_classification = (ViewFlipper) findViewById(R.id.fliper_lockers_classification);
		gridView_lockers = (GridView) findViewById(R.id.gridView_lockers);
		gridView_classification = (GridView) findViewById(R.id.gridView_classification);
	}

	@Override
	protected void initData() {
		classificationAdapter = new ClassificationAdapter(this);
		gridView_classification.setAdapter(classificationAdapter);
		lockersAdapter = new LockersAdapter(this);
		gridView_lockers.setAdapter(lockersAdapter);
		classificationAdapter.setDatas(classificationBeans);
		lockersAdapter.setDatas(lockerBeans);
		getLockers();

	}

	private void getLockers() {
		lockerBeans.clear();
		lockerBeans.addAll(DBUtil.getAllLockersBean(this));
		lockersAdapter.setDatas(lockerBeans);
	}

	@Override
	protected void setListener() {
		gridView_lockers.setOnItemClickListener(new LockersclickListener());
		gridView_classification.setOnItemClickListener(new ClassificationclickListener());
		rvRight.setOnClickListener(this);

	}

	/**
	 * 储物柜列表点击监听
	 * @author wanghonghe
	 *
	 */
	private class LockersclickListener implements OnItemClickListener {
		Intent intent = null;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (lockerBeans != null && position < lockerBeans.size()) {
				//打开对应的储物柜
				intent = new Intent(MainActivity.this, LockersDetailActivity.class);
				intent.putExtra("id", lockerBeans.get(position).lockersid);
			} else {
				//添加储物柜
				intent = new Intent(MainActivity.this, AddLockersActivity.class);
			}
			startActivityForResult(intent, 1);
		}

	}

	/**
	 * 分类列表点击监听
	 * @author wanghonghe
	 *
	 */
	private class ClassificationclickListener implements OnItemClickListener {

		Intent intent = null;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (classificationBeans != null && position < classificationBeans.size()) {
				//打开对应的分类
				intent = new Intent(MainActivity.this, ClassificationDetailActivity.class);
				intent.putExtra("id", classificationBeans.get(position).Id);
			} else {
				//添加分类
				intent = new Intent(MainActivity.this, AddClassificationActivity.class);
			}
			startActivityForResult(intent, 1);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				getLockers();
			}
		}
	}

}
