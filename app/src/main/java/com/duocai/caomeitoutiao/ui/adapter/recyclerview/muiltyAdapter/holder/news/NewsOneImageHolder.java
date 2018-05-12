package com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.holder.news;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.db.ta.sdk.TmActivity;
import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.ui.activity.news.NewsDetailActivity2;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.base.BaseMuiltyViewHolder;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.news.NewsOneImageBean;
import com.duocai.caomeitoutiao.utils.ImageUtils;

/**
 * Created by Dinosa on 2018/4/10.
 */

public class NewsOneImageHolder extends BaseMuiltyViewHolder<NewsOneImageBean> {

    @Override
    public void init(final NewsOneImageBean newsOneImageBean, BaseViewHolder holder, final Context context) {

        holder.setText(R.id.tv_news_title, newsOneImageBean.getTopic());
        ImageUtils.loadImage(context, newsOneImageBean.getImageUrl(), (ImageView) holder.getView(R.id.iv_news_img));
        holder.setText(R.id.tv_source, newsOneImageBean.getSource());
        holder.setText(R.id.tv_time, newsOneImageBean.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsOneImageBean.isWebContent()) {
                    //这里跳转到东方头条
                    TmActivity.a(context, newsOneImageBean.getUrl());
                }else{
                    //跳转新闻详情；
                    context.startActivity(NewsDetailActivity2.getIntent(context,newsOneImageBean.getUrl(),newsOneImageBean.getUrlMd5()));
                }
            }
        });
    }
}
