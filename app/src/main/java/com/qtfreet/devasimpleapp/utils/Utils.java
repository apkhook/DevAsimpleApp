package com.qtfreet.devasimpleapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.qtfreet.devasimpleapp.ui.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qtfreet00 on 2016/3/4.
 */
public class Utils {
    public static boolean isMobilePhone(String phone) {//
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static void showDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.getInstance().exit();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();


    }
}
