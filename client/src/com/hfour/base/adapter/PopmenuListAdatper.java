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
 * 传入的是StringID
 * @author Tony
 *
 */
public class PopmenuListAdatper extends BaseAdapter {
	private Context context;
	private Integer[] menuItemsId;
	private int selectMenuResId;

	public PopmenuListAdatper(Context inContext, int inSelectResID) {
		context = inContext;
		selectMenuResId = inSelectResID;
	}

	public void setItems(Integer[] items){
		menuItemsId = items;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		if (menuItemsId == null) {
			return 0;
		}
		return menuItemsId.length;
	}

	public Object getItem(int position) {
		return position;
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
		int strId = menuItemsId[position];
		holder.itemTV.setText(context.getString(strId));
		
		if(selectMenuResId == strId){
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

}
