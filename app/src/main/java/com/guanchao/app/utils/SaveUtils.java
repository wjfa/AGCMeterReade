package com.guanchao.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.R.attr.value;

/**
 * Created by zzb on 2017/4/6.
 */

public class SaveUtils {
    Context mcontext;

    public SaveUtils(Context mcontext) {
        this.mcontext = mcontext;
    }

    /**************************************加班签到***************************************/
    /**
     * 上班打卡成功的状态
     *
     * @param isChecked
     */
    public void saveGoStatus(boolean isChecked) {
        SharedPreferences saveStatus = mcontext.getSharedPreferences("StatusGo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveStatus.edit();
        editor.putBoolean("saveStatusGo", isChecked);
        editor.commit();
    }

    public boolean getGoStatus() {
        SharedPreferences isChecked = mcontext.getSharedPreferences("StatusGo", Context.MODE_PRIVATE);
        boolean ischecked = isChecked.getBoolean("saveStatusGo", false);
        return ischecked;
    }

    /**
     * 下班打卡成功的状态
     */
    public void setDownValue( boolean value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("savaDowns", Context.MODE_PRIVATE).edit();
        sp.putBoolean("getDowna", value);
        sp.commit();
    }

    public boolean getDownValue() {
        SharedPreferences sp = mcontext.getSharedPreferences("savaDowns", Context.MODE_PRIVATE);
        boolean value = sp.getBoolean("getDowna", false);
        return value;
    }

    /**
     * 上班班打卡成功的时间
     */
    public void setSingGoTimeSave(String value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("savaTimeSing", Context.MODE_PRIVATE).edit();
        sp.putString("getTimeSing", value);
        sp.commit();
    }

    public String getSingGoTime() {
        SharedPreferences sp = mcontext.getSharedPreferences("savaTimeSing", Context.MODE_PRIVATE);
        String value = sp.getString("getTimeSing", "空");
        return value;
    }
    /**
     * 下班打卡成功的时间
     */
    public void setClockDownTimeSave(String value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("savaTimeClock", Context.MODE_PRIVATE).edit();
        sp.putString("getTimeClock", value);
        sp.commit();
    }

    public String getClockDownTime() {
        SharedPreferences sp = mcontext.getSharedPreferences("savaTimeClock", Context.MODE_PRIVATE);
        String value = sp.getString("getTimeClock", "空");
        return value;
    }

/**************************************加班签到***************************************/
    /**
     * 上班打卡成功的状态
     *
     * @param isChecked
     */
    public void saveStatus(boolean isChecked) {
        SharedPreferences saveStatus = mcontext.getSharedPreferences("Status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveStatus.edit();
        editor.putBoolean("saveStatus", isChecked);
        editor.commit();
    }

    public boolean getStatus() {
        SharedPreferences isChecked = mcontext.getSharedPreferences("Status", Context.MODE_PRIVATE);
        boolean ischecked = isChecked.getBoolean("saveStatus", false);
        return ischecked;
    }

    /**
     * 下班打卡成功的状态
     */
    public void setValue(boolean value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("sava", Context.MODE_PRIVATE).edit();
        sp.putBoolean("getd", value);
        sp.commit();
    }

    public boolean getValue() {
        SharedPreferences sp = mcontext.getSharedPreferences("sava", Context.MODE_PRIVATE);
        boolean value = sp.getBoolean("getd", false);
        return value;
    }

    /**
     * 上班班打卡成功的时间
     */
    public void setGoTimeSave(String value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("savaTime", Context.MODE_PRIVATE).edit();
        sp.putString("getTime", value);
        sp.commit();
    }

    public String getGoTime() {
        SharedPreferences sp = mcontext.getSharedPreferences("savaTime", Context.MODE_PRIVATE);
        String value = sp.getString("getTime", "空");
        return value;
    }
    /**
     * 下班打卡成功的时间
     */
    public void setDownTimeSave(String value) {
        SharedPreferences.Editor sp = mcontext.getSharedPreferences("savaTime2", Context.MODE_PRIVATE).edit();
        sp.putString("getTime2", value);
        sp.commit();
    }

    public String getDownTime() {
        SharedPreferences sp = mcontext.getSharedPreferences("savaTime2", Context.MODE_PRIVATE);
        String value = sp.getString("getTime2", "空");
        return value;
    }
}
