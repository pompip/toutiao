package com.duocai.caomeitoutiao.ui.adapter.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.duocai.caomeitoutiao.model.bean.NewsCommentBean;
import com.duocai.caomeitoutiao.ui.adapter.NewsDetailAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dinosa on 2018/3/1.
 */

public class NewsDetailComments implements MultiItemEntity{

    List<NewsCommentBean> mList=new ArrayList<>();

    public List<NewsCommentBean> getList() {
        return mList;
    }

    public void setList(List<NewsCommentBean> list) {
        mList = list;
    }

    @Override
    public int getItemType() {
        return NewsDetailAdapter.TYPE_NEW_COMMENT;
    }
}
