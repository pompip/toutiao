package com.huanxi.toutiao.ui.activity.other;

import android.os.Bundle;
import android.view.View;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.activity.base.BaseTitleActivity;

/**
 * Created by Dinosa on 2018/2/3.
 */

public class AboutUsActivity extends BaseTitleActivity {


    @Override
    public int getBodyLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        super.initView(rootView, savedInstanceState);
        setTitle("关于我们");
        setBackText("");
    }

}
