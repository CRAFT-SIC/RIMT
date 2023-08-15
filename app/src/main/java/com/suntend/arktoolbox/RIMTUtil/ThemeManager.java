package com.suntend.arktoolbox.RIMTUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.suntend.arktoolbox.R;

public class ThemeManager {
    private static final String SETTINGS_PREFERENCES_NAME = "ThemeManager";
    private static SharedPreferences ThemeManager;
    public static void setTheme(Context context, int themeId) {
        getSettingsPreferences(context).edit().putInt("ThemeSelected", themeId).apply();
    }

    public static void setThemeID(Context context, int themeId) {
        getSettingsPreferences(context).edit().putInt("ThemeSelectedID", themeId).apply();
    }

    public static int getThemeID(Context context) {
        return getSettingsPreferences(context).getInt("ThemeSelectedID", 1);
    }

    public static int getTheme(Context context) {
        return getSettingsPreferences(context).getInt("ThemeSelected",0);
    }

    public static void checkTheme(Context context) {
        if (getSettingsPreferences(context).getInt("ThemeSelected", -1) == -1) {
            getSettingsPreferences(context).edit().putInt("ThemeSelected", R.style.Base_Theme_Ark_Kaltsit).apply();
        }
    }

    public static void restartActivity(Activity activity) {
        Intent intent = new Intent(activity, activity.getClass());
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0); // 移除过渡动画
    }

    private static SharedPreferences getSettingsPreferences(Context context) {
        if (ThemeManager == null) {
            ThemeManager = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return ThemeManager;
    }
}