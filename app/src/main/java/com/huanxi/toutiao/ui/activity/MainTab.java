package com.huanxi.toutiao.ui.activity;

import com.huanxi.toutiao.R;
import com.huanxi.toutiao.ui.fragment.news.HomeFragment;
import com.huanxi.toutiao.ui.fragment.user.TaskFragment;
import com.huanxi.toutiao.ui.fragment.user.UserFragmentV1;
import com.huanxi.toutiao.ui.fragment.video.NewVideoFragment;


/**
 * Created by andy on 2017/3/8.
 *
 * 这里我们以枚举的方式实现单例的Fragment：
 */

public enum MainTab {

/*    HOME(0, R.string.main_tab_name_home,R.drawable.main_tab_icon_home, HomeFragment.class),
    STORE(1,R.string.main_tab_name_store, R.drawable.main_tab_icon_store, CategoryListLoadingFragment.class),
    MAP(2,R.string.main_tab_name_map, R.drawable.main_tab_icon_map, MapFragment.class),
    ACCOUNT(3,R.string.main_tab_name_account, R.drawable.main_tab_icon_account, AccountFragment.class);*/

    HOME(0, R.string.main_tab_name_home,R.drawable.selector_tab_home, HomeFragment.class),
    VEDIO(1,R.string.main_tab_name_vedio, R.drawable.selector_tab_vedio, NewVideoFragment.class),
    TASK(2,R.string.main_tab_task, R.drawable.icon_tab_task_new_liu, TaskFragment.class),
    USER(3,R.string.main_tab_name_user, R.drawable.selector_tab_user, UserFragmentV1.class);


    private final int mIdx;
    private final int mDescResource;
    private final int mDrawableResource;
    private final Class mFragmentClazz;

    private MainTab(int idx, int descResource, int drawableResource, Class fragmentClazz) {
        mIdx = idx;
        mDescResource = descResource;
        mDrawableResource = drawableResource;
        mFragmentClazz = fragmentClazz;
    }

    public int getIdx() {
        return mIdx;
    }

    public int getDescResource() {
        return mDescResource;
    }

    public int getDrawableResource() {
        return mDrawableResource;
    }

    public Class getFragmentClazz() {
        return mFragmentClazz;
    }
}
