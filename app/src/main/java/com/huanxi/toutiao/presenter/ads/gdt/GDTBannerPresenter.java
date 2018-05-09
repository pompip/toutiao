package com.huanxi.toutiao.presenter.ads.gdt;

import android.view.View;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.view.ads.GDTBannerView;

/**
 * Created by Dinosa on 2018/2/10.
 */

public class GDTBannerPresenter {


    private GDTBannerView mViewById;

    /**
     * 这里我们要添加一个广点通的
     * @param view
     */
    public void init(View view){

        mViewById = ((GDTBannerView) view.findViewById(R.id.gdt_banner_view));

        if (mViewById != null) {
            mViewById.init();
        }
    }
}
