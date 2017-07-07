package com.guanchao.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.guanchao.app.entery.User;
import com.guanchao.app.entery.Watch;

import static android.R.attr.password;

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
     *
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
     *
     * @param watch
     */
    public static void setWatch(Watch watch) {
        editor.putString(WATCH_AREAID, watch.getAreaId());//抄表任务中的区域
        editor.apply();
    }

    public static Watch getUserWatch() {
        Watch watch = new Watch();
        watch.setAreaId(preferences.getString(WATCH_AREAID, null));
        return watch;
    }

    /********************************保存用户码和密码*********************************/

    public static void savaUserMsg(Context context, String mobile, String password) {
        SharedPreferences.Editor sp = context.getSharedPreferences("Name_password", Context.MODE_PRIVATE).edit();
        sp.putString("mobile", mobile);
        sp.putString("password", password);
        sp.commit();

    }

    public static String[] getUserMsg(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Name_password", Context.MODE_PRIVATE);
        String mobile = sp.getString("mobile", null);
        String password = sp.getString("password", null);
        String[] strS={mobile,password};
        return strS;
    }
    //删除密码
    public static void clearNamePass(Context context){
        SharedPreferences.Editor sp = context.getSharedPreferences("Name_password", Context.MODE_PRIVATE).edit();
        sp.remove("password");
        sp.commit();
    }

    //保存CheckBox的点击状态
    public static void savaCheckBox(Context context, boolean isCbox) {
        SharedPreferences.Editor sp = context.getSharedPreferences("CheckBoxs", Context.MODE_PRIVATE).edit();
        sp.putBoolean("CheckBox", isCbox);
        sp.commit();

    }
    public static boolean getCheckBox(Context context) {
        SharedPreferences sp = context.getSharedPreferences("CheckBoxs", Context.MODE_PRIVATE);
        boolean iscox = sp.getBoolean("CheckBox", false);
        return iscox;
    }
    //删除CheckBox的点击状态
    public static void clearCheckBox(Context context){
        SharedPreferences.Editor sp = context.getSharedPreferences("CheckBoxs", Context.MODE_PRIVATE).edit();
        sp.clear();
        sp.commit();
    }
}
