package com.duocai.caomeitoutiao.ui.view.indicator;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.duocai.caomeitoutiao.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dinosa on 2018/2/2.
 *
 * 这里是home的ViewPager里面的放大和缩小的MargicIndicator的Adapter
 */

public class HomeTabIndicatorAdapter extends CommonNavigatorAdapter {

    public List<String> mTabsTitle;
    Context mContext;
    ViewPager mViewPager;

    public HomeTabIndicatorAdapter(List<String> tabsTitle, Context context, ViewPager viewPager) {
        mTabsTitle = tabsTitle==null? new ArrayList<String>():tabsTitle;
        mContext = context;
        mViewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mTabsTitle.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setText(mTabsTitle.get(index));
        simplePagerTitleView.setTextSize(18.5f);
        simplePagerTitleView.setNormalColor(Color.parseColor("#535353"));
        simplePagerTitleView.setSelectedColor(mContext.getResources().getColor(R.color.base_color_orange));

        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setColors(mContext.getResources().getColor(R.color.base_color_orange));
        return indicator;
    }

    public void refreshData(List<String> data){
        mTabsTitle.clear();
        mTabsTitle.addAll(data);
        notifyDataSetChanged();
    }
}
