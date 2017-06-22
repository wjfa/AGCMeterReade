package com.guanchao.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.guanchao.app.entery.User;
import com.guanchao.app.entery.Watch;

/**
 * 对用户信息本地保存
 */
public class SharePreferencesUtils {

    private static final String NAME = SharePreferencesUtils.class.getSimpleName();
    private static final String KEY_UID = "id";//用户ID
    private static final String WATCH_AREAID = "areaId";//抄表任务中的区域areaId
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SharePreferencesUtils() {
    }

    //在Application中初始化
    @SuppressLint("CommitPrefEdits")
    public static void init(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static void clearAllData() {
        editor.clear();
        editor.apply();//editor.commit()一样
    }

    /**
     * 登入
     * @param user
     */
    public static void setUser(User user) {
        editor.putString(KEY_UID, user.getId());//用户ID
        editor.apply();
    }

    public static User getUser() {
        User watch = new User();
        watch.setId(preferences.getString(KEY_UID, null));
        return watch;
    }
    /**
     * 抄表任务
     * @param watch
     */
    public static void setWatch(Watch watch) {
        editor.putString(WATCH_AREAID,watch.getAreaId());//抄表任务中的区域
        editor.apply();
    }

    public static Watch getUserWatch() {
        Watch watch = new Watch();
        watch.setAreaId(preferences.getString(WATCH_AREAID, null));
        return watch;
    }
}
