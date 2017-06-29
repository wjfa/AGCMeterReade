package com.guanchao.app;

import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private Dialog dialogSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_clock);
        ButterKnife.bind(this);

        //获取日期转换成星期
        tvWeek.setText(ActivityUtils.StringDataDay(false));
        tvDate.setText(setCurrentTime("年月日"));
    }

    @OnClick({R.id.ig_back, R.id.btn_sign_go3, R.id.btn_sign_down, R.id.btn_sign_position})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;

            case R.id.btn_sign_go3:
                openDialogSign();
                //设置控件
                btnGoSign.setBackgroundResource(R.color.lavenderblush);
                btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
                btnGoSign.setText("已签到 "+setCurrentTime("时分"));
                btnGoSign.setGravity(Gravity.RIGHT);
                imgGo.setImageResource(R.mipmap.ic_go_work2);
                btnGoSign.setEnabled(false);

                //播放提示音月（用到了Service服务）
                Intent intent=new Intent(SignClockActivity.this, MyMusicService.class);
                startService(intent);

                //播放系统提示音
              /*  Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
                rt.play();*/

                break;
            case R.id.btn_sign_down:
                openDialogSign();

                //设置控件
                btnDownSign.setBackgroundResource(R.color.lavenderblush);
                btnDownSign.setTextColor(getResources().getColor(R.color.diablo));
                btnDownSign.setText("已退签 "+setCurrentTime("时分"));
                btnDownSign.setGravity(Gravity.RIGHT);
                imgDown.setImageResource(R.mipmap.ic_down_work2);
                btnDownSign.setEnabled(false);

                //播放提示音月（用到了Service服务）
                Intent intent2=new Intent(SignClockActivity.this, MyMusicService.class);
                startService(intent2);

                break;
            case R.id.btn_sign_position:
                break;
        }
    }

    /**
     * 签到成功展示对话框
     */
    private void  openDialogSign(){
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
                    if (dialogSign.isShowing()){
                        dialogSign.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取系统当前时间
     * @param time
     * @return
     */
    private String setCurrentTime(String time){
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        if (time=="年月日时分秒"){//获取年 月 日 时 分 秒
            String time1 =  new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").format(curDate);
          return time1;
        }else if (time=="年月日"){//年 月 日
            String time2 =  new SimpleDateFormat("yyyy年MM月dd日").format(curDate);
            return time2;
        }else if (time=="时分"){//时 分
//            HH：返回的是24小时制的时间
//            hh：返回的是12小时制的时间
            String time3 =  new SimpleDateFormat("HH:mm").format(curDate);
            return time3;
        }
        return null;
    }
}
