package com.wx.base.app.activity;

import androidx.annotation.CallSuper;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wx.base.R;


/**
 * Created by lenovo on 2015/8/3 0003.
 */
public abstract class BaseToolbarActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTvTitle;
    private View.OnClickListener mNavigationOnClickListener;

    @Override
    protected final View generateContentView(Integer layoutRes) {
        View viewRoot = getLayoutInflater().inflate(R.layout.activity_base_toolbar, null);
        LinearLayout llContainer = viewRoot.findViewById(R.id.vg_base_toolbar_root);
        if (layoutRes != null) getLayoutInflater().inflate(layoutRes, llContainer);
//        llContainer.addView();
        return viewRoot;
    }

    @CallSuper
    @Override
    protected void setup() {
        super.setup();
        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);
        initToolBar(mToolbar);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 设置bar的标题
     *
     * @param titleRes
     */
    protected void setToolbarTitle(int titleRes) {
        setToolbarTitle(getString(titleRes));
    }

    protected void setToolbarTitle(String title) {
        mTvTitle.setText(title);
    }

    protected void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);//设置Toolbar
        getSupportActionBar().setHomeButtonEnabled(false);//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示NavigationIcon,这个方法是ActionBar的方法.Toolbar没有这个方法.
        getSupportActionBar().setDisplayShowTitleEnabled(false);//是否显示title
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNavigationOnClickListener != null) mNavigationOnClickListener.onClick(v);
                else finish();
            }
        });

    }

    protected void setNavigationOnClickListener(View.OnClickListener navigationOnClickListener) {
        mNavigationOnClickListener = navigationOnClickListener;
    }
}
