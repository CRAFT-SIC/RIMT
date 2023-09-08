package com.suntend.arktoolbox.RIMTUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.suntend.arktoolbox.APP;
import com.suntend.arktoolbox.ui.arklabel.ArkLabelDirEntity;
import com.suntend.arktoolbox.ui.arklabel.ArkLabelEntity;

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
    private final String key_top_tip_showing = "key_top_tip_showing";
    private String uid_suffix = "";
    private ArrayList<OnArkLabelChangeListener> arkLabelChangeListeners = new ArrayList<>();
    private ArkLabelDirEntity rootEntity = new ArkLabelDirEntity();

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
                            instance.loadLabelList();
                        }
                    });
                    instance.loadLabelList();
                }
            }
        }
        return instance;
    }

    private void saveRootEntityNotify() {
        preferences.edit().putString(key_list_json + uid_suffix, new Gson().toJson(rootEntity)).apply();
        notifyListeners();
    }

    private void loadLabelList() {
        String value = preferences.getString(key_list_json + uid_suffix, "");
        if (!value.isEmpty()) {
            rootEntity = new Gson().fromJson(value, ArkLabelDirEntity.class);
        } else {
            rootEntity = new ArkLabelDirEntity();
            rootEntity.id = 0;
        }
        notifyListeners();

    }

    public ArkLabelDirEntity getRootEntity() {
        return rootEntity;
    }

    private void notifyListeners() {
        for (int i = 0; i < arkLabelChangeListeners.size(); i++) {
            arkLabelChangeListeners.get(i).onChange(rootEntity);
        }
    }

    public void addLabel(ArkLabelEntity entity) {
        int nowId = addLabel(0, rootEntity);
        entity.id = nowId + 1;
        rootEntity.labels.add(entity);
        saveRootEntityNotify();
    }

    private int addLabel(int lostId, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.labels.size(); i++) {
            if (dirEntity.labels.get(i).id > lostId) {
                lostId = dirEntity.labels.get(i).id;
            }
        }
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            lostId = addLabel(lostId, dirEntity.dirs.get(i));
        }
        return lostId;
    }

    public void updateLabel(ArkLabelEntity entity) {
        updateLabel(entity, rootEntity);
        saveRootEntityNotify();
    }

    private void updateLabel(ArkLabelEntity entity, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.labels.size(); i++) {
            ArkLabelEntity item = dirEntity.labels.get(i);
            if (item.id == entity.id) {
                item.name = entity.name;
                item.url = entity.url;
                item.game = entity.game;
                return;
            }
        }
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            updateLabel(entity, dirEntity.dirs.get(i));
        }
    }

    public void deleteLabel(ArkLabelEntity entity) {
        deleteLabel(entity, rootEntity);
        saveRootEntityNotify();
    }

    private void deleteLabel(ArkLabelEntity entity, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.labels.size(); i++) {
            ArkLabelEntity item = dirEntity.labels.get(i);
            if (item.id == entity.id) {
                dirEntity.labels.remove(i);
                return;
            }
        }
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            deleteLabel(entity, dirEntity.dirs.get(i));
        }
    }

    public void addDir(ArkLabelDirEntity entity) {
        int nowId = addDir(0, rootEntity);
        entity.id = nowId + 1;
        rootEntity.dirs.add(entity);
        saveRootEntityNotify();
    }

    private int addDir(int lostId, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            if (dirEntity.dirs.get(i).id > lostId) {
                lostId = dirEntity.dirs.get(i).id;
            }
            lostId = addDir(lostId, dirEntity.dirs.get(i));
        }
        return lostId;
    }

    public void updateDir(ArkLabelDirEntity entity) {
        updateDir(entity, rootEntity);
        saveRootEntityNotify();
    }

    private void updateDir(ArkLabelDirEntity entity, ArkLabelDirEntity dirEntity) {

        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            if (entity.id == dirEntity.dirs.get(i).id) {
                dirEntity.dirs.get(i).name = entity.name;
                dirEntity.dirs.get(i).game = entity.game;
                return;
            }
            updateDir(entity, dirEntity.dirs.get(i));
        }
    }

    public void deleteDir(ArkLabelDirEntity entity) {
        deleteDir(entity, rootEntity);
        saveRootEntityNotify();
    }

    private void deleteDir(ArkLabelDirEntity entity, ArkLabelDirEntity dirEntity) {

        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            if (entity.id == dirEntity.dirs.get(i).id) {
                dirEntity.dirs.remove(i);
                return;
            }
            deleteDir(entity, dirEntity.dirs.get(i));
        }
    }

    public void changeSuperDir(ArkLabelEntity entity, int dirId) {
        deleteLabel(entity, rootEntity);
        changeSuperDirAdded(entity, dirId, rootEntity);
        saveRootEntityNotify();
    }

    private void changeSuperDirAdded(ArkLabelEntity entity, int dirId, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            if (dirId == dirEntity.dirs.get(i).id) {
                dirEntity.dirs.get(i).labels.add(entity);
                return;
            }
            changeSuperDirAdded(entity, dirId, dirEntity.dirs.get(i));
        }
    }

    public void change2RootDir(ArkLabelEntity entity) {
        deleteLabel(entity, rootEntity);
        rootEntity.labels.add(entity);
        saveRootEntityNotify();
    }

    public ArkLabelEntity queryLabelEntity(int labelId) {
        return queryLabelEntity(labelId, rootEntity);
    }

    private ArkLabelEntity queryLabelEntity(int labelId, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.labels.size(); i++) {
            ArkLabelEntity item = dirEntity.labels.get(i);
            if (item.id == labelId) {
                return item;
            }
        }
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            queryLabelEntity(labelId, dirEntity.dirs.get(i));
        }
        return null;
    }

    public ArkLabelDirEntity queryDirEntity(int dirId) {
        return queryDirEntity(dirId, rootEntity);
    }

    private ArkLabelDirEntity queryDirEntity(int dirId, ArkLabelDirEntity dirEntity) {
        for (int i = 0; i < dirEntity.dirs.size(); i++) {
            if (dirId == dirEntity.dirs.get(i).id) {
                return dirEntity.dirs.get(i);
            }
            queryDirEntity(dirId, dirEntity.dirs.get(i));
        }
        return null;
    }

    public boolean isTopTipShowing() {
        return preferences.getBoolean(key_top_tip_showing + uid_suffix, true);
    }

    public void setTopTipShowing(boolean isShow) {
        preferences.edit().putBoolean(key_top_tip_showing + uid_suffix, isShow).apply();
    }

    public void addArkLabelChangeListener(OnArkLabelChangeListener listener) {
        arkLabelChangeListeners.add(listener);
    }

    public void removeArkLabelChangeListener(OnArkLabelChangeListener listener) {
        arkLabelChangeListeners.remove(listener);
    }


    public interface OnArkLabelChangeListener {
        void onChange(ArkLabelDirEntity data);
    }
}
