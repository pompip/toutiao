package com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom;

import android.content.Context;
import android.content.Intent;

import com.duocai.caomeitoutiao.service.AdDownloadServiceNew;
import com.duocai.caomeitoutiao.service.DownloadServiceInterface;
import com.duocai.caomeitoutiao.ui.activity.WebHelperActivity;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.base.BaseMuiltyViewHolder;

/**
 * Created by Dinosa on 2018/4/10.
 * 广告的一个基类的抽取
 */

public abstract class BaseCustomAdHolder<T> extends BaseMuiltyViewHolder<T> {

    //这里要做的一个逻辑就是判断是否是跳转网页，还是下载逻辑

    /**
     * 这里是一个去跳转逻辑
     * @param context
     * @param adBean
     */
    public void startToDownload(Context context, DownloadServiceInterface adBean){
        Intent intent = new Intent(context, AdDownloadServiceNew.class);
        intent.putExtra(AdDownloadServiceNew.DOWNLOAD_INTERFACE_BEAN,adBean);
        context.startService(intent);
    }

    /**
     * 这里是跳转网页的逻辑
     */
    public void startToGoWeb(Context context,String url,String title){
        //这里表示要进入一个网页的操作；
        context.startActivity(WebHelperActivity.getIntent(context,url,title,false));
    }
}
