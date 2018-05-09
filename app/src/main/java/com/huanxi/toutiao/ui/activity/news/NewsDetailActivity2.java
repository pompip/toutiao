package com.huanxi.toutiao.ui.activity.news;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.model.bean.NewsCommentBean;
import com.huanxi.toutiao.model.bean.NewsItemBean;
import com.huanxi.toutiao.net.api.share.ApiShareNewsAndVideoContent;
import com.huanxi.toutiao.net.bean.ResInviteFriendDesc;
import com.huanxi.toutiao.net.bean.ResReadAwarad;
import com.huanxi.toutiao.net.bean.news.ResAward;
import com.huanxi.toutiao.net.bean.news.ResNewsDetailBean;
import com.huanxi.toutiao.presenter.LoginPresenter;
import com.huanxi.toutiao.presenter.NewsDetailPresenter2;
import com.huanxi.toutiao.presenter.ads.gdt.GdtNativeAds;
import com.huanxi.toutiao.ui.activity.WebHelperActivity;
import com.huanxi.toutiao.ui.activity.base.BaseActivity;
import com.huanxi.toutiao.ui.adapter.HomeTabFragmentAdapter;
import com.huanxi.toutiao.ui.adapter.NewsDetailAdapter;
import com.huanxi.toutiao.ui.dialog.CommentDialog;
import com.huanxi.toutiao.ui.dialog.ShareDialog;
import com.huanxi.toutiao.ui.dialog.invite.ShareContentAndVideoDialog;
import com.huanxi.toutiao.ui.view.ReadArticleAwaryView;
import com.huanxi.toutiao.ui.view.loading.MultiStateView;
import com.huanxi.toutiao.ui.view.loading.SimpleMultiStateView;
import com.huanxi.toutiao.utils.FormatUtils;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NewsDetailActivity2 extends BaseActivity {

    private static final String ARTICLE_ID = "articleId";
    private static final String MD5URL = "md5url";

    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRlRefreshLayout;

    @BindView(R.id.SimpleMultiStateView)
    SimpleMultiStateView mSimpleMultiStateView;


    @BindView(R.id.tv_tip)
    TextView mTvTip;


    @BindView(R.id.award_view)
    public ReadArticleAwaryView mReadArticleAwaryView;


    @BindView(R.id.rl_container)
    RelativeLayout mRlContainer;


    @BindView(R.id.pb_read_progress)
    ProgressBar mPbReadProgressBar;

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


    TextView mTvTime;
    TextView mTvSource;

    private NewsDetailAdapter mNewsDetailAdapter;
    private WebView mWebView;

    private TextView mTvTitle;
    private View mHeaderView;
    private CountDownTimer mCountDownTimer;
    private NewsDetailPresenter2 mPresenter;
    private HomeTabFragmentAdapter mRvCommentAdapter;
    private boolean mIsFirst;
    private LoginPresenter mLoginPresenter;

    public NewsDetailPresenter2 getPresenter() {
        return mPresenter;
    }


    @Override
    public int getContentLayout() {
        return R.layout.activity_news_detail_recycle_view;
    }


    @Override
    protected void initData() {
        super.initData();

        mLoginPresenter = new LoginPresenter(this, new LoginPresenter.OnLoginListener() {
            @Override
            public void onLoginSuccess() {

                mPresenter.getNavigationData();

                dismissDialog();
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

    }

    //滑动的进度
    public Float mScrollProgress = 0f;
    //停留时间的进度;
    public Float mStayProgress = 0f;

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        mPresenter = new NewsDetailPresenter2(this);
        initStateView();

        initRecyclerView();

        initWebSetting();


        mPbReadProgressBar.setVisibility(View.INVISIBLE);

        mRlRefreshLayout.setEnableRefresh(false);


        mRlRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.getCommentList();
            }
        });


        mIsFirst = true;
        mRvHome.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int totalY = 0;
            int maxTotal = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mPbReadProgressBar.getProgress() >= 100) {
                    return;
                }
                totalY += dy;
                if (totalY > maxTotal) {
                    //这里是一个滚动的进度
                    maxTotal = totalY;
                    //这里我们对其进行一个监听操作；
                    int height = mHeaderView.getHeight() - mRlContainer.getHeight();
                    Double progress = maxTotal * 1.0 / Math.max(height, maxTotal);

                    //这里是用来记录滚动的记录的
                    mScrollProgress = (float) Float.parseFloat((FormatUtils.decimalFormat(progress.floatValue() * 10)));

                    Float totalProgress = mScrollProgress + mStayProgress;
                    mPbReadProgressBar.setProgress(totalProgress.intValue());

                    if (totalProgress.intValue() >= 100) {
                        //这里是需要调用相应的方法的；
                        if (mIsFirst) {
                            mIsFirst = false;
                            mPresenter.getReadCoin();
                        }
                    }
                    System.out.println("height: " + height + "totalY: " + maxTotal + " dout_prog:" + mPbReadProgressBar.getProgress());
                } else {
                    int height = mHeaderView.getHeight() - mRlContainer.getHeight();
                    if (totalY > height) {
                        int progress = 10 + mStayProgress.intValue();
                        mPbReadProgressBar.setProgress(progress);
                        if (progress >= 100) {
                            //这里是需要调用相应的方法的；
                            if (mIsFirst) {
                                mIsFirst = false;
                                mPresenter.getReadCoin();
                            }
                        }
                    }

                }
            }
        });

    }

    /**
     * 这里是初始化RecyclerView;
     */
    private void initRecyclerView() {

        mRvHome.setLayoutManager(new LinearLayoutManager(this));

        mRvHome.setItemAnimator(null);


        mNewsDetailAdapter = new NewsDetailAdapter(this, null);

        //添加头部布局
        mHeaderView = View.inflate(this, R.layout.header_news_detail, null);

        getHeaderChildView(mHeaderView);

        //添加头view,footeView，和emptyView;
        mNewsDetailAdapter.addHeaderView(mHeaderView);


        View emptyView = LayoutInflater.from(NewsDetailActivity2.this).inflate(R.layout.item_news_comment_empty, mRvHome, false);
        mNewsDetailAdapter.setEmptyView(emptyView);

        //这里我们是需要修改footer里面的页面的；
        View footerView = LayoutInflater.from(this).inflate(R.layout.item_news_comment_footer, mRvHome, false);

        mNewsDetailAdapter.setFooterView(footerView);

        mNewsDetailAdapter.setHeaderAndEmpty(true);

        mRvHome.setAdapter(mNewsDetailAdapter);

    }

    /**
     * 这里我们将初始化webView的设置信息
     */
    private void initWebSetting() {
        addjs(mWebView);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl("file:///android_asset/newsDetail/post_detail.html");
        mWebView.setWebChromeClient(new WebChromeClient() {
            boolean isFirst = true;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                System.out.println("newProgress: " + newProgress);
                if (newProgress >= 100 && isFirst) {
                    isFirst = false;
                    //这里要做的一个操作就是请求接口开始计时
                    mPresenter.getNewsDetail();
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }


    private void initStateView() {
        if (mSimpleMultiStateView == null) return;
        mSimpleMultiStateView.setEmptyResource(R.layout.view_empty)
                .setRetryResource(R.layout.new_view_retry)
                .setLoadingResource(R.layout.new_view_loading)
                .setNoNetResource(R.layout.view_nonet)
                .build()
                .setonReLoadlistener(new MultiStateView.onReLoadlistener() {
                    @Override
                    public void onReload() {
                        onRetry();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //这里要做的一个逻辑就是做对应的操作；
    }

    /**
     * 这里要重试的操作；
     */
    private void onRetry() {
        mPresenter.getNewsDetail();
    }


    public void getHeaderChildView(View headerView) {

        //这里是内容；
        mWebView = ((WebView) headerView.findViewById(R.id.webview));

        //这里是title;
        mTvTime = ((TextView) headerView.findViewById(R.id.tv_time));
        mTvSource = ((TextView) headerView.findViewById(R.id.tv_source));
        mTvTitle = ((TextView) headerView.findViewById(R.id.tv_title));
        //这里是相关推荐；
        RecyclerView rvRecomments = (RecyclerView) headerView.findViewById(R.id.rv_recommments);

        rvRecomments.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvCommentAdapter = new HomeTabFragmentAdapter(this, null);
        rvRecomments.setAdapter(mRvCommentAdapter);
        //接下来是评论的的部分；
    }


    public static Intent getIntent(Context context, String articleId, String md5rl) {

        Intent intent = new Intent(context, NewsDetailActivity2.class);
        intent.putExtra(ARTICLE_ID, articleId);
        intent.putExtra(MD5URL, md5rl);
        return intent;
    }

    public String mUrlConten = "";

    public void updateUI(ResNewsDetailBean resNewsDetailBean) {

        //这里是更新头部
        mTvTime.setText(resNewsDetailBean.getTime());
        mTvSource.setText("来源: " + resNewsDetailBean.getSource());
        mTvTitle.setText(resNewsDetailBean.getTitle());
        //这里更新webView;
        mUrlConten = resNewsDetailBean.getContent();


        mWebView.loadUrl(getUrlContent());

        //更新评论中是收藏的状态；
        mCbCollection.setChecked(resNewsDetailBean.iscollect());

        //这里获取相关推荐
        mPresenter.getRecomment();

        if (isLogin()) {
            //开始计时
            mPresenter.getBeganStart();
            // mPbReadProgressBar.setVisibility(View.VISIBLE);
        } else {
            //这里就需要让其隐藏；
            hideReadProgress();
        }
        showSuccess();
    }

    public void hideReadProgress() {
        mPbReadProgressBar.setVisibility(View.INVISIBLE);
    }


    public String getUrlContent() {
        String content = mUrlConten;
        content = content.replace("figure", "div");
        content = content.replace("h1", "h2");

        String url = "javascript:show_content(\'" + content + "\')";
        return url;
    }


    @OnClick({R.id.iv_back})
    public void onClickBack() {
        finish();
    }

    private void addjs(final WebView webview) {

        class JsObject {
            @JavascriptInterface
            public void jsFunctionimg(final String i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //查看回掉日志；
                        Log.i(NewsDetailActivity2.class.getSimpleName(), "run: " + i);
                    }
                });
            }


            @JavascriptInterface
            public void resize(final float height) {
                NewsDetailActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
                    }
                });
            }
        }
        webview.addJavascriptInterface(new JsObject(), "jscontrolimg");
    }

    @OnClick(R.id.et_conmment)
    public void onClickInputComment() {


        checkLogin(new CheckLoginCallBack() {
            @Override
            public void loginSuccess() {

                CommentDialog commentDialog = new CommentDialog();
                commentDialog.show(NewsDetailActivity2.this, new CommentDialog.OnDialogClickListener() {
                    @Override
                    public void onClickSure(String content) {
                        mPresenter.requestSendComment(content);
                    }
                });
            }

            @Override
            public void loginFaild() {
            }
        });

    }


    @OnClick(R.id.rl_comment)
    public void onClickSend() {
        //这里要滚动到评论的地方中去；
        mRvHome.scrollBy(0, mWebView.getHeight());
        //这里设置进度跳的位置；
    }

    @BindView(R.id.cb_collection)
    CheckBox mCbCollection;

    @OnClick(R.id.fl_collection)
    public void onClickCollection() {
        checkLogin(new CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //这里我们要进行一个收藏操作；
                mCbCollection.toggle();
                if (mCbCollection.isChecked()) {
                    toast("收藏成功");
                } else {
                    toast("取消收藏");
                }
                mPresenter.requestCollection();
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    /**
     * 收藏成功的一个操作；
     */
    public void onCollectionSuccess() {

    }

    private ShareContentAndVideoDialog mShareContentAndVideoDialog;

    /**
     * 这里是点击分享的操作；
     */
    @OnClick(R.id.iv_share)
    public void onClickShare() {
        checkLogin(new CheckLoginCallBack() {


            @Override
            public void loginSuccess() {
                //这里是需要重新做分享的操作；
                //mPresenter.requestShareContent();
                if (mShareContentAndVideoDialog == null) {
                    mShareContentAndVideoDialog = new ShareContentAndVideoDialog(NewsDetailActivity2.this);
                }
                mShareContentAndVideoDialog.show(mPresenter.getUrlMd5(), ApiShareNewsAndVideoContent.NEWS_TYPE);
            }

            @Override
            public void loginFaild() {

            }
        });

    }

    public void showShareDialog(String title, String content, String url) {
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(title, content, url, new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
    }

    /**
     * 分享成功
     *
     * @param resSign
     */
    public void shareSuccess(ResAward resSign) {

        showOpenBoxLogo();
        //RedPacketDialog redPacketDialog = new RedPacketDialog();
        //redPacketDialog.show(NewsDetailRecycleViewActivity.this, resSign.getIntegral());
        //show("10","1/20");
    }

    public void showOpenBoxLogo() {
        mPbReadProgressBar.setProgress(100);
        // mPbReadProgressBar.setVisibility(View.VISIBLE);
    }


    /**
     * 这里获取阅读奖励；
     */
    public void readAward(ResReadAwarad resReadAwarad) {
        showOpenBoxLogo();
        //这里是阅读奖励；
        int totalAward = resReadAwarad.getIntegral() + resReadAwarad.getExtra();
        show(totalAward + "", resReadAwarad.getLastcount(), resReadAwarad.getLastcount_new());
    }


    private void startCountDown() {
        final int totalTime = 13 * 1000;
        int timeUnit = 1000;
        mCountDownTimer = new CountDownTimer(totalTime, timeUnit) {
            @Override
            public void onTick(long millisUntilFinished) {
                mStayProgress += 7.5f;

                Float totalProgress = mStayProgress + mScrollProgress;

                mPbReadProgressBar.setProgress(totalProgress.intValue());
                if (totalProgress.intValue() >= 100) {
                    //这里是需要调用相应的方法的；
                    if (mIsFirst) {
                        mIsFirst = false;
                        mPresenter.getReadCoin();
                    }
                }

                System.out.println("staryProgress: " + mStayProgress);
            }

            @Override
            public void onFinish() {
                System.out.println("staryProgress:over!!!");
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (mDelaySubscribetion != null) {
            if (mDelaySubscribetion.isUnsubscribed()) {
                mDelaySubscribetion.unsubscribe();
            }
        }
        //这里我们要清除所有的广告；
        for (int i = 0; i < mRvHome.getChildCount(); i++) {
            //这里我们要释放所有的广告的View；
            View view = mRvHome.getChildAt(i);
            if (view instanceof NativeExpressADView) {
                GdtNativeAds.newInstance().removeADView(((NativeExpressADView) view));
            }
        }

        super.onDestroy();
    }


    /**
     * 这里我们要做的一个操作就是阅读开始操作；
     */
    public void requestStartCountDown() {
        startCountDown();
        //  mPbReadProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 显示成功的页面
     */
    public void showSuccess() {

        if (mSimpleMultiStateView == null) {
            mSimpleMultiStateView = ((SimpleMultiStateView) findViewById(R.id.SimpleMultiStateView));
        }

        if (mSimpleMultiStateView == null) {
            return;
        }

        mSimpleMultiStateView.showContent();
    }

    /**
     * 显示错误的页面；
     */
    public void showError() {

        if (mSimpleMultiStateView == null) {
            mSimpleMultiStateView = ((SimpleMultiStateView) findViewById(R.id.SimpleMultiStateView));
        }

        if (mSimpleMultiStateView == null) {
            return;
        }
        mSimpleMultiStateView.showErrorView();
    }

    /**
     * 取消加载更多的界面；
     */
    public void loadMoreCompelte() {
        mRlRefreshLayout.finishLoadMore();
    }

    /**
     * 更新评论列表；
     */
    public void updateCommentList(List<NewsCommentBean> list, boolean isFirst) {
        //这里表示刷新和加载的操作；
        if (list != null) {
            //mNewsDetailAdapter.notifyComments(list,list.size() == 0,isFirst);

            if (isFirst) {
                // mNewsDetailAdapter.replaceData(list);
                mNewsDetailAdapter.refreshComment(list);

                // mWebView.loadUrl(getUrlContent());

                if (list.size() <= 0) {
                    mNewsDetailAdapter.getFooterLayout().setVisibility(View.GONE);
                } else {
                    //这里我们是需要天机footer的；
                    mNewsDetailAdapter.getFooterLayout().setVisibility(View.VISIBLE);
                    TextView tvText = (TextView) mNewsDetailAdapter.getFooterLayout().findViewById(R.id.tv_is_can_pull);
                    //这里要设置显示的文字；
                    if (list.size() >= 20) {
                        //这里表示是由分页的；
                        tvText.setText("上拉有彩蛋");
                    } else {

                        tvText.setText("我是有底线的");
                    }
                }
            } else {
                //mNewsDetailAdapter.addData(list);
                mNewsDetailAdapter.addAllComment(list);
                mNewsDetailAdapter.getFooterLayout().setVisibility(View.VISIBLE);

                TextView tvText = (TextView) mNewsDetailAdapter.getFooterLayout().findViewById(R.id.tv_is_can_pull);
                //这里要设置显示的文字；
                if (list.size() >= 20) {
                    //这里表示是由分页的；
                    tvText.setText("上拉有彩蛋");
                } else {

                    tvText.setText("我是有底线的");
                }
            }

            int commentsCount = getCommentCount();
            if (commentsCount == 0) {
                mTvTip.setVisibility(View.INVISIBLE);
            } else {
                mTvTip.setVisibility(View.VISIBLE);
                mTvTip.setText(commentsCount + "");
            }
        }
    }

    /**
     * 获取评论的数量；
     *
     * @return
     */
    public int getCommentCount() {
        return mNewsDetailAdapter.getData().size();
    }


    /**
     * 这里我们是需要更新相关推荐的模块
     *
     * @param multiItemEntities
     */
    public void updateRecomment(ArrayList<NewsItemBean> multiItemEntities) {
        mRvCommentAdapter.replaceData(multiItemEntities);
        //获取评论的列表；
        mPresenter.getCommentList();
    }


    /**
     * @param goldNumber
     * @param progress
     */
    public void show(String goldNumber, String progress, String lastCount) {

        //这里
        String title = "恭喜你获得" + goldNumber + "金币";
        String progressNew = "今天剩余" + lastCount + "次,阅读文章随机领取";
        // mReadArticleAwaryView.init(goldNumber,progress);
        mReadArticleAwaryView.setTitleAndProgress(title, progressNew);
        //这里可能会崩溃
        try {
            showReadView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //延时的一个subscribetion;
    public Subscription mDelaySubscribetion;

    /**
     * 这里是移动出来的动画；
     */
    public void showReadView() {


        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "translationY", 0, UIUtil.dip2px(this, 100));
        ObjectAnimator alphyAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "Alpha", 0, 1);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "scaleX", 0.5f, 1);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "ScaleY", 0.5f, 1);

        animatorSet.setDuration(1500);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //延迟几秒发送开始向下移动；

                mDelaySubscribetion = Observable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                //这里我们开启一个新的动画操作；
                                startMoveDownAnimator();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.playTogether(objectAnimator, alphyAnimator, scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }

    /**
     * 开始一个移动到底部的动画；
     */
    private void startMoveDownAnimator() {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "translationY", UIUtil.dip2px(this, 100), UIUtil.dip2px(this, 200));
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "Alpha", 1, 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1500);
        animatorSet.playTogether(objectAnimator, alphaAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //这里将这个View从界面中移除
                if (mReadArticleAwaryView != null) {
                    ((ViewGroup) mReadArticleAwaryView.getParent()).removeView(mReadArticleAwaryView);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
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
                                    NewsDetailActivity2.this,
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

}
