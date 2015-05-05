package com.honghe.MyLockers.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.honghe.R;
import com.honghe.MyLockers.bean.LockersBean;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LockersAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<LockersBean> beans = null;

	public LockersAdapter(Context context) {
		this.mContext = context;
	}

	public void setDatas(ArrayList<LockersBean> beans) {
		if (null != beans) {
			this.beans = beans;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (null == beans) {
			return 1;
		}
		return beans.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lockers, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView_lockers = (ImageView) convertView.findViewById(R.id.imageView_lockers);
			viewHolder.textView_lockers_name = (TextView) convertView.findViewById(R.id.textView_lockers_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (null != beans && position < beans.size()) {
			LockersBean bean = beans.get(position);
			ImageLoader.getInstance().displayImage("file://" + bean.ImageUri, viewHolder.imageView_lockers);
			viewHolder.textView_lockers_name.setText(bean.LockersName);
		} else {
			ImageLoader.getInstance().displayImage(null, viewHolder.imageView_lockers);
			viewHolder.imageView_lockers.setImageResource(R.drawable.icon_add);
			viewHolder.textView_lockers_name.setText("添加储物柜");
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView imageView_lockers;
		TextView textView_lockers_name;
	}

}
