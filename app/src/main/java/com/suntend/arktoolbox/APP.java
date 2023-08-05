package com.suntend.arktoolbox;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class APP  extends Application {
    public  static Activity currentActivity;
    public static Context appContext;
    private static ArrayList<OnUserChangeListener> userChangeListeners=new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        appContext=this;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    //TODO 非静态类使用时，请在合适时机调用remove
    public static void addUserChangeListener(OnUserChangeListener listener){
        userChangeListeners.add(listener);
    }
    public static void removeUserChangeListener(OnUserChangeListener listener){
        userChangeListeners.remove(listener);
    }

    //todo 通知用户信息更新,用户模块[登录\退出]时请调用此方法
    public static void notifyUserChange(String uid){
        for (int i = 0; i < userChangeListeners.size(); i++) {
            userChangeListeners.get(i).onChange(uid);
        }
    }

   public interface OnUserChangeListener{
        void onChange(String uid);
    }
}
