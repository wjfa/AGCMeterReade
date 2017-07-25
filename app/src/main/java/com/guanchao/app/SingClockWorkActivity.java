package com.guanchao.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanchao.app.service.MyMusicService;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SaveUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingClockWorkActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_work_week)
    TextView tvWeek;
    @BindView(R.id.tv_work_date)
    TextView tvDate;
    @BindView(R.id.img_work_up)
    ImageView imgGo;
    @BindView(R.id.btn_work_up)
    TextView btnGoSign;
    @BindView(R.id.img_work_down)
    ImageView imgDown;
    @BindView(R.id.btn_work_down)
    TextView btnDownSign;
    private Dialog dialogSign;
    private ActivityUtils activityUtils;
    private SaveUtils saveUtils;
    private static boolean isSelectGoWork = false;//是否点击上班  默认是未签到
    private static boolean isSelectDownWork = false;//是否点击下班  默认是未签到
    private static String goworkSuccTime;
    private static String downWorkSuccTime;
    private static String goWorkDate;//上班打卡日期  点击签到后获取系统日期  为了与第二天对比是不是今天过去了


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_clock_work);
        ButterKnife.bind(this);


       /* //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        activityUtils = new ActivityUtils(this);
        saveUtils = new SaveUtils(this);
        //获取日期转换成星期
        tvWeek.setText(activityUtils.StringDataDay(false));
        tvDate.setText(activityUtils.setCurrentTime("年月日"));

        /*从本地获取是否点击签到的控件*/
        isSelectGoWork=saveUtils.getStatus();
        isSelectDownWork=saveUtils.getValue();
        init();
    }
    private void init(){
        //从本地获取日期
        SharedPreferences sp = getSharedPreferences("goWorkTime", Context.MODE_PRIVATE);
        goWorkDate = sp.getString("goWork", "未获取到日期");
        //判断当前日期 与上一天天对比是不是昨天过去了
        if (goWorkDate != null) {
            if (activityUtils.setCurrentTime("年月日").equals(goWorkDate)) {
             //   Log.e("时间", "onCreate: " + activityUtils.setCurrentTime("年月日") + "    " + goWorkDate);
                if (isSelectGoWork) {
                    //设置“上班”控件成功
                    btnGoSign.setBackgroundResource(R.color.lavenderblush);
                    btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
                    /*获取本地时间*/
                    goworkSuccTime= saveUtils.getGoTime();
                    btnGoSign.setText(goworkSuccTime);
                    btnGoSign.setGravity(Gravity.LEFT);
                    imgGo.setImageResource(R.mipmap.ic_go_work2);
                    btnGoSign.setEnabled(false);

                    //设置“下班签到”控件
                    btnDownSign.setBackgroundResource(R.drawable.shape_color_go_work);
                    btnDownSign.setTextColor(getResources().getColor(R.color.white));
                    btnDownSign.setText("下班签退");
                    btnDownSign.setGravity(Gravity.CENTER);
                    imgDown.setImageResource(R.mipmap.ic_down_work);
                    btnDownSign.setEnabled(true);
                }
                if (isSelectDownWork) {
                    //设置“下班”控件成功
                    btnDownSign.setBackgroundResource(R.color.lavenderblush);
                    btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
                    /*获取本地时间*/
                    downWorkSuccTime=   saveUtils.getDownTime();
                    btnDownSign.setText(downWorkSuccTime);
                    btnDownSign.setGravity(Gravity.LEFT);
                    imgDown.setImageResource(R.mipmap.ic_down_work2);
                    btnDownSign.setEnabled(false);
                }

            } else {
                //设置“上班”控件未开始
                btnGoSign.setBackgroundResource(R.drawable.shape_color_go_work);
                btnGoSign.setTextColor(getResources().getColor(R.color.white));
                btnGoSign.setText("上班签到");
                btnGoSign.setGravity(Gravity.CENTER);
                imgGo.setImageResource(R.mipmap.ic_go_work);
                btnGoSign.setEnabled(true);

                //设置“下班”控件未开始
                btnDownSign.setBackgroundResource(R.color.lavenderblush);
                btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
                btnDownSign.setText("未开始");
                btnDownSign.setGravity(Gravity.CENTER);
                imgDown.setImageResource(R.mipmap.ic_down_work);
                btnDownSign.setEnabled(false);
            }
        } else {
            Log.e("时间为空", "onCreate: ");
            return;
        }
    }
    /**
     * 上班签到控件设置
     */
    private void upWork() {

        /*存储日期到本地*/
        SharedPreferences sp = getSharedPreferences("goWorkTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String goWorkTime = activityUtils.setCurrentTime("年月日");
        edit.putString("goWork", goWorkTime);
        edit.commit();

        /*存储点击签到的状态*/
        isSelectGoWork = true;
        saveUtils.saveStatus(isSelectGoWork);
        //设置“下班签到”控件
        btnDownSign.setBackgroundResource(R.drawable.shape_color_go_work);
        btnDownSign.setTextColor(getResources().getColor(R.color.white));
        btnDownSign.setText("下班签退");
        btnDownSign.setGravity(Gravity.CENTER);
        imgDown.setImageResource(R.mipmap.ic_down_work);
        btnDownSign.setEnabled(true);

        openDialogSign();
        //设置“上班签到”控件
        btnGoSign.setBackgroundResource(R.color.lavenderblush);
        btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
        btnGoSign.setText("已签到 " + activityUtils.setCurrentTime("时分"));
        /*存储签到成功的时间*/
        String goSuccTime = btnGoSign.getText().toString();
        saveUtils.setGoTimeSave(goSuccTime);
        btnGoSign.setGravity(Gravity.RIGHT);
        imgGo.setImageResource(R.mipmap.ic_go_work2);
        btnGoSign.setEnabled(false);

        //成功振动效果
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);//振动的频率
        //播放提示音月（用到了Service服务）
        Intent intent = new Intent(SingClockWorkActivity.this, MyMusicService.class);
        startService(intent);
    }
    /**
     * 下班签到控件设置
     */
    private void downWork() {
        //存储点击签到的状态
        isSelectDownWork = true;
        saveUtils.setValue(isSelectDownWork);
        openDialogSign();
        //设置"下班"控件
        btnDownSign.setBackgroundResource(R.color.lavenderblush);
        btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
        btnDownSign.setText("已签退 " + activityUtils.setCurrentTime("时分"));
        /*存储签到成功的时间*/
        String downSuccTime = btnDownSign.getText().toString();
        saveUtils.setDownTimeSave(downSuccTime);
        btnDownSign.setGravity(Gravity.RIGHT);
        imgDown.setImageResource(R.mipmap.ic_down_work2);
        btnDownSign.setEnabled(false);

        //成功振动效果
        Vibrator vibrator2 = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator2.vibrate(500);//振动的频率
        //播放提示音月（用到了Service服务）
        Intent intent2 = new Intent(SingClockWorkActivity.this, MyMusicService.class);
        startService(intent2);

    }

    @OnClick({R.id.ig_back, R.id.btn_work_up, R.id.btn_work_down})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_work_up:
                upWork();
                break;
            case R.id.btn_work_down:
                downWork();
                break;
        }
    }
    /**
     * 签到成功展示对话框
     */
    private void openDialogSign() {
        dialogSign = new Dialog(this, R.style.processDialog);
        dialogSign.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_go_work, null));
        dialogSign.getWindow().setGravity(Gravity.FILL);
        dialogSign.show();
        //设置延时多少秒关闭效果
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (dialogSign.isShowing()) {
                        dialogSign.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 比较两个日期的大小(将时间转换成时间戳比较)
     * 输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     */
    private String compareDateTime(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date d;
        String timeStamp = null;
        try {
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }
}
