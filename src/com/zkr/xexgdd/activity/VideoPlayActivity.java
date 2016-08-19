package com.zkr.xexgdd.activity;

import com.zkr.xexgdd.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class VideoPlayActivity extends Activity{
	
	private WebView mWebView;
	private String mAddress = null;
	private Context ctx;
	private boolean DEBUG = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				 WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_videoplay);
		Intent i = getIntent();
		mAddress = i.getStringExtra("HTML_URL");
		mWebView = (WebView) findViewById(R.id.play_video);
		setWebView();
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		try {
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");
			mWebView.addJavascriptInterface(new HtmlObject(this), "jsVideoPlayObj");
			mWebView.loadUrl(mAddress);
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (DEBUG)
						Log.e("Wing", "..shouldOverrideUrlLoading.. url=" + url);
					view.loadUrl(url);
					return true;
				}
			});
			
			mWebView.setWebChromeClient(new WebChromeClient() {

				@Override
				public boolean onJsAlert(WebView view, String url,
						String message, JsResult result) {
					return super.onJsAlert(view, url, message, result);
				}

				@Override
				public boolean onJsConfirm(WebView view, String url,
						String message, JsResult result) {
					return super.onJsConfirm(view, url, message, result);
				}

			});
		} catch (Exception e) {
			return;
		}
	}
	/**
	 * 与js交互的对象
	 * @author Administrator
	 *
	 */
	public class HtmlObject {
		private Context mContext;
		public HtmlObject(Context mContext) {
			this.mContext = mContext;
		}
		@JavascriptInterface
		public String backVideoPlayActivity_android() {
			try {
				 Log.d("Wing", "VideoPlayActivity is finish  onBackPressed ----->>>>>>123");
				 VideoPlayActivity.this.finish();
				 mWebView.loadUrl("http://xgdd.zkr.hk/exitvideo.html");
				 return "Videoplayactivity is finished";
			} catch (Exception e) {
				// TODO: handle exception
			}
			 return null;
		}
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		Log.d("Wing", "VideoPlayActivity is finish  onBackPressed");
		mWebView.loadUrl("http://xgdd.zkr.hk/exitvideo.html");
		VideoPlayActivity.this.finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//finish();
		Log.d("Wing", "VideoPlayActivity is finish   onDestroy");
	}
	
}
