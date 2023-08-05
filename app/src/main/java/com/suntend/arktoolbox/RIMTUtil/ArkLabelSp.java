package com.suntend.arktoolbox.RIMTUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suntend.arktoolbox.APP;
import com.suntend.arktoolbox.arklabel.ArkLabelEntity;

import java.util.ArrayList;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ArkLabelSp {
    private ArkLabelSp() {
    }

    private static volatile ArkLabelSp instance;
    private SharedPreferences preferences;

    private static final String name = "ark_label";
    private final String key_list_json = "key_list_json";
    private String uid_suffix = "";
    private ArrayList<OnArkLabelChangeListener> arkLabelChangeListeners = new ArrayList<>();

    public static ArkLabelSp getInstance() {
        if (instance == null) {
            synchronized (ArkLabelSp.class) {
                if (instance == null) {
                    instance = new ArkLabelSp();
                    instance.preferences = APP.appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
                    APP.addUserChangeListener(new APP.OnUserChangeListener() {
                        @Override
                        public void onChange(String uid) {
                            instance.uid_suffix = uid;
                        }
                    });
                }
            }
        }
        return instance;
    }

    public void saveLabelList(ArrayList<ArkLabelEntity> list) {
        preferences.edit().putString(key_list_json + uid_suffix, new Gson().toJson(list)).apply();
    }

    public void loadLabelList() {
        String value = preferences.getString(key_list_json + uid_suffix, "");
        if (!value.isEmpty()) {
            ArrayList<ArkLabelEntity> list = new Gson().fromJson(value, new TypeToken<ArrayList<ArkLabelEntity>>() {
            }.getType());
            for (int i = 0; i < arkLabelChangeListeners.size(); i++) {
                arkLabelChangeListeners.get(i).onChange(list);
            }
        }

    }

    public void addArkLabelChangeListener(OnArkLabelChangeListener listener) {
        arkLabelChangeListeners.add(listener);
    }

    public void removeArkLabelChangeListener(OnArkLabelChangeListener listener) {
        arkLabelChangeListeners.remove(listener);
    }


    public interface OnArkLabelChangeListener {
        void onChange(ArrayList<ArkLabelEntity> list);
    }
}
