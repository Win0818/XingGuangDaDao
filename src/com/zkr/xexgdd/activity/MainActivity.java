package com.zkr.xexgdd.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkr.xexgdd.R;
import com.zkr.xexgdd.bean.ShareObject;
import com.zkr.xexgdd.common.Constant;
import com.zkr.xexgdd.utils.JsonUtils;
import com.zkr.xexgdd.utils.LogUtil;
import com.zkr.xexgdd.utils.MySharePreData;
import com.zkr.xexgdd.utils.SharePopMenu;
import com.zkr.xexgdd.utils.SharePopMenu.OnItemClickListener;
import com.zkr.xexgdd.utils.SharePopMenu.shareBottomClickListener;
import com.zkr.xexgdd.utils.Util;
import com.zkr.xexgdd.view.GlobalProgressDialog;

/**
 * 
 * @author Administrator
 */
public class MainActivity extends Activity implements OnClickListener,
	shareBottomClickListener {

	private String TAG = "Wing";
	private WebView mWebView;
	private ImageButton mRefreshBtn;
	private LinearLayout mNoNetworkLinearLayout;
	private boolean DEBUG = true;
	private String mAddress = "http://xgdd.zkr.hk/";
	private String mNowURL;
	private String mShareUrl;
	private String mTitle;
	private Bitmap mIcom;
	private long exitTime = 0;
	private IWXAPI wxApi;
	private GlobalProgressDialog mGlobalProgressDialog;
	private SharePopMenu popMenu;
	private Context mContext;
	private String accessToken;
	private String openId;
	private boolean flag = true;
	public static final int TAKE_PHOTO = 0x00001007;
	public static final int CROP_PHOTO = 0x00001008;
	private Uri imageUri;
	private ImageView picture;
	private String mImageUrl = null;
	private String mDescription = null;
	private RequestQueue requestQueue = null;
	private String downloadUrl = null;
	private String newVersionName = null;
	private String packageSize = null;
	private String updateContent = null;
	private int newVersionCode = 1;
	private String mAuth_Success_Url = null;
	private String WECHAT_PAY_URL = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.HTML_LOADING:
				startProgressDialog();
				break;
			case Constant.HTML_LOADED:
				stopProgressDialog();
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initView() {
		mWebView = (WebView) findViewById(R.id.webview);
		mNoNetworkLinearLayout = (LinearLayout) findViewById(R.id.no_network);
		
		mRefreshBtn = (ImageButton) findViewById(R.id.refresh_btn);
		mRefreshBtn.setOnClickListener(this);
		if (!Util.isNetWorkConnected(this)) {
			mNoNetworkLinearLayout.setVisibility(View.VISIBLE);
		} else {
			setWebView();
		}

		wxApi = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
		wxApi.registerApp(Constant.APP_ID);
		mContext = this;
		// setWebView();

		if (!Util.isNetWorkConnected(this)) {

		}
		// ��ʼ�������˵�
		popMenu = new SharePopMenu(this);
		popMenu.setShareBottomClickListener(this);
		
		/*Button weixinPayBtn = (Button) findViewById(R.id.share);
		weixinPayBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});*/
	}
	
	public void share(View view) {
		
		Log.i("Wing", "wechat pay ------------------->>>>>>>>>>>>>>>>>");
		//getResult();
	/*	popMenu.showAsDropDown(MainActivity.this
				.findViewById(R.id.main_root));*/
	}

	private void initData() {
		
		EventBus.getDefault().register(this);
		requestQueue = Volley.newRequestQueue(mContext);
		//cheakVersion();
	}

	/**
	 * ��Activityִ��onResume()ʱ��WebViewִ��resume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume:  " + Util.WECHAT_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.refresh_btn:
			if (!Util.isNetWorkConnected(this)) {
				mNoNetworkLinearLayout.setVisibility(View.VISIBLE);
			} else {
				mNoNetworkLinearLayout.setVisibility(View.INVISIBLE);
				setWebView();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 分享到微信朋友圈
	 */
	@Override
	public void shareCircle() {
		Log.d("Wing", "SHARE_TO_FREIND_CIRCLE");
		if (mShareUrl != null && mTitle != null && mDescription != null
				&& mImageUrl != null) {
			share(Constant.SHARE_TO_FREIND_CIRCLE, mShareUrl, mTitle,
					mDescription, mImageUrl);
			LogUtil.d("Wing", "mShareUrl: " + mShareUrl + "mTitle: " + mTitle
					+ "mDescription: " + mDescription + "mImageUrl: "
					+ mImageUrl);
		}
	}
	/**
	 * 分享给微信好友
	 */
	@Override
	public void shareFreind() {
		Log.d("Wing", "SHARE_TO_FREIND");
		if (mShareUrl != null && mTitle != null && mDescription != null
				&& mImageUrl != null) {
			share(Constant.SHARE_TO_FREIND, mShareUrl, mTitle, mDescription,
					mImageUrl);
			LogUtil.d("Wing", "mShareUrl: " + mShareUrl + "mTitle: " + mTitle
					+ "mDescription: " + mDescription + "mImageUrl: "
					+ mImageUrl);
		}
	}


	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		try {
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");
			mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");
			mWebView.loadUrl(mAddress);
			mWebView.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& mWebView.canGoBack()) {
						mWebView.goBack();
						mWebView.loadUrl("javascript:window.history.back();");
						return true;
					}
					return false;
				}
			});
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (DEBUG)
						Log.e("Wing", "..shouldOverrideUrlLoading.. url=" + url);
					mNowURL = url;
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					mHandler.sendEmptyMessage(Constant.HTML_LOADING);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
				}

				@Override
				@Deprecated
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description,
							failingUrl);
					mNoNetworkLinearLayout.setVisibility(View.VISIBLE);
				}

			});
			mWebView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onReceivedTitle(WebView view, String title) {
					super.onReceivedTitle(view, title);
				}

				@Override
				public void onReceivedIcon(WebView view, Bitmap icon) {
					super.onReceivedIcon(view, icon);
				}

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress == 100) {
						mHandler.sendEmptyMessage(Constant.HTML_LOADED);
					}
					super.onProgressChanged(view, newProgress);
				}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/*
		 * if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
		 * mWebView.goBack(); return false; } else
		 */if (keyCode == KeyEvent.KEYCODE_BACK /* && !mWebView.canGoBack() */) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.quit_app),
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}

	/**
	 * ΢�ŷ�����
	 * 
	 * @param flag
	 * @param url
	 * @param title
	 * @param description
	 * @param thumb
	 */
	private void share(int flag, String url, String title, String description,
			String imageUrl) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;

		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.app_logo);
		// msg.setThumbImage(thumb);
		Bitmap thumb2 = null;
		try {
			thumb2 = Glide.with(mContext).load(imageUrl).asBitmap() // 必须
					.centerCrop().into(500, 500).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (thumb2 != null) {
			LogUtil.d("Wing", "thumb2 != null  -------->>>>>>>>>>>");
			msg.setThumbImage(thumb2);
		} else {
			// msg.thumbData = Util.bmpToByteArray(thumb, true);
			LogUtil.d("Wing", "thumb2 = null  -------->>>>>>>>>>>++++++++");
			msg.setThumbImage(thumb);
		}
		msg.setThumbImage(thumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		boolean fla = wxApi.sendReq(req);
		System.out.println("fla=" + fla);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 
	 */
	private void loginWechat() {
		// send oauth request
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		wxApi.sendReq(req);
	}
	
	private void loginWechat_2() {
		// send oauth request
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "login_state";
		wxApi.sendReq(req);
	}
	
	
	/**
	 * 与js交互的对象
	 * 
	 * @return
	 */
	private Object getHtmlObject() {

		Object insertObj = new Object() {
			@JavascriptInterface
			public void share_To_Wechat_android(String webpageUrl,
					String title, String description, String imageUrl) {
				LogUtil.d("Wing", "--------------------->>>>>>>>>>>>>>>>>>>webpageUrl: " + webpageUrl + title +description + imageUrl);
				mTitle = title;
				mShareUrl = webpageUrl;
				mDescription = description;
				mImageUrl = imageUrl;
				popMenu.showAsDropDown(MainActivity.this
						.findViewById(R.id.main_root));
			}

			@JavascriptInterface
			public String loginWechat_android() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String uid = MySharePreData.GetData(MainActivity.this,
								Constant.WECHAT_LOGIN_SP_NAME, "union_id");
						if (TextUtils.isEmpty(uid)) {
							loginWechat_2();
						} else {
							Log.d(TAG, "union_id:   " + uid);
							mWebView.loadUrl("http://m.qianft.com/UserLogin/WeChatLogin?unionId=UNIONID"
									.replace("UNIONID", uid));
						}
					}
				});
				return "login succeed";
				// }
			}

			@JavascriptInterface
			public void takePhoto_android(String path, String picFileName) {
				takePhoto(path, picFileName);
			}

			// 微信授权
			@JavascriptInterface
			public String wechat_Auth_Login_android(String userid, String postServerUrl, String auth_Success_Url) {
				
				Log.d("Wing", "userid:   " + userid + "postServerUrl:   " + postServerUrl +
						"  auth_Success_Url :" + auth_Success_Url);
				Util.SERVER_URL = postServerUrl;
				mAuth_Success_Url = auth_Success_Url;
				Util.USER_ID = userid;
				loginWechat();
				return "";
			}


			@JavascriptInterface
			public void JavacallHtml() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml()");
						Toast.makeText(MainActivity.this, "clickBtn",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@JavascriptInterface
			public void JavacallHtml2() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWebView.loadUrl("javascript: showFromHtml2('qian fu tong')");
						Toast.makeText(MainActivity.this, "clickBtn2",
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			/**
			 * This is not called on the UI thread. Post a runnable to invoke
			 * loadUrl on the UI thread.
			 */
			@JavascriptInterface
			public String clickOnAndroid() {
				mHandler.post(new Runnable() {
					public void run() {
					}
				});
				return "aaaaa";
			}

			/**
			 * 图片下载
			 * 
			 * @param imageUrl
			 * @param savePath
			 * @param picFileName
			 * @return
			 */
			@JavascriptInterface
			public void downloadPicture(final String imageUrl,
					final String savePath, final String picFileName) {
				Util.downLoadPicture(imageUrl, savePath, picFileName);
			}

			@JavascriptInterface
			public void clearUserInfo_android() {
				MySharePreData.SetData(mContext, Constant.WECHAT_LOGIN_SP_NAME,
						"union_id", "");
			}

			
			@JavascriptInterface
			public void starVideoPlayActivity_android(String Url) {
				startActivity(Url);
			}
			@JavascriptInterface
			public void wechat_Pay_android (final String json) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject jsonObject = new JSONObject(json);
							String wechat_pay_url = jsonObject.getString("wechat_pay_url");
							String callback = jsonObject.getString("callback");
							String mCancel = jsonObject.getString("cancel");
							getResult(wechat_pay_url);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};

		return insertObj;
	}


	// 网页加载进度开始
	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	// ֹͣ网页加载进度停止
	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			if (mGlobalProgressDialog.isShowing()) {
				mGlobalProgressDialog.dismiss();
			}
			mGlobalProgressDialog = null;
		}
	}
	/**
	 * 
	 * @param path
	 * @param picFileName
	 */
	private void takePhoto(String saveTargetDir, String picFileName) {
		String rootPath = Environment.getExternalStorageDirectory().toString()
				+ "/";
		File pathDir = new File(rootPath + saveTargetDir);
		if (!pathDir.exists()) {
			pathDir.mkdirs();
		}
		File outputImage = new File(pathDir, picFileName + ".jpg");
		try {
			if (outputImage.exists()) {
				outputImage.delete();
			}
			outputImage.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, TAKE_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, CROP_PHOTO);
			}
			break;
		case CROP_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bitmap = BitmapFactory
							.decodeStream(getContentResolver().openInputStream(
									imageUri));
					picture.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
	/**
	 * WebView打开新的Activity， 传入URL
	 * @param Url
	 */
	private void startActivity(String Url) {
		Intent i = new Intent(MainActivity.this, VideoPlayActivity.class);
		i.putExtra("HTML_URL", Url);
		startActivity(i);
	}
	
	// 清除所有cookie
		private void removeAllCookie() {
			CookieSyncManager cookieSyncManager = CookieSyncManager
					.createInstance(mContext);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			cookieManager.removeSessionCookie();

			// String testcookie1 = cookieManager.getCookie(urlpath);

			cookieManager.removeAllCookie();
			cookieSyncManager.sync();

			// String testcookie2 = cookieManager.getCookie(urlpath);
		}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeAllCookie();
		EventBus.getDefault().unregister(this);
	}
	/**
	 * EventBus3.0 微信授权
	 * @param message
	 */

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void helloEventBus(String message) {
		LogUtil.d("Wing", "message:  " + message);
		switch (message) {
		case "hello":
			if (mAuth_Success_Url != null) {
				String uid = MySharePreData.GetData(mContext, 
						Constant.WECHAT_LOGIN_SP_NAME, "union_id");
				LogUtil.d("Wing", "helloEventBus uid:   " + uid);
				mWebView.loadUrl(mAuth_Success_Url + "&unionId=UNION_ID".replace("UNION_ID", uid));
			}
			break;
		case "wechat_pay_success":
			//mWebView.loadUrl("javascript: " + mCallback + "(" + result + ")");
			break;
		case "wechat_pay_fail":
			break;
		case "wechat_pay_cancel":
			//mWebView.loadUrl("javascript: " + mCancel + "(" + result + ")");
			break;
		default:
			break;
		}
		
	}
	private void getResult(String wechat_pay_url) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				wechat_pay_url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.e("get server pay params:",response.toString());
						PayReq req = new PayReq();
						try {
							req.appId			= Constant.APP_ID;
							req.partnerId		= response.getString("partnerid");   //response.getString("partnerid");
							req.prepayId		= response.getString("prepayid");
							req.nonceStr		= response.getString("noncestr");
							req.timeStamp		= response.getString("timestamp");
							req.packageValue	= response.getString("package");
							req.sign			= response.getString("sign");
							wxApi.sendReq(req);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//req.extData			= "app data"; // optional
						//Toast.makeText(MainActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
						// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		      requestQueue.add(jsonObjectRequest);
	}
}
