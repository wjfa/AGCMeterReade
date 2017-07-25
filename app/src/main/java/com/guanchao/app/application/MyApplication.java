package com.guanchao.app.application;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import com.guanchao.app.R;
import com.guanchao.app.utils.SharePreferencesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 王建法 on 2017/6/14.
 * 初始化页面
 */

public class MyApplication extends Application {
    public static List<?> images = new ArrayList<>();
    public static List<String> titles = new ArrayList<>();
    public static int H, W;
    public static MyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化本地配置
        SharePreferencesUtils.init(this);

        app = this;
        getScreen(this);

        String[] urls = getResources().getStringArray(R.array.url);
        String[] tips = getResources().getStringArray(R.array.title);
        List list = Arrays.asList(urls);//图片集合
        images = new ArrayList(list);
        List list1 = Arrays.asList(tips);//文字集合
        titles = new ArrayList(list1);

        //android7.0版本调用相机拍照时出现的错误解决
         /*获取当前系统的android版本号*/
        int intcurrentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (intcurrentapiVersion < 24) {
            return;
        } else {//android7.0版本以上
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

    }

    public void getScreen(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        H = dm.heightPixels;
        W = dm.widthPixels;

    }
}
