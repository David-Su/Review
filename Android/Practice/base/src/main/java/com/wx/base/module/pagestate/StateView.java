package com.wx.base.module.pagestate;

import android.view.View;

/**
 * 状态视图
 * Created by alex on 17-1-7.
 */

public class StateView {
    /**
     * 各种状态枚举
     */
    public enum PageState {
        /**
         *  加载中
         */
        Loading,
        /**
         * 重试
         */
        Retry,
        /**
         * 内容
         */
        Content,
        /**
         * 错误
         */
        Error,
        /**
         * 空视图
         */
        Empty
    }

    //状态
    private PageState state;
    //视图
    private View view;

    public PageState getState() {
        return state;
    }

    public void setState(PageState state) {
        this.state = state;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
