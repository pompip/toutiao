package com.duocai.caomeitoutiao.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.net.api.share.ApiInviteFriendContent;
import com.duocai.caomeitoutiao.net.api.share.ApiShareNewsAndVideoContent;
import com.duocai.caomeitoutiao.net.bean.ResInviteFriendContent;
import com.duocai.caomeitoutiao.net.bean.ResRequestShare;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.dialog.QrcodeShareDialog;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Dinosa on 2018/4/16.
 * 这里是邀请好友的一个工具类；
 * 这里的分享的操作都是没有回掉的逻辑的；
 */

public class ShareUtils {

    /**
     * 微信内容分享的逻辑
     * @param mActivity 上下文的逻辑
     * @param mMd5Url 视频或者新闻的md5
     * @param contentType 类型是视频，还是新闻
     */
    public static void requestContentWechatShare(final BaseActivity mActivity, String mMd5Url, String contentType){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, contentType);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mMd5Url);

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {

            @Override
            public void onNext(ResRequestShare resRequestShare) {
                share(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl(),Wechat.NAME,mActivity);
            }

        },mActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);

    }


    /**
     * 微信内容分享的逻辑
     * @param mActivity 上下文的逻辑
     * @param mMd5Url 视频或者新闻的md5
     * @param contentType 类型是视频，还是新闻
     */
    public static void requestContentWechatCommentShare(final BaseActivity mActivity, String mMd5Url, String contentType){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiShareNewsAndVideoContent.TYPE, contentType);
        paramsMap.put(ApiShareNewsAndVideoContent.SHARE_TYPE,ApiShareNewsAndVideoContent.WECHAT_FRIEND);
        paramsMap.put(ApiShareNewsAndVideoContent.FROM_UID,mActivity.getUserBean().getUserid());
        paramsMap.put(ApiShareNewsAndVideoContent.URLMD5,mMd5Url);

        ApiShareNewsAndVideoContent apiShareNewsAndVideoContent=new ApiShareNewsAndVideoContent(new HttpOnNextListener<ResRequestShare>() {

            @Override
            public void onNext(ResRequestShare resRequestShare) {
                share(resRequestShare.getTitle(),resRequestShare.getDesc(),resRequestShare.getUrl(), WechatMoments.NAME,mActivity);
            }

        },mActivity,paramsMap);
        HttpManager.getInstance().doHttpDeal(apiShareNewsAndVideoContent);
    }




    /**
     * 微信的邀请好友的分享的逻辑
     */
    public static void requestWechatShareBimap(final BaseActivity mContext, String mUid) {

        if(TextUtils.isEmpty(mUid)){
            return;
        }

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,mUid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.WECHAT_FRIEND);

        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(final ResInviteFriendContent resInviteFriendContent) {

                //这里分享图片：img表示背景，erwma表示二维码；

                Glide.with(mContext)
                        .load(resInviteFriendContent.getImg())
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                                //这里在合成图片；
                                Bitmap shareImgBitmap=createBitmap(
                                        resource    ,
                                        QrCodeUtils.generateBitmap(
                                                resInviteFriendContent.getErwema()  ,
                                                UIUtils.dip2px(mContext,170)   ,
                                                UIUtils.dip2px(mContext,170)
                                        ),
                                        mContext
                                );

                                shareWechatFriend(shareImgBitmap,null);

                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, ((BaseActivity) mContext),paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }



    /**
     * 朋友圈带评论的邀请好友分享的逻辑
     */
    public static void requestWechatCommentWithCommentShare(String uid, final BaseActivity mBaseActivity) {

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,uid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.WECHAT_FRIEND_COMMENT);

        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(final ResInviteFriendContent resInviteFriendContent) {


                Glide.with(mBaseActivity)
                        .load(resInviteFriendContent.getImg())
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                //view.setImageDrawable(resource);
                                //这里在合成图片；
                                Bitmap shareImgBitmap=createBitmap(
                                        resource    ,
                                        QrCodeUtils.generateBitmap(
                                                resInviteFriendContent.getErwema()  ,
                                                UIUtils.dip2px(mBaseActivity,170)   ,
                                                UIUtils.dip2px(mBaseActivity,170)
                                        )
                                        ,mBaseActivity
                                );

                                shareToWeChat(resInviteFriendContent.getContent(),shareImgBitmap,mBaseActivity);

                                dismissDialog(mBaseActivity);
                            }
                        });

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissDialog(mBaseActivity);
            }
        },mBaseActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }


    /**
     * QQ文字邀请逻辑
     */
    public static void requestQQTextInviteFriendShare(String uid,BaseActivity mBaseActivity){

        //qq登陆；
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,uid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.QQ);

        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(ResInviteFriendContent resInviteFriendContent) {

                //这里我们要做的一个操作就是;
                // mShareDialog.shareQQ(resInviteFriendContent.getContent(),null);

                shareQQ(resInviteFriendContent.getContent());

            }

        },mBaseActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }


    /**
     * 二维码分享的逻辑
     * @param uid
     * @param mBaseActivity
     */
    public static void requestQrShare(String uid, final BaseActivity mBaseActivity){

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,uid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.WECHAT_FRIEND);

        showDialog(mBaseActivity);
        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(final ResInviteFriendContent resInviteFriendContent) {

                //这里分享图片：img表示背景，erwma表示二维码；

                Glide.with(mBaseActivity)
                        .load(resInviteFriendContent.getImg())
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                //view.setImageDrawable(resource);
                                //这里在合成图片；
                                Bitmap bitmap = QrCodeUtils.generateBitmap(
                                        resInviteFriendContent.getErwema(),
                                        UIUtils.dip2px(mBaseActivity, 170),
                                        UIUtils.dip2px(mBaseActivity, 170)
                                );

                                QrcodeShareDialog qrcodeShareDialog = new QrcodeShareDialog(mBaseActivity, new QrcodeShareDialog.GoReadDialogListener() {
                                    @Override
                                    public void onClickShare() {

                                        //这里要做的一个操作就是分享；

                                        //mShareDialog.show();
                                        //这里是显示三个的分享的对话框的逻辑
                                    }
                                },bitmap);
                                qrcodeShareDialog.show();

                                dismissDialog(mBaseActivity);
                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissDialog(mBaseActivity);
            }
        },mBaseActivity,paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }



    /**
     * 分享qq
     */
    private static void shareQQ(String title){

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setText(title);
        wechatMoments.setShareType(Platform.SHARE_TEXT);
        Platform weixin = ShareSDK.getPlatform(QQ.NAME);
        weixin.share(wechatMoments);
    }


    /**
     * 这里是分享内容；
     * @param title 标题
     * @param content 内容
     * @param url 点击内容的url
     * @param platformName 平台的名字
     * @param context 上下文
     */
    public static void share(String title,String content,String url,String platformName,Context context) {

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setTitle(title);
        wechatMoments.setText(content);
        wechatMoments.setUrl(url);

        wechatMoments.setImageData(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        wechatMoments.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(platformName);

        weixin.share(wechatMoments);
    }


    /**
     * 分享微信以图片的方式分享给好友；
     */
    private static void shareWechatFriend(Bitmap img, PlatformActionListener mListener){

        Platform.ShareParams wechatMoments = new Platform.ShareParams();

        wechatMoments.setImageData(img);

        wechatMoments.setShareType(Platform.SHARE_IMAGE);

        Platform weixin = ShareSDK.getPlatform(Wechat.NAME);

        if(mListener!=null){
            weixin.setPlatformActionListener(mListener);
        }

        weixin.share(wechatMoments);
    }


    /**
     * 这是一个图片合成的方法，
     * @param bgDrawable 背景图片
     * @param qrcodeBitmap 二维码；
     * @param mBaseActivity baseActivity
     * @return
     */
    private static Bitmap createBitmap(Drawable bgDrawable, Bitmap qrcodeBitmap,BaseActivity mBaseActivity){

        Resources resources =mBaseActivity.getResources();

        int w = bgDrawable.getIntrinsicWidth();
        int h = bgDrawable.getIntrinsicHeight();
        bgDrawable.setBounds(0,0,w,h);

        // 取 drawable 的颜色格式
        Bitmap.Config config = bgDrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;

        //这是一个空的Bitmap对象的；
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //创建要给画布对象；
        Canvas canvas = new Canvas(bitmap);
        //画一个背景
        bgDrawable.draw(canvas);


        int size = UIUtils.dip2px(mBaseActivity, 170);

        int left = (bgDrawable.getIntrinsicWidth() - size)/2;
        int top = (bgDrawable.getIntrinsicHeight() - size)/2-UIUtils.dip2px(mBaseActivity,10);

        //画二维码；
        canvas.drawBitmap(qrcodeBitmap,left,top,new Paint());

        return bitmap;
    }

    /**
     * 图文分享微信朋友圈的操作；
     * @param msg
     * @param bitmap
     * @param mBaseActivity
     */
    private static void shareToWeChat(String msg,Bitmap bitmap,BaseActivity mBaseActivity) {
        Context context=mBaseActivity;
        // TODO: 2015/12/13 将需要分享到微信的图片准备好
        try {

            Intent intent = new Intent();
            //分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
//        intent.setType("text/plain");
            //添加Uri图片地址
//        String msg=String.format(getString(R.string.share_content), getString(R.string.app_name), getLatestWeekStatistics() + "");

            intent.putExtra("Kdescription", msg);
            ArrayList<Uri> imageUris = new ArrayList<Uri>();
            // TODO: 2016/3/8 根据不同图片来设置分享
            File dir = context.getExternalFilesDir(null);
            if (dir == null || dir.getAbsolutePath().equals("")) {
                dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
            File pic = new File(dir, "bigbang.jpg");
            pic.deleteOnExit();

            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, new FileOutputStream(pic));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imageUris.add(Uri.fromFile(pic));
            } else {
                //修复微信在7.0崩溃的问题
                Uri uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), pic.getAbsolutePath(), "bigbang.jpg", null));
                imageUris.add(uri);
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            ((Activity) context).startActivityForResult(intent, 1000);
        } catch (Throwable e) {
            toast(mBaseActivity,"未检测到微信");
        }
    }


    //==========这里是辅助的类；=================

    /**
     * 这里是显示对话框
     * @param baseActivity
     */
    private static void showDialog(BaseActivity baseActivity){
        baseActivity.showDialog();
    }

    /**
     * 取消对话框
     * @param baseActivity
     */
    private static void dismissDialog(BaseActivity baseActivity){
        baseActivity.dismissDialog();
    }

    /**
     * 弹窗的操作；
     * @param baseActivity
     * @param content
     */
    private static void toast(BaseActivity baseActivity,String content){
        baseActivity.toast(content);
    }

}
