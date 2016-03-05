package com.qtfreet.devasimpleapp.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSON;

import com.qtfreet.devasimpleapp.ui.App;
import com.qtfreet.devasimpleapp.update.bean.Apkinfo;
import com.qtfreet.devasimpleapp.update.bean.Constant;
import com.qtfreet.devasimpleapp.update.service.DownloadService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by qtfreet on 2016/1/5.
 */
public class CheckUpdate {

    //单例化检查更新类
    private CheckUpdate() {
    }

    private Context mcontext;
    private static CheckUpdate checkUpdate = null;


    public static CheckUpdate getInstance() {
        if (checkUpdate == null) {
            checkUpdate = new CheckUpdate();
        }
        return checkUpdate;
    }


    public void startCheck(Context context) {
        mcontext = context;
        if (App.ignore) {
            return;
        }
        OkHttpUtils//
                .get()//
                .url(Constant.APK_URL)//
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String s) {
                Message message = new Message();
                message.what = 0;
                message.obj = s;
                mhanler.sendMessage(message);
            }
        });


    }

    private Handler mhanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //使用fastjson解析json数据，注意写好bean
                    Apkinfo apkinfo = JSON.parseObject(msg.obj.toString(), Apkinfo.class);
                    compareVersion(apkinfo.getVersion(), apkinfo.getIntroduction(), apkinfo.getUrl()); //与本地版本进行比较
                    break;
            }
        }
    };

    private void compareVersion(int newVersion, String intro, final String url) {
        int versionCode = getVerCode(mcontext);

        if (newVersion > versionCode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("发现更新");
            builder.setMessage(intro);
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mcontext, DownloadService.class);
                    intent.putExtra("url", url);
                    mcontext.startService(intent);
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.ignore = true;
                }
            });
            builder.setCancelable(false).show();
        } else {
            return;
        }
    }


    private int getVerCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

}
