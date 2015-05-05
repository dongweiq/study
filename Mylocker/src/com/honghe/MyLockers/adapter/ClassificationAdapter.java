package com.honghe.MyLockers.adapter;

import java.util.ArrayList;

import com.honghe.MyLockers.bean.ClassificationBean;
import com.honghe.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassificationAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ClassificationBean> beans = null;

	public ClassificationAdapter(Context context) {
		this.mContext = context;
	}

	public void setDatas(ArrayList<ClassificationBean> beans) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_classification, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView_classification = (ImageView) convertView.findViewById(R.id.imageView_classification);
			viewHolder.textView_classification_name = (TextView) convertView.findViewById(R.id.textView_classification_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (null != beans && position < beans.size()) {
			ClassificationBean bean = beans.get(position);
			ImageLoader.getInstance().displayImage(bean.ImageUri, viewHolder.imageView_classification);
			viewHolder.textView_classification_name.setText(bean.ClassificationName);
		} else {
			viewHolder.imageView_classification.setImageResource(R.drawable.icon_add);
			viewHolder.textView_classification_name.setText("添加分类");
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView imageView_classification;
		TextView textView_classification_name;
	}

}
