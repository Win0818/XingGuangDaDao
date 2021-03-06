package com.zkr.xexgdd.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyViewPageAdapter extends PagerAdapter{

	//界面列表
	private ArrayList<View> views;
	public MyViewPageAdapter(ArrayList<View> views) {
		this.views = views;
	}
	
	/**
	 * 获取当前界面�?
	 */
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		else return 0;
	}
	
	/**
	 * 判断是否由对象生成的界面
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}
	/**
	 * �?毁position位置的界�?
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(views.get(position));
	}
	
	/**
	 * 初始化position位置的界�?
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0); 
		return views.get(position);
	}
	
}
