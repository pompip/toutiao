package com.duocai.caomeitoutiao.net.api;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.bean.ResCheckVersion;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/1/31.
 */

public class ApiCheckVersion extends BaseApi<ResCheckVersion> {

    public static String VERSION_CODE="versionCode";
    public static String APP_CHANNEL="channel";
    private final HashMap<String, String> mParamsMap;

    public ApiCheckVersion(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String,String> paramsMap) {
        super(listener, rxAppCompatActivity);
        mParamsMap = paramsMap;
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).checkVersion(mParamsMap);
    }
}
