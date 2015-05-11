package com.honghe.MyLockers.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.base.utils.T;
import com.honghe.R;
import com.honghe.MyLockers.TitleActivity;
import com.honghe.MyLockers.adapter.ClassificationAdapter;
import com.honghe.MyLockers.adapter.LockersAdapter;
import com.honghe.MyLockers.bean.ClassificationBean;
import com.honghe.MyLockers.bean.LockersBean;
import com.honghe.MyLockers.bean.LockersDetailBean;
import com.honghe.MyLockers.db.DBUtil;
import com.honghe.MyLockers.util.ConsUtil;

public class MainActivity extends TitleActivity implements OnClickListener {
	private ViewFlipper fliper_lockers_classification;
	private GridView gridView_lockers;
	private GridView gridView_classification;
	private ClassificationAdapter classificationAdapter;
	private LockersAdapter lockersAdapter;
	private ArrayList<LockersBean> lockerBeans = new ArrayList<LockersBean>();
	private ArrayList<ClassificationBean> classificationBeans = new ArrayList<ClassificationBean>();
	private Dialog dialog;
	private AlertDialog alertDialog;
	private String lockersId;

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
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择要进行的操作");
		builder.setItems(R.array.edit, new DialogClickListener());
		dialog = builder.create();
		Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("确定要删除？");
		alertBuilder.setMessage("删除储物柜会连储物柜中的物品一同删除！");
		alertBuilder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						delLockers();
						alertDialog.dismiss();
					}
				});
		alertBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		alertDialog = alertBuilder.create();
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
		gridView_lockers
				.setOnItemLongClickListener(new LockersLongClickListener());
		gridView_classification
				.setOnItemClickListener(new ClassificationclickListener());
		rvRight.setOnClickListener(this);

	}

	/**
	 * 储物柜列表点击监听
	 * 
	 * @author wanghonghe
	 *
	 */
	private class LockersclickListener implements OnItemClickListener {
		Intent intent = null;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (lockerBeans != null && position < lockerBeans.size()) {
				// 打开对应的储物柜
				intent = new Intent(MainActivity.this,
						LockersDetailActivity.class);
				intent.putExtra("id", lockerBeans.get(position).lockersid);
			} else {
				// 添加储物柜
				intent = new Intent(MainActivity.this, AddLockersActivity.class);
			}
			startActivityForResult(intent, 1);
		}

	}

	/**
	 * 分类列表点击监听
	 * 
	 * @author wanghonghe
	 *
	 */
	private class ClassificationclickListener implements OnItemClickListener {

		Intent intent = null;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (classificationBeans != null
					&& position < classificationBeans.size()) {
				// 打开对应的分类
				intent = new Intent(MainActivity.this,
						ClassificationDetailActivity.class);
				intent.putExtra("id", classificationBeans.get(position).Id);
			} else {
				// 添加分类
				intent = new Intent(MainActivity.this,
						AddClassificationActivity.class);
			}
			startActivityForResult(intent, 1);
		}

	}

	private class LockersLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (lockerBeans != null && position < lockerBeans.size()) {
				// 打开对应的储物柜
				lockersId = lockerBeans.get(position).lockersid;
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
				intent = new Intent(MainActivity.this, AddLockersActivity.class);
				intent.putExtra("lockersId", lockersId);
				startActivityForResult(intent, 1);
				dialog.dismiss();
				break;
			case 1:// 删除
				ArrayList<LockersDetailBean> beans = DBUtil
						.getAllLockersDetailBean(MainActivity.this, lockersId);
				int count = beans.size();
				if (count > 0) {
					alertDialog.setMessage("您的储物柜中还有" + count
							+ "件物品，删除储物柜会将物品一同删除,确定删除？");
					alertDialog.show();
				} else {
					delLockers();
				}
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 删除储物柜
	 */
	private void delLockers() {
		int isSucess = DBUtil.delLockersBean(MainActivity.this, lockersId);
		if (isSucess == 0) {
			T.showShort(MainActivity.this, "删除失败");
		} else {
			T.showShort(MainActivity.this, "删除成功");
			getLockers();
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
