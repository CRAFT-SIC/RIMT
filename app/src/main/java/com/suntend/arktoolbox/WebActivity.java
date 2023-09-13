package com.suntend.arktoolbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLDecoder;

/*
****************************************************************************************************
这是一个 WebView 轮子，可以实现在 APP 内部打开网页
后续还会添加 “跳转到其他APP”、“跳转到浏览器” 等功能
****************************************************************************************************
如何调用：

你的某个方法(){
    ***
    String url = "***";
    StartWebActivity(url);
    }

public void StartWebActivity(String url){
        Intent intent;
        intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }

"url" 就是需要打开的网页链接
****************************************************************************************************
敬铧
2023/9/12
***************************************************************************************************
*/

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private WebSettings webSettings;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        webView = new WebView(WebActivity.this);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许与Javascript交互
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片
        webSettings.setUseWideViewPort(true);//将图片调整到适合大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
        webSettings.setSupportZoom(true);//支持缩放
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setDomStorageEnabled(true);//支持前端调用localStorage，false则浩舰数据库下拉框失效
        //webSettings.setBuiltInZoomControls(true);//设置内置的缩放控件
        //webSettings.setDisplayZoomControls(false);//隐藏原生的缩放控件
        //webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        //webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//使用cookies(from v4.3 抽卡分析)
        webView.setWebViewClient(new MyWebViewClient());

        if(url != null){
            webView.loadUrl(url);
            //todo
            //setContentView(webView);
            //R.layout.activity_web.addView(webView);
        }
        else{
            Toast.makeText(WebActivity.this, "URL错误！", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        //管理cookies
        public void onPageFinished(WebView webView, String url){
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieString = cookieManager.getCookie(url);
            if (cookieString != null){
                Log.i("cookie", cookieString);
            }
            super.onPageFinished(webView, url);
        }

        //实现跳转到其他应用
        /*
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest){
            String url = webResourceRequest.getUrl().toString();
            if (url.startsWith("bilibili://")){
                //（例）大岛 bilibili://space/17008802?frommodule=H5&h5awaken=b3Blbl9hcHBfZnJvbV90eX
                //并不需要知道具体的scheme，因为url会以string的形式直接传给intent
                //B站包名tv.danmaku.bili
                Intent intent = new Intent();
                intent.setData(Uri.parse(url));
                intent.setPackage("tv.danmaku.bili");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try{
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "没有权限或未找到应用", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, webResourceRequest);
        }*/
    }

    @Override
    public void onBackPressed(){
        if (webView.canGoBack()){
            webView.goBack();
        } else {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
            finish();
            overridePendingTransition(0, 0); //禁用activity动画
        }
    }
}