package com.suntend.arktoolbox.application;

import android.support.multidex.MultiDexApplication;

import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;

/**
 * 程序初始化类
 */
public class AppApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        initDatabase();
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        //明日方舟数据库
        ArkToolDatabaseHelper.getInstance(this);
    }
}
