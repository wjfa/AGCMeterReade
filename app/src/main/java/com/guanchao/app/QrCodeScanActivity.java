package com.guanchao.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshViewFooter;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.adapter.UserSelectAdapter;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.UserSelcetWatchMessage;
import com.guanchao.app.entery.UserSelect;
import com.guanchao.app.entery.Watch;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.guanchao.app.utils.StatusBarUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;


/**
 * public static final float LIGHT_SUNLIGHT_MAX =120000.0f;
 * public static final float LIGHT_SUNLIGHT=110000.0f;
 * public static final float LIGHT_SHADE=20000.0f;
 * public static final float LIGHT_OVERCAST= 10000.0f;
 * public static final float LIGHT_SUNRISE= 400.0f;
 * public static final float LIGHT_CLOUDY= 100.0f;
 * public static final float LIGHT_FULLMOON= 0.25f;
 * public static final float LIGHT_NO_MOON= 0.001f;
 * <p>
 * 上面的八个常量只是临界值，在实际使用光线传感器时要根据实际情况确定一个范围。
 * 例如，当太阳逐渐升起时，values[0]的值很可能会超过LIGHT_SUNRISE，当values[0]的值逐渐增大时，
 * 就会逐渐越过LIGHT_OVERCAST，而达到LIGHT_SHADE，当然，如果天特别好的话，也可能会达到LIGHT_SUNLIGHT，甚至更高。
 */
public class QrCodeScanActivity extends AppCompatActivity {
    private SensorManager sensorManager;  //光线感应器管理器
    @BindView(R.id.close_flashlight)
    TextView tvlight;
    @BindView(R.id.scan_barcode)
    TextView tvCode;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.choose_qrcde_from_gallery)
    TextView chooseQrcdePhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.prBar)
    ProgressBar prgressBar;
    @BindView(R.id.rl_pressBar)
    RelativeLayout relPressBar;
    private QRCodeView mQRCodeView;//二维码
    private int light = 0;//0 开灯  1 关灯
    private boolean isQcode = false;//默认是条形码
    private static final int QRCODE_FROM_GALLERY = 666;//相册请求码
    public static int QrCode;
    private Sensor sensor;

    public static final int QrResult_Artifcial = 400;//二维码扫描数据传送到到人工拍照
    private ActivityUtils activityUtils;
    private String watermeterId;//水表id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        activityUtils = new ActivityUtils(this);
       /* //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        mQRCodeView = (ZXingView) findViewById(R.id.qc_code_zxingview);
        initQRCode();
        //获得感应器服务
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //注册监听器     光线传感器: Sensor.TYPE_LIGHT
        sensorManager.registerListener(eventLightistener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /**
     * 初始化二维码扫一扫
     */
    private void initQRCode() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mQRCodeView.setDelegate(delegate);
                mQRCodeView.startSpotAndShowRect();//开始识别显示扫描框
                mQRCodeView.showScanRect();//显示扫描框
                mQRCodeView.startCamera();//开始预览
                mQRCodeView.startSpotDelay(1000);// 延迟delay毫秒后开始识别
            }
        }, 1000);
    }


    /**
     * 扫描成功结果的处理监听
     */
    QRCodeView.Delegate delegate = new QRCodeView.Delegate() {
        @Override
        public void onScanQRCodeSuccess(String result) {

            // TODO: 2017/7/4 待网络上传 显示加载
            // relPressBar.setVisibility(View.VISIBLE);
            //扫描成功振动效果
            setVibrator();
//            Log.e("扫描结果", "onScanQRCodeSuccess: "+result);
            //用户查询 网络请求 为了获取水表watermeterId
            okHttpUserSelect(result);
            //选择用户 网络请求 获取信息  需要睡眠一段时间，否则可能先请求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        okHttpUserSelectWatch(watermeterId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            QrCode = 1;
//            startActivity(new Intent(QrCodeScanActivity.this, QrCodeShowMsgActivity.class).putExtra("qcResult", result));
//            finish();//必须要finish 不然该页面不关闭相机一直开着

            mQRCodeView.startSpot();//扫描成功是否在此二维码页面继续识别
        }
        @Override
        public void onScanQRCodeOpenCameraError() {
            //返回到主页面
//            startActivity(new Intent(QrCodeScanActivity.this, MainActivity.class));
//            finish();
            Toast.makeText(QrCodeScanActivity.this, "打开相机出错啦，请查看相机权限是否开启", Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * 用户选择   网络请求
     */
    private void okHttpUserSelect(String customerNo) {
        //获取存储本地的抄表区域areaId
        Watch userWatch = SharePreferencesUtils.getUserWatch();
        String areaId = userWatch.getAreaId();
        // relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().userSelectQrCode(areaId, customerNo).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
//                if (relRefresh.getVisibility() == View.VISIBLE) {
//                    relRefresh.setVisibility(View.GONE);
//                }
                activityUtils.showDialog("用户选择", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
//                Log.e("用户选择", "onResponseUI: " + json);
                BaseEntity<UserSelect> entity = Parser.parserUserSelect(json);
                if (entity.getSuccess() == true) {
//                    if (relRefresh.getVisibility() == View.VISIBLE) {
//                        relRefresh.setVisibility(View.GONE);
//                    }
                    List<UserSelect.ListBean> listBean = entity.getData().getList();
                    //如果返回的list为空 则提示
                    if (listBean.size() == 0) {
                        activityUtils.showDialog("抄表信息", "该户不是本组用户，请您扫描本组的用户");
                        return;
                    } else {
                        for (int i = 0; i < listBean.size(); i++) {
                            //获取水表id
                            watermeterId = listBean.get(i).getWatermeterId();
                        }
                    }

                    Log.e("获取水表id", "onResponseUI: " + watermeterId + "  " + json);
                    // activityUtils.showToast("扫描成功");
                } else {
//                    if (relRefresh.getVisibility() == View.VISIBLE) {
//                        relRefresh.setVisibility(View.GONE);
//                    }
                    activityUtils.showDialog("用户选择", entity.getMessage());
                }

            }
        });
    }

    /**
     * 用户选择后抄表页面获取用户信息  网络请求
     */
    private void okHttpUserSelectWatch(final String watermeterId) {
//        relRefresh.setVisibility(View.VISIBLE);
        //如果水表Id为空则返回
        if (watermeterId == null) {
            return;
        } else {
            OkHttpClientEM.getInstance().userSelectWatchInform(watermeterId).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
//                if (relRefresh.getVisibility() == View.VISIBLE) {
//                    relRefresh.setVisibility(View.GONE);
//                }
                    activityUtils.showDialog("获取用户信息", "网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                Log.e("用户二维码扫描", "onResponseUI: " + watermeterId);
                    BaseEntity<UserSelcetWatchMessage> entity = Parser.parserUserSelectWatchMsg(json);
                    if (entity.getSuccess() == true) {
//                    if (relRefresh.getVisibility() == View.VISIBLE) {
//                        relRefresh.setVisibility(View.GONE);
//                    }
                        UserSelcetWatchMessage userWatchMsg = entity.getData();
                        String customerNo = userWatchMsg.getCustomerNo();
                        String customerName = userWatchMsg.getName();
                        String houseNumber = userWatchMsg.getHouseNumber();
                        String phone = userWatchMsg.getMobile();
                        String address = userWatchMsg.getAddress();
                        String oldReading = userWatchMsg.getReading();
                        String watermeterNo = userWatchMsg.getWatermeterNo();

                        String location = userWatchMsg.getLocation();
                        String longitude = (String) userWatchMsg.getLongitude();
                        String latitude = (String) userWatchMsg.getLatitude();

                        UserSelcetWatchMessage userSelcetWatchMessage = new UserSelcetWatchMessage(customerNo, customerName, houseNumber, phone, address, oldReading, watermeterNo, location, longitude, latitude);
                        //返回结果 跳转到人工拍照页面 把信息带过去
                        setResult(QrResult_Artifcial, new Intent().putExtra("QRCodeResult", userSelcetWatchMessage));
                        finish();
                        Log.e("解析成功", "onResponseUI: " + userSelcetWatchMessage + "          " + json);

                    } else {
//                    if (relRefresh.getVisibility() == View.VISIBLE) {
//                        relRefresh.setVisibility(View.GONE);
//                    }
                        activityUtils.showDialog("获取用户信息", entity.getMessage());
                    }
                }
            });
        }

    }

    @OnClick({R.id.close_flashlight, R.id.scan_barcode, R.id.tv_back, R.id.choose_qrcde_from_gallery})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.choose_qrcde_from_gallery://相册
                /**
                 * 动态获取访问本地的照片  媒体文件内容和文件的权限  Manifest.permission.WRITE_EXTERNAL_STORAGE
                 */

                if (PermissionsUtil.hasPermission(QrCodeScanActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //成功跳转到本地相册  成功允许时的操作
                    startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), QRCODE_FROM_GALLERY);
                    // Toast.makeText(QrCodeScanActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
                } else {
                    PermissionsUtil.requestPermission(QrCodeScanActivity.this, new PermissionListener() {
                        @Override//接受
                        public void permissionGranted(@NonNull String[] permissions) {
                            Toast.makeText(QrCodeScanActivity.this, "用户成功授权访问本地照片和媒体文件", Toast.LENGTH_LONG).show();
                        }

                        @Override//拒绝
                        public void permissionDenied(@NonNull String[] permissions) {
                            Toast.makeText(QrCodeScanActivity.this, "用户残忍拒绝访问本地照片和媒体文件", Toast.LENGTH_LONG).show();
                        }
                    }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }

                break;
            case R.id.scan_barcode://二维码
                if (isQcode == false) {
                    mQRCodeView.changeToScanBarcodeStyle();//条形码
                    tvCode.setSelected(true);
                    tvCode.setText("二维码");
                    isQcode = true;
                } else if (isQcode == true) {
                    mQRCodeView.changeToScanQRCodeStyle();//二维码
                    tvCode.setSelected(false);
                    tvCode.setText("条形码");
                    isQcode = false;
                }
                break;
            case R.id.close_flashlight://灯
                if (light == 0) {
                    mQRCodeView.openFlashlight();
                    tvlight.setSelected(true);
                    tvlight.setText("关灯");
                    light = 1;
                } else if (light == 1) {
                    mQRCodeView.closeFlashlight();
                    tvlight.setSelected(false);
                    tvlight.setText("开灯");
                    light = 0;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();

        //本地相册
        if (resultCode == Activity.RESULT_OK && requestCode == QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            /*
            *同步解析本地图片二维码。该方法是耗时操作，请在子线程中调用。
            * @param picturePath 要解析的二维码图片本地路径
            * @return 返回二维码图片里的内容 或 null*/
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        QrCode = 2;
                        //扫描成功振动效果
                        setVibrator();
                        Toast.makeText(QrCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(QrCodeScanActivity.this, QrCodeShowMsgActivity.class).putExtra("qcResultError", "未发现二维码"));
                        mQRCodeView.startSpot();//扫描成功是否继续识别
                    } else {
                        QrCode = 3;
                        //扫描成功振动效果
                        setVibrator();
                        startActivity(new Intent(QrCodeScanActivity.this, QrCodeShowMsgActivity.class).putExtra("photoResult", result));
                        mQRCodeView.startSpot();//扫描成功是否继续识别
                        Toast.makeText(QrCodeScanActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    /**
     * 感应器事件监听器
     */
    private SensorEventListener eventLightistener = new SensorEventListener() {

        //当感应器精度发生变化
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        //当传感器监测到的数值发生变化时
        @Override
        public void onSensorChanged(SensorEvent event) {
            //mQRCodeView.openFlashlight();//开灯
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                // values数组中第一个值就是当前的光照强度
                // 获取光线强度
                float value = event.values[0];
                //判断光度强度值在什么范围内  打开灯光
                if (value >= SensorManager.LIGHT_NO_MOON && value <= SensorManager.LIGHT_FULLMOON) {
                    mQRCodeView.openFlashlight();//开灯
                    tvlight.setSelected(true);
                    tvlight.setText("关灯");
                    light = 1;
                }
            }
//            light.setText("当前亮度 " + value + " lx(勒克斯)");
        }
    };

    public void setVibrator() {
        //扫描成功振动效果
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);//振动的频率
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        // mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        //注销光线传感器
        if (sensorManager != null) {
            sensorManager.unregisterListener(eventLightistener, sensor);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        /**
         * 停止识别，并且隐藏扫描框
         */
        mQRCodeView.stopSpotAndHiddenRect();
        mQRCodeView.onDestroy();
        //注销光线传感器
        if (sensorManager != null) {
            sensorManager.unregisterListener(eventLightistener, sensor);
        }

        ButterKnife.bind(this).unbind();
        super.onDestroy();
    }
}
