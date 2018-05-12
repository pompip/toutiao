package com.duocai.caomeitoutiao.presenter;

import com.duocai.caomeitoutiao.model.bean.NewsCommentBean;
import com.duocai.caomeitoutiao.model.bean.NewsItemBean;
import com.duocai.caomeitoutiao.net.api.news.ApiEndReadIssure;
import com.duocai.caomeitoutiao.net.api.news.ApiHomeNews;
import com.duocai.caomeitoutiao.net.api.news.ApiNewsCommentList;
import com.duocai.caomeitoutiao.net.api.news.ApiNewsDetail;
import com.duocai.caomeitoutiao.net.api.news.ApiStartReadIssure;
import com.duocai.caomeitoutiao.net.api.share.ApiShareNewsAndVideoContent;
import com.duocai.caomeitoutiao.net.api.user.ApiInviteFriendDesc;
import com.duocai.caomeitoutiao.net.api.user.ApiShareSuccess;
import com.duocai.caomeitoutiao.net.api.user.ApiUserDoLike;
import com.duocai.caomeitoutiao.net.api.user.ApiUserShare;
import com.duocai.caomeitoutiao.net.api.user.collection.ApiUserCollection;
import com.duocai.caomeitoutiao.net.api.user.comment.ApiSendComment;
import com.duocai.caomeitoutiao.net.bean.ResEmpty;
import com.duocai.caomeitoutiao.net.bean.ResInviteFriendDesc;
import com.duocai.caomeitoutiao.net.bean.ResReadAwarad;
import com.duocai.caomeitoutiao.net.bean.ResRequestShare;
import com.duocai.caomeitoutiao.net.bean.ResSplashAds;
import com.duocai.caomeitoutiao.net.bean.comment.ResNewsCommentList;
import com.duocai.caomeitoutiao.net.bean.news.ResAward;
import com.duocai.caomeitoutiao.net.bean.news.ResNewsDetailBean;
import com.duocai.caomeitoutiao.net.bean.news.ResTabBean;
import com.duocai.caomeitoutiao.ui.activity.news.NewsDetailActivity2;
import com.duocai.caomeitoutiao.ui.adapter.AdBean;
import com.zhxu.library.exception.HttpTimeException;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dinosa on 2018/2/6.
 *
 * 这里是新闻详情页的业务逻辑处理类；
 */

public class NewsDetailPresenter2 {

    private static final String ARTICLE_ID = "articleId";
    private static final String MD5URL = "md5url";

    NewsDetailActivity2 mActivity;

    private String mMd5Url;
    private String mArticleId;
    private ApiUserDoLike mApiUserDoLike;

    public NewsDetailPresenter2(NewsDetailActivity2 activity) {
        mActivity = activity;
        mArticleId = mActivity.getIntent().getStringExtra(ARTICLE_ID);
        mMd5Url = mActivity.getIntent().getStringExtra(MD5URL);
    }

    public String getUrlMd5(){
        return mMd5Url;
    }

    /**
     * 请求分享内容；
     */
    public void requestShareContent(){

        //请先登陆
        //请求获取服务器的分享列表；
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiUserShare.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiUserShare.TYPE,ApiUserShare.TYPE_ARTICLE);
        paramsMap.put(ApiUserShare.URLMD5,mMd5Url);

        ApiUserShare apiUserShare=new ApiUserShare(new HttpOnNextListener<ResRequestShare>() {
            @Override
            public void onNext(ResRequestShare resRequestShare) {
                //这里我们要做的一个操作就是进行一个分享的操作；
                //展示一个一键分享
                mActivity.showShareDialog(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl());
            }
        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiUserShare);
    }

    /**
     * 请求分享成功
     */
    public void requestShareSuccess(){

        //请先登陆
        //请求获取服务器的分享列表；
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiUserShare.FROM_UID,mActivity.getUserBean().getUserid());
        ApiShareSuccess apiUserShare=new ApiShareSuccess(new HttpOnNextListener<ResAward>() {
            @Override
            public void onNext(ResAward resRequestShare) {
                //这里表示分享成功
                mActivity.shareSuccess(resRequestShare);
            }
        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiUserShare);
    }


    /**
     * 这里开始计时操作?
     */
    public  void getBeganStart(){
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiStartReadIssure.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiStartReadIssure.URLMD5,mMd5Url);
        ApiStartReadIssure apiStartReadIssure=new ApiStartReadIssure(new HttpOnNextListener<ResEmpty>() {

            @Override
            public void onNext(ResEmpty resEmpty) {

                mActivity.requestStartCountDown();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof HttpTimeException) {
                    HttpTimeException exception = (HttpTimeException) e;
                    //这里表示已经领取过该任务了
                   /* if(exception.getMessage().equals("您已领取过任务了")){
                        //这里我们是需要切换宝箱的状态了；
                        mActivity.showOpenBoxLogo();
                    }else if(exception.getMessage().equals("已领取")){
                        mActivity.showOpenBoxLogo();
                    } else if (exception.getMessage().equals("今天已达到最大次数")) {
                        mActivity.hideReadProgress();
                    }*/
                   mActivity.hideReadProgress();
                }
            }
        },mActivity,paramsMap);
        HttpManager.getInstance().doHttpDeal(apiStartReadIssure);
    }

    public boolean mHasGetCoin=false;

    /**
     * 结束计时操作
     */
    public void getReadCoin() {
        if(mHasGetCoin){
           return;
        }
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiEndReadIssure.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiEndReadIssure.URLMD5,mMd5Url);
        ApiEndReadIssure apiStartReadIssure=new ApiEndReadIssure(new HttpOnNextListener<ResReadAwarad>() {
            @Override
            public void onNext(ResReadAwarad resReadNewsAward) {

                mHasGetCoin=true;
                mActivity.readAward(resReadNewsAward);
                //这里要更新进度；
           }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        },mActivity,paramsMap);
        HttpManager.getInstance().doHttpDeal(apiStartReadIssure);
    }

    /**
     * 请求收藏的页面的；
     */
    public void requestCollection(){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiUserCollection.FROM_UID, mActivity.getUserBean().getUserid());
        paramsMap.put(ApiUserCollection.URLMD5, mMd5Url);

        ApiUserCollection apiUserCollection = new ApiUserCollection(new HttpOnNextListener<ResEmpty>() {

            @Override
            public void onNext(ResEmpty resEmpty) {
            }

        },mActivity, paramsMap);

        HttpManager.getInstance().doHttpDeal(apiUserCollection);
    }

    /**
     * 发送评论的操作；
     */
    public void requestSendComment(String content){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiSendComment.ISSUE_ID, mMd5Url);
        paramsMap.put(ApiSendComment.FROM_UID, mActivity.getUserBean().getUserid());
        paramsMap.put(ApiSendComment.COMMENT_CONTENT, content);

        //这里做的一个操作就是发送评论；
        ApiSendComment apiSendComment = new ApiSendComment(new HttpOnNextListener<ResEmpty>() {
            @Override
            public void onNext(ResEmpty o) {
                mActivity.toast("评论成功");
                mPage=1;
                getCommentList();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.toast("评论失败");
            }
        }, mActivity, paramsMap);
        HttpManager.getInstance().doHttpDeal(apiSendComment);
    }


    /**
     * 获取新闻详情；
     */
    public void getNewsDetail(){


        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiNewsDetail.URL, mArticleId);
        if (mActivity.getUserBean() != null) {
            paramsMap.put(ApiNewsDetail.FROM_UID, mActivity.getUserBean().getUserid());
        }

        ApiNewsDetail mApiNewsDetail = new ApiNewsDetail(new HttpOnNextListener<ResNewsDetailBean>() {

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.showError();
            }
            @Override
            public void onNext(final ResNewsDetailBean resNewsDetailBean) {

                mActivity.updateUI(resNewsDetailBean);

                getNavigationData();

            }
        }, paramsMap, mActivity);

        HttpManager.getInstance().doHttpDeal(mApiNewsDetail);
    }


    int mPage=1;

    /**
     * 获取评论的列表；
     */
    public void getCommentList(){

        //获取评论
        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiNewsCommentList.ISSUE_ID, mMd5Url);
        paramsMap.put(ApiNewsCommentList.PAGE_NUMBER, mPage + "");

        ApiNewsCommentList apiNewsCommentList = new ApiNewsCommentList(new HttpOnNextListener<ResNewsCommentList>() {

            @Override
            public void onNext(ResNewsCommentList resNewsCommentList) {

                if (resNewsCommentList.getList() != null) {
                    //这里我们要做的一个逻辑就是
                    mActivity.updateCommentList(resNewsCommentList.getList(),mPage==1);

                    if (resNewsCommentList.getList().size() > 0) {
                        mPage++;
                    }
                } else {
                    if(mPage!=1){
                        //这里表示部位第一
                       // mActivity.toast("没有更多数据");
                    }
                    //表示数据为空的操作;
                    mActivity.updateCommentList(new ArrayList<NewsCommentBean>(),mPage==1);
                }
                mActivity.loadMoreCompelte();
            }
        }, mActivity, paramsMap);
        HttpManager.getInstance().doHttpDeal(apiNewsCommentList);
    }

    /**
     * 这里我们要做的一个操作就是获取推荐信息；
     */
    public void getRecomment() {

        //这里我们要做第一个操作就是拼接内容；
        getData();
    }


    public void getData(){

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put("type","xiaohua");
        paramsMap.put("qid","qid02561");

        ApiHomeNews apiHomeNews = new ApiHomeNews(mActivity, paramsMap, new HttpOnNextListener<ResTabBean>() {
            @Override
            public void onNext(ResTabBean resTabBean) {
                //这里表示获取得到了返回的内容；
                List<NewsItemBean> list = resTabBean.getList();
                //这里我们要取出来两条；
                if (list!=null ) {
                    ArrayList<NewsItemBean> multiItemEntities = new ArrayList<>();
                    if(list.size()>5){
                        //这里我们要做的一个操作就是；
                        ResSplashAds resAds = mActivity.getMyApplication().getResAds();
                        ArrayList<AdBean> newdetail = resAds.getNewdetail();

                        NewsItemBean newsItemBean1 = new NewsItemBean("qmttad", "ta");
                        //拼接广告的操作；
                        if (newdetail!=null && newdetail.size()>0) {
                            //这里做一个适配的操作；
                            AdBean adBean = newdetail.get(0);

                           /* String id;
                            String title;
                            String content;
                            String imgurl;
                            String url;
                            String downurl;
                            Long size;
                            String packename;
                            String appname;*/

                            newsItemBean1.setQmttcontenttype(NewsItemBean.AD);
                            newsItemBean1.setType(adBean.getType());

                            newsItemBean1.setId(adBean.getId());
                            newsItemBean1.setTitle(adBean.getTitle());
                            newsItemBean1.setCont(adBean.getCont());
                            newsItemBean1.setImgurl(adBean.getImgurl());
                            newsItemBean1.setUrl(adBean.getUrl());
                            newsItemBean1.setDownurl(adBean.getDownurl());
                            newsItemBean1.setSize(adBean.getSize());
                            newsItemBean1.setPackename(adBean.getPackageName());
                            newsItemBean1.setAppname(adBean.getAppname());
                            newsItemBean1.setImgurls(adBean.getImgurls());
                        }

                        NewsItemBean newsItemBean2 = new NewsItemBean("qmttad", "gdt");
                        if (mActivity.isHasAds()) {

                            multiItemEntities.add(newsItemBean1);
                            multiItemEntities.add(list.get(0));
                            multiItemEntities.add(list.get(1));
                            multiItemEntities.add(list.get(2));
                            multiItemEntities.add(list.get(3));
                            multiItemEntities.add(list.get(4));
                            multiItemEntities.add(newsItemBean2);
                        }else{
                            multiItemEntities.add(list.get(0));
                            multiItemEntities.add(list.get(1));
                            multiItemEntities.add(list.get(2));
                            multiItemEntities.add(list.get(3));
                            multiItemEntities.add(list.get(5));
                        }


                    } else{
                        //这里表示小于2的逻辑
                        if(mActivity.isHasAds()){
                            NewsItemBean newsItemBean1 = new NewsItemBean("qmttad", "ta");
                            NewsItemBean newsItemBean2 = new NewsItemBean("qmttad", "gdt");
                            multiItemEntities.add(newsItemBean1);
                            multiItemEntities.addAll(list);
                            multiItemEntities.add(newsItemBean2);
                        }else{
                            multiItemEntities.addAll(list);
                        }
                    }
                    mActivity.updateRecomment(multiItemEntities);
                }
            }
        });

        HttpManager.getInstance().doHttpDeal(apiHomeNews);
    }

    /**
     * 执行点赞的逻辑；
     * @param uid 用户的id
     * @param commentId 评论的id
     */
    public void onClickDoLike(String uid,String commentId){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiUserDoLike.FROM_UID,uid);
        paramsMap.put(ApiUserDoLike.COMMENT_ID,commentId);

        //这里表示
        mApiUserDoLike = new ApiUserDoLike(new HttpOnNextListener<ResEmpty>() {
            @Override
            public void onNext(ResEmpty resEmpty) {

                mActivity.toast(mApiUserDoLike.getMsg());
                //这里表示
                mPage=1;
                getCommentList();
            }
        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(mApiUserDoLike);
    }


    //四个分享的接口的逻辑;

    public void getNewsWechatShareContent(){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, ApiShareNewsAndVideoContent.NEWS_TYPE);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mMd5Url);

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {

            @Override
            public void onNext(ResRequestShare resRequestShare) {
               // mActivity.shareContentToWechatFriend(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl());
            }

        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);

    }

    public void getNewsWechatCommentShareContent(){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, ApiShareNewsAndVideoContent.NEWS_TYPE);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND_COMMENT);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mMd5Url);

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {


            @Override
            public void onNext(ResRequestShare resRequestShare) {

               // mActivity.shareContentToWechatComment(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl());
            }

        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);
    }



    /**
     * 获取Navigation里面的内容；
     */
    public void getNavigationData() {


        ApiInviteFriendDesc apiInviteFriendDesc = new ApiInviteFriendDesc(new HttpOnNextListener<ResInviteFriendDesc>() {

            @Override
            public void onNext(final ResInviteFriendDesc resInviteFriendDesc) {

                mActivity.initRightBottomToast(resInviteFriendDesc);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, mActivity, new HashMap<String, String>());

        HttpManager.getInstance().doHttpDeal(apiInviteFriendDesc);
    }


  /*  //这里要做的一个操作就是显示对应的
                if ("1".equals(resInviteFriendDesc.getShowTextForLogin())) {
        //这里要做的一个逻辑就是显示title
        mLlNavigation.setVisibility(View.VISIBLE);
        //这里要做的另一个逻辑就是；
        if(!mActivity.isLogin()){

            mLlNavigationNoLogin.setVisibility(View.VISIBLE);
            mTvNavigationText.setVisibility(View.GONE);

            mLlNavigationNoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //去登陆的逻辑
                    Intent intent = new Intent(getBaseActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

        }else{
            mLlNavigationNoLogin.setVisibility(View.GONE);
            mTvNavigationText.setVisibility(View.VISIBLE);
            mTvNavigationText.setText(resInviteFriendDesc.getTextforlogin().getTitle());
            mTvNavigationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = WebHelperActivity.getIntent(getBaseActivity(), resInviteFriendDesc.getTextforlogin().getUrl(), "", false);
                    startActivity(intent);
                }
            });
        }

    }else{
        //这里让其隐藏；
        mLlNavigation.setVisibility(View.GONE);
    }*/

}
