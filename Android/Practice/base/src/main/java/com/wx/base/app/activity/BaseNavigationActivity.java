package com.wx.base.app.activity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.wx.base.R;
import com.wx.base.entity.FragmentResource;
import com.wx.base.widget.viewpager.CustomViewPager;
import com.wx.base.widget.viewpager.adapter.FragmentViewPagerAdapter;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class BaseNavigationActivity extends BaseToolbarActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;
    private FragmentViewPagerAdapter mViewPagerAdapter;
    private CustomViewPager mViewPager;


    @Override
    protected Integer getLayoutRes() {
        return R.layout.activity_base_bottom_tab;
    }

    @CallSuper
    @Override
    protected void setup() {
        super.setup();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav);
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager()));

        if (userDrawer(mDrawerLayout, mNavigationView)) {
            //toolbar增加打开关闭抽屉按钮
            initActionBarDrawerToggle(mDrawerLayout);
        } else {
            //不使用抽屉(锁定)
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    protected void initActionBarDrawerToggle(DrawerLayout drawerLayout) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    protected boolean userDrawer(DrawerLayout drawerLayout, NavigationView navigationView) {
        return false;
    }

    protected final void updatePages(final List<FragmentResource> resources) {
        updatePages(resources, false, resources.size()-1);
    }

    protected void updatePages(final List<FragmentResource> resources, boolean canScroll, int preLoadSize) {
        //初始化标题
        setToolbarTitle(resources.size() > 0 ? resources.get(0).getName() : "");
        //ViewPager预加载
        mViewPager.setOffscreenPageLimit(preLoadSize);
        //设置Viewpager能否滑动
        mViewPager.setCanScroll(canScroll);
        //Viewpager刷新数据
        mViewPagerAdapter.addAll(resources);
        //BottomNavigationView添加item
        mBottomNavigationView.getMenu().clear();
        for (FragmentResource resource : resources) {
            if (resource.getIconDrawable() != null) {
                mBottomNavigationView.getMenu().add(resource.getName()).setIcon(resource.getIconDrawable());
            } else {
                mBottomNavigationView.getMenu().add(resource.getName()).setIcon(resource.getIcon());
            }
        }
        //BottomNavigationView添加选择监听,选择时切换ViewPager
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                for (int i = 0; i < resources.size(); i++) {
                    if (mBottomNavigationView.getMenu().getItem(i) == menuItem) {
                        mViewPager.setCurrentItem(i);
                        return true;
                    }
                }
                return false;
            }
        });
        //ViewPager添加滑动监听,切换时选择BottomNavigationView的对应item
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                MenuItem menuItem = mBottomNavigationView.getMenu().getItem(position);
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                }
                setToolbarTitle(resources.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public BottomNavigationView getBottomNavigationView() {
        return mBottomNavigationView;
    }

    public CustomViewPager getViewPager() {
        return mViewPager;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }
}
