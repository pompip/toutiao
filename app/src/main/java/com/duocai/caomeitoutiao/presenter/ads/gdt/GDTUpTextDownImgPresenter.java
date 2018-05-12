package com.duocai.caomeitoutiao.presenter.ads.gdt;

import android.view.View;

import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.ui.view.ads.GDTUpTextDownImg;

/**
 * Created by Dinosa on 2018/2/10.
 */

public class GDTUpTextDownImgPresenter {


    private GDTUpTextDownImg mViewById;

    /**
     * 这里我们要添加一个广点通的
     * @param view
     */
    public void init(View view){

        mViewById = ((GDTUpTextDownImg) view.findViewById(R.id.gdt_up_text_down));

        if (mViewById != null) {
            mViewById.init();
        }
    }
}
