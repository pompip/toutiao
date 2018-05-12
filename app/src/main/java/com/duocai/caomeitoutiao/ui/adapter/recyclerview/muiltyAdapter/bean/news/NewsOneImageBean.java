package com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.news;

import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.BaseMuiltyAdapterBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.holder.news.NewsOneImageHolder;

/**
 * Created by Dinosa on 2018/4/10.
 */

public class NewsOneImageBean extends BaseMuiltyAdapterBean{

    public String imageUrl;
    public String topic;
    public String source;
    public String date;


    public String url;
    public String urlMd5;

    public boolean isWebContent;


    public NewsOneImageBean(String imageUrl, String topic, String source, String date, String url, String urlMd5,boolean isWebContent) {
        this.imageUrl = imageUrl;
        this.topic = topic;
        this.source = source;
        this.date = date;
        this.url = url;
        this.urlMd5 = urlMd5;
        this.isWebContent=isWebContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlMd5() {
        return urlMd5;
    }

    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }

    public boolean isWebContent(){
        return isWebContent;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getItemType() {
        return NewsOneImageHolder.class.hashCode();
    }
}
