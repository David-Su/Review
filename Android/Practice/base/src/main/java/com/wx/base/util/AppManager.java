package com.wx.base.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.wx.base.util.tools.Tools;

import java.util.Stack;

/**
 * Author  Administer
 * Time    2017/12/5 17:54
 * Des     ${TODO}
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance = new AppManager();

    private AppManager() {
        activityStack = new Stack<>();
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public synchronized void addActivity(Activity activity) {
        if (activity != null) {
            if (activityStack.lastIndexOf(activity) != -1) {
                activityStack.remove(activity);
            }
            activityStack.push(activity);
        }
    }

    /**
     * 移除Activity到堆栈
     */
    public synchronized void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public synchronized Activity topActivity() {
        return activityStack.size() == 0 ? null : activityStack.lastElement();
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        finishActivity(activityStack.lastElement());
    }


    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityStack) {
            finishActivity(activity);
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
