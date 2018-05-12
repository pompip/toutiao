package com.duocai.caomeitoutiao.ui.fragment.user.collection;

import com.duocai.caomeitoutiao.model.bean.UserBean;
import com.duocai.caomeitoutiao.net.api.user.browerRecord.ApiBrowerRecordNewsList;
import com.duocai.caomeitoutiao.net.api.user.collection.ApiUserVideoCollections;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.fragment.user.browerRecord.VideoBrowerRecordFragment;
import com.zhxu.library.http.HttpManager;

import java.util.HashMap;

/**
 * Created by Dinosa on 2018/2/3.
 */

public class UserVideoCollectionFragment extends VideoBrowerRecordFragment {


    @Override
    public void requestAdapterData(boolean isFirst) {

        UserBean userBean = ((BaseActivity) getActivity()).getUserBean();

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiBrowerRecordNewsList.FROM_UID,userBean.getUserid());
        paramsMap.put(ApiBrowerRecordNewsList.PAGE_NUM,1+"");

        ApiUserVideoCollections apiUserCollection=new ApiUserVideoCollections(getRequestResultListener(isFirst), ((BaseActivity) getActivity()),paramsMap);
        HttpManager.getInstance().doHttpDeal(apiUserCollection);
    }

    public String getEmptyDesc() {
        return "暂无收藏 , <font color='#ff7c34'>去浏览</font>";
    }

    @Override
    public void requestNextAdapterData() {
        UserBean userBean = ((BaseActivity) getActivity()).getUserBean();

        HashMap<String, String> paramsMap = new HashMap<>();

        paramsMap.put(ApiBrowerRecordNewsList.FROM_UID,userBean.getUserid());
        paramsMap.put(ApiBrowerRecordNewsList.PAGE_NUM,page+"");

        ApiUserVideoCollections apiUserCollection=new ApiUserVideoCollections(getLoadMoreResultListener(), ((BaseActivity) getActivity()),paramsMap);
        HttpManager.getInstance().doHttpDeal(apiUserCollection);
    }
}
