package com.wx.base.widget.viewpager.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.wx.base.entity.FragmentResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/8/12 0012.
 */
public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentResource> mResources = new ArrayList<FragmentResource>();

    public FragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public void add(FragmentResource fragment){
        this.mResources.add(fragment);
        notifyDataSetChanged();
    }

    public void addAll(List<FragmentResource> maps){
        this.mResources.addAll(maps);
        notifyDataSetChanged();
    }

    public void updateAll(List<FragmentResource> maps){
        this.mResources = maps;
        notifyDataSetChanged();
    }
    //================================================//



    @Override
    public Fragment getItem(int position) {
        FragmentResource resource = mResources.get(position);
        if(resource.getFragment() == null){
            return new Fragment();
        }

        // 如果有定制，可以根据

        return resource.getFragment();
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

	@Override
    public CharSequence getPageTitle(int position) {
        return mResources.get(position).getName();
    }

    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    //@Override
    //public void destroyItem(ViewGroup container, int position, Object object) {
    //    //super.destroyItem(container, position, object);
    //}
}
