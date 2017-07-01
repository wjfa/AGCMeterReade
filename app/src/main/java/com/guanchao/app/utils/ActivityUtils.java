package com.guanchao.app.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.guanchao.app.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.gson.internal.UnsafeAllocator.create;

/**
 * Some common methods associated with {@link Activity}, such as
 * {@link #startActivity(Class)} and {@link #showToast(CharSequence)}.
 * I use this class as an alternative approach of BaseActivity,
 * to reduce the extra complexity introduced by the super deep inheritance level.
 * It is kind of like a non-standard delegate pattern.
 * <p>
 * <p/>
 * 此类包含与Activity相关的一些常用方法，例如startActivity和showToast。
 * 我使用这个类做为BaseActivity的替代方案，以避免过深的继承树引入的复杂性。
 * 有点类似于一种不太标准的委托模式。
 */
@SuppressWarnings("unused")
public class ActivityUtils {

    // 使用弱引用，避免不恰当地持有Activity或Fragment的引用。
    // 持有Activity的引用会阻止Activity的内存回收，增大OOM的风险。
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;

    private Toast toast;
    Activity activiy;

    public ActivityUtils(Activity activity) {
        this.activiy=activity;
        activityWeakReference = new WeakReference<>(activiy);
    }

    public ActivityUtils(Fragment fragment) {
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    /**
     * Get the reference of the specific {@link Activity}, this method
     * may return null, you should check the result when you invoke it.
     * <p>
     * <p/>
     * 通过弱引用获取Activity对象，此方法可能返回null，调用后需要做检查。
     *
     * @return reference of {@link Activity}
     */
    private
    @Nullable
    Activity getActivity() {

        if (activityWeakReference != null) return activityWeakReference.get();
        if (fragmentWeakReference != null) {
            Fragment fragment = fragmentWeakReference.get();
            return fragment == null ? null : fragment.getActivity();
        }
        return null;
    }

    public void showToast(CharSequence msg) {
        Activity activity = getActivity();
        if (activity != null) {
            if (toast == null) toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        }
    }

    @SuppressWarnings("SameParameterValue")
    public void showToast(@StringRes int resId) {
        Activity activity = getActivity();
        if (activity != null) {
            String msg = activity.getString(resId);
            showToast(msg);
        }
    }

    public void startActivity(Class<? extends Activity> clazz) {
        Activity activity = getActivity();
        if (activity == null) return;
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }


    /**
     * Unfortunately Android doesn't have an official API to retrieve the height of
     * StatusBar. This is just a way to hack around, may not work on some devices.
     *
     * @return The height of StatusBar.
     */
    public int getStatusBarHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        Resources resources = getActivity().getResources();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getScreenWidth() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public int getScreenHeight() {
        Activity activity = getActivity();
        if (activity == null) return 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public void hideSoftKeyboard() {
        Activity activity = getActivity();
        if (activity == null) return;

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //打开浏览器
    public void startBrowser(String url) {
        if (getActivity() == null) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        getActivity().startActivity(intent);
    }
    /**
     * 掉此方法输入所要转换的时间输入例如（"2014年06月14日16时09分00秒"）返回时间戳
     *
     * @param time
     * @return
     */
    public String data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 自定义的消息弹窗,用来消息提示,不做其它用途
     * @param title  标题
     * @param msg   内容
     */
    public void showDialog(String title,String msg){
        //header头部
        View viewHeader = LayoutInflater.from(activiy).inflate(R.layout.alter_dialog_header, null);
        TextView tvTitle= (TextView) viewHeader.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        //context内容
        View viewContex = LayoutInflater.from(activiy).inflate(R.layout.alter_dialog_context, null);
        TextView tvContext= (TextView) viewContex.findViewById(R.id.tv_context);
        tvContext.setText(msg+" !");
        //弹窗
        DialogPlus dialogPlus=DialogPlus.newDialog(activiy)
                .setContentHolder(new ViewHolder(viewContex))
                .setHeader(viewHeader)
                .setFooter(R.layout.alter_dialog_footer)//添加脚布局
                .setInAnimation(R.anim.alpha1)//类似于IOS底部出现效果
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .setGravity(Gravity.CENTER)
                .setExpanded(true)
                .setCancelable(false)
                .create();
        dialogPlus.show();
    }

    /**
     * 获取系统当前时间
     *
     * @param time
     * @return
     */
    public String setCurrentTime(String time) {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        if (time == "年月日时分秒") {//获取年 月 日 时 分 秒
            String time1 = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").format(curDate);
            return time1;
        } else if (time == "年月日") {//年 月 日
            String time2 = new SimpleDateFormat("yyyy年MM月dd日").format(curDate);
            return time2;
        } else if (time == "时分") {//时 分
//            HH：返回的是24小时制的时间
//            hh：返回的是12小时制的时间
            String time3 = new SimpleDateFormat("HH:mm").format(curDate);
            return time3;
        } else if (time == "年月") {
            String time4 = new SimpleDateFormat("yyyy-MM").format(curDate);
            return time4;
        }
        return null;
    }
    /**
     * 获取系统当前时间转换成星期几
     * @return  isDlack  是否显示年月日
     */
    public  String StringDataDay(boolean isDlack) {

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }

        if (isDlack){
            return mYear + "年" + mMonth + "月" + mDay + "日" + "\t\t星期" + mWay;
        }else {
            return "星期" + mWay;
        }

    }
}
