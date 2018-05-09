package com.huanxi.toutiao.net.api.video;

import com.huanxi.toutiao.net.ApiServices;
import com.huanxi.toutiao.net.api.BaseParamsApi;
import com.huanxi.toutiao.net.bean.video.ResVideoList;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/1/25.
 */

public class ApiVedioList extends BaseParamsApi<ResVideoList> {


    public static String CATEGORY="category";
    public static String LATITUDE="latitude";
    public static String LONGITUDE="longitude";
    public static String CITY="city";

    //category=video&latitude=11&longitude=100&city=%E5%8C%97%E4%BA%AC

    public ApiVedioList(HttpOnNextListener listener, HashMap<String,String> paramsMap, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity,paramsMap);
        mParamsMap.put(LATITUDE,"11");
        mParamsMap.put(LONGITUDE,"100");
        mParamsMap.put(CITY,"%E5%8C%97%E4%BA%AC");
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getVedioList(mParamsMap);
    }


}
