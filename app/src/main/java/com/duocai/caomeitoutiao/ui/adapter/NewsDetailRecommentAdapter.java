package com.duocai.caomeitoutiao.ui.adapter;

import com.duocai.caomeitoutiao.model.bean.NewsItemBean;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;

import java.util.List;

/**
 * Created by Dinosa on 2018/1/18.
 */

public class NewsDetailRecommentAdapter extends HomeTabFragmentAdapter {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param activity
     * @param data     A new list is created out of this one to avoid mutable list
     */
    public NewsDetailRecommentAdapter(BaseActivity activity, List<NewsItemBean> data) {
        super(activity, data);
    }


}
