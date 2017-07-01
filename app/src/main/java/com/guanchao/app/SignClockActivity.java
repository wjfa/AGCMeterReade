package com.guanchao.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.guanchao.app.service.MyMusicService;
import com.guanchao.app.utils.ActivityUtils;
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
    private boolean isGowork = false;//是否上班签到
    private int status;//上下班签到状态
    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_clock);
        ButterKnife.bind(this);
        activityUtils=new ActivityUtils(this);
        //获取日期转换成星期
        tvWeek.setText(activityUtils.StringDataDay(false));
        tvDate.setText(activityUtils.setCurrentTime("年月日"));

        Log.e("时间转化成时间戳", "onCreate: " + Long.valueOf(compareTime("13:00")) + "   " + Long.valueOf(compareTime("18:01")));

        init();
    }

    private void init() {
        if (isGowork == true) {

        } else {
            //设置控件未开始
            btnDownSign.setBackgroundResource(R.color.lavenderblush);
            btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
            btnDownSign.setText("未开始");
            btnDownSign.setGravity(Gravity.LEFT);
            btnDownSign.setEnabled(false);
        }

        //判断是否是周末（隐藏和显示需要的布局）
        if ("星期六".equals(activityUtils.StringDataDay(false))||"星期日".equals(activityUtils.StringDataDay(false))){
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

        }else {
            relOvertime.setVisibility(View.GONE);
            tvResttime.setVisibility(View.GONE);
            relUpDown.setVisibility(View.VISIBLE);
        }
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
                    //播放系统提示音
                /*  Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
                rt.play();*/
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
     * 上班签到控件设置
     */
    private void upWork() {
        //设置“下班签到”控件
        btnDownSign.setBackgroundResource(R.drawable.shape_color_go_work);
        btnDownSign.setTextColor(getResources().getColor(R.color.white));
        btnDownSign.setText("下班签到");
        btnDownSign.setGravity(Gravity.CENTER);
        btnDownSign.setEnabled(true);

        openDialogSign();
        //设置“上班签到”控件
        btnGoSign.setBackgroundResource(R.color.lavenderblush);
        btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
        btnGoSign.setText("已签到 " + activityUtils.setCurrentTime("时分"));
        btnGoSign.setGravity(Gravity.RIGHT);
        imgGo.setImageResource(R.mipmap.ic_go_work2);
        btnGoSign.setEnabled(false);

        //播放提示音月（用到了Service服务）
        Intent intent = new Intent(SignClockActivity.this, MyMusicService.class);
        startService(intent);

    }

    /**
     * 下班签到控件设置
     */
    private void downWork() {
        openDialogSign();
        //设置控件
        btnDownSign.setBackgroundResource(R.color.lavenderblush);
        btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
        btnDownSign.setText("已退签 " + activityUtils.setCurrentTime("时分"));
        btnDownSign.setGravity(Gravity.RIGHT);
        imgDown.setImageResource(R.mipmap.ic_down_work2);
        btnDownSign.setEnabled(false);
        //播放提示音月（用到了Service服务）
        Intent intent2 = new Intent(SignClockActivity.this, MyMusicService.class);
        startService(intent2);

        //显示“切换到加班页面”
        relOvertime.setVisibility(View.VISIBLE);
        tvOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignClockActivity.this, SingClockWorkActivity.class));
                finish();
            }
        });
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
        EditText edtContext = (EditText) viewContex.findViewById(R.id.edt_context);
        edtContext.setHint(msg);
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(viewContex))
                .setHeader(viewHeader)
                .setFooter(R.layout.alter_dialog_footer)//添加脚布局
                .setInAnimation(R.anim.alpha1)//类似于IOS底部出现效果
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        //// TODO: 2017/7/1 获取edittext的内容上传  待实现 

                        if (status == 1) {
                            upWork();
                        }
                        if (status == 2) {
                            downWork();
                        }
                        dialog.dismiss();
                    }
                })
                .setGravity(Gravity.CENTER)
                .setExpanded(true)
                .setCancelable(true)
                .create();

        dialogPlus.show();
    }

    @OnClick(R.id.tv_work_overtime)
    public void onClick() {
    }

    /**
     * 比较两个时间的大小(将时间转换成时间戳比较)
     * 输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳
     */
    private String compareTime(String timeString) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
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
