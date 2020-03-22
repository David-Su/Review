package com.wx.base.module.pagestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 状态视图布局
 * Created by alex on 17-1-4.
 */

public class PageStateLayout extends FrameLayout {
    //视图列表
    List<StateView> mStateviews;
    //当前页面视图状态
    StateView.PageState mCurrentState = StateView.PageState.Content;

    public PageStateLayout(Context context) {
        super(context);
        mStateviews = new ArrayList<>();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mCurrentState != StateView.PageState.Content || super.onInterceptTouchEvent(ev);
    }

    /**
     * 添加视图
     *
     * @param stateView 传进来的视图
     */
    public void addStateView(StateView stateView) {
        removeState(stateView.getState());
        mStateviews.add(stateView);
        this.addView(stateView.getView());
        changeState(stateView.getState());
    }


    /**
     * 更改视图，让符合状态的视图显示出来，其他的隐藏(content视图也会显示,但是被目标视图覆盖)
     *
     * @param state 视图的状态
     */
    public void changeState(StateView.PageState state) {
        if (!checkState(state)) {
            return;
        }
        for (StateView stateView : mStateviews) {
            if (stateView.getState() == state || stateView.getState() == StateView.PageState.Content) {
                stateView.getView().setVisibility(View.VISIBLE);
            } else {
                stateView.getView().setVisibility(View.GONE);
            }
        }
        this.mCurrentState = state;

    }

    /**
     * 获取当前页面视图状态
     *
     * @return
     */
    public StateView.PageState getCurrentState() {
        return mCurrentState;
    }

    /**
     * 移除状态
     *
     * @param state 状态
     */
    private void removeState(StateView.PageState state) {
        for (StateView stateView : mStateviews) {
            if (state == stateView.getState()) {
                //存在相同状态，替换
                removeStateView(stateView);
                return;
            }
        }
    }

    public void removeAllStateView() {
        for (StateView stateView : mStateviews) {
            this.removeView(stateView.getView());
        }
        mStateviews.clear();
    }

    /**
     * 移除视图
     *
     * @param stateView 视图
     */
    private void removeStateView(StateView stateView) {
        mStateviews.remove(stateView);
        this.removeView(stateView.getView());
    }

    /**
     * 判断当前视图列表里是否存在该视图
     *
     * @param state 状态
     * @return true 表示存在，false 表示不存在
     */
    private boolean checkState(StateView.PageState state) {
        for (StateView stateView : mStateviews) {
            if (stateView.getState() == state) {
                return true;
            }
        }
        return false;
    }
}
