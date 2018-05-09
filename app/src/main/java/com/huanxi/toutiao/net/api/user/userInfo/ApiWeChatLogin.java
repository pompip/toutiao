package com.huanxi.toutiao.net.api.user.userInfo;

import com.huanxi.toutiao.net.ApiServices;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/1/31.
 */

public class ApiWeChatLogin extends BaseApi<String> {

    private final HashMap<String, String> mParamsMap;

    //微信登陆需要的参数；
    public static final String WECHAT_UNIONID="unionid";
    public static final String WECHAT_OPENID="openid";
    public static final String WECHAT_NICKNAME="nickname";
    public static final String WECHAT_SEX="sex";
    public static final String WECHAT_PROVINCE="province";
    public static final String WECHAT_CITY="city";
    public static final String WECHAT_COUNTRY="country";
    public static final String WECHAT_HEADIMGURL="headimgurl";

    public ApiWeChatLogin(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String,String> paramsMap) {
        super(listener, rxAppCompatActivity);
        mParamsMap = paramsMap;
        setShowProgress(true);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).requestWeChatLogin(mParamsMap);
    }

}
