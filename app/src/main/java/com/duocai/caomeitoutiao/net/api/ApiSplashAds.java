package com.duocai.caomeitoutiao.net.api;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.bean.ResSplashAds;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/2/9.
 */

public class ApiSplashAds extends BaseApi<ResSplashAds> {

    public ApiSplashAds(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getSplashAds();
    }

    @Override
    public void handleException(Throwable e) {
    }
}
