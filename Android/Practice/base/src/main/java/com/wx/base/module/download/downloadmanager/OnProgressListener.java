package com.wx.base.module.download.downloadmanager;

import android.net.Uri;

public interface OnProgressListener {

    void onProgress(int currentLength,int maxLength);

    void onComplete(Uri fileUri);

    void onFail(int error);
}
