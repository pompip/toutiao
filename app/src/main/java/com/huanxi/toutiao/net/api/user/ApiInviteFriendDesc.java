package com.huanxi.toutiao.net.api.user;

import com.huanxi.toutiao.net.ApiServices;
import com.huanxi.toutiao.net.api.BaseParamsApi;
import com.huanxi.toutiao.net.bean.ResInviteFriendDesc;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/4/12.
 */

public class ApiInviteFriendDesc extends BaseParamsApi<ResInviteFriendDesc> {

    public ApiInviteFriendDesc(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String, String> paramsMap) {
        super(listener, rxAppCompatActivity, paramsMap);
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getInviteFriendDesc(mParamsMap);
    }
}
