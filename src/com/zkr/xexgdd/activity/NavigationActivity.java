package com.zkr.xexgdd.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zkr.xexgdd.R;
import com.zkr.xexgdd.adapter.MyViewPageAdapter;
import com.zkr.xexgdd.common.Constant;
import com.zkr.xexgdd.utils.MySharePreData;

public class NavigationActivity extends Activity implements
		OnPageChangeListener, OnClickListener{
	//定义ViewPager对象
	private ViewPager viewPager;
	//定义ViewPager适配�?
	private MyViewPageAdapter vpAdapter;
	//定义�?个ArrayList来存放View
	private ArrayList<View> views;
	private ImageView mIvEnter;
	//引导图片资源
	private int[] pics = new int[]{R.drawable.app_start, R.drawable.app_start,
			R.drawable.app_start, R.drawable.app_start};
	
	//底部小点的图�?
	private ImageView[] points;
	//记录当前选中的位�?
	private int currentIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		initView();
		initData();
	}
	/**
	 * 初始化组�?
	 */
	private void initView() {
		//实例化ArrayList对象
		views = new ArrayList<View>();
		//实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		mIvEnter = (ImageView) findViewById(R.id.iv_navigation_enter);
		
		mIvEnter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入主界�?
				enterMainActivity();
			}
		});
		//实例化Viewpager的adapter
		vpAdapter = new MyViewPageAdapter(views);
		
	}
	
	/**
	 * initial data
	 */
	@SuppressWarnings("deprecation")
	private void initData() {
		//定义�?个布�?并设置参�?
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT);
		
		//初始化引导图片界�?
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			//防止图片不能填满屏幕
			iv.setScaleType(ScaleType.FIT_XY);
			//加载图片资源
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		//设置数据
		viewPager.setAdapter(vpAdapter);
		viewPager.setOnPageChangeListener(this);
		
		//初始化底部小�?
		initPoint();
		
	}
	/**
	 * 初始化底部小�?
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
		points = new ImageView[pics.length];
		
		//循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			//得到�?个LinearLayout下面的每�?个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			//默认都设为灰�?
			points[i].setEnabled(true);
			points[i].setOnClickListener(this);
			//设置位置tag，方便圈与当前位置对�?
			points[i].setTag(i);
		}
		
		//设置当前默认位置
		 currentIndex = 0;
		//设置为白色，即�?�中状�??
		points[currentIndex].setEnabled(false);
		
	}
	private void enterMainActivity() {
		MySharePreData.SetBooleanData(this, Constant.NAVIGATION_SP_NAME, 
				"is_first", false);
		//finish();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// 进入主页�?
				startActivity(new Intent(NavigationActivity.this,
						MainActivity.class));
				finish();
			}
		}).start();
	}
	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}
	
	/**
	 * 当前页面状�?�改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 当前页面滑动时调�?
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		//设置底部小点选中状�??
		setCurDot(position);
		if (position == 3) {
			mIvEnter.setVisibility(View.VISIBLE);
		} else {
			mIvEnter.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int position) {
		if ((position < 0) || (position > pics.length - 1) || 
				(currentIndex == position)) {
			return;
		}
		points[position].setEnabled(false);
		points[currentIndex].setEnabled(true);
		
		currentIndex = position;
	}
	/**
	 * 设置当前页面的位�?
	 */
	private void setCurView(int position) {
		if ((position < 0) || (position > pics.length)) {
			return;
		}
		viewPager.setCurrentItem(position);
	}
}
