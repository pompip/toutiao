package com.huanxi.toutiao.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.service.DownloadServiceWithNotifytation;
import com.huanxi.toutiao.ui.fragment.base.BaseFragment;

import java.util.LinkedList;

import butterknife.BindView;

/**
 * Created by Dinosa on 2018/2/28.
 *
 * 这里是webView的公共类；
 */

public class WebViewFragment extends BaseFragment {

    @BindView(R.id.web_view)
    WebView mWebView;

    @BindView(R.id.pb_progress)
    ProgressBar mProgess;


    private final static String TAG = "MainActivity";

    //android webView调用原生的相册的操作；
    private final static int CAPTURE_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private String filePath;


    public static final String WEB_TITLE="title";
    public static final String WEB_URL="url";
    public static final String WEB_IN="isJumpWeb";
    private boolean mWebInJump;
    private String mUrl;

    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;

    //用一个LinkedList来维护；
    LinkedList<String> mUrls = new LinkedList<>();

    @Override
    protected View getContentView() {
        return inflatLayout(R.layout.fragment_web_view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        mUrl = arguments.getString(WEB_URL);
        mWebInJump=arguments.getBoolean(WEB_IN);
    }

    @Override
    protected void initView() {
        super.initView();

        mUrl = getActivity().getIntent().getStringExtra(WEB_URL);
        mWebInJump = getActivity().getIntent().getBooleanExtra(WEB_IN,true);
    }


    @Override
    public void initData() {
        //webView的设置
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);//适应分辨率
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(webSettings.getUserAgentString());

        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        String dir = getActivity().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dir);
        webSettings.setGeolocationDatabasePath(dir);

        webSettings.setAppCacheEnabled(true);
        String cacheDir = getActivity().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(cacheDir);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 10);
        webSettings.setAllowFileAccess(true);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webSettings.setBuiltInZoomControls(false);
        //  webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDefaultTextEncodingName("utf-8");




        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);


        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.i("tag", "url="+url);

                Log.i("tag", "userAgent="+userAgent);

                Log.i("tag", "contentDisposition="+contentDisposition);

                Log.i("tag", "mimetype="+mimetype);

                Log.i("tag", "contentLength="+contentLength);

                startDownoad(url);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.getSettings().setBlockNetworkImage(false);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                // Pre GINGERBREAD
                return super.shouldOverrideUrlLoading(view,request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(url.startsWith("http")||url.startsWith("https")){

                    //这里进行一个出栈的操作；

                    mUrls.add(url);

                    mWebView.loadUrl(url);
                    return true;

                    //这里要做的一个逻辑就是
                   /* if(mWebInJump){

                        mWebView.loadUrl(url);
                        return true;

                    }else{
                        return super.shouldOverrideUrlLoading(view, url);
                    }*/

                }else {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse(url);
                        intent.setData(data);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //这里表示没有找到对应的包名
                        toast("未找到相关应用");
                    }
                    return true;
                }
            }

            // The webPage has 2 filechoosers and will send a
            // console message informing what action to perform,
            // taking a photo or updating the file

            public boolean onConsoleMessage(ConsoleMessage cm) {

                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("androidruntime", "Show console messages, Used for debugging: " + message);
            }
        });
        //去掉webView右边的滚动条
       // mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
       // mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mProgess.setVisibility(View.GONE);
                } else {
                    // 加载中
                    mProgess.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Log.d(TAG, "openFileChoose(ValueCallback<Uri> uploadMsg)");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getBaseActivity().startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                Log.d(TAG, "openFileChoose( ValueCallback uploadMsg, String acceptType )");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getBaseActivity().startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }
            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                Log.d(TAG, "openFileChoose(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getBaseActivity().startActivityForResult( Intent.createChooser( i, "File Browser" ), WebViewFragment.FILECHOOSER_RESULTCODE );
            }
            // For Android 5.0+
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Log.d(TAG, "onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                getBaseActivity().startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
                return true;
            }
        });

        mWebView.addJavascriptInterface(new MyInterface(),"android");

        //加载url
        mWebView.loadUrl(mUrl);
    }



    public static WebViewFragment getFragment(String url, boolean isWebInJump){

        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();

        bundle.putString(WEB_URL,url);
        bundle.putBoolean(WEB_IN,isWebInJump);

        webViewFragment.setArguments(bundle);

        return webViewFragment;
    }

    public static Fragment getFragment(String url){

        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();

        bundle.putString(WEB_URL,url);
        bundle.putBoolean(WEB_IN,true);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }



    public class MyInterface{

        @JavascriptInterface
        public void installAPP(String url){

            //Toast.makeText(getBaseActivity(),"js调用了InstallAPP的方法"+url,Toast.LENGTH_SHORT).show();
            //下载安装的逻辑
            startDownoad(url);

        }

        @JavascriptInterface
        public void browser(String url){
            //浏览器的操作
            //Toast.makeText(getBaseActivity(),"js调用了Browser的方法"+url,Toast.LENGTH_SHORT).show();

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 是否可以返回的操作；
     * @return
     */
    public boolean canBack(){
       /* if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }*/
        String url = "";
        if(mUrls!=null && !mUrls.isEmpty()){
            url = mUrls.removeLast();
        }

        if(!TextUtils.isEmpty(url)){
            mWebView.loadUrl(url);
            return true;
       }
        return false;
    }

    /**
     * 开始下载
     */
    public void startDownoad(String url){
        Intent intent = new Intent(getBaseActivity(), DownloadServiceWithNotifytation.class);
        intent.putExtra(DownloadServiceWithNotifytation.DOWNLOAD_URL,url);
        getBaseActivity().startService(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
            else  if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }
}
