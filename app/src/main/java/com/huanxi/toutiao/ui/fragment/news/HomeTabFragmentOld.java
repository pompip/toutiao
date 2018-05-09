package com.huanxi.toutiao.ui.fragment.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huanxi.toutiao.R;
import com.huanxi.toutiao.net.api.news.ApiHomeNews;
import com.huanxi.toutiao.net.api.news.ApiNewsAndVideoList;
import com.huanxi.toutiao.net.bean.news.HomeTabBean;
import com.huanxi.toutiao.net.bean.news.ResNewsAndVideoBean;
import com.huanxi.toutiao.presenter.news.NewsInfoFlowPresenter;
import com.huanxi.toutiao.ui.activity.other.MainActivity;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.NewsAdapter;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.gdt.GDTImgAds;
import com.huanxi.toutiao.ui.adapter.recyclerview.muiltyAdapter.bean.ads.ta.TaLeftTitleRightImgAds;
import com.huanxi.toutiao.ui.fragment.base.BaseLoadingRecyclerViewFragment;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dinosa on 2018/1/15.
 * 这里是homeTab的Fragment
 */

public class HomeTabFragmentOld extends BaseLoadingRecyclerViewFragment {


    public static final String TAB_BEAN = "tabBean";

    private HomeTabBean mHomeTabBean;
    protected NewsAdapter mAdapter;

    //这里是下拉刷新多少条的一个逻辑

    public LinkedList<NativeExpressADView> mGdtAdLists=new LinkedList<>();
    private GDTImgAds mGdtImgAds;
    private TaLeftTitleRightImgAds mTaLeftTitleRightImgAds;
    private NewsInfoFlowPresenter mNewsInfoFlowPresenter;

    int mPage = 1;

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

        mTaLeftTitleRightImgAds = new TaLeftTitleRightImgAds();

        mNewsInfoFlowPresenter = new NewsInfoFlowPresenter(mGdtImgAds,mTaLeftTitleRightImgAds);

    }

    @Override
    public int getLoadingContentLayoutId() {
        return R.layout.layout_refresh_recycler_view;
    }


    public void getData(final boolean isShowContent) {

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put("type", mHomeTabBean.getCode());
        paramsMap.put("qid", "qid02561");
        paramsMap.put(ApiNewsAndVideoList.PAGE_NUM, "1");

        ApiNewsAndVideoList apiHomeNews = new ApiNewsAndVideoList(new HttpOnNextListener<ResNewsAndVideoBean>() {

            @Override
            public void onNext(ResNewsAndVideoBean resNewsAndVideoBean) {

                if (resNewsAndVideoBean.getList() != null && resNewsAndVideoBean.getList().size() > 0) {
                    mPage = 2;
                    mAdapter.replaceData(mNewsInfoFlowPresenter.filterData(resNewsAndVideoBean.getList()));
                    if (isShowContent) {
                        showSuccess();
                    } else {
                        refreshComplete();
                        if (resNewsAndVideoBean.getList().size() > 0) {

                            ((MainActivity) getBaseActivity()).getNewsFragment().showRefreshBanner(resNewsAndVideoBean.getList().size());

                        }
                    }
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (isShowContent) {
                    showFaild();
                } else {
                    refreshComplete();
                }
            }
        }, ((RxAppCompatActivity) getActivity()), paramsMap);

        HttpManager.getInstance().doHttpDeal(apiHomeNews);
    }

    /**
     * 获取HomeTabFragment
     *
     * @param bean 传入的对象；
     * @return 一个HomeTabFragment;
     */
    public static HomeTabFragmentOld getHomeTabFragment(HomeTabBean bean) {
        HomeTabFragmentOld homeTabFragment = new HomeTabFragmentOld();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAB_BEAN, bean);
        homeTabFragment.setArguments(bundle);
        return homeTabFragment;
    }


    public void loadMore() {

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put("type", mHomeTabBean.getCode());
        paramsMap.put("qid", "qid02561");
        paramsMap.put("num", "20");
        paramsMap.put(ApiHomeNews.PAGE_NUM, mPage + "");

        ApiNewsAndVideoList apiHomeNews = new ApiNewsAndVideoList(new HttpOnNextListener<ResNewsAndVideoBean>() {

            @Override
            public void onNext(ResNewsAndVideoBean resNewsAndVideoBean) {

                //这里我们要对数据进行一个过滤的操作；
                if (resNewsAndVideoBean.getList() != null && resNewsAndVideoBean.getList().size() > 0) {
                    //这里表示服务器返回有数据；
                    List<MultiItemEntity> multiItemEntities = mNewsInfoFlowPresenter.filterData(resNewsAndVideoBean.getList());

                    if (multiItemEntities != null && multiItemEntities.size() > 0) {
                        mPage++;
                        mAdapter.addData(multiItemEntities);
                    }
                    loadMoreComplete();

                }
                loadMoreComplete();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                loadMoreComplete();
            }
        }, ((RxAppCompatActivity) getActivity()), paramsMap);
        HttpManager.getInstance().doHttpDeal(apiHomeNews);

    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewsAdapter(null);
        }
        return mAdapter;
    }

    @Override
    public void requestAdapterData(boolean isFirst) {
        getData(isFirst);
    }

    @Override
    public void requestNextAdapterData() {
        loadMore();
    }

    @Override
    public void onDestroy() {
        //销毁所有的广告的逻辑
       mNewsInfoFlowPresenter.destory();
        super.onDestroy();
    }


    public String getTabName(){
        return mHomeTabBean.getTitle();
    }

}
