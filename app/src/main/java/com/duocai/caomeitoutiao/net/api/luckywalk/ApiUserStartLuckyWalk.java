package com.duocai.caomeitoutiao.net.api.luckywalk;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.api.BaseParamsApi;
import com.duocai.caomeitoutiao.net.bean.luckywalk.ResLuckwalkProductBean;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/4/19.
 */

public class ApiUserStartLuckyWalk extends BaseParamsApi<ResLuckwalkProductBean> {

    public ApiUserStartLuckyWalk(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String, String> paramsMap) {
        super(listener, rxAppCompatActivity, paramsMap);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).requestStartLuckyWalk(mParamsMap);
    }
}
