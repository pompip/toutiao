package com.huanxi.toutiao.net.api.user.browerRecord;

import com.huanxi.toutiao.net.ApiServices;
import com.huanxi.toutiao.net.bean.browerRecord.ResUserVideoBrowerRecord;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/2/3.
 */

public class ApiBrowerRecordVideoList extends BaseApi<ResUserVideoBrowerRecord> {


    private  HashMap<String, String> mParamsMap;

    public static String FROM_UID="from_uid";
    public static String PAGE_SIZE="pageSize";
    public static String PAGE_NUM="pageNum";
    private static String TYPE="type";

    public ApiBrowerRecordVideoList(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity,HashMap<String,String> paramsMap) {
        super(listener, rxAppCompatActivity);
        mParamsMap=paramsMap;
        mParamsMap.put(TYPE,"2");
        mParamsMap.put(PAGE_SIZE,"20");
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getVideoBrowerRecordList(mParamsMap);
    }
}
