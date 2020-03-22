package com.wx.base.module.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.wx.base.R;
import com.wx.base.util.tools.app.ToastTool;
import com.wx.base.util.tools.log.LogTool;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;


public class UpdateOperation {

    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    /**
     * 安装权限请求
     */
    private final int INSTALL_REQUEST = 5;

    private Context activity;
    /**
     * 从服务器中下载APK
     */
    private static File file;
    private String downUrl = "";

    /**
     * 初始化上下文
     *
     * @param activity
     */
    public UpdateOperation(Context activity) {
        this.activity = activity;
    }

    /**
     * 下载链接
     *
     * @param url
     */
    public void checkUpdate(String url) {
        try {
            CheckVersionTask cv = new CheckVersionTask(url);
            LogTool.instance().d("test " + "updates ");
            new Thread(cv).start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogTool.instance().d("错误 " + e.toString());
        }
    }


    /**
     * 下载线程
     */
    public class CheckVersionTask implements Runnable {

        /**
         * 下载地址
         */
        private final String url;

        public CheckVersionTask(String url) {
            this.url = url;
        }

        public void run() {
            try {
                /**准备发送消息下载**/
                Message msg = new Message();
                msg.what = UPDATA_CLIENT;
                msg.obj = url;
                handler.sendMessage(msg);
            } catch (Exception e) {
                /**捕捉错误**/
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }


    /**
     * 处理消息
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CLIENT:
                    //下载apk
                    downLoadApk((String) msg.obj);
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    ToastTool.instance().getLongToastByString(activity, "获取服务器更新信息失败");
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    ToastTool.instance().getLongToastByString(activity, "下载新版本失败");
                    break;
                case INSTALL_REQUEST:
                    //弹框提示用户手动打开
                    showAlert(activity, "安装权限", "需要打开允许来自此来源，请去设置中开启此权限", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LogTool.instance().d("showAlert go");
                                Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                                Activity c = (Activity) activity;
                                c.startActivityForResult(intent, 23);
                            }
                        }
                    });
                    break;
            }
        }
    };


    /**
     * 下载apk
     *
     * @param downUrl
     */
    protected void downLoadApk(final String downUrl) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(activity);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCancelable(false);
//        pd.setProgressPercentFormat(null); //不显示百分比
        pd.setProgressNumberFormat(" "); //不显示数字
        pd.show();
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    file = DownLoadManager.getFileFromServer(downUrl, pd);
                    sleep(500);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 准备安装apk
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void installApk(File file) {
        LogTool.instance().d("installApk");
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LogTool.instance().d("Android 8.0以上");
            //Android 8.0以上
            boolean haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //权限没有打开则提示用户去手动打开
                LogTool.instance().d("Android 8.0以上 权限没有打开");
                handler.sendEmptyMessage(INSTALL_REQUEST);
            } else {
                //权限已经打开
                LogTool.instance().d("Android 8.0以上 安装");
                install();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //Android8.0 以下  7.0或以上
            LogTool.instance().d("Android8.0 以下  7.0或以上");
            install();
        } else {
            //Android其他版本
            LogTool.instance().d("Android其他版本");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }

    }

    /**
     * 请求允许来自未知应用安装权限的提示框
     * 适配 Android 8.0 及以上
     *
     * @param context  上下文
     * @param title    标题
     * @param message  消息
     * @param listener 监听器
     */
    public static void showAlert(Context context, String title, String message, OnClickListener listener) {
        LogTool.instance().d("showAlert");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", listener);
        builder.setCancelable(false);
//        builder.setIcon(R.drawable.ic_bar_menu_more);//弹框的图标
        builder.show();
    }

    /**
     * 打开安装界面
     */
    public void install() {
        LogTool.instance().d("install");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(activity.getApplicationContext(), "com.gy.ssscada.fileprovider", file);
        activity.grantUriPermission(activity.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }


    /**
     * 静默安装apk
     *
     * @param apkAbsolutePath 安装应用的路径
     */
    protected void silentInstallApk(String apkAbsolutePath) {
        //初始化pm指令
        String[] args = {"pm", "install", "-r", apkAbsolutePath};
        //进程生成器，将指令封装成独立的进程，进行调用
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        //进程
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            //字节流，临时存储控制台的输出内容
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            //执行安装apk的pm指令
            process = processBuilder.start();
            //接收控制台输出的异常情况，输入流
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write("\r\n".getBytes());
            //正常输出
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            //将控制台输出转化为字符串返回
//            String result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//最后必须将所有输入输出流关闭
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    //执行shell命令
    protected int excuteSuCMD(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(
                    (OutputStream) process.getOutputStream());
            // 部分手机Root之后Library path 丢失，导入library path可解决该问题
            dos.writeBytes((String) "export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
            cmd = String.valueOf(cmd);
            dos.writeBytes((String) (cmd + "\n"));
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
            int result = process.exitValue();
            return (Integer) result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }
}
