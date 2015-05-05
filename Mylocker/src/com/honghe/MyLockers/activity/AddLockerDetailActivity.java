package com.honghe.MyLockers.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.base.utils.LogUtil;
import com.base.utils.T;
import com.honghe.R;
import com.honghe.MyLockers.TitleActivity;
import com.honghe.MyLockers.bean.LockersBean;
import com.honghe.MyLockers.bean.LockersDetailBean;
import com.honghe.MyLockers.db.DBUtil;
import com.honghe.MyLockers.util.ConsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddLockerDetailActivity extends TitleActivity implements OnClickListener {
	private ImageView imageView_add_locker;
	private EditText editText_lockerName;
	private Dialog dialog;
	private String cameraPicName;
	private LockersDetailBean bean = new LockersDetailBean();

	@Override
	protected void initView() {
		addView(View.inflate(this, R.layout.activity_add_lockers, null));
		setTitle("添加物品");
		tvRight.setText("保存");
		imageView_add_locker = (ImageView) findViewById(R.id.imageView_add_locker);
		editText_lockerName = (EditText) findViewById(R.id.editText_lockerName);
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择照片");
		builder.setItems(R.array.choosePic, new DialogClickListener());
		dialog = builder.create();
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void setListener() {
		rvLeft.setOnClickListener(this);
		rvRight.setOnClickListener(this);
		imageView_add_locker.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rv_title_left:
			finish();
			break;
		case R.id.rv_title_right:
			//保存
			saveLockerDetail();
			break;
		case R.id.imageView_add_locker:
			//修改图片
			dialog.show();
			break;

		default:
			break;
		}
	}

	/**
	 * 保存物品
	 */
	private void saveLockerDetail() {
		String picPath = bean.ImageUri;
		String name = editText_lockerName.getText().toString().trim();
		if (null != picPath && !TextUtils.isEmpty(name)) {
			bean.LockersDetailName = name;
			bean.lockerdetailsid = System.currentTimeMillis() + "";
			try {
				DBUtil.addLockersDetailBean(this, bean);
				setResult(RESULT_OK);
				finish();
				T.showLong(this, "添加物品成功！");
			} catch (Exception e) {
				T.showLong(this, "添加物品失败！");
				LogUtil.e("增加物品失败！");
				e.printStackTrace();
			}
		} else {
			T.showLong(this, "未选择图片或名称不能为空");
		}
	}

	private class DialogClickListener implements DialogInterface.OnClickListener {
		Intent intent;

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
				cameraPicName = System.currentTimeMillis() + ".jpeg";
				ConsUtil.createAppDir(AddLockerDetailActivity.this);
				File photofile = new File(ConsUtil.picPath + cameraPicName);
				Uri uri = Uri.fromFile(photofile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, 0);
				dialog.dismiss();
				break;
			case 1:
				intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				startActivityForResult(intent, 1);
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
		if (requestCode == 0) {
			//从相机返回
			File picFile = new File(ConsUtil.picPath + cameraPicName);
			if (picFile.exists()) {
				ImageLoader.getInstance().displayImage("file://" + Uri.fromFile(picFile).getPath(), imageView_add_locker);
				bean.ImageUri = picFile.getPath();
			}
		} else if (requestCode == 1) {
			//从相册返回
			if (resultCode == RESULT_OK) {
				//外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData(); //获得图片的uri 
				String[] proj = { MediaStore.Images.Media.DATA };
				//好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(originalUri, proj, null, null, null);
				//按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				//最后根据索引值获取图片路径
				String path = cursor.getString(column_index);
				ImageLoader.getInstance().displayImage("file://" + path, imageView_add_locker);
				bean.ImageUri = path;
			} else {
				Toast.makeText(this, "请重新选择图片", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
