package com.wx.base.widget.viewpager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wx.base.entity.FragmentResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/8/12 0012.
 */
public class FragmentViewPager2Adapter extends FragmentStateAdapter {

    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public FragmentViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }


    public void add(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    public void addAll(List<Fragment> fragments) {
        mFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    public void updateAll(List<Fragment> fragments) {
        mFragments = fragments;
        notifyDataSetChanged();
    }
    //================================================//

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

}
