package com.procuratorate.app;

import android.app.Application;
import android.content.Context;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Qing on 2016/8/1.
 */
public class CheckApplication extends Application{
    private static Context sContext = null;
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        x.Ext.init(this);
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
