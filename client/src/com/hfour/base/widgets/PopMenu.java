package com.hfour.base.widgets;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hfour.base.activity.RootActivity;
import com.hfour.base.adapter.PopmenuListAdatper;
import com.hfour.nearplayer.R;

public class PopMenu extends ZoomPopWindow{
	private ListView menuList;
	private Integer[] items;
	private PopmenuListAdatper listAdapter;
	private TextView cancelTV;
	public static final int NONE_CHOOSE = 0xffff;
	
	public PopMenu(Context context){
		super(context);
	}
	
	/**
	 * 
	 * @param inActivity
	 * @param menuItems
	 * @param listener
	 * @param title
	 * @param chooseId : 选中的Menu StringId
	 */
	public PopMenu(RootActivity inActivity, Integer[] menuItems, final OnMenuItemClickListener listener, String title, int chooseItemResId, boolean isAnim){
		super(inActivity.getApplicationContext());
		context = inActivity.getApplicationContext();
		items = menuItems;
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View menuView = layoutInflater.inflate(R.layout.popmenu, null);
		menuList = (ListView) menuView.findViewById(R.id.popmenu_list_view);
		listAdapter = new PopmenuListAdatper(context, chooseItemResId);

		menuList.setAdapter(listAdapter);
		listAdapter.setItems(menuItems);
		menuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listener.onMenuClick(items[position]);
				dismiss();
			}
		});
		cancelTV = (TextView) menuView.findViewById(R.id.menu_cancel_tv);
		cancelTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		((TextView)menuView.findViewById(R.id.menu_title_tv)).setText(title);
		menuList.setOnKeyListener(new OnKeyListener() { //menuList能获取key事件，故用该事件进行监听
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((KeyEvent.KEYCODE_MENU == keyCode) || (KeyEvent.KEYCODE_BACK == keyCode)){
					if(isShowing()){
						dismiss();
					}
					
					return true;
				}
				return false;
			}
		});
		
		initZoomPopWindow(inActivity, (View)menuView, isAnim);
	}
	
	public interface OnMenuItemClickListener {
		/**
		 * 返回是菜单的stringId
		 * 
		 * @param stringId
		 */
		public void onMenuClick(int stringId);
	}
	
	public void showPopMenu(){
		showPopWin();
	}
}
