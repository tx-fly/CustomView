package com.sunofbeach.customview;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class App extends Application {

    private static Handler mHandler = null;
    private static Context sContext = null;

    /**
     * 在这里创建拥有最长的生命周期
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
    public static Handler getHandler() {
        return mHandler;
    }
}
