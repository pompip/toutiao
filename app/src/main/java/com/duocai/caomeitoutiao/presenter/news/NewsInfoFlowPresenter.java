package com.duocai.caomeitoutiao.presenter.news;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.duocai.caomeitoutiao.MyApplication;
import com.duocai.caomeitoutiao.net.bean.news.ResNewsAndVideoBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomBanner20_3Bean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomBigBannerBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomLeftTitlRightImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomTitleDownThreeImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomUpTitleDownImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.gdt.GDTImgAds;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.gdt.GdtBigBannerBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.ta.TaLeftTitleRightImgAds;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.ta.TaLeftTitleRightImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.news.NewsOneImageBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.news.NewsThreeImageBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.video.VideoListBean;
import com.qq.e.ads.nativ.NativeExpressADView;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dinosa on 2018/4/13.
 * 新闻信息流的业务逻辑类；这里将信息流的数据进行一个格式化的操作
 */

public class NewsInfoFlowPresenter {


    public LinkedList<NativeExpressADView> mGdtAdLists=new LinkedList<>();
    private GDTImgAds mGdtImgAds;
    private TaLeftTitleRightImgAds mTaLeftTitleRightImgAds;

    public NewsInfoFlowPresenter( GDTImgAds gdtImgAds, TaLeftTitleRightImgAds taLeftTitleRightImgAds) {

        mGdtImgAds = gdtImgAds;
        mTaLeftTitleRightImgAds = taLeftTitleRightImgAds;

    }

    /**
     * 这里我们要做的一个操作就是过滤并填充数据的一个操作；
     *
     * @param list
     */
    public List<MultiItemEntity> filterData(List<ResNewsAndVideoBean.HomeInfoBean> list) {

        ArrayList<MultiItemEntity> multiItemEntities = new ArrayList<>();

        if (list != null) {
            for (ResNewsAndVideoBean.HomeInfoBean homeInfoBean : list) {

                if (homeInfoBean.isNews()) {
                    //新闻的逻辑
                    multiItemEntities.add(getNews(homeInfoBean));
                    continue;
                }

                if (homeInfoBean.isVideo()) {
                    multiItemEntities.add(getVideo(homeInfoBean));
                    continue;
                }

                if (homeInfoBean.isAd()) {
                    //这里暂时不做任何处理的操作；
                    if (getAd(homeInfoBean) != null) {
                        multiItemEntities.add(getAd(homeInfoBean));
                    }
                    continue;
                }
            }
        }
        return multiItemEntities;
    }

    private MultiItemEntity getVideo(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {
        //这里表示是视频的逻辑
        MultiItemEntity multiItemEntity = null;


        String title = homeInfoBean.getTitle();
        String source = homeInfoBean.getSource();
        String urlMd5 = homeInfoBean.getUrlmd5();
        String imageUrl = "";
        String item_id = homeInfoBean.getItem_id();
        String group_id = homeInfoBean.getGroup_id();
        String video_id = homeInfoBean.getVideo_id();
        String publishTime = homeInfoBean.getPublish_time();
        Long duration = homeInfoBean.getVideo_duration();

        try {
            imageUrl = homeInfoBean.getLarge_image_list().get(0).getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        multiItemEntity = new VideoListBean(source, urlMd5, title, imageUrl, item_id, group_id, video_id, publishTime, duration);

        return multiItemEntity;
    }

    private MultiItemEntity getNews(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {


        String topic = homeInfoBean.getTopic();
        String source = homeInfoBean.getSource();
        String date = homeInfoBean.getDate();
        String urlMd5 = homeInfoBean.getUrlmd5();
        boolean isWebContent = homeInfoBean.isWebContent();
        String url = homeInfoBean.getUrl();

        MultiItemEntity itemEntity = null;

        if (homeInfoBean.isMuiltImgNews()) {

            //这里表示是单图的新闻；
            List<String> imageUrl = new ArrayList<>();

            try {
                if (homeInfoBean.getMiniimg() != null && homeInfoBean.getMiniimg().size() > 0) {

                    for (ResNewsAndVideoBean.MiniimgBean miniimgBean : homeInfoBean.getMiniimg()) {
                        imageUrl.add(miniimgBean.getSrc());
                    }
                    if (imageUrl.size()<3) {
                        for (int i=0;i<3-imageUrl.size();i++){
                            //这里会出现图小于3个的吗？
                            // TODO: 2018/4/11
                            imageUrl.add(homeInfoBean.getMiniimg().get(0).getSrc());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemEntity = new NewsThreeImageBean(imageUrl, topic, source, date, url, urlMd5, isWebContent);

        } else {

            //这里表示是多图的新闻；
            String imageUrl = null;
            try {
                imageUrl = homeInfoBean.getMiniimg().get(0).getSrc();
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemEntity = new NewsOneImageBean(imageUrl, topic, source, date, url, urlMd5, isWebContent);
        }
        return itemEntity;
    }

    private MultiItemEntity getAd(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {

        MultiItemEntity multiItemEntity = null;

        if (homeInfoBean.isGdtAd()) {
            multiItemEntity = getGdtAd(homeInfoBean);
        }

        if (homeInfoBean.isTaAd()) {
            multiItemEntity = getTuiAAd(homeInfoBean);
        }

        if (homeInfoBean.isCustomAd()) {
            multiItemEntity = getCustomAd(homeInfoBean);
        }
        // homeInfoBean
        return multiItemEntity;
    }

    /**
     * 获取自定义的广告；
     *
     * @return
     */
    private MultiItemEntity getCustomAd(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {

        MultiItemEntity multiItemEntity = null;


        String id=homeInfoBean.getId();
        String cont=homeInfoBean.getCont();
        String imgurl=homeInfoBean.getImgurl();
        String url=homeInfoBean.getUrl();
        String downurl=homeInfoBean.getDownurl();
        Long size=homeInfoBean.getSize();
        String packename=homeInfoBean.getPackename();
        String appname=homeInfoBean.getAppname();
        List<String> imgurls=homeInfoBean.getImgurls();
        String title=homeInfoBean.getTitle();


        if (homeInfoBean.isCustomBanner20_3Ad()) {

            multiItemEntity=new CustomBanner20_3Bean(downurl,url,size,packename,appname,imgurl);
        }

        if (homeInfoBean.isCustomBigBannerAd()) {
            multiItemEntity=new CustomBigBannerBean(downurl,url,size,packename,appname,imgurl);
        }

        if (homeInfoBean.isCustomLeftTitleRightImg()) {
            multiItemEntity=new CustomLeftTitlRightImgBean(downurl,url,size,packename,appname,cont,title,imgurl);
        }

        if (homeInfoBean.isCustomUpTitleDownImg()) {
            //上文下图；
            multiItemEntity=new CustomUpTitleDownImgBean(downurl,url,size,packename,appname,title,cont,imgurl);
        }

        if (homeInfoBean.isUpTitleDownMuiltyImg()) {
            multiItemEntity=new CustomTitleDownThreeImgBean(downurl,url,size,packename,appname,title,imgurls,cont);
        }

        return multiItemEntity;
    }

    /**
     * 获取推啊的广告；
     *
     * @return
     */
    private MultiItemEntity getTuiAAd(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {

        MultiItemEntity multiItemEntity=null;

        View view = mTaLeftTitleRightImgAds.getView();
        if (view != null) {
            multiItemEntity=new TaLeftTitleRightImgBean(view);
        }
        return multiItemEntity;
    }

    /**
     * 这里是获取广点通的广告
     *
     * @return
     */
    private MultiItemEntity getGdtAd(ResNewsAndVideoBean.HomeInfoBean homeInfoBean) {

        MultiItemEntity multiItemEntity=null;;

        if (mGdtAdLists!=null && mGdtAdLists.size()>0) {
            NativeExpressADView gdtadView = mGdtAdLists.removeFirst();

            gdtadView.setPadding(UIUtil.dip2px(getContext(), 12), UIUtil.dip2px(getContext(), 12),
                    UIUtil.dip2px(getContext(), 10), UIUtil.dip2px(getContext(), 10));

            multiItemEntity = new GdtBigBannerBean(gdtadView);
            if(mGdtAdLists.size()<2){
                mGdtImgAds.load();
            }
        }else{
            mGdtImgAds.load();
        }
        return multiItemEntity;
    }


    public void destory(){
        if (mGdtImgAds != null) {
            mGdtImgAds.destory();
        }

        if (mTaLeftTitleRightImgAds != null) {
            mTaLeftTitleRightImgAds.destory();
        }
        mGdtAdLists=null;
    }

    public Context getContext(){
        return MyApplication.mContext;
    }

}
