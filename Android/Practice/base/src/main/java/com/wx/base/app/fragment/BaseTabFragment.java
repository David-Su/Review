package com.wx.base.app.fragment;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.CallSuper;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.wx.base.R;
import com.wx.base.widget.viewpager.adapter.FragmentViewPagerAdapter;
import com.wx.base.entity.FragmentResource;

import java.util.List;

/**
 * Created by alex on 16-12-8.
 */

public abstract class BaseTabFragment<T extends FragmentActivity> extends BaseFragment<T> {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentViewPagerAdapter adapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_common_tab;
    }

    @CallSuper
    @Override
    protected void setup() {
        tabLayout = getView().findViewById(R.id.tab_layout);
        viewPager = getView().findViewById(R.id.viewpager);

        //FragmentAdapter
        adapter = new FragmentViewPagerAdapter(getChildFragmentManager());
        //ViewPager
        viewPager.setAdapter(adapter);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void addPage(FragmentResource resource){
        adapter.add(resource);
    }

    public void updatePages(List<FragmentResource> resources) {
        updatePages(resources, 0);
    }

    public void updatePages(List<FragmentResource> resources, int selectIndex) {
        viewPager.setOffscreenPageLimit(resources.size() - 1);
        adapter.updateAll(resources);
        if (resources.size() > 5) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
//        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(selectIndex).select();
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public void setPageIndex(int pageIndex) {
        tabLayout.getTabAt(pageIndex).select();
    }
}
