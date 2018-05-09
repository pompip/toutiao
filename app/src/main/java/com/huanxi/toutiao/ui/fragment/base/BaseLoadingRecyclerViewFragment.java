package com.huanxi.toutiao.ui.fragment.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huanxi.toutiao.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;

/**
 * Created by Dinosa on 2018/1/19.
 * <p>
 * 这里是一个显示界面就是一个默认的RecyclerView的界面，
 * 我们对其进行一个抽取出来；
 */

public abstract class BaseLoadingRecyclerViewFragment extends BaseLoadingFrament {


    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;



    @Override
    public int getLoadingContentLayoutId() {
        return R.layout.layout_refresh_recycler_view;
    }

    @Override
    protected void initView() {
        super.initView();
        mRvHome.setLayoutManager(getLayoutManager());
        ((BaseQuickAdapter) getAdapter()).bindToRecyclerView(mRvHome);


        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                BaseLoadingRecyclerViewFragment.this.onRefreshing();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                BaseLoadingRecyclerViewFragment.this.onLoadMore();
            }
        });

        mRefreshLayout.setDisableContentWhenLoading(true);
        mRefreshLayout.setDisableContentWhenRefresh(true);
    }


    @Override
    protected void initData() {
        super.initData();
        requestAdapterData(true);
    }

    public void autoRefresh(){
        mRvHome.scrollToPosition(0);
        mRefreshLayout.autoRefresh(100);
    }


    /*
     *提供一个RecyclerView需要的一个适配器；
     */
    public abstract RecyclerView.Adapter getAdapter();

    /**
     * 请求Adapter中的数据；
     */
    public abstract void requestAdapterData(boolean isFirst);

    /**
     * 请求Adapter请求下一页的数据的
     */
    public abstract void requestNextAdapterData();

    @Override
    protected void onRetry() {
        super.onRetry();
        requestAdapterData(true);
    }

    /**
     * 上拉加载
     */
    public void onLoadMore() {
        requestNextAdapterData();
    }

    /**
     * 下拉刷新的操作；
     */
    public void onRefreshing() {
        requestAdapterData(false);
    }

    public void loadMoreComplete() {
        //mRlRefreshLayout.loadMoreComplete();
        mRefreshLayout.finishLoadMore(200);
    }


    public void refreshComplete() {
        //mRlRefreshLayout.refreshComplete();
        mRefreshLayout.finishRefresh(200);
    }

    /**
     * 设置RecyclerView的一个布局管理器，默认的是一个线性的布局；
     *
     * @return
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    public RecyclerView getRvHome() {
        return mRvHome;
    }


    @Override
    public void onPause() {
        super.onPause();
    /*    mRlRefreshLayout.refreshComplete();
        mRlRefreshLayout.loadMoreComplete();*/
        mRefreshLayout.finishLoadMore();
        mRefreshLayout.finishRefresh();

    }

    /**
     * 设置是否有下拉刷新
     * @param isEnable
     */
    public void setRefreshEnable(boolean isEnable){

        mRefreshLayout.setEnableRefresh(isEnable);

    }

    /**
     * 是否有上拉加载
     * @param isEnable
     */
    public void setLoadingEnable(boolean isEnable){

        mRefreshLayout.setEnableLoadMore(isEnable);
    }


}
