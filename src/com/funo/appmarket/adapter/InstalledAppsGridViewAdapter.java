package com.funo.appmarket.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.funo.appmarket.R;
import com.funo.appmarket.bean.AppBean;
import com.funo.appmarket.constant.Constants;
import com.funo.appmarket.util.CommonUtils;
import com.funo.appmarket.util.ViewHolderUtils;
import com.funo.appmarket.view.RatingView;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InstalledAppsGridViewAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<AppBean> appBeans = new ArrayList<AppBean>();
	
	private SparseIntArray colorMap = new SparseIntArray();
	
	public InstalledAppsGridViewAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return appBeans.size();
	}

	@Override
	public AppBean getItem(int position) {
		return appBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.gridview_item_installed_apps, null);
		}
		RatingView ratingView = ViewHolderUtils.get(convertView, R.id.ratingView);
		ImageView tag_img = ViewHolderUtils.get(convertView, R.id.tag_img);
		ImageView appLogo = ViewHolderUtils.get(convertView, R.id.appLogo);
		TextView appName = ViewHolderUtils.get(convertView, R.id.appName);
		View contentView = ViewHolderUtils.get(convertView, R.id.contentView);
		AppBean appBean = getItem(position);
		Glide.with(mContext).load(Constants.IMAGE_URL + appBean.getAppLogo()).into(appLogo);
		appName.setText(appBean.getAppName());
		ratingView.setScore(appBean.getScore());
		
		int appId = (int) appBean.getAppId();
		int color = colorMap.get(appId, -1);
		if (color == -1) {
			int randomColor = CommonUtils.getRandomColor();
			contentView.setBackgroundColor(randomColor);
			colorMap.append(appId, randomColor);
		} else {
			contentView.setBackgroundColor(color);
		}
		
		int tag = 0;
		try {
			tag = Integer.parseInt(appBean.getTag());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		switch (tag) {// 标签类型 0:无标签 1:精品 2:NEW 3:HOT
		case 0:
			tag_img.setVisibility(View.GONE);
			break;
		case 1:
			tag_img.setVisibility(View.VISIBLE);
			tag_img.setImageResource(R.drawable.recommend_best);
			break;
		case 2:
			tag_img.setVisibility(View.VISIBLE);
			tag_img.setImageResource(R.drawable.recommend_new);
			break;
		case 3:
			tag_img.setVisibility(View.VISIBLE);
			tag_img.setImageResource(R.drawable.recommend_hot);
			break;
		}
		return convertView;
	}

	public void setData(List<AppBean> appInfos) {
		this.appBeans = appInfos;
		notifyDataSetChanged();
	}
	
}
