package com.huanxi.toutiao.presenter;

import com.huanxi.toutiao.MyApplication;
import com.huanxi.toutiao.model.bean.UserBean;
import com.huanxi.toutiao.ui.activity.base.BaseActivity;

/**
 * Created by Dinosa on 2018/1/12.
 */

public class BasePresenter {


    BaseActivity mBaseActivity;

    public BasePresenter(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    public void toast(String toast){
        mBaseActivity.toast(toast);
    }

    public void showDialog(){
        mBaseActivity.showDialog();
    }

    public void dismissDialog(){
        mBaseActivity.dismissDialog();
    }

    public void runOnUiThread(Runnable runnable){
        mBaseActivity.runOnUiThread(runnable);
    }

    public MyApplication getMyApplication(){

        return mBaseActivity.getMyApplication();

    }

    public void updateUser(UserBean bean){
        mBaseActivity.updateUser(bean);
    }

    public UserBean getUserBean(){
        return mBaseActivity.getUserBean();
    }

}
