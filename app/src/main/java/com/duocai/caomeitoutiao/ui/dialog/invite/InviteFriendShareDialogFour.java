package com.duocai.caomeitoutiao.ui.dialog.invite;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.net.api.share.ApiInviteFriendContent;
import com.duocai.caomeitoutiao.net.bean.ResInviteFriendContent;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.dialog.QrcodeShareDialog;
import com.duocai.caomeitoutiao.utils.QrCodeUtils;
import com.duocai.caomeitoutiao.utils.UIUtils;
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
 * Created by Dinosa on 2018/4/12.
 *
 * 邀请好友的分享对话框；
 */

public class InviteFriendShareDialogFour {


    private final Context mContext;
    private final OnClickShareType mOnClickShareType;

    private final String mUid;    //这里是uid的一个操作；

    private PlatformActionListener mListener;
    private Dialog mDialog;
    private String mTitle;
    private String mContent;
    private String mUrl;
    protected View mShareDialogView;

    InviteFriendShareDialog mShareDialog;


    public InviteFriendShareDialogFour(Activity context, OnClickShareType onClickShareType, String uid) {

        mContext = context;
        initView();

        mOnClickShareType = onClickShareType;
        mShareDialog=new InviteFriendShareDialog(context,null,uid);

        mUid = uid;
    }

    public interface OnClickShareType{
        public void onClickQQ();
        public void onClickWechat();
        public void onClickWechatComment();
    }


    private void initView() {


        mDialog = new Dialog(mContext, R.style.dialog_share);

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        mShareDialogView = View.inflate(mContext, R.layout.dialog_invite_friend_share_four, null);


        //======================================================

        mShareDialogView.findViewById(R.id.ll_wechat_comment_share_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //微信朋友圈的操作；
                if (mOnClickShareType != null) {
                    mOnClickShareType.onClickWechatComment();
                }
                reqeustWechatComment();
                dismiss();

            }
        });

        mShareDialogView.findViewById(R.id.ll_wechat_friend_share_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //微信好友的分享的操作；
                if (mOnClickShareType != null) {
                    mOnClickShareType.onClickWechat();
                }
                requestWechatShare();
                dismiss();
            }
        });

        mShareDialogView.findViewById(R.id.ll_qq_share_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里要做的一个qq分享的操作；

                if (mOnClickShareType != null) {
                    mOnClickShareType.onClickQQ();
                }
                requestQQShare();
                dismiss();
            }
        });

        mShareDialogView.findViewById(R.id.ll_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里要做的一个qq分享的操作；

                //requestQQShare();
                dismiss();
                shareNetQrCode();
            }
        });

        ///===============================================================



        mShareDialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


        mDialog.setContentView(mShareDialogView);

        WindowManager systemService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = systemService.getDefaultDisplay().getWidth();
        //将对话框的宽度设置为屏幕的宽度；
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width=width;

        window.setAttributes(attributes);
    }


    /**
     * 这里是封装的show的方法；
     * @param title
     * @param content
     * @param url
     * @param listener
     */
    public  void show(String title,String content,String url,PlatformActionListener listener){

        mTitle = title;
        mContent = content;
        mUrl = url;
        setShareListener(listener);
        mDialog.show();
    }

    public void show(){
        mDialog.show();
    }

    public void dismiss(){
        mDialog.dismiss();
    }


    public void share(String platformName) {

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setTitle(mTitle);
        wechatMoments.setText(mContent);
        wechatMoments.setUrl(mUrl);


        wechatMoments.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher));
        wechatMoments.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(platformName);

        if(mListener!=null){
            weixin.setPlatformActionListener(mListener);
        }

        weixin.share(wechatMoments);
    }

    public void share(String title,String content,String url,String platformName) {

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setTitle(title);
        wechatMoments.setText(content);
        wechatMoments.setUrl(url);

        wechatMoments.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher));
        wechatMoments.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(platformName);

        if(mListener!=null){
            weixin.setPlatformActionListener(mListener);
        }

        weixin.share(wechatMoments);
    }

    public void setShareListener(PlatformActionListener listener) {
        this.mListener = listener;
    }

    /**
     * 分享qq
     */
    public void shareQQ(String title,PlatformActionListener listener){

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setText(title);

        wechatMoments.setShareType(Platform.SHARE_TEXT);
        Platform weixin = ShareSDK.getPlatform(QQ.NAME);

        if(mListener!=null){
            weixin.setPlatformActionListener(mListener);
        }

        weixin.share(wechatMoments);

    }

    /**
     * 分享微信好友；
     */
    public void shareWechatFriend(Bitmap img, PlatformActionListener listener){

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
     * 分享朋友圈；
     */
    public void shareWechatComment(Bitmap imgUrl,String comment,String url,PlatformActionListener listener){

        Platform.ShareParams wechatMoments = new Platform.ShareParams();
        wechatMoments.setUrl(url);
        wechatMoments.setText(comment);
        wechatMoments.setTitle(comment);
        wechatMoments.setImageData(imgUrl);

        wechatMoments.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(WechatMoments.NAME);

        if(mListener!=null){
            weixin.setPlatformActionListener(mListener);
        }

        weixin.share(wechatMoments);
    }



    /**
     * 启动qq分享的功能
     */
    private void requestQQShare() {

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,mUid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.QQ);

        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(ResInviteFriendContent resInviteFriendContent) {

                //这里我们要做的一个操作就是;
                shareQQ(resInviteFriendContent.getContent(),null);

            }

        }, ((BaseActivity) mContext),paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }

    //微信分享；

    /**
     * 微信的分享；
     */
    private void requestWechatShare() {


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
                                        )
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
     * 创建一个二维码的对象；
     * @return
     */
    public Bitmap createBitmap(Drawable bgDrawable, Bitmap qrcodeBitmap){

        Resources resources =getBaseActivity().getResources();

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


        int size = UIUtils.dip2px(getBaseActivity(), 170);

        int left = (bgDrawable.getIntrinsicWidth() - size)/2;
        int top = (bgDrawable.getIntrinsicHeight() - size)/2-UIUtils.dip2px(getBaseActivity(),10);

        //画二维码；
        canvas.drawBitmap(qrcodeBitmap,left,top,new Paint());

        return bitmap;
    }

    //朋友圈分享；


    public BaseActivity getBaseActivity(){
        return ((BaseActivity) mContext);
    }




    /**
     * 朋友圈的分享；
     */
    private void reqeustWechatComment() {

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,mUid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.WECHAT_FRIEND_COMMENT);

        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(final ResInviteFriendContent resInviteFriendContent) {




                Glide.with(getBaseActivity())
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
                                                UIUtils.dip2px(getBaseActivity(),170)   ,
                                                UIUtils.dip2px(getBaseActivity(),170)
                                        )
                                );


                                shareToWeChat(resInviteFriendContent.getContent(),shareImgBitmap);

                            }
                        });


            }

        },getBaseActivity(),paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);
    }



    public  void shareToWeChat(String msg,Bitmap bitmap) {
        Context context=getBaseActivity();
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
            toast("未检测到微信");
        }
    }

    public void toast(String string){
        getBaseActivity().toast(string);
    }


    private void shareNetQrCode() {

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiInviteFriendContent.FROM_UID,mUid);
        paramsMap.put(ApiInviteFriendContent.TYPE,ApiInviteFriendContent.WECHAT_FRIEND);


        ApiInviteFriendContent apiInviteFriendContent=new ApiInviteFriendContent(new HttpOnNextListener<ResInviteFriendContent>() {

            @Override
            public void onNext(final ResInviteFriendContent resInviteFriendContent) {

                //这里分享图片：img表示背景，erwma表示二维码；

                Glide.with(getBaseActivity())
                        .load(resInviteFriendContent.getImg())
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                //view.setImageDrawable(resource);
                                //这里在合成图片；
                                Bitmap bitmap = QrCodeUtils.generateBitmap(
                                        resInviteFriendContent.getErwema(),
                                        UIUtils.dip2px(getBaseActivity(), 170),
                                        UIUtils.dip2px(getBaseActivity(), 170)
                                );

                                QrcodeShareDialog qrcodeShareDialog = new QrcodeShareDialog(getBaseActivity(), new QrcodeShareDialog.GoReadDialogListener() {
                                    @Override
                                    public void onClickShare() {

                                        //这里要做的一个操作就是分享；

                                        mShareDialog.show();

                                    }
                                },bitmap);
                                qrcodeShareDialog.show();

                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        },getBaseActivity(),paramsMap);

        HttpManager.getInstance().doHttpDeal(apiInviteFriendContent);

    }

}
