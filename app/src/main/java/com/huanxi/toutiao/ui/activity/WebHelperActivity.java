package com.huanxi.toutiao.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.activity.base.BaseTitleActivity;
import com.huanxi.toutiao.ui.fragment.WebViewFragment;


public class WebHelperActivity extends BaseTitleActivity  {


    public static final String WEB_TITLE="title";
    public static final String WEB_URL="url";
    public static final String WEB_IN="isJumpWeb";
    private boolean mWebInJump;
    private String mUrl;
    private String mWebTitle;
    private WebViewFragment mFragment;

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        super.initView(rootView, savedInstanceState);

        setBackText("");
        setTitle("");

        mWebTitle = getIntent().getStringExtra(WEB_TITLE);
        mUrl = getIntent().getStringExtra(WEB_URL);
        mWebInJump = getIntent().getBooleanExtra(WEB_IN,true);

        setTitle(mWebTitle);

        mFragment = WebViewFragment.getFragment(mUrl,mWebInJump);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container,mFragment)
                .commitNowAllowingStateLoss();
    }



    public static Intent getIntent(Context context,String url,String title,boolean isWebIn){

        Intent intent = new Intent(context, WebHelperActivity.class);
        intent.putExtra(WEB_URL,url);
        intent.putExtra(WEB_IN,isWebIn);
        intent.putExtra(WEB_TITLE,title);
        return intent;
    }

    public static Intent getIntent(Context context,String url,String title){

       return getIntent(context,url,title,true);
    }



    @Override
    public void onBackPressed() {
        if (!mFragment.canBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickBack() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode,resultCode,data);
    }
}
