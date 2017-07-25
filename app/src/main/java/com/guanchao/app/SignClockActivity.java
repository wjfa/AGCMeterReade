package com.guanchao.app;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MyLocationStyle;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.service.MyMusicService;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SaveUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.guanchao.app.utils.StatusBarUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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
    @BindView(R.id.rel_sign_work)
    RelativeLayout relLogin;//刷新页面
    private int status;//上下班签到状态
    private static boolean isSelectGoWork = false;//是否点击上班  默认是未签到
    private static boolean isSelectDownWork = false;//是否点击下班  默认是未签到
    private ActivityUtils activityUtils;
    private SaveUtils saveUtils;
    private static String goworkSuccTime;
    private static String downWorkSuccTime;
    //利用SharedPreferences对时间的存储
    private static String goWorkDate;//上班打卡日期  点击签到后获取系统日期  为了与第二天对比是不是今天过去了
    private LocationManager locationManager;//定位管理器
    private String locationProvider;
    private String edtContent;//迟到早退的控件

    //定位
    private AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double longitude;
    private double lantitude;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置到经纬度控件上
            Bundle data = msg.getData();
            longitude = data.getDouble("mLongitude");
            lantitude = data.getDouble("mLantitude");
            Log.e("经纬度", "经度：" + longitude + "   纬度：" + lantitude);
            //activityUtils.showToast("获取位置信息成功");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //此方法要再setContentView方法之前实现
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_sign_clock);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        saveUtils = new SaveUtils(this);

   /*     //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        //获取日期转换成星期
        tvWeek.setText(activityUtils.StringDataDay(false));
        tvDate.setText(activityUtils.setCurrentTime("年月日"));
        /*从本地获取是否点击签到的控件*/
        isSelectGoWork = saveUtils.getGoStatus();
        isSelectDownWork = saveUtils.getDownValue();
        init();
        locationPermission();//获取地理位置权限（定位）
        // initPositions();//定位管理器


    }

    private void init() {
        //从本地获取日期
        SharedPreferences sp = getSharedPreferences("goWorkTime", Context.MODE_PRIVATE);
        goWorkDate = sp.getString("goWork", "未获取到日期");
        if (goWorkDate != null) {
            if (activityUtils.setCurrentTime("年月日").equals(goWorkDate)) {
                // Log.e("时间", "onCreate: " + activityUtils.setCurrentTime("年月日") + "    " + goWorkDate);
                if (isSelectGoWork) {
                    //设置“上班”控件成功
                    btnGoSign.setBackgroundResource(R.color.lavenderblush);
                    btnGoSign.setTextColor(getResources().getColor(R.color.diablo));
                    /*获取本地时间*/
                    goworkSuccTime = saveUtils.getSingGoTime();
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
                    downWorkSuccTime = saveUtils.getClockDownTime();
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
                    }, 150);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ("星期六".equals(activityUtils.StringDataDay(false)) || "星期日".equals(activityUtils.StringDataDay(false))) {

                    relOvertime.setVisibility(View.VISIBLE);
                    tvResttime.setVisibility(View.VISIBLE);
                    relUpDown.setVisibility(View.GONE);
                    //显示“切换到加班页面”
                    relOvertime.setVisibility(View.VISIBLE);
                    tvOvertime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialogWorkvertime("是否进入加班考勤模式");
                        }
                    });
                } else {
                    relOvertime.setVisibility(View.GONE);
                    tvResttime.setVisibility(View.GONE);
                    relUpDown.setVisibility(View.VISIBLE);
                }
            }
        }).start();

    }


    /**
     * 上班签到控件设置
     */
    private void upWork() {

        /*存储日期到本地 为了与第二天对比是不是今天过去了,如果今天过去了就还原打卡状态*/
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


    }

    /**
     * 下班签到控件设置
     */
    private void downWork() {
        /*存储点击签到的状态*/
        isSelectDownWork = true;
        saveUtils.setDownValue(isSelectDownWork);

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

        //显示“切换到加班页面”
        relOvertime.setVisibility(View.VISIBLE);
        tvOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogWorkvertime("是否进入加班考勤模式");
            }
        });
    }

    /**
     * 定位初始化
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        mLocationOption.setOnceLocation(true);  //该方法默认为false。true表示启动单次定位，false表示使用默认的连续定位策略
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
//        mLocationOption.setInterval(1000);
        mLocationOption.setNeedAddress(true); //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setWifiActiveScan(false);//设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setHttpTimeOut(1000);  //设置定位时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setLocationCacheEnable(true);//可选，设置是否使用缓存定位，默认为true
        //设置定位参数
        locationClient.setLocationOption(mLocationOption);

        // 启动定位
        locationClient.startLocation();
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (null != aMapLocation) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (aMapLocation.getErrorCode() == 0) {
//                    activityUtils.showToast("定位成功");
                    activityUtils.showToast("高德地图: " + "定位成功" +"    返回值： "+aMapLocation.getErrorCode()+ "\n" + "经    度    : " + aMapLocation.getLongitude() + "纬    度    : " + aMapLocation.getLatitude()
                            + "\n" + "地    址    : " + aMapLocation.getAddress() + "\n" + "兴趣点    : " + aMapLocation.getPoiName());

                    Log.e("", "onLocationChanged: " + "定位成功" +"    返回值： "+aMapLocation.getErrorCode()+ "\n" + "经    度    : " + aMapLocation.getLongitude() + "纬    度    : " + aMapLocation.getLatitude()
                            + "\n" + "地    址    : " + aMapLocation.getAddress() + "\n" + "兴趣点    : " + aMapLocation.getPoiName());

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("mLongitude", aMapLocation.getLongitude());
                    bundle.putDouble("mLantitude",  aMapLocation.getLatitude());
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } else {
                    //定位失败
                    activityUtils.showToast("定位失败" + "\n" + "错误码:" + aMapLocation.getErrorCode() + "\n" + "错误信息:" + aMapLocation.getErrorInfo() + "\n"
                            + "错误描述:" + aMapLocation.getLocationDetail() + "\n");
                }

            } else {
                activityUtils.showToast("定位失败");
            }
        }
    };

    /**
     * 动态获取访问您的地理位置权限
     */
    private void locationPermission() {
        /**
         * 动态获取访问相机  Manifest.permission.CAMERA
         */
        if (PermissionsUtil.hasPermission(SignClockActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //成功允许时的操作
            initLocation();
        } else {
            PermissionsUtil.requestPermission(SignClockActivity.this, new PermissionListener() {
                @Override//接受
                public void permissionGranted(@NonNull String[] permissions) {
                    initLocation();
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
     * 上班打卡  网络请求
     */
    private void okHttpSignClockGoWork() {
        //获取当前时间
        String currentTime = activityUtils.setCurrentTime("年-月-日");
        String userId = SharePreferencesUtils.getUser().getId();
        relLogin.setVisibility(View.VISIBLE);
        Log.e("上班经纬度", "okHttpSignClockGoWork: " + longitude + "       " + lantitude);
//        Log.e("上班经纬度", "okHttpSignClockGoWork: "+longitude+"       "+lantitude );
        OkHttpClientEM.getInstance().signClickGoWork(currentTime, userId, edtContent, longitude, lantitude).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                relLogin.setVisibility(View.GONE);
                activityUtils.showDialog("用户登入", "网络异常，请重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Log.e("上班", "onResponseUI: " + json);
                BaseEntity entity = Parser.parserSignClickWork(json);
                if (entity.getSuccess() == true) {
                    relLogin.setVisibility(View.GONE);
                    openDialogSign(LayoutInflater.from(SignClockActivity.this).inflate(R.layout.dialog_go_work, null));//弹出成功对话框
                    upWork();//打卡成功时设置控件
                    //成功振动效果
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);//振动的频率
                    //播放提示音月（用到了Service服务）
                    Intent intent2 = new Intent(SignClockActivity.this, MyMusicService.class);
                    startService(intent2);

                } else {
                    relLogin.setVisibility(View.GONE);
                    activityUtils.showDialog("签到打卡", entity.getMessage());
                }
            }
        });
    }

    /**
     * 下班打卡  网络请求
     */
    private void okHttpSignClockDownWork() {
        //获取当前时间
        String currentTime = activityUtils.setCurrentTime("年-月-日");
        String userId = SharePreferencesUtils.getUser().getId();
        relLogin.setVisibility(View.VISIBLE);
        Log.e("下班经纬度", "okHttpSignClockGoWork: " + longitude + "       " + lantitude);
        OkHttpClientEM.getInstance().signClickDownWork(currentTime, userId, edtContent, longitude, lantitude).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                relLogin.setVisibility(View.GONE);
                activityUtils.showDialog("用户登入", "网络异常，请重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Log.e("下班", "onResponseUI: " + json);
                BaseEntity entity = Parser.parserSignClickWork(json);
                if (entity.getSuccess() == true) {
                    relLogin.setVisibility(View.GONE);
                    //弹出成功对话框
                    openDialogSign(LayoutInflater.from(SignClockActivity.this).inflate(R.layout.dialog_down_work, null));
                    downWork();//签退成功设置控件
                    //成功振动效果
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);//振动的频率
                    //播放提示音月（用到了Service服务）
                    Intent intent2 = new Intent(SignClockActivity.this, MyMusicService.class);
                    startService(intent2);
                } else {
                    relLogin.setVisibility(View.GONE);
                    activityUtils.showDialog("签到打卡", entity.getMessage());
                }
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
                    okHttpSignClockGoWork();//上班打卡 网络请求
                }
                break;
            case R.id.btn_sign_down://下班签到
                //获取当前时间与16:00比较
                if (Long.valueOf(compareTime(activityUtils.setCurrentTime("时分"))) < Long.valueOf(compareTime("18:00"))) {
                    status = 2;
                    showDialogWorkContent("下班考勤早退", "请输入您下班早退的理由");
                } else {
                    okHttpSignClockDownWork();//下班打卡 网络请求
                }
                break;
            case R.id.btn_sign_position://获取经纬度
                initLocation();//定位管理器
                break;
        }
    }

    /**
     * 签到成功展示对话框
     *
     * @param view 签到成功或失败的布局
     */
    private void openDialogSign(View view) {
        dialogSign = new Dialog(this, R.style.processDialog);
        dialogSign.setContentView(view);
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
                                edtContent = edtContext.getText().toString().trim();
                                if (edtContent.length() == 0) {
                                    activityUtils.showToast("理由不能为空！");
                                } else {
                                    if (status == 1) {
                                        okHttpSignClockGoWork();//上班打卡 网络请求

                                    }
                                    if (status == 2) {
                                        okHttpSignClockDownWork();//下班打卡 网络请求

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

    /**
     * 是否进入加班考勤模式
     *
     * @param msg
     */
    private void showDialogWorkvertime(String msg) {
        //context内容
        View viewContex = LayoutInflater.from(this).inflate(R.layout.dialog_work_content2, null);
        TextView tvContext = (TextView) viewContex.findViewById(R.id.tv_context_work);
        tvContext.setText(msg);
        //弹窗
        final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(viewContex))
                .setFooter(R.layout.dialog_work_footer)//添加脚布局
                .setInAnimation(R.anim.alpha1)//类似于IOS底部出现效果
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.tv_work_no:
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                break;
                            case R.id.tv_work_yes://确定
                                startActivity(new Intent(SignClockActivity.this, SingClockWorkActivity.class));
                                dialog.dismiss();
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
    /**
     * 中国正常坐标系GCJ02(火星坐标)协议的坐标，高德地图  转换到 百度地图对应的 BD09(百度坐标) 协议坐标
     *
     * @param lat
     * @param lng
     * @return 0经度 1纬度
     */
    private  double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    public  double[] Convert_GCJ02_To_BD09_Lat( double lng,double lat) {
        double x = lng, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        //对double类型数据保留小数点后多少位，高德地图转码返回的就是 小数点后6位，
        lng = dataDigit(6,z * Math.cos(theta) + 0.0065);
        lat = dataDigit(6,z * Math.sin(theta) + 0.006);
        double[] lngLat={lng,lat};
        return lngLat;
    }
    /**
     * 对double类型数据保留小数点后多少位
     *  高德地图转码返回的就是 小数点后6位，为了统一封装一下
     * @param digit 位数
     * @param in 输入
     * @return 保留小数位后的数
     */
    static double dataDigit(int digit,double in){
        return new BigDecimal(in).setScale(digit,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    protected void onStop() {
        // 停止定位
        locationClient.stopLocation();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            mLocationOption = null;
        }
        super.onDestroy();
    }

}
