package com.duocai.caomeitoutiao.ui.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.duocai.caomeitoutiao.net.api.video.ApiVedioList;
import com.duocai.caomeitoutiao.net.bean.news.HomeTabBean;
import com.duocai.caomeitoutiao.net.bean.video.ResVideoList;
import com.duocai.caomeitoutiao.ui.activity.other.MainActivity;
import com.duocai.caomeitoutiao.ui.adapter.bean.VideoBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.VideoListAdapter;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomBanner20_3Bean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomBigBannerBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomLeftTitlRightImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomTitleDownThreeImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.custom.CustomUpTitleDownImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.gdt.GDTImgAds;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.gdt.GdtBigBannerBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.ta.TaUpTitleDownImgAds;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.ta.TaUpTitleDownImgBean;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.video.VideoListBean;
import com.duocai.caomeitoutiao.ui.fragment.base.BaseLoadingRecyclerViewFragment;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;
import com.zhxu.library.utils.SystemUtils;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dinosa on 2018/1/15.
 * <p>
 * 封装抽取；
 */

public class VideoTabFragment extends BaseLoadingRecyclerViewFragment {


    public static final String TAB_BEAN = "tabBean";

    private HomeTabBean mHomeTabBean;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            Object object = getArguments().getSerializable(TAB_BEAN);
            if (object != null) {
                mHomeTabBean = ((HomeTabBean) object);
            }
        }
    }

    public LinkedList<NativeExpressADView> mGdtAdLists = new LinkedList<>();
    GDTImgAds mGdtImgAds;
    TaUpTitleDownImgAds mTaLeftTitleRightImgAds;

    @Override
    protected void initView() {
        super.initView();

        //这里初始化广点通广告的逻辑
        mGdtImgAds = new GDTImgAds(new GDTImgAds.OnAdReceived() {
            @Override
            public void onGdtImgAdReceived(List<NativeExpressADView> mImgAds) {
                if (mImgAds != null) {
                    mGdtAdLists.addAll(mImgAds);
                }
            }
        });

        mTaLeftTitleRightImgAds = new TaUpTitleDownImgAds();
    }

    private void getDataFromNet(final boolean isFirst) {

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiVedioList.CATEGORY, mHomeTabBean.getCode());

        paramsMap.put("device_id", SystemUtils.getIMEI(getActivity()));
        paramsMap.put("device_platform", "android");
        paramsMap.put("device_type", SystemUtils.getSystemModel());
        paramsMap.put("device_brand", SystemUtils.getDeviceBrand());
        paramsMap.put("os_api", SystemUtils.getSystemApi());
        paramsMap.put("os_version", SystemUtils.getSystemVersion());
        paramsMap.put("uuid", SystemUtils.getIMEI(getActivity()));
        paramsMap.put("openudid", SystemUtils.getOpenUid(getActivity()));
        paramsMap.put("resolution", SystemUtils.getResolution(getActivity()));
        paramsMap.put("dpi", SystemUtils.getDensity(getActivity()));
        paramsMap.put(ApiVedioList.PAGE_NUM, "1");

        ApiVedioList apiVedioList = new ApiVedioList(getRefreshListener(isFirst), paramsMap, ((RxAppCompatActivity) getActivity()));

        HttpManager.getInstance().doHttpDeal(apiVedioList);
    }

    public HttpOnNextListener getRefreshListener(final boolean isFirst){
      return  new HttpOnNextListener<ResVideoList>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showFaild();
            }

            @Override
            public void onNext(ResVideoList vedioDataBeen) {
                if (isFirst) {
                    if (vedioDataBeen.getList() != null && vedioDataBeen.getList().size() > 0) {

                        showSuccess();
                    } else {

                        showEmpty();
                    }

                } else {
                    refreshComplete();
                    if (vedioDataBeen.getList().size() > 0) {

                        ((MainActivity) getBaseActivity()).getVideoFragment().showRefreshBanner(vedioDataBeen.getList().size());

                    }
                }

                if (vedioDataBeen.getList() != null && vedioDataBeen.getList().size() > 0) {
                    mPage = 2;

                    mVideoListAdapter.replaceData(filterData(vedioDataBeen.getList()));
                }
            }
        };
    }

    /**
     * 这里我们要做的一个操作就是显示过滤数据的一个操作；
     *
     * @param list
     * @return
     */
    private List<MultiItemEntity> filterData(List<VideoBean> list) {

        ArrayList<MultiItemEntity> multiItemEntities = new ArrayList<>();

        if (list != null && list.size() > 0) {

            for (VideoBean videoBean : list) {

                if (videoBean.isAd()) {
                    MultiItemEntity ad = getAd(videoBean);
                    if (ad != null) {
                        multiItemEntities.add(ad);
                    }
                    continue;
                }

                if (videoBean.isVideo()) {
                    multiItemEntities.add(getVideo(videoBean));
                    continue;
                }

            }

        }

        return multiItemEntities;
    }

    private MultiItemEntity getVideo(VideoBean videoBean) {

        //这里表示是视频的逻辑
        MultiItemEntity multiItemEntity = null;

        String title = videoBean.getContent().getTitle();
        String source = videoBean.getContent().getSource();
        String urlMd5 = videoBean.getUrlmd5();
        String imageUrl = "";
        String item_id = videoBean.getContent().getItem_id();
        String group_id = videoBean.getContent().getGroup_id();
        String video_id = videoBean.getContent().getVideo_id();
        String publishTime = videoBean.getContent().getPublish_time();
        Long duration = videoBean.getContent().getVideo_duration();

        try {
            imageUrl = videoBean.getContent().getLarge_image_list().get(0).getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        multiItemEntity = new VideoListBean(source, urlMd5, title, imageUrl, item_id, group_id, video_id, publishTime, duration);

        return multiItemEntity;
    }


    private MultiItemEntity getAd(VideoBean homeInfoBean) {
        //这里我们做的一个操作就是
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

    //====================下面做的一个操作就是显示广告；================

    private MultiItemEntity getCustomAd(VideoBean homeInfoBean) {
        //这里又分了5中；
        MultiItemEntity multiItemEntity = null;


        String id = homeInfoBean.getId();
        String cont = homeInfoBean.getCont();
        String imgurl = homeInfoBean.getImgurl();
        String url = homeInfoBean.getUrl();
        String downurl = homeInfoBean.getDownurl();
        Long size = homeInfoBean.getSize();
        String packename = homeInfoBean.getPackename();
        String appname = homeInfoBean.getAppname();
        List<String> imgurls = homeInfoBean.getImgurls();
        String title = homeInfoBean.getTitle();


        if (homeInfoBean.isCustomBanner20_3Ad()) {

            multiItemEntity = new CustomBanner20_3Bean(downurl, url, size, packename, appname, imgurl);
        }

        if (homeInfoBean.isCustomBigBannerAd()) {
            multiItemEntity = new CustomBigBannerBean(downurl, url, size, packename, appname, imgurl);
        }

        if (homeInfoBean.isCustomLeftTitleRightImg()) {
            multiItemEntity = new CustomLeftTitlRightImgBean(downurl, url, size, packename, appname, cont, title, imgurl);
        }

        if (homeInfoBean.isCustomUpTitleDownImg()) {
            //上文下图；
            multiItemEntity = new CustomUpTitleDownImgBean(downurl, url, size, packename, appname, title, cont, imgurl);
        }

        if (homeInfoBean.isUpTitleDownMuiltyImg()) {
            multiItemEntity = new CustomTitleDownThreeImgBean(downurl, url, size, packename, appname, title, imgurls, cont);
        }

        return multiItemEntity;
    }

    private MultiItemEntity getTuiAAd(VideoBean videoBean) {

        MultiItemEntity multiItemEntity = null;

        View view = mTaLeftTitleRightImgAds.getView();
        if (view != null) {
            multiItemEntity = new TaUpTitleDownImgBean(view);
        }

        return multiItemEntity;
    }

    private MultiItemEntity getGdtAd(VideoBean videoBean) {
        //这里要做的一个操作;
        MultiItemEntity multiItemEntity = null;
        if (mGdtAdLists != null && mGdtAdLists.size() > 0) {

            NativeExpressADView gdtadView = mGdtAdLists.removeFirst();

            gdtadView.setPadding(0, 0, 0, UIUtil.dip2px(getContext(), 10));

            multiItemEntity = new GdtBigBannerBean(gdtadView);
            if (mGdtAdLists.size() < 2) {
                mGdtImgAds.load();
            }
        } else {
            mGdtImgAds.load();
        }
        return multiItemEntity;
    }

    ///================================


    @Override
    protected void onRetry() {
        super.onRetry();
        //之类我们执行相应的逻辑；
        getDataFromNet(true);
    }

    ///=========进行一个抽取=================
    private VideoListAdapter mVideoListAdapter;


    @Override
    public RecyclerView.Adapter getAdapter() {
        if (mVideoListAdapter == null) {
            mVideoListAdapter = new VideoListAdapter(null);
        }
        return mVideoListAdapter;
    }

    @Override
    public void requestAdapterData(boolean isFirst) {
        getDataFromNet(isFirst);
    }

    protected int mPage = 1;

    @Override
    public void requestNextAdapterData() {
        //请求更多数据；

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiVedioList.CATEGORY, mHomeTabBean.getCode());

        paramsMap.put("device_id", SystemUtils.getIMEI(getActivity()));
        paramsMap.put("device_platform", "android");
        paramsMap.put("device_type", SystemUtils.getSystemModel());
        paramsMap.put("device_brand", SystemUtils.getDeviceBrand());
        paramsMap.put("os_api", SystemUtils.getSystemApi());
        paramsMap.put("os_version", SystemUtils.getSystemVersion());
        paramsMap.put("uuid", SystemUtils.getIMEI(getActivity()));
        paramsMap.put("openudid", SystemUtils.getOpenUid(getActivity()));
        paramsMap.put("resolution", SystemUtils.getResolution(getActivity()));
        paramsMap.put("dpi", SystemUtils.getDensity(getActivity()));
        paramsMap.put(ApiVedioList.PAGE_NUM, mPage + "");

        ApiVedioList apiVedioList = new ApiVedioList(getLoadMoreListener(), paramsMap, ((RxAppCompatActivity) getActivity()));

        HttpManager.getInstance().doHttpDeal(apiVedioList);
    }

    public HttpOnNextListener getLoadMoreListener(){
        return new HttpOnNextListener<ResVideoList>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                loadMoreComplete();
            }

            @Override
            public void onNext(ResVideoList vedioDataBeen) {

                if (vedioDataBeen.getList() != null) {

                    if (vedioDataBeen.getList().size() > 0) {
                        mPage++;
                    }
                    mVideoListAdapter.addData(filterData(vedioDataBeen.getList()));
                }

                loadMoreComplete();
            }
        };
    }


    /**
     * 获取HomeTabFragment
     *
     * @param bean 传入的对象；
     * @return 一个HomeTabFragment;
     */
    public static VideoTabFragment getVideoTabFragment(HomeTabBean bean) {
        VideoTabFragment homeTabFragment = new VideoTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAB_BEAN, bean);
        homeTabFragment.setArguments(bundle);
        return homeTabFragment;
    }


    @Override
    public void onDestroy() {

        if (mGdtImgAds != null) {
            mGdtImgAds.destory();
        }

        if (mTaLeftTitleRightImgAds != null) {
            mTaLeftTitleRightImgAds.destory();
        }

        super.onDestroy();
    }

}
