package com.wx.base.module.pagestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.wx.base.R;

/**
 * 页面状态控制器
 * Created by alex on 17-1-4.
 */

public class PageStateController {
    private PageStateLayout mPageStateLayout;
    private LayoutInflater mInflater;

    public PageStateController(Context context) {
        mPageStateLayout = new PageStateLayout(context);
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 初始化视图，设置当前视图和状态
     *
     * @param root 传进来的视图
     * @return 返回界面布局
     */
    public View init(View root) {
        StateView contentStateView = new StateView();
        //添加主视图
        contentStateView.setState(StateView.PageState.Content);
        contentStateView.setView(root);
        mPageStateLayout.addStateView(contentStateView);
        //添加子视图
        useDefaultPageState();
        //显示主视图
        mPageStateLayout.changeState(StateView.PageState.Content);
        return mPageStateLayout;
    }

    /**
     * 释放所引用的view,调用之后要再次使用需要重新调用init(),或者重新初始化新的对象再调用init()
     */
    public void release(){
        mPageStateLayout.removeAllStateView();
        mPageStateLayout = null;
    }

    /**
     * 更改页面状态
     *
     * @param state 状态
     */
    public void changeState(StateView.PageState state) {
        mPageStateLayout.changeState(state);
    }

    /**
     * 往当前的页面控制器添加页面状态
     *
     * @param state
     */
    public void addState(StateView state) {
        mPageStateLayout.addStateView(state);
    }

    /**
     * 获取当前页面的状态
     *
     * @return
     */
    public StateView.PageState getCurrentState() {
        return mPageStateLayout.getCurrentState();
    }

    /**
     * 添加页面状态
     * <p><strong>目前默认添加:</strong> 加载中、内容为空时 两种视图：<p/>
     */
    private void useDefaultPageState() {
        mPageStateLayout.addStateView(getLoadingStateView());
        mPageStateLayout.addStateView(getEmptyStateView());
    }

    /**
     * 获取加载中的视图
     *
     * @return 视图
     */
    private StateView getLoadingStateView() {
        StateView stateView = new StateView();
        //设置加载中的状态
        stateView.setState(StateView.PageState.Loading);
        //加载菊花转布局
        stateView.setView(mInflater.inflate(R.layout.layout_loading, mPageStateLayout, false));
        return stateView;
    }

    /**
     * 获取内容为空的视图
     *
     * @return 视图
     */
    private StateView getEmptyStateView() {
        StateView stateView = new StateView();
        //设置空状态
        stateView.setState(StateView.PageState.Empty);
        //加载空视图时的布局
        stateView.setView(mInflater.inflate(R.layout.layout_empty, mPageStateLayout, false));
        return stateView;
    }

}
