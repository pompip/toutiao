package com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.news;

import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.BaseMuiltyAdapterBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.holder.news.NewsThreeImageHolder;

import java.util.List;

/**
 * Created by Dinosa on 2018/4/10.
 */

public class NewsThreeImageBean extends BaseMuiltyAdapterBean {

    public List<String> imageUrls;
    public String topic;
    public String source;
    public String date;

    public boolean isWebContent;

    public String url;
    public String urlMd5;

    public NewsThreeImageBean(List<String> imageUrls, String topic, String source, String date, String url, String urlMd5, boolean isWebContent) {
        this.imageUrls = imageUrls;
        this.topic = topic;
        this.source = source;
        this.date = date;
        this.isWebContent = isWebContent;
        this.url = url;
        this.urlMd5 = urlMd5;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public boolean isWebContent() {
        return isWebContent;
    }

    public void setWebContent(boolean webContent) {
        isWebContent = webContent;
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

    @Override
    public int getItemType() {
        return NewsThreeImageHolder.class.hashCode();
    }
}
