package com.zkr.xexgdd.activity;


import com.zkr.xexgdd.bean.ActivityCollector;
import com.zkr.xexgdd.utils.LogUtil;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("BaseActivity", getClass().getSimpleName());
		
		ActivityCollector.addActivity(this);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
	
	
}
