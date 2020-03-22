package com.wx.base.widget.recyclerView.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.wx.base.widget.recyclerView.holder.BaseHolder;
import com.wx.base.widget.recyclerView.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2015/8/5 0005.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {//RecyclerView.Adapter<VH>{

    protected Context mContext;

    protected List<T> items = new ArrayList<>();

    protected OnItemClickListener<T> listener;


    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }

    public List<T> getItems() {
        return this.items;
    }

    public T getItem(int position) {
        return this.items.get(position);
    }

    public void addHead(T item) {
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    public void addPos(int pos, T item) {
        this.items.add(pos, item);
        notifyItemInserted(pos);
    }

    public void add(T item) {
        this.items.add(item);
        notifyItemInserted(items.size());
    }

    public void addAllHead(List<T> items) {
        this.items.addAll(0, items);
        notifyDataSetChanged();
    }

    public void addAllPos(int pos, List<T> items) {
        this.items.addAll(pos, items);
    }

    public void addAll(List<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void updateAll(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void remove(int i) {
        this.items.remove(i);
        notifyItemRemoved(i);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

}
