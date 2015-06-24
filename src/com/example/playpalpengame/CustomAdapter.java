package com.example.playpalpengame;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private int[] valueViewID;

	private ItemView itemView;

	private class ItemView {
		RoundedImageView itemHeadImage;
		TextView itemUserName;
		TextView itemMoreInfo;
	}

	public CustomAdapter(Context c, ArrayList<HashMap<String, Object>> appList,
			int resource, String[] from, int[] to) {
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		valueViewID = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, valueViewID, 0, to.length);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			itemView = (ItemView) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.user_list_layout, null);
			itemView = new ItemView();
			itemView.itemHeadImage = (RoundedImageView) convertView
					.findViewById(valueViewID[0]);
			itemView.itemUserName = (TextView) convertView
					.findViewById(valueViewID[1]);
			itemView.itemMoreInfo = (TextView) convertView
					.findViewById(valueViewID[2]);
			convertView.setTag(itemView);
		}

		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String imageFileName = (String) appInfo.get(keyString[0]);
			String name = (String) appInfo.get(keyString[1]);
			String info = (String) appInfo.get(keyString[2]);
			itemView.itemUserName.setText(name);
			itemView.itemMoreInfo.setText(info);
			File f = new File(imageFileName);
			if(f.exists()) {
				Bitmap bMap = BitmapFactory.decodeFile(imageFileName);
				itemView.itemHeadImage.setImageBitmap(bMap);;
			}
			else 
				itemView.itemHeadImage.setImageResource(R.drawable.login_head);
		}
		return convertView;
	}
}
