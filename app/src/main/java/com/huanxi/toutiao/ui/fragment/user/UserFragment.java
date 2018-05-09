package com.huanxi.toutiao.ui.fragment.user;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.db.ta.sdk.NonStandardTm;
import com.db.ta.sdk.NsTmListener;
import com.db.ta.sdk.TmActivity;
import com.google.gson.Gson;
import com.huanxi.toutiao.MyApplication;
import com.huanxi.toutiao.R;
import com.huanxi.toutiao.globle.ConstantAd;
import com.huanxi.toutiao.model.bean.UserBean;
import com.huanxi.toutiao.net.api.user.userInfo.ApiUserInfo;
import com.huanxi.toutiao.net.bean.ResSplashAds;
import com.huanxi.toutiao.net.bean.ResTaCustomAdBean;
import com.huanxi.toutiao.ui.activity.base.BaseActivity;
import com.huanxi.toutiao.ui.activity.other.ContactKeFuActivity;
import com.huanxi.toutiao.ui.activity.other.EmptyActivity;
import com.huanxi.toutiao.ui.activity.other.MainActivity;
import com.huanxi.toutiao.ui.activity.other.SettingActivity;
import com.huanxi.toutiao.ui.activity.user.IntergralShopActivity;
import com.huanxi.toutiao.ui.activity.user.MyFriendsActivity;
import com.huanxi.toutiao.ui.activity.user.MyMessageActivity;
import com.huanxi.toutiao.ui.activity.user.ProfitDetailActivity;
import com.huanxi.toutiao.ui.activity.user.QuestionsActivity;
import com.huanxi.toutiao.ui.activity.user.UserBrowerRecordsActivity;
import com.huanxi.toutiao.ui.activity.user.UserCollectionActivity;
import com.huanxi.toutiao.ui.activity.user.UserCommentActivity;
import com.huanxi.toutiao.ui.activity.user.UserSignActivity;
import com.huanxi.toutiao.ui.activity.user.UserTaskActivity;
import com.huanxi.toutiao.ui.adapter.AdsAdapter;
import com.huanxi.toutiao.ui.fragment.base.BaseFragment;
import com.huanxi.toutiao.utils.ImageUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhxu.library.http.HttpManager;
import com.zhxu.library.listener.HttpOnNextListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Dinosa on 2018/1/12.
 */

public class UserFragment extends BaseFragment {


    @BindView(R.id.iv_icon_user)
    public ImageView mIvUserIcon;

    @BindView(R.id.tv_user_name)
    public TextView mTvUsername;

    @BindView(R.id.tv_wallet_number)
    public TextView mTvWalletNumber;

    @BindView(R.id.tv_money)
    public TextView mTvMoney;

    @BindView(R.id.tv_friend_number)
    public TextView mTvFriednNumber;

    @BindView(R.id.iv_ad_banner)
    public ImageView mIvAdBanner;


    @BindView(R.id.rv_ads)
    public RecyclerView mRvAds;
    @BindView(R.id.view_divider)
    public View mDivider;


    private AdsAdapter mAdsAdapter;
    private NonStandardTm mNonStandardTm;


    @Override
    protected View getContentView() {
        return inflatLayout(R.layout.fragment_user);
    }

    @Override
    protected void initData() {
        super.initData();
        UserBean userBean = ((MainActivity) getActivity()).getUserBean();
        if (userBean != null) {
            updateText(userBean);
        }

        mRvAds.setLayoutManager(new LinearLayoutManager(getBaseActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        //这里:
        ResSplashAds resAds = ((MyApplication) getActivity().getApplication()).getResAds();

        //这里用户中心没有广告了
        mDivider.setVisibility(View.GONE);

       /* if(isHasAds()){
            initTaCusomter();
            mDivider.setVisibility(View.VISIBLE);
        }else{
            mDivider.setVisibility(View.GONE);
        }*/
    }

    private void initTaCusomter() {

        mNonStandardTm = new NonStandardTm(getBaseActivity());
        mNonStandardTm.setAdListener(new NsTmListener() {
            @Override
            public void onReceiveAd(String s) {
                System.out.println("233233: "+s);

                Gson gson = new Gson();
                final ResTaCustomAdBean resTaCustomAdBean = gson.fromJson(s, ResTaCustomAdBean.class);
                ImageUtils.loadImage(getBaseActivity(),resTaCustomAdBean.getImg_url(),mIvAdBanner);
                mNonStandardTm.adExposed();

                mIvAdBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNonStandardTm.adClicked();
                        //这里还是需要做对应的操作；
                        TmActivity.a(getActivity(),resTaCustomAdBean.getClick_url());
                    }
                });
            }

            @Override
            public void onFailedToReceiveAd() {
                System.out.println("获取失败2333");
                //mNonStandardTm.loadAd(ConstantAd.TuiAAD.CUSTOM_AD_MY);
            }
        });

        mNonStandardTm.loadAd(ConstantAd.TuiAAD.CUSTOM_AD_MY);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNonStandardTm != null) {
            mNonStandardTm.destroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
        //刷新ui；
    }

    private void getUserInfo() {

        UserBean userBean = ((BaseActivity) getActivity()).getUserBean();
        if (userBean == null) {
            return;
        }

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ApiUserInfo.uid, userBean.getUserid());
        ApiUserInfo apiUserInfo = new ApiUserInfo(new HttpOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean userBean) {
                ((MyApplication) getActivity().getApplication()).updateUser(userBean);
                updateText(userBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }, ((RxAppCompatActivity) getActivity()), paramsMap);

        HttpManager.getInstance().doHttpDeal(apiUserInfo);
    }

    private void updateText(UserBean userBean) {

        if (userBean == null) {
            return;
        }

        String nickname= TextUtils.isEmpty(userBean.getNickname())?"0":userBean.getNickname();
        String money= TextUtils.isEmpty(userBean.getMoney())?"0":userBean.getMoney();
        String integral= TextUtils.isEmpty(userBean.getIntegral())?"0":userBean.getIntegral();
        String friend= TextUtils.isEmpty(userBean.getFriend())?"0":userBean.getFriend();

        mTvUsername.setText(nickname);    //昵称
        mTvWalletNumber.setText(money);   //余额
        mTvMoney.setText(integral);       //金币
        mTvFriednNumber.setText(friend);  //好友

        ImageUtils.loadImage(getActivity(), userBean.getAvatar(), mIvUserIcon);
    }

    //=========这里是header的模块；===========
    @OnClick(R.id.ll_ranking)
    public void onClickRanking() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                startActivity(new Intent(getActivity(), EmptyActivity.class));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    @OnClick(R.id.iv_icon_user_message)
    public void onClickMyMessage() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                startActivity(new Intent(getActivity(), MyMessageActivity.class));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    public final int requestExit = 2;

    @OnClick(R.id.iv_icon_user)
    public void onClickUserIcon() {


        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //这里是点击用户头像；
                startActivityForResult(new Intent(getActivity(), SettingActivity.class), requestExit);
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestExit && resultCode == Activity.RESULT_OK) {
            ((MainActivity) getActivity()).getTabhost().setCurrentTab(0);
        }
    }

    @OnClick(R.id.ll_user_sign)
    public void onClickUserSign() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //签到的模块；
                startActivity(new Intent(getActivity(), UserSignActivity.class));
            }

            @Override
            public void loginFaild() {

            }
        });
    }


    //这里表示钱包余额、我的金币、我的好友；
    @OnClick(R.id.ll_gold_wallet)
    public void onClickWallet() {


        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //收益明细的里面的钱包；
                startActivity(ProfitDetailActivity.getIntent(getActivity(), false));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    @OnClick(R.id.ll_get_money)
    public void onClickMoney() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                //这里是跳转到的收益明细里面的金币；
                startActivity(ProfitDetailActivity.getIntent(getActivity(), true));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    @OnClick(R.id.ll_my_friends)
    public void onClickMyFriends() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                startActivity(MyFriendsActivity.getIntent(getActivity(),false));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

    //==============邀请好友，我的团队========；
    @OnClick(R.id.ll_invite)
    public void onClickInvite() {
        startActivity(MyFriendsActivity.getIntent(getActivity(), true));
    }


    //=============这里是中间的任务大厅，拆红包、兑现提现；===================

    @OnClick(R.id.ll_task)
    public void onClickTask() {
        //任务模块
        startActivity(new Intent(getActivity(), UserTaskActivity.class));
    }

    //提现兑换；
    @OnClick(R.id.ll_withdrawals)
    public void onClickProfitDetail() {
        startActivity(new Intent(getActivity(), IntergralShopActivity.class));
    }

    @OnClick(R.id.ll_red_pack)
    public void onClickRedPacket() {
        //这里表示点击拆红包；
        startActivity(new Intent(getActivity(), UserTaskActivity.class));
    }


    //底部的四个模块：

    @OnClick(R.id.ll_collection)
    public void onClickUserCollection() {

        startActivity(new Intent(getActivity(), UserCollectionActivity.class));

    }

    @OnClick(R.id.ll_comment)
    public void onClickUserComment() {
        startActivity(new Intent(getActivity(), UserCommentActivity.class));
    }

    //联系客服的
    @OnClick(R.id.ll_contacts)
    public void onClickConstactUs() {
        startActivity(new Intent(getActivity(), ContactKeFuActivity.class));
    }


    @OnClick(R.id.ll_question)
    public void onClickQuestion() {
        startActivity(new Intent(getActivity(), QuestionsActivity.class));
    }

    @OnClick(R.id.ll_history)
    public void onClickBrowerRecord() {

        ((BaseActivity) getActivity()).checkLogin(new BaseActivity.CheckLoginCallBack() {
            @Override
            public void loginSuccess() {
                startActivity(new Intent(getActivity(), UserBrowerRecordsActivity.class));
            }

            @Override
            public void loginFaild() {

            }
        });
    }

}
