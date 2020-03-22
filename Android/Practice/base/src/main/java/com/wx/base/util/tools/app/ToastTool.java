package com.wx.base.util.tools.app;

import android.content.Context;
import android.widget.Toast;

import com.wx.base.util.tools.log.LogTool;

/**
 * Created by chensy160803 on 2017-02-23 14:49.
 */

public class ToastTool {


    private static final Object lock = new Object();
    private static volatile ToastTool instance;
    private Toast toast = null;

    private ToastTool() {
    }

    public static ToastTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ToastTool();
                }
            }
        }
        return instance;
    }




    public void getShortToast(Context context,int retId){
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), retId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();
    }


    public void getShortToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), hint, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        toast.show();
    }


    public void getLongToast(Context context,int retId){
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), retId, Toast.LENGTH_LONG);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.show();
    }


    /**
     * 这里有大约4秒的防抖处理
     * @param context
     * @param hint
     */
    public void getLongToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), hint, Toast.LENGTH_LONG);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }
}
