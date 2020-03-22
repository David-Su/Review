package com.wx.base.widget.recyclerView.holder;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wx.base.R;
import com.wx.base.widget.recyclerView.adapter.BaseRecyclerViewAdapter;
import com.wx.base.widget.recyclerView.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lenovo on 2015/8/27 0027.
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    protected final String TAG = "wx.ui.holder.base";
    protected Context mContext;
    protected OnItemClickListener<T> listener = null;
    protected int type;
    private List<View> receiveClickEventViews;
    private List<View> receiveLongClickEventViews;

    public BaseHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public BaseHolder(View itemView, OnItemClickListener<T> listener) {
        this(itemView);
        this.listener = listener;
    }

    public BaseHolder(ViewGroup parent, int resid) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(resid, parent, false));
    }

    //SuK ++
    public BaseHolder(ViewGroup parent, int resid, OnItemClickListener<T> listener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(resid, parent, false), listener);
    }


    public void setRespondClickView(View... views) {
        Log.d("520", "views:" + views);
        if (receiveClickEventViews == null) {
            receiveClickEventViews = new ArrayList<>();
        } else {
            receiveClickEventViews.clear();
        }
        if (views != null && views.length > 0) {
            for (View view : views) {
                view.setOnClickListener(this);
                receiveClickEventViews.add(view);
            }
        }
    }

    public void setRespondLongClickView(View... views) {
        if (receiveLongClickEventViews == null) {
            receiveLongClickEventViews = new ArrayList<>();
        } else {
            receiveLongClickEventViews.clear();
        }
        if (views != null && views.length > 0) {
            for (View view : views) {
                view.setOnLongClickListener(this);
                receiveLongClickEventViews.add(view);
            }
        }
    }


    @Override
    public void onClick(View v) {
        T entity = (T) v.getTag(R.id.tag_entity);
        Object tag = v.getTag(R.id.tag_position);
        int position = tag == null ? -1 : (int) tag;
        onClick(v, entity, position);
        if (listener != null)
            listener.onItemClick(v, entity, position, OnItemClickListener.TYPE_CLICK);
    }

    @Override
    public boolean onLongClick(View v) {
        T entity = (T) v.getTag(R.id.tag_entity);
        Object tag = v.getTag(R.id.tag_position);
        int position = tag == null ? -1 : (int) tag;
        onLongClick(v, entity, position);
        if (listener != null)
            listener.onItemClick(v, entity, position, OnItemClickListener.TYPE_LONG_CLICK);
        return false;
    }

    protected void onClick(View v, T entity, int position) {
    }

    protected void onLongClick(View v, T entity, int position) {
    }

    @CallSuper
    public boolean bindViewHolder(T entity, int position) {
        setTag(receiveClickEventViews, entity, position);
        setTag(receiveLongClickEventViews, entity, position);
        return false;
    }

    private void setTag(List<View> views, T entity, int position) {
        if (views != null) {
            for (View view : views) {
                view.setTag(R.id.tag_position, position);
                view.setTag(R.id.tag_entity, entity);
            }
        }
    }

}
