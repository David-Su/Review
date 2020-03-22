package com.wx.base.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.*;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.wx.base.module.pagestate.PageStateController;
import com.wx.base.util.tools.Tools;

/**
 * Created by lenovo on 2016/2/26 0026.
 */
public abstract class BaseFragment<T extends FragmentActivity> extends Fragment {
    protected String TAG = "wx.base:";
    protected Context mContext;
    protected T mActivity;
    private PageStateController mPageStateController;
    private Unbinder mUnbinder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.log().d(TAG + "  " + getClass().getSimpleName() + "onCreate");
        setRetainInstance(true);
        TAG = getClass().getName();
        this.mContext = this.getContext();//this.getActivity();
        this.mActivity = (T) this.getActivity();
        this.setHasOptionsMenu(true); //设置菜单可见
        initBundle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Tools.log().d(TAG + "  " + getClass().getSimpleName() + "onCreateView");
        View contentView = generateContentView(inflater, container, getLayoutRes());
        if (usePageStateController()) {
            mPageStateController = new PageStateController(mContext);
            contentView = mPageStateController.init(contentView);
        }
        mUnbinder = ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tools.log().d(TAG + "  " + getClass().getSimpleName() + "onViewCreated");
        setup();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Tools.log().d(TAG + "  " + getClass().getSimpleName() + "onActivityCreated");
        //swipeContainer.setEnabled(false);

        initViews();
        initDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Tools.log().d(TAG + "  " + getClass().getSimpleName() + "onDestroyView");
        mUnbinder.unbind();
        if (mPageStateController != null) mPageStateController.release();
    }

    protected abstract int getLayoutRes();

    protected void initBundle() {
    }


    protected void setup() {
    }

    protected void initViews() {
    }

    protected void initDatas() {
    }

    public boolean onBackPressed() {
        return false;
    }

    protected View generateContentView(LayoutInflater inflater, ViewGroup container, int layoutRes) {
        Tools.log().d(TAG + "  " + "generateContentView:" + getClass().getSimpleName());
        return inflater.inflate(layoutRes, container, false);
    }

    /**
     * @return 是否使用页面切换控制器
     */
    protected boolean usePageStateController() {
        return false;
    }

    public PageStateController getPageStateController() {
        return mPageStateController;
    }

    /**
     * @return 屏幕方向
     */
    protected int getOrientation() {
        return getResources().getConfiguration().orientation;
    }


    protected void finish() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    protected void showDialog(DialogFragment dialog) {
        FragmentManager fm;

        if (getParentFragment() == null) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.add(dialog, "dialog");
        ft.commitAllowingStateLoss();
    }

    protected void dismissDiaog() {
        FragmentManager fm;
        if (getParentFragment() == null) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        Fragment prev = fm.findFragmentByTag("dialog");

        Log.d("520", "prev" + prev);

        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            //            df.dismiss();
            //            df.dismissAllowingStateLoss();
            fm.beginTransaction().remove(df).commitNowAllowingStateLoss();
        }
    }

}
