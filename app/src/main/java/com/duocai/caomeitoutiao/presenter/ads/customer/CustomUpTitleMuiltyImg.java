package com.duocai.caomeitoutiao.presenter.ads.customer;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.service.AdDownloadServiceNew;
import com.duocai.caomeitoutiao.ui.activity.WebHelperActivity;
import com.duocai.caomeitoutiao.ui.adapter.AdBean;
import com.duocai.caomeitoutiao.utils.ImageUtils;

/**
 * Created by Dinosa on 2018/3/28.
 * 这里是自定义浮标的业务逻辑了；
 */

public class CustomUpTitleMuiltyImg {


    public void init(View helper, final AdBean adBean, final Context context){

        TextView view = (TextView) helper.findViewById(R.id.tv_ad_title);

        view.setText(adBean.getCont());

        ImageView image1 = (ImageView) helper.findViewById(R.id.iv_img1);
        ImageView image2 = (ImageView) helper.findViewById(R.id.iv_img2);
        ImageView image3 = (ImageView) helper.findViewById(R.id.iv_img3);

        try {
            ImageUtils.loadImage(context,adBean.getImgurls().get(0),image1);
            ImageUtils.loadImage(context,adBean.getImgurls().get(1),image2);
            ImageUtils.loadImage(context,adBean.getImgurls().get(2),image3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(adBean.getDownurl())) {
                    //这里要做的逻辑就是判断开启任务下载
                    Intent intent = new Intent(context, AdDownloadServiceNew.class);
                    intent.putExtra(AdDownloadServiceNew.DOWNLOAD_INTERFACE_BEAN,adBean);
                    context.startService(intent);
                }else {
                    //这里表示要进入一个网页的操作；
                    context.startActivity(WebHelperActivity.getIntent(context,adBean.getUrl(),adBean.getTitle(),false));
                }
            }
        });
    }

}
