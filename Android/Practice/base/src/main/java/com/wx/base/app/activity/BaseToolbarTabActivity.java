package com.wx.base.app.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.wx.base.R;
import com.wx.base.entity.FragmentResource;
import com.wx.base.widget.viewpager.adapter.FragmentViewPagerAdapter;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


/**
 * Created by lenovo on 2015/8/3 0003.
 */
public class BaseToolbarTabActivity extends BaseToolbarActivity {

    private TabLayout tabLayout;
    protected ViewPager viewPager;
    private FragmentViewPagerAdapter adapter;

    @Override
    protected Integer getLayoutRes() {
        return R.layout.fragment_common_tab;
    }

    @CallSuper
    @Override
    protected void setup() {
        super.setup();
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

        //FragmentAdapter
        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        //ViewPager
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    public void updatePages(List<FragmentResource> resources) {
        viewPager.setOffscreenPageLimit(resources.size());
        tabLayout.setVisibility(resources.size() > 1 ? View.VISIBLE : View.GONE);
        adapter.updateAll(resources);
        if (resources.size() > 5) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

    }


}
