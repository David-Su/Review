package com.wx.base.module.update;

import android.app.ProgressDialog;
import android.os.Environment;


import com.wx.base.util.tools.Tools;
import com.wx.base.util.tools.log.LogTool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownLoadManager {


    /**
     * 获取下载文件大小
     *
     * @param path 文件地址
     * @return
     * @throws Exception
     */
    public static void getFileSize(final String path) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    URL url = null;
                    try {
                        url = new URL(path);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        //获取到文件的大小
                        int size = conn.getContentLength() / 1024 / 1024; //单位mb
                        LogTool.instance().d(" size " + size);
                        /**弹出升级提示框**/
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    /**
     * 从服务器下载apk
     *
     * @param path 下载路径
     * @param pd   进度条控件
     * @return
     * @throws Exception
     */
    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            int length = conn.getContentLength();
            pd.setMax(length);
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "update.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);

            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }


    /**
     * 从服务器下载apk(不带进度条)
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static File getFileFromServer(String path) {
        try {
            //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);
                InputStream is = conn.getInputStream();
                File file = new File(Environment.getExternalStorageDirectory(), "update.apk");
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                bis.close();
                is.close();
                return file;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

}
