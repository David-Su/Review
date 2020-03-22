package com.wx.base.widget.recyclerView.listener;

import android.view.View;

/**
 * @author SuK
 * @time 2019/3/12 11:18
 * @des
 */
public interface OnItemClickListener<T> {
    int TYPE_CLICK = 0;
    int TYPE_LONG_CLICK = 1;

    void onItemClick(View view, T item, int position, int eventType);

}
