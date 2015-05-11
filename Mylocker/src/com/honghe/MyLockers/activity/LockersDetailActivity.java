package com.honghe.MyLockers.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.base.utils.T;
import com.honghe.R;
import com.honghe.MyLockers.TitleActivity;
import com.honghe.MyLockers.adapter.LockersDetailAdapter;
import com.honghe.MyLockers.bean.LockersDetailBean;
import com.honghe.MyLockers.db.DBUtil;

public class LockersDetailActivity extends TitleActivity implements
		OnClickListener {
	private GridView gridView_lockers_detail;
	private ArrayList<LockersDetailBean> beans = new ArrayList<LockersDetailBean>();
	private LockersDetailAdapter lockersDetailAdapter;
	private String belongsId;
	private String lockerdetailsid;
	private Dialog dialog;

	@Override
	protected void initView() {
		addView(View.inflate(this, R.layout.activity_lockers_detail, null));
		gridView_lockers_detail = (GridView) findViewById(R.id.gridView_lockers_detail);
		setTitle("储物柜详情");
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择要进行的操作");
		builder.setItems(R.array.edit, new DialogClickListener());
		dialog = builder.create();
	}

	@Override
	protected void initData() {
		if (null != getIntent()) {
			belongsId = getIntent().getStringExtra("id");
		}
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
		beans.addAll(DBUtil.getAllLockersDetailBean(this, belongsId));
		lockersDetailAdapter.setDatas(beans);
	}

	@Override
	protected void setListener() {
		rvLeft.setOnClickListener(this);
		gridView_lockers_detail
				.setOnItemClickListener(new Lockers_Detail_click_listener());
		gridView_lockers_detail
				.setOnItemLongClickListener(new LockersDetailLongClickListener());
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
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (beans != null && position < beans.size()) {
				// 打开物品
				intent = new Intent(LockersDetailActivity.this,
						AddLockerDetailActivity.class);
				intent.putExtra("detailId", beans.get(position).lockerdetailsid);
			} else {
				// 添加物品
				intent = new Intent(LockersDetailActivity.this,
						AddLockerDetailActivity.class);
				intent.putExtra("id", belongsId);
			}
			startActivityForResult(intent, 1);
		}

	}

	private class LockersDetailLongClickListener implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (beans != null && position < beans.size()) {
				// 打开对应的储物柜
				lockerdetailsid = beans.get(position).lockerdetailsid;
				dialog.show();
			}
			return true;
		}

	}

	private class DialogClickListener implements
			DialogInterface.OnClickListener {
		Intent intent;

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:// 编辑
				intent = new Intent(LockersDetailActivity.this,
						AddLockerDetailActivity.class);
				intent.putExtra("detailId", lockerdetailsid);
				startActivityForResult(intent, 1);
				dialog.dismiss();
				break;
			case 1:// 删除
				int isSucess = DBUtil.delLockersDetailBean(
						LockersDetailActivity.this, lockerdetailsid);
				if (isSucess == 0) {
					T.showShort(LockersDetailActivity.this, "删除失败");
				} else {
					T.showShort(LockersDetailActivity.this, "删除成功");
					getLockerDetails();
				}
				dialog.dismiss();
				break;

			default:
				break;
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
