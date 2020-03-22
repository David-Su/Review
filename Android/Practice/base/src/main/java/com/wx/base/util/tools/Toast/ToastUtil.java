package com.wx.base.util.tools.Toast;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chensy160803 on 2017-02-23 14:49.
 */

public class ToastUtil {


    private static final Object lock = new Object();
    private static volatile ToastUtil instance;
    private Toast toast = null;

    private ToastUtil() {
    }

    public static ToastUtil instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ToastUtil();
                }
            }
        }
        return instance;
    }




    public void getShortToast(Context context,int retId){
        if (toast == null) {
            toast = Toast.makeText(context, retId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }


        toast.show();
    }


    public void getShortToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_SHORT);
        }


        toast.show();
    }


    public void getLongToast(Context context,int retId){
        if (toast == null) {
            toast = Toast.makeText(context, retId, Toast.LENGTH_LONG);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_LONG);
        }


        toast.show();
    }


    public void getLongToastByString(Context context,String hint){
        if (toast == null) {
            toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.show();
    }
}
