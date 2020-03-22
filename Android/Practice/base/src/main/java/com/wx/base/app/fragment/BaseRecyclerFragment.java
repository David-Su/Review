package com.wx.base.app.fragment;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wx.base.R;
import com.wx.base.widget.recyclerView.adapter.BaseRecyclerViewAdapter;

//import com.wx.R;
//import com.wx.app.ui.component.adapter.recycler.product.ProductAdapter;
//import com.wx.base.adapter.BaseHeaderAdapter;
//import com.wx.base.adapter.BaseRecyclerViewAdapter;
//import com.wx.project.config.Constants;
//import com.wx.project.request.UICallBackInterface;
//import com.wx.support.android.widget.LoadingStateView;

/**
 * Created by lenovo on 2016/2/27 0027.
 */
public abstract class BaseRecyclerFragment<T extends FragmentActivity> extends BaseFragment<T> {


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private BaseRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_common_recyclerview;
    }

    @Override
    protected void setup() {
        super.setup();
        recyclerView = getView().findViewById(R.id.recycler);
        swipeLayout = getView().findViewById(R.id.swipeLayout);

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addItemDecoration(getItemDecoration());
        enableSwipe(false);
    }


    /**
     * 刷新数据回调
     */
    protected void requestData() {
    }

    /**
     * 重写方法自定义LayoutManager
     * @return
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        layoutManager = layoutManager == null ? new LinearLayoutManager(mContext) : layoutManager;
        return layoutManager;
    }

    /**
     * 重写方法自定义ItemDecoration
     * @return
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return itemDecoration == null ? itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL) : itemDecoration;
    }

    /**
     * 设置是否能下拉刷新以及下拉监听回调逻辑
     * @param enabled
     */
    protected final void enableSwipe(boolean enabled) {
        swipeLayout.setEnabled(enabled);
        if (enabled) {
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onRefresh: ");
                    requestData();
                }
            });
        } else {
            swipeLayout.setOnRefreshListener(null);
        }
    }

    /**
     * 设置是否显示加载状态
     * @param refreshing 是否
     */
    protected final void setSwipeRefresh(boolean refreshing) {
        swipeLayout.setRefreshing(refreshing);
    }

    protected final void setRecyclerAdapter(BaseRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    protected final BaseRecyclerViewAdapter getRecyclerAdapter() {
        return this.adapter;
    }



}
