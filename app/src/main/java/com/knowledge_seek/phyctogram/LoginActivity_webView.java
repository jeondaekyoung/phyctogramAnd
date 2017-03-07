package com.knowledge_seek.phyctogram;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity_webView;

/**
 * Created by jdk on 2016-12-28
 */
public class LoginActivity_webView extends BaseActivity_webView {

    WebView mWebView;

    long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_webview);
        mWebView=(WebView)findViewById(R.id.login_webView);
        mWebView.loadUrl("http://phyctogram.com/views/webview/login.jsp");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setWebViewClient(new myWebClient());
        mWebView.setWebChromeClient(new MyWebChromeClient(getBaseContext()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }
        else{
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();

                Toast.makeText(getApplicationContext(), R.string.baseActivity_exit, Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                //moveTaskToBack(true);
                finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }

        }


    }
}
