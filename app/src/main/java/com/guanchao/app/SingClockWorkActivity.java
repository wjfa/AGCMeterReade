package com.guanchao.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanchao.app.service.MyMusicService;
import com.guanchao.app.utils.ActivityUtils;

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
    ImageView imgUp;
    @BindView(R.id.btn_work_up)
    TextView btnkUp;
    @BindView(R.id.img_work_down)
    ImageView imgDown;
    @BindView(R.id.btn_work_down)
    TextView btnDown;
    private Dialog dialogSign;
    private ActivityUtils activityUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_clock_work);
        ButterKnife.bind(this);

        activityUtils=new ActivityUtils(this);
        //获取日期转换成星期
        tvWeek.setText(activityUtils.StringDataDay(false));
        tvDate.setText(activityUtils.setCurrentTime("年月日"));
    }

    @OnClick({R.id.ig_back, R.id.btn_work_up, R.id.btn_work_down})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_work_up:
                //设置“下班签到”控件
                btnDown.setBackgroundResource(R.drawable.shape_color_go_work);
                btnDown.setTextColor(getResources().getColor(R.color.white));
                btnDown.setText("下班签到");
                btnDown.setGravity(Gravity.CENTER);
                btnDown.setEnabled(true);

                openDialogSign();
                //设置“上班签到”控件
                btnkUp.setBackgroundResource(R.color.lavenderblush);
                btnkUp.setTextColor(getResources().getColor(R.color.diablo));
                btnkUp.setText("已签到 " + activityUtils.setCurrentTime("时分"));
                btnkUp.setGravity(Gravity.RIGHT);
                imgUp.setImageResource(R.mipmap.ic_go_work2);
                btnkUp.setEnabled(false);

                //播放提示音月（用到了Service服务）
                Intent intent = new Intent(SingClockWorkActivity.this, MyMusicService.class);
                startService(intent);

                break;
            case R.id.btn_work_down:
                openDialogSign();
                //设置控件
                btnDown.setBackgroundResource(R.color.lavenderblush);
                btnDown.setTextColor(getResources().getColor(R.color.diablo));
                btnDown.setText("已退签 " + activityUtils.setCurrentTime("时分"));
                btnDown.setGravity(Gravity.RIGHT);
                imgDown.setImageResource(R.mipmap.ic_down_work2);
                btnDown.setEnabled(false);
                //播放提示音月（用到了Service服务）
                Intent intent2 = new Intent(SingClockWorkActivity.this, MyMusicService.class);
                startService(intent2);

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

}
