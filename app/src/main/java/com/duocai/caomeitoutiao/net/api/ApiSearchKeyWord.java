package com.duocai.caomeitoutiao.net.api;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/2/7.
 */

public class ApiSearchKeyWord extends BaseApi<List<String>> {
    public ApiSearchKeyWord(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getSearchKeyList();
    }
}
