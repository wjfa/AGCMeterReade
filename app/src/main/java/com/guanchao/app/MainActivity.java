package com.guanchao.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.guanchao.app.fragmet.FristFragment;
import com.guanchao.app.fragmet.RepairsFragment;
import com.guanchao.app.fragmet.ServiceRepairsFragment;
import com.guanchao.app.fragmet.WatchFragment;

import me.majiajie.pagerbottomtabstrip.MaterialMode;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
/**
 * Fragment替换页面
 */
public class MainActivity extends AppCompatActivity {
    private FristFragment fristFragment;
    private WatchFragment watchFragment;
    private RepairsFragment repairsFragment;
    private ServiceRepairsFragment serviceRepairsFragment;
    private NavigationController mNavigationController;
    public static int selected;
    private long exitTime = 0;//点击2次返回，退出程序

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initBottom();

        //默认“首页”
        setReplaceFragment(fristFragment);

        if (FristFragment.select == getIntent().getIntExtra("watch", 1)) {
            setReplaceFragment(watchFragment);
            mNavigationController.setSelect(1);
        }else if (FristFragment.select == getIntent().getIntExtra("repairs", 2)) {
            setReplaceFragment(repairsFragment);
            mNavigationController.setSelect(2);
        }else if (FristFragment.select == getIntent().getIntExtra("service_repairs", 3)||RepairsFragment.stutesOK==1) {
            setReplaceFragment(serviceRepairsFragment);
            mNavigationController.setSelect(3);
        }



    }

    private void initFragment() {
        fristFragment = new FristFragment();
        watchFragment = new WatchFragment();
        repairsFragment = new RepairsFragment();
        serviceRepairsFragment = new ServiceRepairsFragment();
    }

    private void initBottom() {
        PageBottomTabLayout pageBottomTabLayout = (PageBottomTabLayout) findViewById(R.id.tab);
        //未选中状态的颜色
        mNavigationController = pageBottomTabLayout.material()
                .addItem(R.drawable.ic_main_frist, "首页", getResources().getColor(R.color.color_main_select))
                .addItem(R.drawable.ic_main_meterreading, "抄表", getResources().getColor(R.color.color_main_select))
                .addItem(R.drawable.ic_main_repairs, "报修", getResources().getColor(R.color.color_main_select))
                .addItem(R.drawable.ic_main_maintain, "维修", getResources().getColor(R.color.color_main_select))
                .setDefaultColor(getResources().getColor(R.color.color_main_no_select))//未选中状态的颜色
                .build();
        //设置Item选中事件的监听
        mNavigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //如果要替换Fragment，则为：
                switch (index) {
                    case 0:
                        setReplaceFragment(fristFragment);
                        break;
                    case 1:
                        if (FristFragment.select == 0) ;
                        setReplaceFragment(watchFragment);
                        break;
                    case 2:
                        setReplaceFragment(repairsFragment);
                        break;
                    case 3:
                        setReplaceFragment(serviceRepairsFragment);
                        break;
                }
            }

            @Override
            public void onRepeat(int index) {
                //重复选中
                // Log.i("asd", "onRepeat selected: " + index);
            }
        });

    }


    //点击两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {//两秒内再次点击返回则退出
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置替换的Fragment
     */
    private void setReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment).commit();
    }
}
