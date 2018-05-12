package com.duocai.caomeitoutiao.ui.activity.video;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.model.bean.UserBean;
import com.duocai.caomeitoutiao.net.api.news.ApiEndReadIssure;
import com.duocai.caomeitoutiao.net.api.news.ApiStartReadIssure;
import com.duocai.caomeitoutiao.net.api.share.ApiShareNewsAndVideoContent;
import com.duocai.caomeitoutiao.net.api.user.collection.ApiUserCollection;
import com.duocai.caomeitoutiao.net.api.user.comment.ApiSendComment;
import com.duocai.caomeitoutiao.net.api.video.ApiVedioDetail;
import com.duocai.caomeitoutiao.net.bean.ResEmpty;
import com.duocai.caomeitoutiao.net.bean.ResReadAwarad;
import com.duocai.caomeitoutiao.net.bean.ResRequestShare;
import com.duocai.caomeitoutiao.net.bean.video.ResVedioSource;
import com.duocai.caomeitoutiao.presenter.LoginPresenter;
import com.duocai.caomeitoutiao.presenter.ads.gdt.GDTImgAds;
import com.duocai.caomeitoutiao.presenter.ads.gdt.GdtNativeAds;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.adapter.recyclerview.muiltyAdapter.bean.video.VideoListBean;
import com.duocai.caomeitoutiao.ui.dialog.CommentDialog;
import com.duocai.caomeitoutiao.ui.dialog.ShareDialog;
import com.duocai.caomeitoutiao.ui.dialog.invite.InviteFriendShareDialog;
import com.duocai.caomeitoutiao.ui.dialog.invite.ShareContentAndVideoDialog;
import com.duocai.caomeitoutiao.ui.dialog.invite.ShareNewsAndVideoContentDialog;
import com.duocai.caomeitoutiao.ui.fragment.video.VideoDetailFragment;
import com.duocai.caomeitoutiao.ui.view.MyVideoPlayer;
import com.duocai.caomeitoutiao.ui.view.ReadArticleAwaryView;
import com.duocai.caomeitoutiao.utils.ImageUtils;
import com.jaeger.library.StatusBarUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;
import com.zhxu.library.utils.SystemUtils;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Dinosa on 2018/1/25.
 */

public class VideoItemDetailActivity extends BaseActivity {

    @BindView(R.id.videoplayer)
    MyVideoPlayer mVideoplayer;

    @BindView(R.id.tv_tip)
    TextView mTvTip;

    @BindView(R.id.skip_view)
    TextView mTvSkipView;

    @BindView(R.id.fl_ad_container)
    FrameLayout mAdContainer;

    @BindView(R.id.fl_ads)
    FrameLayout mFlAds;



    @BindView(R.id.award_view)
    public ReadArticleAwaryView mReadArticleAwaryView;



    private VideoListBean mVideoBean;
    //private VideoItemDetailFragment mCommentsFragment;
    private VideoDetailFragment mCommentsFragment;
    private ShareNewsAndVideoContentDialog mShareDialog;
    private LoginPresenter mLoginPresenter;


    public VideoDetailFragment getVideoDetailFragment(){
        return mCommentsFragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_video_item_deatil;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        StatusBarUtil.setColor(this, Color.BLACK);

        mVideoBean = ((VideoListBean) getIntent().getSerializableExtra(VIDEO_BEAN));



        mCommentsFragment = VideoDetailFragment.getFragment(mVideoBean);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_comment,mCommentsFragment)
                .commitAllowingStateLoss();

        startRead();
    }

    private void startRead() {

        if(getUserBean() == null){
            return;
        }

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiStartReadIssure.FROM_UID,getUserBean().getUserid());
        paramsMap.put(ApiStartReadIssure.URLMD5,mVideoBean.getUrlmd5());
        ApiStartReadIssure apiStartReadIssure=new ApiStartReadIssure(new HttpOnNextListener<ResEmpty>() {

            @Override
            public void onNext(ResEmpty resEmpty) {

                requestStartCountDown();
            }


        },this,paramsMap);
        HttpManager.getInstance().doHttpDeal(apiStartReadIssure);

    }

    public CountDownTimer mStayCountDown;

    private void requestStartCountDown() {

            final int totalTime = 30 * 1000;
            int timeUnit = 1000;
            mStayCountDown = new CountDownTimer(totalTime, timeUnit) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    //这里获取金币

                    if(getUserBean() == null){
                        return;
                    }

                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(ApiEndReadIssure.FROM_UID,getUserBean().getUserid());
                    paramsMap.put(ApiEndReadIssure.URLMD5,mVideoBean.getUrlmd5());
                    ApiEndReadIssure apiStartReadIssure=new ApiEndReadIssure(new HttpOnNextListener<ResReadAwarad>() {
                        @Override
                        public void onNext(ResReadAwarad resReadNewsAward) {

                            int integral = resReadNewsAward.getIntegral();
                           // RedPacketDialog.show(VideoItemDetailActivity.this,integral+"",null);

                            show(resReadNewsAward.getIntegral()+"",resReadNewsAward.getLastcount(),resReadNewsAward.getLastcount_new());
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    },VideoItemDetailActivity.this,paramsMap);
                    HttpManager.getInstance().doHttpDeal(apiStartReadIssure);

                }
            };
        mStayCountDown.start();
    }

    /**
     *
     * @param goldNumber
     * @param progress
     */
    public void show(String goldNumber,String progress,String lastCount){
        //这里
        String title="恭喜你获得"+goldNumber+"金币";
        String progressNew="今天剩余"+lastCount+"次,观看视频随机领取";
        // mReadArticleAwaryView.init(goldNumber,progress);
        mReadArticleAwaryView.setTitleAndProgress(title,progressNew);
        //这里可能会崩溃
        try {
            showReadView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        super.initData();



        mFlAds.setVisibility(View.VISIBLE);
        mVideoplayer.setVisibility(View.INVISIBLE);

        initPlayer();
        //这里我们请求数据
        if(isHasAds()){
            dispalyGDTAds();
        }else{
            startPlay();
        }
/*     mVideoplayer.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子闭眼睛");*/
        //mVideoplayer.thumbImageView.setImageURI(Uri.parse(mVideoBean.getCont().getLarge_image_list().get(0).getUrl()));

        String uid="";

        if(getUserBean()!=null){
            uid=getUserBean().getUserid();
        }

        mShareDialog = new ShareNewsAndVideoContentDialog(this, new InviteFriendShareDialog.OnClickShareType() {
            @Override
            public void onClickQQ() {

                mShareDialog.dismiss();
            }

            @Override
            public void onClickWechat() {

                onClickWechatShare();
                mShareDialog.dismiss();
            }

            @Override
            public void onClickWechatComment() {
                onClickWechatCommentShare();
                mShareDialog.dismiss();
            }
        },uid);

    }




    private void onClickWechatShare() {
        //这里好友的分享；
        getVideoWechatShareContent();
    }

    private void onClickWechatCommentShare() {
        //朋友圈的分享；
        getVideoWechatCommentShareContent();
    }

    public void getVideoWechatCommentShareContent(){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, ApiShareNewsAndVideoContent.VIDEO_TYPE);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND_COMMENT);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mVideoBean.getUrlmd5());

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {


            @Override
            public void onNext(ResRequestShare resRequestShare) {
                mShareDialog.share(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl(), WechatMoments.NAME);
            }

        },this,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);
    }

    public void getVideoWechatShareContent(){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, ApiShareNewsAndVideoContent.VIDEO_TYPE);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mVideoBean.getUrlmd5());

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {

            @Override
            public void onNext(ResRequestShare resRequestShare) {
                mShareDialog.share(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl(), Wechat.NAME);
            }

        },this,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);
    }


    NativeExpressADView mImgAdView;

    GDTImgAds mGDTImgAds;

    /**
     * 这里我们展示一下广点通的广告；
     */
    private void dispalyGDTAds() {

/*
        GdtNativeAds gdtNativeAds = GdtNativeAds.newInstance();
        mImgAdView = gdtNativeAds.getImgAdView();

        if (mImgAdView != null) {
            mImgAdView.render();
            mAdContainer.addView(mImgAdView);
            startCountDown(6);
        }else{
            startPlay();
        }
*/


        mGDTImgAds = new GDTImgAds(null, 1, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(AdError adError) {

                System.out.println("errorCode: "+adError.getErrorCode()+" msg: "+adError.getErrorMsg());
                startPlay();
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                //这里个操作就是获取广澳；
                if (list != null && list.size()>0) {
                    NativeExpressADView nativeExpressADView = list.get(0);
                    nativeExpressADView.render();
                    mAdContainer.addView(nativeExpressADView);
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {


                System.out.println("errorCode: "+"渲染失败");
                startPlay();
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                mTvSkipView.setVisibility(View.VISIBLE);
                startCountDown(4);
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }
        });

    }

    CountDownTimer mCountDownTimer;

    /**
     * 开始一个广告的倒计时的操作
     */
    private void startCountDown(int totalTime) {
        int timeUnit=1000;
        totalTime=totalTime*1000;
        //去做一个更新的操作；
        mCountDownTimer = new CountDownTimer(totalTime,timeUnit) {
            @Override
            public void onTick(long millisUntilFinished) {
                //去做一个更新的操作；
                mTvSkipView.setText("跳过 "+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                //这里我们将修改状态；变成可领取的状态；
                startPlay();
            }
        };
        mCountDownTimer.start();
    }

    @OnClick(R.id.skip_view)
    public void onClickSkip(){
        //停止倒计时；
        mCountDownTimer.cancel();
        startPlay();
    }

    private void startPlay() {

        mVideoplayer.setVisibility(View.VISIBLE);
        if (mFlAds != null) {
            mFlAds.setVisibility(View.INVISIBLE);

            if (mFlAds.getParent() != null) {
                //移除广告容器；
                ((ViewGroup) mFlAds.getParent()).removeView(mFlAds);
            }
        }
        //回收广告；
        GdtNativeAds gdtNativeAds = GdtNativeAds.newInstance();
        if (gdtNativeAds != null) {
            gdtNativeAds.releaseImgAdView(mImgAdView);
        }
        //开始播放的操作；
        getVideoSourceData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }


        if (mStayCountDown != null) {
            mStayCountDown.cancel();
        }

        if (mImgAdView != null) {
            GdtNativeAds.newInstance().releaseImgAdView(mImgAdView);
        }

        if (mDelaySubscribetion != null) {
            if (mDelaySubscribetion.isUnsubscribed()) {
                mDelaySubscribetion.unsubscribe();
            }
        }

        JZVideoPlayer.releaseAllVideos();
    }

    /**
     * 这里是初始化播放器的一个操作；
     */
    private void initPlayer() {
        mVideoplayer.backButton.setVisibility(View.VISIBLE);
        mVideoplayer.mRetryLayout.setVisibility(View.INVISIBLE);
        mVideoplayer.thumbImageView.setBackgroundColor(Color.GRAY);
        mVideoplayer.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageUtils.loadImage(this,mVideoBean.getImgUrl(),mVideoplayer.thumbImageView);
    }

    //这里我们是请求我们自己的vedio的url
    private void getVideoSourceData() {

        UserBean userBean = getUserBean();
        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put("category","video");
        paramsMap.put("urlmd5",mVideoBean.getUrlmd5());
        if (userBean != null) {
            paramsMap.put("from_uid",userBean.getUserid());
        }
        paramsMap.put("category","video");
        paramsMap.put("video_id",mVideoBean.getVideo_id());
        paramsMap.put("item_id",mVideoBean.getVideo_duration()+"");


        paramsMap.put("device_id", SystemUtils.getIMEI(this));
        paramsMap.put("device_platform","android");
        paramsMap.put("device_type",SystemUtils.getSystemModel());
        paramsMap.put("device_brand",SystemUtils.getDeviceBrand());
        paramsMap.put("os_api", SystemUtils.getSystemApi());
        paramsMap.put("os_version",SystemUtils.getSystemVersion());
        paramsMap.put("uuid",SystemUtils.getIMEI(this));
        paramsMap.put("openudid",SystemUtils.getOpenUid(this));
        paramsMap.put("resolution",SystemUtils.getResolution(this));
        paramsMap.put("dpi",SystemUtils.getDensity(this));

        ApiVedioDetail apiVedioDetail = new ApiVedioDetail(new HttpOnNextListener<ResVedioSource>() {

            @Override
            public void onNext(ResVedioSource resVedioSource) {
                //这里我们要做的一个操作；
                mVideoplayer.setUp(resVedioSource.getUrl().getData().getVideo_list().getVideo_1().getMain_url()
                        , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                mVideoplayer.backButton.setVisibility(View.VISIBLE);
                mCbCollection.setChecked(resVedioSource.isIscollect());

                mVideoplayer.play();
            }
        },paramsMap,this);

        HttpManager.getInstance().doHttpDeal(apiVedioDetail);
    }


    public static String VIDEO_BEAN = "videoBean";

    /**
     * 这里我们是传入的对应的东西的
     *
     * @return
     */
    public static Intent getIntent(Context context, VideoListBean bean) {
        Intent intent = new Intent(context, VideoItemDetailActivity.class);
        intent.putExtra(VIDEO_BEAN, bean);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    //这里是点击底部的按钮实现的；
    @OnClick(R.id.iv_back)
    public void onClickBottomBack(){
        finish();
    }

    @OnClick(R.id.et_conmment)
    public void onClickInputComment() {

        checkLogin(new CheckLoginCallBack() {
            @Override
            public void loginSuccess() {

                CommentDialog commentDialog = new CommentDialog();
                commentDialog.show(VideoItemDetailActivity.this, new CommentDialog.OnDialogClickListener() {
                    @Override
                    public void onClickSure(String content) {

                        UserBean userBean = getUserBean();

                        HashMap<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(ApiSendComment.ISSUE_ID, mVideoBean.getUrlmd5());
                        paramsMap.put(ApiSendComment.FROM_UID, userBean.getUserid());
                        paramsMap.put(ApiSendComment.COMMENT_CONTENT, content);

                        //这里做的一个操作就是发送评论；
                        ApiSendComment apiSendComment = new ApiSendComment(new HttpOnNextListener<ResEmpty>() {
                            @Override
                            public void onNext(ResEmpty o) {
                                toast("评论成功");
                                //这里我们要做一个操作；
                                mCommentsFragment.requestAdapterData(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                toast("评论失败");
                            }
                        }, VideoItemDetailActivity.this, paramsMap);
                        HttpManager.getInstance().doHttpDeal(apiSendComment);

                    }
                });
            }

            @Override
            public void loginFaild() {

            }
        });
    }


    @OnClick(R.id.iv_message)
    public void onClickSend() {
        //点击消息的部分,这里是需要滚动的
    }

    @BindView(R.id.cb_collection)
    CheckBox mCbCollection;

    @OnClick(R.id.fl_collection)
    public void onClickCollection(){
        checkLogin(new CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //这里我们要进行一个收藏操作；
                mCbCollection.toggle();

                if(mCbCollection.isChecked()){
                    toast("收藏成功");
                }else{
                    toast("取消收藏");
                }
                UserBean userBean = getUserBean();
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(ApiUserCollection.FROM_UID,userBean.getUserid());
                paramsMap.put(ApiUserCollection.URLMD5,mVideoBean.getUrlmd5());

                ApiUserCollection apiUserCollection=new ApiUserCollection(new HttpOnNextListener<ResEmpty>() {

                    @Override
                    public void onNext(ResEmpty resEmpty) {
                        //这里要做的一个操作就是成功；
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                },VideoItemDetailActivity.this,paramsMap);

                HttpManager.getInstance().doHttpDeal(apiUserCollection);
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    ShareContentAndVideoDialog mShareContentAndVideoDialog;

    /**
     * 这里是点击分享的操作；
     */
    @OnClick(R.id.iv_share)
    public void onClickShare(){
        //这里是点击分享的操作；
        //这里我们将使用
        checkLogin(new CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                /*HashMap<String, String> paramsMap = new HashMap<>();

                paramsMap.put(ApiUserShare.FROM_UID,getUserBean().getUserid());
                paramsMap.put(ApiUserShare.TYPE,ApiUserShare.TYPE_VIDEO);
                paramsMap.put(ApiUserShare.URLMD5,mVideoBean.getUrlmd5());

                ApiUserShare apiUserShare=new ApiUserShare(new HttpOnNextListener<ResRequestShare>(){

                    @Override
                    public void onNext(ResRequestShare resRequestShare) {
                        showShareDialog(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl());
                    }
                },VideoItemDetailActivity.this,paramsMap);
                HttpManager.getInstance().doHttpDeal(apiUserShare);*/
                if (mShareContentAndVideoDialog == null) {
                    mShareContentAndVideoDialog = new ShareContentAndVideoDialog(VideoItemDetailActivity.this);
                }
                mShareContentAndVideoDialog.show(mVideoBean.getUrlmd5(), ApiShareNewsAndVideoContent.VIDEO_TYPE);
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    public void showShareDialog(String title,String content,String url){
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
     * 获取提示的View;
     * @return
     */
    public TextView getTipView(){
        return mTvTip;
    }


    Subscription mDelaySubscribetion;

    /**
     * 这里是移动出来的动画；
     */
    public void showReadView(){


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

                mDelaySubscribetion= Observable.timer(1, TimeUnit.SECONDS)
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

        animatorSet.playTogether(objectAnimator,alphyAnimator,scaleXAnimator,scaleYAnimator);
        animatorSet.start();
    }

    /**
     * 开始一个移动到底部的动画；
     */
    private void startMoveDownAnimator() {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "translationY", UIUtil.dip2px(this, 100),UIUtil.dip2px(this, 200));
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mReadArticleAwaryView, "Alpha", 1,0);

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(1500);
        animatorSet.playTogether(objectAnimator,alphaAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //这里将这个View从界面中移除
                if(mReadArticleAwaryView!=null){
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


}
