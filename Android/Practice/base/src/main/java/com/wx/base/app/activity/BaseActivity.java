package com.wx.base.app.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.wx.base.module.pagestate.PageStateController;
import com.wx.base.module.pagestate.StateView;
import com.wx.base.util.AppManager;
import com.wx.base.util.tools.Tools;


/**
 * Created by lenovo on 2016/1/3 0003.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG;
    protected Context mContext;
    private PageStateController mPageStateController;
    private Unbinder mUnbinder;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.log().d(getClass().getSimpleName() + "onCreate "+this);
        TAG = getClass().getName();
        this.mContext = this;
        initBundle();
        //生成视图
        View view = generateContentView(getLayoutRes());
        if (usePageStateController()) {
            //不同的状态页
            mPageStateController = new PageStateController(mContext);
            if (view != null) setContentView(mPageStateController.init(view));
        } else {
            if (view != null) setContentView(view);
        }
        //butterKnife
        mUnbinder = ButterKnife.bind(this);
        setup();
        initViews();
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.log().d(getClass().getSimpleName() + "onDestroy"+this);
        //解绑黄油刀
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (mPageStateController!=null && mPageStateController.getCurrentState() == StateView.PageState.Loading) {
            return;
        }
        super.onBackPressed();
    }

    protected abstract Integer getLayoutRes();

    protected void initBundle() {
    }

    protected void setup() {
    }

    protected void initViews() {
    }

    protected void initDatas() {
    }

    /**
     * 加载布局并返回视图
     *
     * @param layoutRes
     * @return
     */
    protected View generateContentView(Integer layoutRes) {
        return layoutRes == null ? null : getLayoutInflater().inflate(layoutRes, null);
    }

    /**
     * @return 是否使用页面切换控制器
     */
    protected boolean usePageStateController() {
        return false;
    }

    protected final PageStateController getPageStateController() {
        return mPageStateController;
    }

    protected void showDialog(DialogFragment dialog) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        //DialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
        dialog.show(ft, "dialog");
    }

    protected void dismissDiaog() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    /**
     * 设置状态栏透明,并且全屏
     * @return
     */
    protected void setStatusBarsTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }

}
