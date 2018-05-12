package com.duocai.caomeitoutiao.net.api;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.bean.luckywalk.ResLuckwalkProductBean;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/4/20.
 */

public class ApiRequestUserAwardRecord extends BaseParamsApi<List<ResLuckwalkProductBean>> {


    public ApiRequestUserAwardRecord(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String, String> paramsMap) {
        super(listener, rxAppCompatActivity, paramsMap);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).requestUserLuckyWalkAwardRecord(mParamsMap);
    }
}
