package com.honghe.MyLockers.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.honghe.R;
import com.honghe.MyLockers.TitleActivity;
import com.honghe.MyLockers.adapter.LockersDetailAdapter;
import com.honghe.MyLockers.bean.LockersDetailBean;
import com.honghe.MyLockers.db.DBUtil;

public class LockersDetailActivity extends TitleActivity implements OnClickListener {
	private GridView gridView_lockers_detail;
	private ArrayList<LockersDetailBean> beans = new ArrayList<LockersDetailBean>();
	private LockersDetailAdapter lockersDetailAdapter;

	@Override
	protected void initView() {
		addView(View.inflate(this, R.layout.activity_lockers_detail, null));
		gridView_lockers_detail = (GridView) findViewById(R.id.gridView_lockers_detail);
		setTitle("储物柜详情");
	}

	@Override
	protected void initData() {
		lockersDetailAdapter = new LockersDetailAdapter(this);
		gridView_lockers_detail.setAdapter(lockersDetailAdapter);
		lockersDetailAdapter.setDatas(beans);
		getLockerDetails();
	}

	/**
	 * 获取橱柜详情
	 */
	private void getLockerDetails() {
		beans.clear();
		beans.addAll(DBUtil.getAllLockersDetailBean(this));
		lockersDetailAdapter.setDatas(beans);
	}

	@Override
	protected void setListener() {
		rvLeft.setOnClickListener(this);
		gridView_lockers_detail.setOnItemClickListener(new Lockers_Detail_click_listener());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rv_title_left:
			finish();
			break;

		default:
			break;
		}
	}

	private class Lockers_Detail_click_listener implements OnItemClickListener {
		Intent intent = null;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (beans != null && position < beans.size()) {
				//打开物品
				//				intent = new Intent(LockersDetailActivity.this, AddLockerDetailActivity.class);
				//				intent.putExtra("id", beans.get(position).lockerdetailsid);
			} else {
				//添加物品
				intent = new Intent(LockersDetailActivity.this, AddLockerDetailActivity.class);
				startActivityForResult(intent, 1);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				getLockerDetails();
			}
		}
	}

}
