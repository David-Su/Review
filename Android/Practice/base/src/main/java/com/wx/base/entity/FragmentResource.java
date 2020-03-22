package com.wx.base.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

import androidx.fragment.app.Fragment;

/**
 * Created by lenovo on 2015/8/12 0012.
 */
public class FragmentResource implements Serializable {

    protected int icon;
    protected Drawable iconDrawable;
    protected String name;          //资源名称
    protected Fragment fragment;    //资源Fragment

    public FragmentResource(String name, Fragment fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public FragmentResource(String name, int icon, Fragment fragment) {
        this(name, fragment);
        this.icon = icon;
    }

    public FragmentResource(String name, Drawable iconDrawable, Fragment fragment) {
        this.iconDrawable = iconDrawable;
        this.name = name;
        this.fragment = fragment;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
