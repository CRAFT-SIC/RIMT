package com.suntend.arktoolbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.suntend.arktoolbox.RIMTUtil.DownloadImageUtil;

import java.io.FileNotFoundException;

/*
****************************************************************************************************
这是一个 WebView 轮子，可以实现在 APP 内部打开网页
已实现：上传图片、保存图片、使用系统浏览器打开当前网址
使用的额外工具类：DownloadImageUtil.java
使用的布局：activity_web.xml
****************************************************************************************************
如何调用：

你的某个方法(){
    ***

    String url = "xxx"; //需要打开的网页链接
    Intent intent;
    intent = new Intent(getActivity(), WebActivity.class);
    intent.putExtra("url", url);
    startActivity(intent);

    ***
    }
****************************************************************************************************
敬铧，2023/9/12
最后修改：2023/10/29
****************************************************************************************************
*/

public class WebActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private String current_url;
    private Context context;

    //用于上传图片
    private ValueCallback<Uri[]> mFileUploadCallback;
    private final static int REQUEST_CODE_FILE_PICKER = 51426;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        progressBar = findViewById(R.id.web_ProgressBar);
        context = this;

        //使用系统浏览器打开当前网址
        ImageView open_by_browser = findViewById(R.id.btn_open_by_browser);
        open_by_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_url != null){
                    Uri uri = Uri.parse(current_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        webView = findViewById(R.id.webView_layout);
        //webView = new WebView(WebActivity.this);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许与Javascript交互
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片
        webSettings.setUseWideViewPort(true);//将图片调整到适合大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
        webSettings.setSupportZoom(true);//支持缩放
        webSettings.supportMultipleWindows();//支持多窗口
        webSettings.setSupportMultipleWindows(true);//支持多窗口
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//支持内容重新布局
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setDomStorageEnabled(true);//支持前端调用localStorage
        webSettings.setDatabaseEnabled(true);//支持数据库
        webSettings.setDatabasePath(context.getDir("database", Context.MODE_PRIVATE).getPath());
        webSettings.setBuiltInZoomControls(true);//设置内置的缩放控件
        webSettings.setDisplayZoomControls(false);//隐藏原生的缩放控件
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//缓存策略
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许加载混合网络协议
        webSettings.setAllowFileAccess(false);//禁用file加载
        webSettings.setAllowFileAccessFromFileURLs(false);//禁用file加载
        webSettings.setAllowUniversalAccessFromFileURLs(false);//禁用file加载
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//设置渲染进程优先级
        //webSettings.setNeedInitialFocus(true);//当webView调用requestFocus时为webView设置节点

        //webView.requestFocusFromTouch();//如果webView中需要用户手动输入用户名、密码或其他，则webView必须设置支持获取手势焦点
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //获取类型
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                //如果是图片类型
                if(hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
                    //则弹出对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("保存图片");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String url = hitTestResult.getExtra();
                            DownloadImageUtil.downPic(url, new DownloadImageUtil.DownFinishListener() {
                                @Override
                                public void getDownPath(String s) {
                                    Message msg = Message.obtain();
                                    msg.obj = s;
                                    handler.sendMessage(msg);
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return true;
            }
        });

        if(url != null){
            webView.loadUrl(url);
            //LinearLayout linearLayout = findViewById(R.id.web_layout);
            //linearLayout.addView(webView);
        }
        else{
            Toast.makeText(context, "URL错误！", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            current_url = url; //同步当前链接以便跳转到浏览器打开
            if(webView.getVisibility() != View.INVISIBLE){
                webView.setVisibility(View.INVISIBLE);
            }
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView webView, String url){
            if(webView.getVisibility() != View.VISIBLE){
                webView.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.INVISIBLE);

            //管理cookies
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieString = cookieManager.getCookie(url);
            if (cookieString != null){
                Log.i("cookie", cookieString);
            }
            super.onPageFinished(webView, url);
        }

        //实现跳转到其他应用
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest){
            if (current_url.startsWith("bilibili://")){
                //（例）大岛 bilibili://space/17008802?frommodule=H5&h5awaken=b3Blbl9hcHBfZnJvbV90eX
                //并不需要知道具体的scheme，因为url会以string的形式直接传给intent
                //B站包名tv.danmaku.bili
                Intent intent = new Intent();
                intent.setData(Uri.parse(current_url));
                intent.setPackage("tv.danmaku.bili");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try{
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(context, "没有权限或未找到应用", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }

        //处理https的ssl证书错误
        @SuppressLint("WebViewClientOnReceivedSslError")
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            handler.proceed();//接受证书
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        //进度条
        public void onProgressChanged(WebView webView, int newProgress){
            if (newProgress < 100){
                if (progressBar.getProgress() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            } else if (newProgress == 100){
                progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(webView, newProgress);
        }

        //打开文件选择器 - 上传图片
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            //是否支持多选
            final boolean allowMultiple = fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE;
            openFileInput(filePathCallback, allowMultiple);
            return true;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String picFile = (String) msg.obj;
            String[] split = picFile.split("/");
            //通知图库更新
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picFile)));
            Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallback != null) {
            mFileUploadCallback.onReceiveValue(null);
        }
        mFileUploadCallback = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        String mUploadableFileTypes = "image/*";
        i.setType(mUploadableFileTypes);
        startActivityForResult(Intent.createChooser(i, "选择文件"), REQUEST_CODE_FILE_PICKER);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallback != null) {
                        Uri[] dataUris = null;
                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                            } else {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    dataUris = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        mFileUploadCallback.onReceiveValue(dataUris);
                        mFileUploadCallback = null;
                    }
                }
            } else {
                //在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
                //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
                if (mFileUploadCallback != null) {
                    mFileUploadCallback.onReceiveValue(null);
                    mFileUploadCallback = null;
                }
            }
        }
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