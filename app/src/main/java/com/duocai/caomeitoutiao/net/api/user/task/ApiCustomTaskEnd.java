package com.duocai.caomeitoutiao.net.api.user.task;

import com.duocai.caomeitoutiao.net.ApiServices;
import com.duocai.caomeitoutiao.net.api.BaseParamsApi;
import com.duocai.caomeitoutiao.net.bean.news.ResAward;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/4/13.
 */

public class ApiCustomTaskEnd extends BaseParamsApi<ResAward> {

    public final static String TASK_ID="tid";

    public ApiCustomTaskEnd(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String, String> paramsMap) {
        super(listener, rxAppCompatActivity, paramsMap);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).requestEndCustomTask(mParamsMap);
    }

    @Override
    public void handleException(Throwable e) {

    }
}
