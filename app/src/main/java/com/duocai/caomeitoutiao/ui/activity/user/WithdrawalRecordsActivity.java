package com.duocai.caomeitoutiao.ui.activity.user;

import android.os.Bundle;
import android.view.View;

import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.ui.activity.base.BaseTitleActivity;
import com.duocai.caomeitoutiao.ui.fragment.user.WithdrawlRecordsFragment;

/**
 * Created by Dinosa on 2018/1/26.
 */

public class WithdrawalRecordsActivity extends BaseTitleActivity {


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        setStatusBarImmersive(null);
        setBackText("");
        setTitle("进度查询");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container,new WithdrawlRecordsFragment())
                .commitAllowingStateLoss();
    }
}
