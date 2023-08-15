package com.suntend.arktoolbox.RIMTUtil;
/*
 * JamXi
 */
import android.content.Context;
import android.content.SharedPreferences;

public class DataExchange {
    private static final String SETTINGS_PREFERENCES_NAME = "Settings";
    private static SharedPreferences SettingsPreferences;
    public int GameSelected;
    public int NavSelected;
    public void setNavSelected(int selectid){
        NavSelected = selectid;
    }
    public int getGameSelected(){
        return GameSelected;
    }
    public void setGameSelected(int selectid){
        GameSelected = selectid;
    }
    public int getNavSelected(){
        return NavSelected;
    }

    public void toggleAutoUpdate(Context context) {
        // 获取自动更新设置的值
        int currentStatus = getAutoUpdateStatus(context);
        // 取反自动更新设置的值，并保存到SharedPreferences中
        setAutoUpdateStatus(context, currentStatus == 0 ? 1 : 0);
    }

    public int getAutoUpdateStatus(Context context) {
        return getSettingsPreferences(context).getInt("AutoUpdate", 0);
    }

    public void setAutoUpdateStatus(Context context, int status) {
        getSettingsPreferences(context).edit().putInt("AutoUpdate", status).apply();
    }

    private SharedPreferences getSettingsPreferences(Context context) {
        if (SettingsPreferences == null) {
            SettingsPreferences = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return SettingsPreferences;
    }

}
