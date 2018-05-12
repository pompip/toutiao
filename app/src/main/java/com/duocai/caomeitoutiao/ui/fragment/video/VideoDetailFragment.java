package com.duocai.caomeitoutiao.ui.fragment.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajguan.library.EasyRefreshLayout;
import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.globle.ConstantAd;
import com.duocai.caomeitoutiao.net.api.news.ApiNewsCommentList;
import com.duocai.caomeitoutiao.net.api.user.ApiInviteFriendDesc;
import com.duocai.caomeitoutiao.net.api.user.ApiUserDoLike;
import com.duocai.caomeitoutiao.net.bean.ResEmpty;
import com.duocai.caomeitoutiao.net.bean.ResInviteFriendDesc;
import com.duocai.caomeitoutiao.net.bean.ResSplashAds;
import com.duocai.caomeitoutiao.net.bean.comment.ResNewsCommentList;
import com.duocai.caomeitoutiao.presenter.LoginPresenter;
import com.duocai.caomeitoutiao.ui.activity.WebHelperActivity;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.activity.video.VideoItemDetailActivity;
import com.duocai.caomeitoutiao.ui.adapter.AdsAdapter;
import com.duocai.caomeitoutiao.ui.adapter.VideoDetailAdapter;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.video.VideoListBean;
import com.duocai.caomeitoutiao.ui.fragment.base.BaseLoadingFrament;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by Dinosa on 2018/3/6.
 */

public class VideoDetailFragment extends BaseLoadingFrament {


    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

 /*   @BindView(R.id.fl_banner_container)
    FrameLayout mFlBannerContainer;*/

    @BindView(R.id.ns_float_ad)
    NestedScrollView mNsFloatView;

    @BindView(R.id.rv_float_ad)
    RecyclerView mRvFloatAd;

    @BindView(R.id.rl_refreshLayout)
    EasyRefreshLayout mRlRefreshLayout;



    //这里是底部的popupWindow;
    @BindView(R.id.ll_popwindow_container)
    View mLlPopupWindowContainer;

    @BindView(R.id.tv_pop_title)
    TextView mTvPopTitle;

    @BindView(R.id.tv_pop_content)
    TextView mTvPopContent;

    @BindView(R.id.iv_close)
    View mIvPopClose;



    @BindView(R.id.rl_pop_window)
    View mRlPopWidow;



    TextView mTvTitle;

    BannerView mRecyclerViewBv;
    BannerView mAdFloatView;

    LinearLayout mLlAdBannerContainer;
    private View mLlTitleContainer;
    private View mNsView;


    @Override
    public void onRetry() {
        requestAdapterData(true);
    }

    VideoDetailAdapter mVideoDetailAdapter;

    //这里是获取得到content的loadingLayout;
    @Override
    public int getLoadingContentLayoutId() {
        return R.layout.fragment_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();

        mRvHome.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        mRvHome.setAdapter(getAdapter());

        mRlRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
                requestNextAdapterData();
            }

            @Override
            public void onRefreshing() {
                requestAdapterData(false);
            }
        });
        mRvHome.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalDy=0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //这里我们要记录的
                if(isHasAds()){
                    totalDy+=dy;
                    if (mLlTitleContainer != null) {
                        //如果大于
                        if(totalDy >= mLlTitleContainer.getHeight()){
                            //这里我们就让我们的广告显示出来；
                            //mFlBannerContainer.setVisibility(View.VISIBLE);
                            mNsFloatView.setVisibility(View.VISIBLE);
                        }else{
                            mNsFloatView.setVisibility(View.INVISIBLE);
                            //一隐藏；
                            //mFlBannerContainer.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
    }

    public RecyclerView.Adapter getAdapter() {

        if (mVideoDetailAdapter == null) {
            mVideoDetailAdapter = new VideoDetailAdapter(getActivity(), null);
            View headerView = View.inflate(getBaseActivity(), R.layout.header_item_video_detail, null);
            initHeaderView(headerView);
            mVideoDetailAdapter.addHeaderView(headerView);

            mVideoDetailAdapter.setHeaderAndEmpty(true);
            //添加头view,footeView，和emptyView;

            View emptyView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_news_comment_empty, getRvHome(), false);
            mVideoDetailAdapter.setEmptyView(emptyView);

            //这里我们是需要修改footer里面的页面的；
            View footerView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.item_news_comment_footer, getRvHome(), false);

            mVideoDetailAdapter.setFooterView(footerView);

            mVideoDetailAdapter.setHeaderAndEmpty(true);
        }
        return mVideoDetailAdapter;
    }

    /**
     * 这里我们要初始化headerView；
     *
     * @param headerView
     */
    private void initHeaderView(View headerView) {

        mLlTitleContainer = headerView.findViewById(R.id.ll_title_container);
        mTvTitle = (TextView) headerView.findViewById(R.id.tv_title);

        //这里是广告的逻辑
        mNsView = headerView.findViewById(R.id.ns_view);


        //mLlAdBannerContainer = (LinearLayout) headerView.findViewById(R.id.bannerContainer);

        //是否有广告显示；
        if(isHasAds()){
            mNsView.setVisibility(View.VISIBLE);
        }else{
            mNsView.setVisibility(View.GONE);
        }


    }

    LoginPresenter mLoginPresenter;

    @Override
    protected void initData() {


        mLoginPresenter = new LoginPresenter(getBaseActivity(), new LoginPresenter.OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                dismissDialog();
                getNavigationData();
            }

            @Override
            public void onLoginStart() {
                showDialog();
            }

            @Override
            public void onLoginFaild() {
                dismissDialog();
            }
        });

        getEasyRefreshLayout().setEnablePullToRefresh(false);

        mTvTitle.setText(mVideoDetail.getTitle());

        //初始化广告
       // initAds();
        initAdsNew();
        //这里我们要做的一个操作就是显示banner
        requestAdapterData(true);

        getNavigationData();
    }

    /**
     * 获取Navigation里面的内容；
     */
    public void getNavigationData() {


        ApiInviteFriendDesc apiInviteFriendDesc = new ApiInviteFriendDesc(new HttpOnNextListener<ResInviteFriendDesc>() {

            @Override
            public void onNext(final ResInviteFriendDesc resInviteFriendDesc) {

                initRightBottomToast(resInviteFriendDesc);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, getBaseActivity(), new HashMap<String, String>());

        HttpManager.getInstance().doHttpDeal(apiInviteFriendDesc);
    }



    /**
     * 这里要做的一个操作就是显示登陆领取红包的一个操作的；
     *
     * @param resInviteFriendDesc
     */
    public void initRightBottomToast(final ResInviteFriendDesc resInviteFriendDesc) {

        String showTextForLoginDetail = resInviteFriendDesc.getShowTextForLoginDetail();
        if ("1".equals(showTextForLoginDetail)) {

            mLlPopupWindowContainer.setVisibility(View.VISIBLE);

            mIvPopClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLlPopupWindowContainer.setVisibility(View.GONE);
                }
            });

            //这里要做的一个逻辑就是显示
            if(isLogin()){

                mTvPopTitle.setText(resInviteFriendDesc.getTextforloginDetail().getTitle());
                mTvPopContent.setText(resInviteFriendDesc.getTextforloginDetail().getCont());

                mRlPopWidow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!TextUtils.isEmpty(resInviteFriendDesc.getTextforloginDetail().getUrl())) {

                            Intent intent = WebHelperActivity.getIntent(
                                    getBaseActivity(),
                                    resInviteFriendDesc.getTextforloginDetail().getUrl(),
                                    resInviteFriendDesc.getTextforloginDetail().getTitle(),
                                    false);
                            startActivity(intent);
                        }

                    }
                });


            }else{
                //登陆的逻辑
                String title="<font color='#be6c14'>微信登陆领取<font color='#f13021' ><b>2500</b></font>金币</font>";
                String content="<font color='#be6c14'>海量<font color='#f13021'>任务</font>、限时<font color='#f13021'>红包</font>、边看<font color='#f13021'>边赚</font>!</font>";
                //没有登陆的逻辑
                mTvPopTitle.setText(Html.fromHtml(title));
                mTvPopContent.setText(Html.fromHtml(content));

                mRlPopWidow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLoginPresenter.login();
                    }
                });

            }
        }else{
            mLlPopupWindowContainer.setVisibility(View.GONE);
        }
    }


    private void initAdsNew() {

        //这里是header广告；
        RecyclerView adRecyclerView = (RecyclerView) mNsView.findViewById(R.id.rv_ads);

        adRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        ResSplashAds resAds = getBaseActivity().getMyApplication().getResAds();
        AdsAdapter adsAdapter = new AdsAdapter(resAds.getVideodetail());
        adRecyclerView.setAdapter(adsAdapter);

        //悬浮的广告；
        AdsAdapter floatAdapter = new AdsAdapter(resAds.getVideodetail());
        mRvFloatAd.setLayoutManager(new LinearLayoutManager(getBaseActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvFloatAd.setAdapter(floatAdapter);
    }


    @Deprecated
    private void initAds() {
        //这里是RecyclerView里面的Banner;m
        mRecyclerViewBv = new BannerView(getBaseActivity(), ADSize.BANNER, ConstantAd.GdtAD.APPID, ConstantAd.GdtAD.BANNER_AD);
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        mRecyclerViewBv.setRefresh(0);
        mRecyclerViewBv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.i(
                        "AD_DEMO",
                        String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                                error.getErrorMsg()));
                mRecyclerViewBv.loadAD();
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        mLlAdBannerContainer.addView(mRecyclerViewBv);
        //这里是需要初始化；隐藏的悬浮的广告；
        mRecyclerViewBv.loadAD();

        //这里要初始化悬浮的广告
       mAdFloatView = new BannerView(getBaseActivity(), ADSize.BANNER, ConstantAd.GdtAD.APPID, ConstantAd.GdtAD.BANNER_AD);
        mAdFloatView.setRefresh(0);
        mAdFloatView.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.i(
                        "AD_DEMO",
                        String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                                error.getErrorMsg()));
                mAdFloatView.loadAD();
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
       // mFlBannerContainer.addView(mAdFloatView);
        //这里是需要初始化；隐藏的悬浮的广告；
        mAdFloatView.loadAD();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRecyclerViewBv != null) {
            mRecyclerViewBv.destroy();
        }

        if (mAdFloatView != null) {
            mAdFloatView.destroy();
        }

    }

    //这里是之前的获取的评论的接口的；
    public void requestAdapterData(boolean isFirst) {
        mActivity = ((VideoItemDetailActivity) getActivity());
        //这里我们要做的一个操作
        //获取评论
        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiNewsCommentList.ISSUE_ID, mVideoDetail.getUrlmd5());
        paramsMap.put(ApiNewsCommentList.PAGE_NUMBER, 1 + "");

        ApiNewsCommentList apiNewsCommentList = new ApiNewsCommentList(new HttpOnNextListener<ResNewsCommentList>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getEasyRefreshLayout().loadMoreComplete();
            }

            @Override
            public void onNext(ResNewsCommentList resNewsCommentList) {

                if (resNewsCommentList.getList() != null && resNewsCommentList.getList().size() > 0) {
                    //这里我们要做的一个逻辑就是
                    mVideoDetailAdapter.getFooterLayout().setVisibility(View.VISIBLE);
                    TextView tvText = (TextView) mVideoDetailAdapter.getFooterLayout().findViewById(R.id.tv_is_can_pull);

                    if (resNewsCommentList.getList().size() < 20) {
                        //这里表示没有分页数据了
                        //这里要设置显示的文字；
                        tvText.setText("我是有底线的");
                    } else {
                        tvText.setText("上拉有彩蛋");
                    }
                    mVideoDetailAdapter.replaceData(resNewsCommentList.getList());
                    mPage = 2;
                } else {
                    //这里服务器没有数据；
                    mVideoDetailAdapter.getFooterLayout().setVisibility(View.GONE);
                }

                updateCommentTip();

                showSuccess();

            }
        }, mActivity, paramsMap);
        HttpManager.getInstance().doHttpDeal(apiNewsCommentList);
    }

    private void updateCommentTip() {
        if (getCommentCount() > 0) {
            mActivity.getTipView().setVisibility(View.VISIBLE);
            mActivity.getTipView().setText(getCommentCount() + "");
        } else {
            mActivity.getTipView().setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取评论的数量；
     *
     * @return
     */
    public int getCommentCount() {

        return mVideoDetailAdapter.getData().size();
    }


    int mPage = 1;

    /**
     * 获取更多的评论
     */
    public void getMoreComment() {
        //获取评论
        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiNewsCommentList.ISSUE_ID, mVideoDetail.getUrlmd5());
        paramsMap.put(ApiNewsCommentList.PAGE_NUMBER, mPage + "");

        ApiNewsCommentList apiNewsCommentList = new ApiNewsCommentList(new HttpOnNextListener<ResNewsCommentList>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getEasyRefreshLayout().loadMoreComplete();
            }

            @Override
            public void onNext(ResNewsCommentList resNewsCommentList) {

                if (resNewsCommentList.getList() != null && resNewsCommentList.getList().size() > 0) {
                    //mNewsDetailComments.setList(resNewsCommentList.getList());
                    //这里我们要做的一个逻辑就是

                    if (resNewsCommentList.getList().size() < 20) {
                        //这里表示没有分页数据了
                        //这里要设置显示的文字；
                        showFooterText("我是有底线的");
                    } else {
                        showFooterText("上拉有彩蛋");
                    }
                    mVideoDetailAdapter.addData(resNewsCommentList.getList());
                    mPage++;
                }

                updateCommentTip();
                getEasyRefreshLayout().loadMoreComplete();
            }
        }, mActivity, paramsMap);
        HttpManager.getInstance().doHttpDeal(apiNewsCommentList);
    }

    public void showFooterText(String text) {

        mVideoDetailAdapter.getFooterLayout().setVisibility(View.VISIBLE);
        TextView tvText = (TextView) mVideoDetailAdapter.getFooterLayout().findViewById(R.id.tv_is_can_pull);
        tvText.setText(text);
    }


    public void requestNextAdapterData() {
        getMoreComment();
    }


    //这里我们实现
    public static String VIDEDO_DETAIL = "videoDetail";
    private VideoListBean mVideoDetail;
    private VideoItemDetailActivity mActivity;

    //这里是视频的详情；
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mVideoDetail = (VideoListBean) arguments.getSerializable(VIDEDO_DETAIL);
    }

    public static VideoDetailFragment getFragment(VideoListBean detail) {
        VideoDetailFragment videoItemDetailFragment = new VideoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEDO_DETAIL, detail);
        videoItemDetailFragment.setArguments(bundle);
        return videoItemDetailFragment;
    }



    public EasyRefreshLayout getEasyRefreshLayout(){
        return mRlRefreshLayout;
    }

    public RecyclerView getRvHome() {
        return mRvHome;
    }


    /**
     * 这里我们执行点赞的逻辑
     */
    public void doOnClickLike(final String commId){

        checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {

                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(ApiUserDoLike.FROM_UID,getUserBean().getUserid());
                paramsMap.put(ApiUserDoLike.COMMENT_ID,commId);

                //这里执行的对应的逻辑；
                ApiUserDoLike apiUserDoLike=new ApiUserDoLike(new HttpOnNextListener<ResEmpty>() {

                    @Override
                    public void onNext(ResEmpty resEmpty) {
                        //获取
                        requestAdapterData(false);
                    }

                },getBaseActivity(),paramsMap);

                HttpManager.getInstance().doHttpDeal(apiUserDoLike);
            }

            @Override
            public void loginFaild() {

            }
        });

    }

}
