package com.huanxi.toutiao.ui.activity.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.huanxi.toutiao.MyApplication;
import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.activity.base.BaseTitleActivity;
import com.huanxi.toutiao.ui.adapter.AdBean;
import com.huanxi.toutiao.ui.adapter.AdsAdapter;
import com.huanxi.toutiao.ui.fragment.user.TaskFragment;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Dinosa on 2018/1/20.
 */

public class UserTaskActivityOld extends BaseTitleActivity {

    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;

    @BindView(R.id.rv_ads)
    RecyclerView mRvAds;

    private TaskFragment mTaskFragment;


    @Override
    public int getBodyLayoutId() {
        return R.layout.activity_user_task;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        setStatusBarImmersive(null);

        setTitle("任务大厅");
        setBackText("");


        //填充广告；
        if(isHasAds()){
            //我们添加一个AD
            MyApplication application = (MyApplication) getApplication();
            List<AdBean> tasklist = application.getResAds().getTasklist();
            //这里我们使用一个广点通的
            AdsAdapter adsAdapter = new AdsAdapter(tasklist);
            mRvAds.setLayoutManager(new LinearLayoutManager(this){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            mRvAds.setAdapter(adsAdapter);
        }


        //这里是任务大厅的一个条目；
        mTaskFragment = new TaskFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_task_container, mTaskFragment)
                .commitAllowingStateLoss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTaskFragment = null;
    }

}
