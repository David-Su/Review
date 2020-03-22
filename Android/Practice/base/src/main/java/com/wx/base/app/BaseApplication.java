package com.wx.base.app;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.wx.base.BuildConfig;
import com.wx.base.util.AppManager;
import com.wx.base.util.tools.Tools;

import androidx.multidex.MultiDexApplication;

/**
 * Created by alex on 16-11-15.
 */

public class BaseApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        //工具类初始化
        Tools.init(this);
        if (BuildConfig.DEBUG) {
            //stetho网页调试工具
//            Stetho.initializeWithDefaults(this);
            //内存泄漏监控初始化
//            LeakCanary.install(this);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        AppManager.getInstance().addActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getInstance().removeActivity(activity);
    }
}
