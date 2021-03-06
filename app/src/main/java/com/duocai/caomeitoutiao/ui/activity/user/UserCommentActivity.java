package com.duocai.caomeitoutiao.ui.activity.user;

import android.os.Bundle;
import android.view.View;

import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.ui.activity.base.BaseTitleActivity;
import com.duocai.caomeitoutiao.ui.fragment.user.comment.UserCommentFragment;

/**
 * Created by Dinosa on 2018/1/26.
 * 用户评论的模块；
 */

public class UserCommentActivity extends BaseTitleActivity {

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        super.initView(rootView, savedInstanceState);
        setBackText("");
        setTitle("我的评论");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container,new UserCommentFragment())
                .commitAllowingStateLoss();
    }
}
