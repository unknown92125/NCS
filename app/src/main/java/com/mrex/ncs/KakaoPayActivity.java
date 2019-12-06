package com.mrex.ncs;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class KakaoPayActivity extends AppCompatActivity {

    private WebView wvKakaoPay;
    private String nextUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_pay);

        wvKakaoPay = findViewById(R.id.wv_kakao_pay);

        Intent intent = getIntent();
        nextUrl = intent.getStringExtra("nextUrl");

//        WebView webView = new WebView(this);
//        setContentView(webView);
//        webView.loadUrl(nextUrl);

        wvKakaoPay.getSettings().setJavaScriptEnabled(true);
        wvKakaoPay.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvKakaoPay.getSettings().setLoadWithOverviewMode(true);
        wvKakaoPay.getSettings().setUseWideViewPort(true);

        wvKakaoPay.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(nextUrl);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        wvKakaoPay.setWebChromeClient(new WebChromeClient());
//        wvKakaoPay.loadUrl(nextUrl);
        wvKakaoPay.loadUrl(nextUrl);

    }


}
