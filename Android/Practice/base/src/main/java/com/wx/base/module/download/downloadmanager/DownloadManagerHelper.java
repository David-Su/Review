package com.wx.base.module.download.downloadmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.crash.BuglyBroadcastReceiver;

import java.io.File;
import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;

public class DownloadManagerHelper {

    public static final int ERROR_NO_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static final int ERROR_DOWNLOAD_FAIL = 2;

    //    private WeakReference<OnProgressListener> mOnProgressListenerSr;
    private OnProgressListener mOnProgressListener;
    private DownloadManager mDownloadManager;
    private final DownloadManager.Request mRequest;
    private String mAuthority;
    private String mFilePath;
    private boolean mAutoOpen;
    private Long mDownloadId;

    private DownloadManagerHelper(Builder builder) {
//        mOnProgressListenerSr = new WeakReference<>(builder.mOnProgressListener);
        mOnProgressListener = builder.mOnProgressListener;
        mAuthority = builder.mAuthority;
        mFilePath = builder.mFilePath;
        mAutoOpen = builder.mAutoOpen;
        mRequest = new DownloadManager
                .Request(Uri.parse(builder.mUrl))
                .setAllowedNetworkTypes(builder.mJustWifi ? DownloadManager.Request.NETWORK_WIFI : ~0) // ~0全部类型
                .setNotificationVisibility(builder.mShowNotification ? DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED : DownloadManager.Request.VISIBILITY_HIDDEN)
                .setTitle(builder.mTitle)
                .setVisibleInDownloadsUi(true)
                .setDestinationUri(Uri.fromFile(new File(builder.mFilePath)))
                .setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(builder.mUrl)));
    }

    public void enqueue(final FragmentActivity activity) {
        enqueue(activity, activity.getLifecycle());
    }

    public void enqueue(final Fragment fragment) {
        enqueue(fragment.getActivity(), fragment.getLifecycle());
    }

    @SuppressLint("CheckResult")
    private void enqueue(final FragmentActivity activity, final Lifecycle lifecycle) {

        mDownloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        new RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean granted) throws Exception {
                if (granted) {
                    //先删除已有文件
                    File targetFile = new File(mFilePath);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    //如果有任务先移除之前的任务
                    if (mDownloadId != null) {
                        mDownloadManager.remove(mDownloadId);
                    }
                    mDownloadId = mDownloadManager.enqueue(mRequest);
                    //DownloadManager会将下载的状态文件大小等信息写入数据库,通过ContentObserver监听数据库变化
                    final ContentObserver contentObserver;
                    final DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
                    activity.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                        @Override
                        public void onChange(boolean selfChange) {
                            super.onChange(selfChange);
                            Log.d("onChange", "onChange: ");
                            Cursor cursor = null;
                            try {
                                cursor = mDownloadManager.query(query);
                                if (cursor != null && cursor.moveToFirst()) {
                                    //已经下载文件大小
                                    int currentSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    //下载文件的总大小
                                    int totalSize = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    //下载状态
                                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                                    if (mOnProgressListener != null) {
                                        switch (status) {
                                            case DownloadManager.STATUS_FAILED:
                                                mOnProgressListener.onFail(ERROR_DOWNLOAD_FAIL);
                                                mOnProgressListener = null;
                                                return;
                                        }
                                        mOnProgressListener.onProgress(currentSize, totalSize);
                                    }
                                }
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }

                            }
                        }
                    });
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
                    //注册接收完成和点击通知的广播
                    final BroadcastReceiver broadcastReceiver;
                    activity.registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            switch (intent.getAction()) {
                                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                                    Uri downLoadFileUri = mDownloadId != null ? mDownloadManager.getUriForDownloadedFile(mDownloadId) : null;
                                    if (mAutoOpen && downLoadFileUri != null) {
                                        openFile(context, downLoadFileUri);
                                    }
                                    if (mOnProgressListener != null) {
                                        mOnProgressListener.onComplete(downLoadFileUri);
                                    }
                                    release(context, contentObserver, this);
                                    break;
                            }
                        }
                    }, intentFilter);
                    //activity生命周期结束,释放资源
                    lifecycle.addObserver(new DefaultLifecycleObserver() {

                        @Override
                        public void onCreate(@NonNull LifecycleOwner owner) {
                        }

                        @Override
                        public void onStart(@NonNull LifecycleOwner owner) {
                        }

                        @Override
                        public void onResume(@NonNull LifecycleOwner owner) {
                        }

                        @Override
                        public void onPause(@NonNull LifecycleOwner owner) {
                        }

                        @Override
                        public void onStop(@NonNull LifecycleOwner owner) {
                        }

                        @Override
                        public void onDestroy(@NonNull LifecycleOwner owner) {
                            Log.d("activity", "onDestroy: ");
                            release(activity, contentObserver, broadcastReceiver);
                        }
                    });
                } else {
                    if (mOnProgressListener != null)
                        mOnProgressListener.onFail(ERROR_NO_WRITE_EXTERNAL_STORAGE_PERMISSION);
                }
            }
        });
    }

    public void cancel() {
        if (mDownloadId != null) {
            long downloadId = mDownloadId;
            mDownloadId = null;
            mDownloadManager.remove(downloadId);
        }
    }

    private void release(Context context, ContentObserver contentObserver, BroadcastReceiver broadcastReceiver) {
        try {
            mOnProgressListener = null;
            context.getContentResolver().unregisterContentObserver(contentObserver);
            context.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(Context context, Uri downLoadFileUri) {
        String mimeType = mDownloadManager.getMimeTypeForDownloadedFile(mDownloadId);
//        Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? FileProvider.getUriForFile(context, mAuthority, file) : Uri.fromFile(file);
        //打开文件界面
//        context.startActivity(new Intent(Intent.ACTION_VIEW)
        context.startActivity(new Intent(Intent.ACTION_INSTALL_PACKAGE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(downLoadFileUri, mimeType)
        );
        //未知来源应用安装权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && "application/vnd.android.package-archive".equals(mimeType)) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                //打开这个设置界面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public static class Builder {
        private String mUrl;
        private String mFilePath;
        private String mTitle;
        private String mAuthority;
        private boolean mShowNotification = false;
        private boolean mJustWifi = false;
        private boolean mAutoOpen = false;
        private OnProgressListener mOnProgressListener;

        /**
         * @param url
         * @param filePath 只能为External的路径
         */
        public Builder(String url, String filePath) {
            mUrl = url;
            mFilePath = filePath;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setAuthority(String authority) {
            mAuthority = authority;
            return this;
        }

        public Builder setShowNotification(boolean showNotification) {
            mShowNotification = showNotification;
            return this;
        }

        public Builder setJustWifi(boolean justWifi) {
            mJustWifi = justWifi;
            return this;
        }

        public Builder setAutoOpen(boolean autoOpen) {
            mAutoOpen = autoOpen;
            return this;
        }

        public Builder setOnProgressListener(OnProgressListener onProgressListener) {
            mOnProgressListener = onProgressListener;
            return this;
        }

        public DownloadManagerHelper build() {
            return new DownloadManagerHelper(this);
        }
    }
}
