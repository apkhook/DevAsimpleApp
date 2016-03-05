package com.qtfreet.devasimpleapp.utils;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qtfreet00 on 2016/3/4.
 */
public class SMSContentObserver extends ContentObserver {
    public final String SMS_URI_INBOX = "content://sms/inbox";//收信箱
    private Activity activity = null;
    private String smsContent = "";//验证码
    private EditText verifyText = null;//验证码编辑框
    private String SMS_ADDRESS_PRNUMBER = "1069003200004";//短息发送提供商
    private String smsID = "";//短信观察者 收到一条短信时 onchange方法会执行两次，所以比较短信id，如果一致则不处理

    public SMSContentObserver(Activity activity, Handler handler, EditText verifyText) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);


        Cursor cursor = null;// 光标
        // 读取收件箱中指定号码的短信
        cursor = activity.getContentResolver().query(Uri.parse(SMS_URI_INBOX),
                new String[]{"_id", "address", "body", "read"}, //要读取的属性
                "address=? and read=?", //查询条件是什么
                new String[]{SMS_ADDRESS_PRNUMBER, "0"},//查询条件赋值
                "date desc");//排序
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                //比较和上次接收到短信的ID是否相等
                if (!smsID.equals(cursor.getString(cursor.getColumnIndex("_id")))) {

                    String smsbody = cursor.getString(cursor.getColumnIndex("body"));

                    //用正则表达式匹配验证码
                    Pattern pattern = Pattern.compile("[0-9]{4}");
                    Matcher matcher = pattern.matcher(smsbody);
                    if (matcher.find()) {//匹配到n位的验证码
                        smsContent = matcher.group();
                        if (verifyText != null && null != smsContent && !"".equals(smsContent)) {
                            verifyText.requestFocus();//获取焦点
                            verifyText.setText(smsContent);//设置文本
                            verifyText.setSelection(smsContent.length());//设置光标位置
                        }
                    }

                    smsID = cursor.getString(cursor.getColumnIndex("_id"));
                }

            }
        }


    }
}