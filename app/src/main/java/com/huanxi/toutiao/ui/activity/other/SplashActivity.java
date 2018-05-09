package com.huanxi.toutiao.ui.activity.other;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.huanxi.toutiao.MyApplication;
import com.huanxi.toutiao.R;
import com.huanxi.toutiao.model.bean.JPushBroadcastBean;
import com.huanxi.toutiao.net.api.ApiSplashAds;
import com.huanxi.toutiao.net.bean.ResSplashAds;
import com.huanxi.toutiao.ui.activity.base.BaseActivity;
import com.huanxi.toutiao.ui.fragment.base.BaseFragment;
import com.huanxi.toutiao.ui.fragment.main.SplashGDTFragment;
import com.huanxi.toutiao.ui.fragment.main.SplashH5AdFragment;
import com.huanxi.toutiao.ui.fragment.main.SplashTuiAFragment;
import com.huanxi.toutiao.utils.SharedPreferencesUtils;
import com.huanxi.toutiao.utils.SystemUtils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Dinosa on 2018/1/4.
 */

public class SplashActivity extends BaseActivity  {


    @BindView(R.id.fl_ad_container)
    FrameLayout mFrameLayout;
    private Bundle mExtras;

    @Override
    public int getContentLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String [] permissions={
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA
        };

        mExtras = getIntent().getExtras();

        RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity instance
        rxPermissions
                .request(permissions)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {

                            //这里表示获取权限成功
                            if (isFirst()) {
                                getMyApplication().clearUser();
                                //第一次进入新手引导页；
                                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                                finish();
                            } else {
                                doGetAds();
                            }

                        } else {
                            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
                            toast("应用缺少必要的权限！请点击'权限'，打开所需要的权限。");
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        System.out.println("出异常了");
                    }
                });
    }

    public Bundle getExtras(){
        return mExtras;
    }

    /**
     * 获取所有的广告信息；
     */
    private void doGetAds() {

        ApiSplashAds apiSplashAds=new ApiSplashAds(new HttpOnNextListener<ResSplashAds>() {

            @Override
            public void onNext(ResSplashAds resSplashAds) {


                    //第二次直接进入广告；
                    BaseFragment baseFragment=null;

                    if (resSplashAds.getSplash().getType().equals(ResSplashAds.SplashBean.TYPE_GDT)) {
                        baseFragment=new SplashGDTFragment();
                    }else if(resSplashAds.getSplash().getType().equals(ResSplashAds.SplashBean.TYPE_CUSTOM)){

                        baseFragment=SplashH5AdFragment.getSplashH5Fragment(SplashActivity.this,resSplashAds.getSplash().getImgurl(),resSplashAds.getSplash().getUrl());

                    }else{
                        //这里我们要执行的是推啊的广告；
                        baseFragment=new SplashTuiAFragment();
                    }
                    //这里是执行广点通的广告；
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_ad_container,baseFragment)
                            .commitAllowingStateLoss();


                ((MyApplication) getApplication()) .setResAds(resSplashAds);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //这里我们就等待几秒中跳转到
                Observable.timer(3, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //startActivity(MainActivity.getIntent(SplashActivity.this,getExtras()));
                        startActivity();
                        finish();
                    }
                });
            }
        },this);

        HttpManager.getInstance().doHttpDeal(apiSplashAds);
    }


    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 这里我们判断是否是最新版本的第一次进入；
     * @return
     */
    public boolean isFirst(){
        String showGuide = SharedPreferencesUtils.getInstance(SplashActivity.this).isShowGuide(SplashActivity.this);
        return !SystemUtils.getVersionCode(this).equals(showGuide);
    }

    /**
     * 开启一个activity,进行一个跳转的操作；
     */
    public void startActivity(){

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.JPUSH_BROAD_CAST_BEAN,getJPushBroadcastBeanFromIntent());

        startActivity(intent);
    }

    /**
     * 这里从intent访问JPushBroadcast内容；
     * @return
     */
    public JPushBroadcastBean getJPushBroadcastBeanFromIntent(){
        return ((JPushBroadcastBean) getIntent().getSerializableExtra(MainActivity.JPUSH_BROAD_CAST_BEAN));
    }


}
