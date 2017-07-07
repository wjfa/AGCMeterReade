package com.guanchao.app;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.service.MyMusicService;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SaveUtils;
import com.guanchao.app.utils.StatusBarUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.guanchao.app.R.id.view;
import static com.guanchao.app.R.mipmap.a;

public class SignClockActivity extends AppCompatActivity {
    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_sign_week)
    TextView tvWeek;
    @BindView(R.id.tv_sign_date)
    TextView tvDate;
    @BindView(R.id.img_sign_go)
    ImageView imgGo;
    @BindView(R.id.btn_sign_go3)
    TextView btnGoSign;
    @BindView(R.id.img_sign_down)
    ImageView imgDown;
    @BindView(R.id.btn_sign_down)
    TextView btnDownSign;
    @BindView(R.id.btn_sign_position)
    TextView tvPosition;
    @BindView(R.id.tv_work_overtime)
    TextView tvOvertime;//切换加班时间点击跳转
    @BindView(R.id.reltime)
    RelativeLayout relOvertime;//切换加班布局（隐藏和显示）
    @BindView(R.id.tv_week_overwork)
    TextView tvResttime;//周末休息时间（隐藏和显示）
    @BindView(R.id.ryle_work)
    RelativeLayout relUpDown;//上下班打卡布局（隐藏和显示）
    private Dialog dialogSign;

    private int status;//上下班签到状态
    private static boolean isSelectGoWork = false;//是否点击上班  默认是未签到
    private static boolean isSelectDownWork = false;//是否点击下班  默认是未签到
    private ActivityUtils activityUtils;
    private SaveUtils saveUtils;
    private static String goworkSuccTime;
    private static String downWorkSuccTime;
    //利用SharedPreferences对时间的存储
    private static String goWorkDate;//上班打卡日期  点击签到后获取系统日期  为了与第二天对比是不是今天过去了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_clock);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        saveUtils = new SaveUtils(this);

        //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);

        //获取日期转换成星期
        tvWeek.setText(activityUtils.StringDataDay(false));
        tvDate.setText(activityUtils.setCurrentTime("年月日"));
        /*从本地获取是否点击签到的控件*/
        isSelectGoWork=saveUtils.getGoStatus();
        isSelectDownWork=saveUtils.getDownValue();
        init();
        locationPermission();//获取地理位置权限

    }

    private void init() {
        //从本地获取日期
        SharedPreferences sp = getSharedPreferences("goWorkTime", Context.MODE_PRIVATE);
        goWorkDate = sp.getString("goWork", "未获取到日期");
        if (goWorkDate!=null){
            if (activityUtils.setCurrentTime("年月日").equals(goWorkDate)) {
               // Log.e("时间", "onCreate: " + activityUtils.setCurrentTime("年月日") + "    " + goWorkDate);
                if (isSelectGoWork) {
                    //设置“上班”控件成功
                    btnGoSign.setBackgroundResource(R.color.lavenderblush);
                    btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
                    /*获取本地时间*/
                    goworkSuccTime=   saveUtils.getSingGoTime();
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
                    downWorkSuccTime=   saveUtils.getClockDownTime();
                    btnDownSign.setText(downWorkSuccTime);
                    btnDownSign.setGravity(Gravity.LEFT);
                    imgDown.setImageResource(R.mipmap.ic_down_work2);
                    btnDownSign.setEnabled(false);


                    //需要更新UI
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //显示“切换到加班页面”
                            relOvertime.setVisibility(View.VISIBLE);
                            tvOvertime.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(SignClockActivity.this, SingClockWorkActivity.class));
                                }
                            });
                            //隐藏定位
                            tvPosition.setVisibility(View.GONE);
                        }
                    },100);
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

        //判断是否是周末（隐藏和显示需要的布局）
        if ("星期六".equals(activityUtils.StringDataDay(false)) || "星期日".equals(activityUtils.StringDataDay(false))) {
            relOvertime.setVisibility(View.VISIBLE);
            tvResttime.setVisibility(View.VISIBLE);
            relUpDown.setVisibility(View.GONE);
            //显示“切换到加班页面”
            relOvertime.setVisibility(View.VISIBLE);
            tvOvertime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignClockActivity.this, SingClockWorkActivity.class));
                    finish();
                }
            });

        } else {
            relOvertime.setVisibility(View.GONE);
            tvResttime.setVisibility(View.GONE);
            relUpDown.setVisibility(View.VISIBLE);
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
        saveUtils.saveGoStatus(isSelectGoWork);
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
        saveUtils.setSingGoTimeSave(goSuccTime);
        btnGoSign.setGravity(Gravity.RIGHT);
        imgGo.setImageResource(R.mipmap.ic_go_work2);
        btnGoSign.setEnabled(false);

        //播放提示音月（用到了Service服务）
        Intent intent = new Intent(SignClockActivity.this, MyMusicService.class);
        startService(intent);
        //成功振动效果
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);//振动的频率

    }

    /**
     * 下班签到控件设置
     */
    private void downWork() {
        /*存储点击签到的状态*/
        isSelectDownWork= true;
        saveUtils.setDownValue(isSelectDownWork);

        openDialogSign();
        //设置"下班"控件
        btnDownSign.setBackgroundResource(R.color.lavenderblush);
        btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
        btnDownSign.setText("已签退 " + activityUtils.setCurrentTime("时分"));
        /*存储签到成功的时间*/
        String downSuccTime = btnDownSign.getText().toString();
        saveUtils.setClockDownTimeSave(downSuccTime);
        btnDownSign.setGravity(Gravity.RIGHT);
        imgDown.setImageResource(R.mipmap.ic_down_work2);
        btnDownSign.setEnabled(false);
        //隐藏定位
        tvPosition.setVisibility(View.GONE);

        //成功振动效果
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);//振动的频率
        //播放提示音月（用到了Service服务）
        Intent intent2 = new Intent(SignClockActivity.this, MyMusicService.class);
        startService(intent2);

        //显示“切换到加班页面”
        relOvertime.setVisibility(View.VISIBLE);
        tvOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignClockActivity.this, SingClockWorkActivity.class));
            }
        });
    }

    @OnClick({R.id.ig_back, R.id.btn_sign_go3, R.id.btn_sign_down, R.id.btn_sign_position})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_sign_go3://上班签到
                //获取当前时间与09:00比较
                if (Long.valueOf(compareTime(activityUtils.setCurrentTime("时分"))) > Long.valueOf(compareTime("09:00"))) {
                    status = 1;
                    showDialogWorkContent("上班考勤迟到", "请输入您上班迟到的理由");
                } else {
                    upWork();
                }
                break;
            case R.id.btn_sign_down://下班签到

                if (Long.valueOf(compareTime(activityUtils.setCurrentTime("时分"))) < Long.valueOf(compareTime("18:00"))) {
                    status = 2;
                    showDialogWorkContent("下班考勤早退", "请输入您下班早退的理由");
                } else {
                    downWork();
                }

                break;
            case R.id.btn_sign_position:
                break;
        }
    }

    /**
     * 签到成功展示对话框
     */
    private void openDialogSign() {
        dialogSign = new Dialog(this, R.style.processDialog);
        dialogSign.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_icon, null));
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
     * 上班迟到和未到下班时间
     * * @param title  标题
     *
     * @param msg 内容
     */
    private void showDialogWorkContent(String title, String msg) {
        //header头部
        View viewHeader = LayoutInflater.from(this).inflate(R.layout.alter_dialog_header, null);
        TextView tvTitle = (TextView) viewHeader.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        //context内容
        View viewContex = LayoutInflater.from(this).inflate(R.layout.dialog_work_context, null);
        final EditText edtContext = (EditText) viewContex.findViewById(R.id.edt_context);
        edtContext.setHint(msg);
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(viewContex))
                .setHeader(viewHeader)
                .setFooter(R.layout.dialog_work_footer)//添加脚布局
                .setInAnimation(R.anim.alpha1)//类似于IOS底部出现效果
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        // TODO: 2017/7/1 获取edittext的内容上传  待实现
                        switch (view.getId()) {
                            case R.id.tv_work_no:
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                break;
                            case R.id.tv_work_yes://确定

                                if (edtContext.getText().toString().trim().length() == 0) {
                                    activityUtils.showToast("理由不能为空！");
                                } else {
                                    if (status == 1) {
                                        upWork();
                                    }
                                    if (status == 2) {
                                        downWork();
                                    }
                                    dialog.dismiss();
                                }
                                break;
                        }

                    }
                })
                .setGravity(Gravity.CENTER)
                .setExpanded(true)
                .setCancelable(false)
                .create();

        dialogPlus.show();
    }

    @OnClick(R.id.tv_work_overtime)
    public void onClick() {
    }
    /**
     * 动态获取访问您的地理位置权限
     */
    private void locationPermission(){
        /**
         * 动态获取访问相机  Manifest.permission.CAMERA
         */
        if (PermissionsUtil.hasPermission(SignClockActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //成功允许时的操作

            // Toast.makeText(QrCodeScanActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(SignClockActivity.this, new PermissionListener() {
                @Override//接受
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(SignClockActivity.this, "用户成功授权访问你的地理位置", Toast.LENGTH_LONG).show();
                }

                @Override//拒绝
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(SignClockActivity.this, "用户残忍拒访问你的地理位置", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }
    /**
     * 比较两个时间的大小(将时间转换成时间戳比较)
     * 输入所要转换的时间输入例如（"09-00"）返回时间戳
     */
    private String compareTime(String timeString) {
        String timeStamp = null;
        //  HH：返回的是24小时制的时间
        //  hh：返回的是12小时制的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d;
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
