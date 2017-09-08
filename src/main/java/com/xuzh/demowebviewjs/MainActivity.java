package com.xuzh.demowebviewjs;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	WebView mWebView;


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		// 设置编码
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		// 支持js
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		// 设置背景颜色 透明
		mWebView.setBackgroundColor(Color.rgb(96, 96, 96));
		mWebView.setWebViewClient(new WebViewClientDemo());//添加一个页面相应监听类
		// 载入包含js的html
		mWebView.loadData("", "text/html", null);
		mWebView.loadUrl("file:///android_asset/test.html");




		Intent intent = new Intent();
		intent.setPackage("woyou.aidlservice.jiuiv5");
		intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
		startService(intent);//启动打印服务
		bindService(intent, connService, Context.BIND_AUTO_CREATE);
	}

	class WebViewClientDemo extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			 /**
	         * 注册JavascriptInterface，其中"lee"的名字随便取，如果你用"lee"，那么在html中只要用  lee.方法名()
	         * 即可调用MyJavascriptInterface里的同名方法，参数也要一致
	         */
			mWebView.addJavascriptInterface(new JsObject(), "lee");
		}

	}

	class JsObject {

		@JavascriptInterface
		public void funAndroid(final String i) {
			Toast.makeText(getApplicationContext(), "通过JS调用本地方法funAndroid " + i,	Toast.LENGTH_SHORT).show();

			try {
				woyouService.printerSelfChecking(callback);//这里使用的AIDL方式打印
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void initViews() {
		mWebView = (WebView) findViewById(R.id.wv_view);
	}

	private IWoyouService woyouService;

	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			woyouService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			woyouService = IWoyouService.Stub.asInterface(service);
		}
	};

	ICallback callback = new ICallback.Stub() {

		@Override
		public void onRunResult(boolean success) throws RemoteException {
		}

		@Override
		public void onReturnString(final String value) throws RemoteException {
		}

		@Override
		public void onRaiseException(int code, final String msg)
				throws RemoteException {
		}
	};

}
