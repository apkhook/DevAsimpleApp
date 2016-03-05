package com.qtfreet.devasimpleapp.ui;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by qtfreet00 on 2016/3/3.
 */
public class App extends Application {
    private List<Activity> activityList = new LinkedList<Activity>();
    private static App instance;
    public static boolean ignore = false;

    public App() {
    }


    // 单例模式中获取唯一的MyApplication实例
    public static App getInstance() {
        if (null == instance) {
            instance = new App();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
}
