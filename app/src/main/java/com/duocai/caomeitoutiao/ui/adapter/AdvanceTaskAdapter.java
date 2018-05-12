package com.duocai.caomeitoutiao.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.duocai.caomeitoutiao.R;
import com.duocai.caomeitoutiao.net.api.user.task.ApiCustomTaskEnd;
import com.duocai.caomeitoutiao.net.api.user.task.ApiCustomTaskStart;
import com.duocai.caomeitoutiao.net.bean.news.ResAward;
import com.duocai.caomeitoutiao.ui.activity.WebHelperActivity;
import com.duocai.caomeitoutiao.ui.activity.base.BaseActivity;
import com.duocai.caomeitoutiao.ui.adapter.bean.TaskItemBean;
import com.duocai.caomeitoutiao.ui.adapter.bean.TaskItemContentBean;
import com.duocai.caomeitoutiao.ui.adapter.bean.TaskTitleBean;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;
import java.util.List;

import sd.sazs.erd.listener.Interface_ActivityListener;
import sd.sazs.erd.os.OffersManager;

/**
 * Created by Dinosa on 2018/1/22.
 */

public class AdvanceTaskAdapter extends TaskAdapter{


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param baseActivity
     * @param data         A new list is created out of this one to avoid mutable list
     */
    public AdvanceTaskAdapter(BaseActivity baseActivity, List<MultiItemEntity> data) {
        super(baseActivity, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        super.convert(helper,item);

        if (item instanceof AdBean) {
        }else{

            switch (helper.getItemViewType()) {
                case TYPE_LEVEL_0:
                    //这里是一级title的操作；
                    mTypeLevel0Presenter.init(helper, ((TaskTitleBean) item),this);
                    break;
                case TYPE_LEVEL_1:
                    //这里是二级标题；
                    mTypeLevel1Presenter.init(helper, ((TaskItemBean) item),this);
                    break;
                case TYPE_LEVEL_2:
                    //这里是三级内容的标签的；
                   // mTypeLevel2Presenter.init(helper, ((TaskItemContentBean) item));
                    //这里要做的一个操作就是显示
                    init(helper, ((TaskItemContentBean) item));
                    break;
            }
        }
    }



    public void init(final BaseViewHolder viewHolder, final TaskItemContentBean bean){

        viewHolder.setText(R.id.tv_task_content,bean.getContent());
        viewHolder.setText(R.id.tv_task_button,bean.getButtonContent());


        viewHolder.getView(R.id.tv_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(bean.getUrl())) {
                    //这里表示跳转网页；
                    //这里要做的一个操作就是领取高佣任务的奖励的操作；

                    mCurTaskId = bean.getId();

                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(ApiCustomTaskEnd.FROM_UID,mBaseActivity.getUserBean().getUserid());
                    paramsMap.put(ApiCustomTaskEnd.TASK_ID, mCurTaskId);

                    ApiCustomTaskStart apiCustomTaskStart=new ApiCustomTaskStart(new HttpOnNextListener<ResAward>() {

                        @Override
                        public void onNext(ResAward s) {
                            mBaseActivity.startActivity(WebHelperActivity.getIntent(mBaseActivity,bean.getUrl(),bean.getTitle(),false));
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            e.printStackTrace();
                            mBaseActivity.startActivity(WebHelperActivity.getIntent(mBaseActivity,bean.getUrl(),bean.getTitle(),false));
                        }
                    },mBaseActivity,paramsMap);

                    HttpManager.getInstance().doHttpDeal(apiCustomTaskStart);
                }else{
                    //这里是有米的跳转的操作:

                   /* if ("开始试用".equals(bean.getButtonContent())) {
                        OffersManager.getInstance(mBaseActivity).showOffersWall(new Interface_ActivityListener() {

                            *//**
                             * 当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
                             *//*
                            @Override
                            public void onActivityDestroy(Context context) {

                            }
                        });
                    }else if("开始任务".equals(bean.getButtonContent())){
                        //这里要做的一个操作是趣米的操作；
                        //积分墙调用(传入userId)
                        QuMiWall.get().launch(mBaseActivity.getUserBean().getUserid());
                    }*/

                    OffersManager.getInstance(mBaseActivity).showOffersWall(new Interface_ActivityListener() {

                            /**
                             当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
                             */
                        @Override
                        public void onActivityDestroy(Context context) {

                        }
                    });
                }
            }
        });
    }


}
