package com.huanxi.toutiao.net.api.user.comment;

import com.huanxi.toutiao.net.ApiServices;
import com.huanxi.toutiao.net.bean.user.ResUserComments;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.api.BaseApi;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Dinosa on 2018/2/2.
 */

public class ApiUserCommentsList extends BaseApi<ResUserComments> {

    HashMap<String,String> mParamsMap = new HashMap<String,String>();

    public static  final String FROM_UID="from_uid";
    public static  final String COMMENT_CONTENT="content";
    public static  final String ISSUE_ID="urlmd5";

    public ApiUserCommentsList(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity, HashMap<String,String> paramsMap) {
        super(listener, rxAppCompatActivity);
        mParamsMap=paramsMap;
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        return retrofit.create(ApiServices.class).getMineCommentList(mParamsMap);
    }
}
