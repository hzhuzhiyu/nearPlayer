package com.hfour.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfour.nearplayer.R;
/**
 * 传入的是String
 * @author Tony
 *
 */
public class PopMenuStringListAdapter extends BaseAdapter {
	private Context context;
	private String[] menuStringItems;
	private String selectItem;

	public PopMenuStringListAdapter(Context inContext, String inSelectStr) {
		context = inContext;
		selectItem = inSelectStr;
	}

	public void setItems(String[] items){
		menuStringItems = items;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		if (menuStringItems == null) {
			return 0;
		}
		return menuStringItems.length;
	}

	public String getItem(int position) {
		if(position >= menuStringItems.length){
			return null;
		}
		return menuStringItems[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context.getApplicationContext())
					.inflate(R.layout.popmenu_item, null);

			holder = new ViewHolder();
			holder.itemTV = (TextView) convertView
					.findViewById(R.id.popmenu_item_tv);
			holder.chooseIV = (ImageView) convertView.findViewById(R.id.choose_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String str = menuStringItems[position];
		holder.itemTV.setText(str);
		
		if(str.equals(selectItem)){
			holder.chooseIV.setVisibility(View.VISIBLE);
		}else{
			holder.chooseIV.setVisibility(View.GONE);
		}

		return convertView;
	}

	public class ViewHolder {
		TextView itemTV;
		ImageView chooseIV;
	}

	public void setChooseItem(String chooseItem) {
		selectItem = chooseItem;
		notifyDataSetChanged();
	}

}
