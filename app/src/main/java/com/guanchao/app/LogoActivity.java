package com.guanchao.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.utils.SystemBarCompat;
import com.guanchao.app.utils.SystemStatusCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.guanchao.app.R.id.viewpage;
/**
 * 欢迎页面
 */
public class LogoActivity extends AppCompatActivity {

    @BindView(viewpage)
    ViewPager viewPager;
    @BindView(R.id.tv_jump_time)
    TextView tvJumpTime;
    @BindView(R.id.rel_jump)
    RelativeLayout relJump;
    private ViewPagerAdapter pagerAdapter;
    private List<View> viewList;
    private CountDownTimer mCountDownTimer;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    createTimer(5);//倒计时
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        //设置状态栏颜色
        SystemStatusCompat.setStatusBarBg(this, R.color.white);
        //设置状态栏字体的颜色
        SystemBarCompat.setFlymeStatusBarDarkIcon(this,true);
        ButterKnife.bind(this);
        initData();


    }

    //添加图片到list里面
    private void initData() {
        viewList = new ArrayList<View>();
        viewList.add(getLayoutInflater().inflate(R.layout.logo_1, null));

        pagerAdapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        //设置滑动处理事件
        viewPager.setOnPageChangeListener(pageChangeListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private boolean isSelect;//判断是否点击或滑动页面
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        /**界面切换时调用*/
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        /**当界面切换后调用*/
        @Override
        public void onPageSelected(int position) {

        }

        /**滑动状态变化时调用*/
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                isSelect = true;
                startActivity(new Intent(LogoActivity.this, LoginActivity.class));
                finish();
            }

        }
    };

    @OnClick(R.id.rel_jump)
    public void onClick() {
        isSelect = true;
        startActivity(new Intent(LogoActivity.this, LoginActivity.class));
        finish();

    }

    /**
     * 验证码获取时间
     *
     * @param time 验证码获取的总时间
     */
    private void createTimer(final long time) {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                long mTimeRemaining = ((millisUnitFinished / 1000) + 1);
                tvJumpTime.setText(mTimeRemaining + "s");//设置多少秒跳转

            }

            //等于0时的处理
            @Override
            public void onFinish() {
                if (isSelect == false) {//如果滑动或点击页面
                    startActivity(new Intent(LogoActivity.this, LoginActivity.class));
                    finish();
                } else {

                    return;
                }
            }
        };
        mCountDownTimer.start();
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> list;

        public ViewPagerAdapter(List<View> list) {
            this.list = list;
        }

        /**
         * 设置控件中显示界面的数量
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 销毁 position 位置的界面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        /**
         * 初始化 position 位置的界面
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = list.get(position);
            container.addView(view);
            return view;
        }
    }

}
