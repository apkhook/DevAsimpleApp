package com.qtfreet.devasimpleapp.update.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.qtfreet.devasimpleapp.R;
import com.qtfreet.devasimpleapp.update.util.StorageUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by qtfreet on 2016/1/6.
 */
public class DownloadService extends IntentService {
    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    int oldprogress = 0;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Builder(this);
        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;
        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String apkurl = intent.getStringExtra("url");
        final String apkName = apkurl.substring(apkurl.lastIndexOf("/") + 1, apkurl.length());
        final String path = StorageUtils.getCacheDirectory(this).getPath();
        //使用Okhttp下载文件，抛弃之前的httpUrlConnetction，提升效率
        OkHttpUtils.get().url(apkurl).build().execute(new FileCallBack(path, apkName) {
            @Override
            public void inProgress(float progress, long total) {
                int p = (int) (100 * progress);
                if (p == oldprogress) {
                    //当进度一样时不更新通知栏，避免过度操作卡顿
                } else {
                    updateProgress(p);
                }
                oldprogress = p;
                Log.e("TAG", p + "");
            }

            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(File response) {
                Log.e("TAG", "onResponse :" + response.getAbsolutePath());
                mBuilder.setContentText(getString(R.string.download_success)).setProgress(0, 0, false);
                mNotifyManager.cancelAll();
                //直接清除通知栏状态
                Intent installAPKIntent = new Intent(Intent.ACTION_VIEW);
                installAPKIntent.setDataAndType(Uri.fromFile(new File(path, apkName)), "application/vnd.android.package-archive");
                installAPKIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installAPKIntent);
                //弹出安装界面
            }
        });

    }

    private void updateProgress(int progress) {
        mBuilder.setContentText(this.getString(R.string.download_progress, progress)).setProgress(100, progress, false);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(0, mBuilder.build());
    }

}
