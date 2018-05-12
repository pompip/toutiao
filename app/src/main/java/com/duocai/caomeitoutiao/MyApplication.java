package com.duocai.caomeitoutiao;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.db.ta.sdk.TaSDK;
import com.duocai.caomeitoutiao.globle.ConstantThreePart;
import com.duocai.caomeitoutiao.model.bean.UserBean;
import com.duocai.caomeitoutiao.net.bean.ResSplashAds;
import com.duocai.caomeitoutiao.ui.dialog.LoadingDialog;
import com.duocai.caomeitoutiao.utils.SharedPreferencesUtils;
import com.duocai.caomeitoutiao.utils.SystemUtils;
import com.mob.MobSDK;
import com.qumi.jfq.QuMiWall;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.zhxu.library.RxRetrofitApp;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.MultiActionsNotificationBuilder;
import sd.sazs.erd.AdManager;


/**
 * Created by Dinosa on 2018/1/18.
 */

public class MyApplication extends Application {

    private ResSplashAds mResSplashAds;
    public static Context mContext;
    private List<String> mAllPackageName;


    @Override
    public void onCreate() {
        super.onCreate();


        initUM();

        initMob();

        initBugly();

        initNet();

        initJPush();

        initTa();

        initYouMi();

        initQuMi();


        mContext=this;
    }

    private void initQuMi() {
        QuMiWall.init(getApplicationContext());
    }

    private void initYouMi() {

        //有米的sdk
        String appId = ConstantThreePart.YOUMI_APPKEY;
        String appSecret = ConstantThreePart.YOUMI_APPSECRET;
        AdManager.getInstance(this).init(appId, appSecret, true);
    }

    private void initTa() {

        TaSDK.init(this);
    }

    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //这里添加点击事件；
        MultiActionsNotificationBuilder builder = new MultiActionsNotificationBuilder(this);
        JPushInterface.setPushNotificationBuilder(10, builder);
    }

    private void initNet() {


        RxRetrofitApp.init(this,true, LoadingDialog.class,SystemUtils.getAppMetaData(this)+"",SystemUtils.getVersionCode(this));

        //这里我们做一个相应的操作；
        String token = SharedPreferencesUtils.getInstance(this).getToken();
        UserBean userBean = SharedPreferencesUtils.getInstance(this).getUserBean();
        if (!TextUtils.isEmpty(token) && userBean!=null && !TextUtils.isEmpty(userBean.getUserid()) ) {
            RxRetrofitApp.setUidAndToken(token,userBean.getUserid());
        }
    }

    private void initBugly() {

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);

        String[] stringArray = getResources().getStringArray(R.array.channel);

        Integer appMetaData = SystemUtils.getAppMetaData(this);
        if(appMetaData>stringArray.length){
            //防止欢喜的是100
            appMetaData=stringArray.length;
        }
        strategy.setAppChannel(stringArray[appMetaData-1]);  //设置渠道
        strategy.setAppVersion(SystemUtils.getVersionName(this));      //App的版本

        ///CrashReport.initCrashReport(this, ConstantThreePart.BUGLY_APP_ID, true,strategy);
        Bugly.init(this, ConstantThreePart.BUGLY_APP_ID, true,strategy);
    }

    private void initMob() {

        MobSDK.init(this);
    }

    private void initUM() {

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);

    }


    public UserBean getUserBean(){

        return SharedPreferencesUtils.getInstance(this).getUserBean();
    }

    public void updateUser(UserBean userBean){
        //这里我们是需要更新token；
        SharedPreferencesUtils.getInstance(this).setUser(userBean);
    }

    public void updateTokenAndUid(String token,String uid){
        if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(uid)){
            RxRetrofitApp.setUidAndToken(token,uid);
            SharedPreferencesUtils.getInstance(this).saveToken(token);
        }
    }

    public void clearUser(){
        RxRetrofitApp.setToken("");
        RxRetrofitApp.setUid("");
        SharedPreferencesUtils.getInstance(this).clearToken();
        SharedPreferencesUtils.getInstance(this).clearUser();
    }

    /**
     * 绑定广告的返回值；
     */
    public void setResAds(ResSplashAds resSplashAds){
        mResSplashAds = resSplashAds;
    }

    public boolean isHasAds(){
        return mResSplashAds!=null;
    }


    /**
     * 返回所有的广告的操作；
     * @return
     */
    public ResSplashAds getResAds(){
        return mResSplashAds==null?new ResSplashAds():mResSplashAds;
    }


    public static Context getConstantContext(){
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
        // 安装tinker
        Beta.installTinker();
    }

    /**
     * 设置所有的包名的名字
     */
    public void setAllPackageName(List<String> allPackageName){
        mAllPackageName = allPackageName;
    }

    /**
     * 获取所有的包名
     * @return
     */
    public List<String> getAllPackageName(){
        return mAllPackageName;
    }


}
