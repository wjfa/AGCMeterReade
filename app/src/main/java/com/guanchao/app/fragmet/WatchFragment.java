package com.guanchao.app.fragmet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.guanchao.app.ArtificialPhotoActivity;
import com.guanchao.app.R;
import com.guanchao.app.adapter.WatchAdapter;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.User;
import com.guanchao.app.entery.Watch;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 抄表页面
 */
public class WatchFragment extends Fragment {
    View view;
    @BindView(R.id.tv_show_time)
    TextView tvShowTime;
    @BindView(R.id.tv_select_time)
    TextView tvSelectTime;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyView_watch)
    RecyclerView recyView;
    @BindView(R.id.rel_watch_ref)
    RelativeLayout relRefresh;//刷新页面
    private WatchAdapter watchAdapter;
    private ActivityUtils activityUtils;
    private TimePickerView pvCustomTime;
    private String timeControls;
    private BaseEntity<List<Watch>> parserWatchList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_watch, container, false);
        ButterKnife.bind(this, view);
        activityUtils = new ActivityUtils(getActivity());

        //获取当前时间
        tvShowTime.setText(activityUtils.setCurrentTime("年月"));

        setWatchNetRequest();
        //设置一下actionbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        return view;
    }

    public static String taskId;
    //item监听
    private WatchAdapter.onNewItemClickListener newItemClickListener = new WatchAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
            //将信息存储到本地（或者设置为静态变量start）
            String areaId = parserWatchList.getData().get(postion).getAreaId();
            taskId = parserWatchList.getData().get(postion).getTaskId();//设置为静态变量static  后面人工拍照需要用
            Watch watch = new Watch(areaId);
            SharePreferencesUtils.setWatch(watch);
            //实现效果
            //Toast.makeText(getActivity(), "点击了" + postion, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), ArtificialPhotoActivity.class));
        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 选择日期
     */
    @OnClick(R.id.tv_select_time)
    public void onClick() {
        initCustomTimePicker();
    }

    /**
     * 抄表任务 网络请求
     */
    private void setWatchNetRequest() {
        String time = tvShowTime.getText().toString();
        timeControls = time;
        //将2017-08替换为201708格式
        String newStrTime = timeControls.replace("-", "");
        Log.e("获取控件时间", newStrTime);
        //获取本地保存的数据
        User watchId = SharePreferencesUtils.getUser();
        String cbUserID = watchId.getId();
        //网络请求
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().watchTask(cbUserID, newStrTime).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility()==View.VISIBLE){
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("抄表任务列表","网络异常，请重试！");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Log.e("抄表", json);
                //解析json
                parserWatchList = Parser.parserWatch(json);
                if (parserWatchList.getSuccess() == true) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    //将信息存储到本地
                  /*  String areaId = parserWatchList.getData().get(postionID).getAreaId();
                    Watch watch=new Watch(areaId);
                    SharePreferencesUtils.setWatch(watch);*/

                    watchAdapter = new WatchAdapter(getActivity(), parserWatchList);
                    recyView.setAdapter(watchAdapter);
                    watchAdapter.notifyDataSetChanged();
                    recyView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    recyView.setItemAnimator(new DefaultItemAnimator());
                    //设置监听
                    watchAdapter.setonNewItemClickListener(newItemClickListener);
                    if (parserWatchList.getData().size() == 0) {
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showDialog("抄表任务列表","该月份无抄表任务记录");
                    } else {
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
                        //activityUtils.showToast(parserWatchList.getMessage());
                    }
                } else {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("抄表任务列表",parserWatchList.getMessage());
                }

            }
        });
    }

    /**
     * 时间对话框
     */
    private void initCustomTimePicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();//开始时间
        startDate.set(2012, 5, 23);
        Calendar endDate = Calendar.getInstance();//结束时间
        endDate.set(2025, 5, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调

                //可根据需要自行截取数据显示在控件上  yyyy-MM-dd HH:mm:ss  或yyyy-MM-dd
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
                String timeFormat = format.format(date);
                tvShowTime.setText(timeFormat);
                //直接从对话框获取选择的时间
                Log.e("设置时间", getTime(date) + "");
            }
        })
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .isCyclic(false)// 是否循环
                .setTitleColor(Color.BLACK)
                .setLineSpacingMultiplier(2.4f)//设置两横线之间的间隔倍数
                .setDividerColor(Color.parseColor("#24E1E4"))//设置分割线的颜色
                .setDividerType(WheelView.DividerType.WRAP)//设置分割线的类型
                .setTextColorCenter(Color.parseColor("#FF4081"))//设置选中文字的颜色#64AE4A
                .setTextColorOut(Color.parseColor("#112079"))//设置选中项以外的颜色#64AE4A
                // .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.parseColor("#dbdbd8"))//背景颜色(必须是16进制) Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .gravity(Gravity.CENTER)//设置控件显示位置 default is center*/
                .isDialog(true)//设置显示位置(屏幕中心)
                .setOutSideCancelable(false)

                //.setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
                // 六种选择模式，年月日时分秒，年月日，时分，月日时分，年月，年月日时分
                .setType(TimePickerView.Type.YEAR_MONTH)//设置显示年月日类型
                .setDate(selectedDate)//系统当前时间
                .setRangDate(startDate, endDate)
                /**自定义时间布局填充
                 * 如果想使用默认的直接去掉.setLayoutRes()即可
                 * */
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView currtenTime = (TextView) v.findViewById(R.id.tv_content_time);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        //设置当前时间转换成星期几
                        currtenTime.setText("" + activityUtils.StringDataDay(true));
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                //获取控件时间
                                timeControls = tvShowTime.getText().toString();
                                Log.e("获取控件时间", timeControls + "");
                                //刷新适配器   网络请求
                                setWatchNetRequest();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .build();

        if (pvCustomTime != null) {
            pvCustomTime.show();
        }


    }

    //可根据需要自行截取数据显示在控件上  yyyy-MM-dd HH:mm:ss  或yyyy-MM-dd  或 HH:mm:ss
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }


}
