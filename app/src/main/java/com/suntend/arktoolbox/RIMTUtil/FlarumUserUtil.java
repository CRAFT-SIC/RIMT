package com.suntend.arktoolbox.RIMTUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class FlarumUserUtil {
    private static final String SETTINGS_PREFERENCES_NAME = "FlarumUser";
    private static SharedPreferences FlarumUsers;

    public static String getUser(Context context, String param) {
        return getSettingsPreferences(context).getString(param, "0");
    }

    public static void setUser(Context context, String param, String value) {
        getSettingsPreferences(context).edit().putString(param, value).apply();
    }

    public static void Logout(Context context) {
        getSettingsPreferences(context).edit().clear().apply();
    }

    private static SharedPreferences getSettingsPreferences(Context context) {
        if (FlarumUsers == null) {
            FlarumUsers = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return FlarumUsers;
    }
}