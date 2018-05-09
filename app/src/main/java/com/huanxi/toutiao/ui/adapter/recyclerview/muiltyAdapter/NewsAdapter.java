package com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.base.BaseMuiltyAdapter;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.base.MuiltyBean;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom.Banner20_3Holder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom.BigBannerHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom.LeftTitleRightImgHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom.UpTitleDownImgHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.custom.UpTitleDownThreeImgHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.gdt.GdtBigBannerAdHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.ads.ta.TaLeftTitleRightImgHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.news.NewsOneImageHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.news.NewsThreeImageHolder;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.holder.video.VideoListBeanHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dinosa on 2018/4/10.
 * 这里是新闻的最新的adapter;
 */

public class NewsAdapter extends BaseMuiltyAdapter {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public NewsAdapter(List<MultiItemEntity> data) {
        super(data);
        //这里要注册多种数据类型；
        //这里要注册多种数据类型；
        ArrayList<MuiltyBean> muiltyBeen = new ArrayList<>();

        muiltyBeen.add(new MuiltyBean(new VideoListBeanHolder(), R.layout.item_vedio_layout_in_news));  //视频的逻辑；

        muiltyBeen.add(new MuiltyBean(new NewsOneImageHolder(), R.layout.item_home_img_one));  // 新闻单图
        muiltyBeen.add(new MuiltyBean(new NewsThreeImageHolder(), R.layout.item_home_img_three));  //新闻多图


        muiltyBeen.add(new MuiltyBean(new BigBannerHolder(), R.layout.item_customer_ad1));  //大banner；
        muiltyBeen.add(new MuiltyBean(new UpTitleDownImgHolder(), R.layout.item_customer_ad2));  //广告上文下图
        muiltyBeen.add(new MuiltyBean(new LeftTitleRightImgHolder(), R.layout.item_customer_ad3));  //左文右图
        muiltyBeen.add(new MuiltyBean(new Banner20_3Holder(), R.layout.item_customer_ad4));  //小banner；
        muiltyBeen.add(new MuiltyBean(new UpTitleDownThreeImgHolder(), R.layout.item_customer_ad5));  //上文下三图


        muiltyBeen.add(new MuiltyBean(new GdtBigBannerAdHolder(), R.layout.item_gdt_up_text_down_img));  //广点通的广告；
        muiltyBeen.add(new MuiltyBean(new TaLeftTitleRightImgHolder(), R.layout.item_ad_container));  //推啊的广告；

        //每次增加这种绑定关系就ojbk

        //muiltyBeen.add(new MuiltyBean(new Banner20_3Holder(), R.layout.item_new_vedio_layout));  //其他

        register(muiltyBeen);
    }
}