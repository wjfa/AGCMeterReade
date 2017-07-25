package com.guanchao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OperationManualActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_manual);
        ButterKnife.bind(this);

        //设置 webview 的属性缓存模式离线(固定格式)
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //设置加载完成后的监听
        webView.setWebViewClient(viewClient);

        //设置正在加载时的监听
        webView.setWebChromeClient(chromeClient);
        //设置路径
        webView.loadUrl("http://218.92.200.222:8081/zhsswx/");
    }

    //加载完成后的监听
    private WebViewClient viewClient=new WebViewClient(){
        @Override
        //在点击请求的是链接时才会调用，重写此方法返回 true 表明点击网页里
        //面的链接还是在当前的 webview 里跳转，不跳到其它浏览器那边
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }
    };
    //正在加载时的监听
    private WebChromeClient chromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
           /* proBar_show.setProgress(newProgress);//设置随着数据加载变化而变化（默认为100）
            if (proBar_show.getProgress() >=100){
                proBar_show.setVisibility(View.GONE);
            }*/
        }
    };
    @OnClick(R.id.ig_back)
    public void onClick() {
        finish();
    }
}
