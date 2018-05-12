package com.duocai.caomeitoutiao.net.api.luckywalk;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.bean.luckywalk.ResLuckwalkProductBean;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/4/19.
 */

public class ApiLuckyWalkDes extends BaseApi<ResLuckwalkProductBean> {

    public ApiLuckyWalkDes(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getLuckGameDesc();
    }
}
