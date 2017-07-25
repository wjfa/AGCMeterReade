package com.guanchao.app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.ImgUpdate;
import com.guanchao.app.entery.UserSelcetWatchMessage;
import com.guanchao.app.fragmet.WatchFragment;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.ImageUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 人工拍照页面
 */
public class ArtificialPhotoActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_aretificl_photo)
    ImageView imgPhoto;//图片展示
    @BindView(R.id.tv_artifical_prompt)
    TextView tvlPrompt;//拍照提示
    @BindView(R.id.tv_artific_select)
    TextView tvUserSelect;//用户选择
    @BindView(R.id.tv_artific_get_position)
    TextView tvPosition;//获取位置
    @BindView(R.id.tv_artific_photo_collect)
    TextView tvPhotoCollect;//拍照
    @BindView(R.id.edt_artific_house_number)
    EditText edtHouseNumber;//户号
    @BindView(R.id.edt_artific_house_name)
    EditText edtHouseName;//户名
    @BindView(R.id.edt_artific_house_door_number)
    EditText edtDoorNumber;//门牌号
    @BindView(R.id.edt_artific_phone)
    EditText edtPhone;//电话
    @BindView(R.id.edt_artific_addres)
    EditText edtAddres;//详细地址
    @BindView(R.id.edt_artific_last_reade)
    EditText edtOldtReade;//上次读数
    @BindView(R.id.edt_artific_now_reade)
    EditText edtNewReade;//本次读数
    @BindView(R.id.edt_artific_water_number)
    TextView edtWaterNumber;//水表钢号
    @BindView(R.id.edt_artific_now_amouter)
    EditText edtNewDosage;//本次用量
    @BindView(R.id.edt_artific_longitude)
    EditText edtLongitude;//经度
    @BindView(R.id.edt_artific_Latitude)
    EditText edtLatude;//纬度
    @BindView(R.id.edt_artific_installation_position)
    EditText edtInstallPosition;//安装位置
    @BindView(R.id.edt_artific_note)
    EditText edtRemark;//备注
    @BindView(R.id.btn_aretific_ok)
    Button btnOk;
    @BindView(R.id.rel_artficl_ref)
    RelativeLayout relRefresh;//刷新页面
    private static int REQUEST_CODE = 100;
    private int IMAGEVIEWSTATUS;//设置点击相机或相册的状态
    protected static final int TAKE_PICTURE = 1;//相机请求码
    protected static final int CHOOSE_PICTURE = 2;//相册请求码
    private static final int CROP_SMALL_PICTURE = 3;//图片裁剪后按确认
    public static String imagePath = "", portraitPath = "";//图片路径   设置图片路径
    private ActivityUtils activityUtils;
    private Uri tempUri;
    private String fileId;
    private boolean isSelectPhoto = false;//是否已经设置图片在控件上
    private String imgFilePath;
    private File tempFile;
    private final int QrCodeRequest = 500;//跳转二维码扫描请求吗

    //定位
    private AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置到经纬度控件上
            Bundle data = msg.getData();
            double longitude = data.getDouble("mLongitude");
            double lantitude = data.getDouble("mLantitude");
            edtLongitude.setText(longitude + "");
            edtLatude.setText(lantitude + "");
            //判断经纬度控件不为空  上传
            if (edtLongitude.length()!=0&&edtLatude.length()!=0){
                okHttpWaterLatLtude();//获取地理位置经纬度  网络上传
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artificial_photo);
        activityUtils = new ActivityUtils(this);
        //设置一下actionbar
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        //控件的输入监听
        edtHouseName.addTextChangedListener(textWatcher);
        edtNewReade.addTextChangedListener(textWatcher);
        //edtNewDosage.addTextChangedListener(textWatcher);
        edtRemark.addTextChangedListener(textWatcher);


    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (edtHouseName.length() == 0 || edtNewReade.length() == 0 || edtRemark.length() == 0) {// isSelectPhoto:false  未设置图片在控件上
                btnOk.setEnabled(false);
                btnOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            } else {
                //设置button可点击;
                btnOk.setEnabled(true);
                btnOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            }
            if (edtNewReade.getText().toString().trim().length() == 0) {
                edtNewDosage.setText("");
            } else {
                String newReading = edtNewReade.getText().toString().trim();
                String oldReading = edtOldtReade.getText().toString().trim();
                if (newReading.length() != 0 && oldReading.length() != 0) {
                    //本次用量=本次读数-上次读数
                    double num = Double.valueOf(newReading) - Double.valueOf(oldReading);
                    String nowStr = num + "";
                    edtNewDosage.setText(nowStr);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * 获取剪切后的图片意图展示在控件上并保持到本地
     *
     * @param imageView
     */
    protected void setImageToView(Intent data, ImageView imageView) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            //将处理过的图片显示在界面上，并保存到本地
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    @OnClick({R.id.ig_back, R.id.img_aretificl_photo, R.id.edt_artific_now_amouter, R.id.tv_artific_select, R.id.tv_artific_get_position, R.id.tv_artific_photo_collect, R.id.btn_aretific_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back://返回
                finish();
                break;
           /* case R.id.edt_artific_now_amouter:
                break;*/
            case R.id.tv_artific_select://用户选择
                Intent intent = new Intent(ArtificialPhotoActivity.this, UserSelectActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_artific_get_position://获取位置
                locationPermission();//获取地理位置权限（定位）
                break;
            case R.id.img_aretificl_photo://拍照
                setShowDialogPlus();
                break;
            case R.id.tv_artific_photo_collect://拍照
                setShowDialogPlus();
                break;
            case R.id.btn_aretific_ok://保存
                okHttpPeoplePhoto();//人工拍照 网络请求

                break;
        }
    }

    /**
     * 图片上传 网络请求
     */
    private void okHttpPhotoUpdate(String filePath) {
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().photoUpdate(filePath).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility() == View.VISIBLE) {
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("图片上传", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {

                BaseEntity<ImgUpdate> imgUpdate = Parser.parserImgUpdate(json);
                if (imgUpdate.getSuccess() == true) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    ImgUpdate imgData = imgUpdate.getData();
                    //获取文件ID
                    fileId = imgData.getFileId();
                    activityUtils.showToast("图片获取并上传成功");
                } else {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("图片上传", imgUpdate.getMessage());
                }
            }
        });
    }

    /**
     * 设置水表经纬度 网络请求
     */
    private void okHttpWaterLatLtude() {
        //获取水表ID  在用户选择抄表信息请求存储
        String watermeterId = UserSelectActivity.watermeterId;
        String longtude = edtLongitude.getText().toString().trim();
        String latude = edtLatude.getText().toString().trim();
        Log.e("位置上传", "onResponseUI: " + longtude + "        " + latude);

        relRefresh.setVisibility(View.VISIBLE);
        if ("null".equals(longtude) || "null".equals(latude)) {
            activityUtils.showDialog("水表经纬度", "水表经纬度不能为空");
        } else {
            OkHttpClientEM.getInstance().setWaterLatLtude(watermeterId, longtude, latude).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("水表经纬度", "网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    BaseEntity entity = Parser.parserWaterLatLutde(json);
                    if (entity.getSuccess() == true) {
                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showToast("获取位置信息成功");
                    } else {
                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showDialog("位置信息", entity.getMessage());
                    }
                }
            });
        }

    }

    /**
     * 人工拍照 网络请求
     */
    private void okHttpPeoplePhoto() {
        String taskId = WatchFragment.taskId;//获取任务ID  在抄表列表页面请求存储
        String waterId = UserSelectActivity.watermeterId;//获取水表ID  在用户选择抄表信息请求存储
        String waterReading = edtNewReade.getText().toString().trim();
        String remark = edtRemark.getText().toString().trim();

        if (waterReading.length() == 0 || edtNewDosage.getText().toString().length() == 0 || edtRemark.getText().toString().length() == 0) {
            activityUtils.showDialog("人工拍照", "请补全信息");
        } else if (isSelectPhoto == false) {
            activityUtils.showDialog("人工拍照", "请对水表的度数进行拍照");
        } else {
            relRefresh.setVisibility(View.VISIBLE);
            OkHttpClientEM.getInstance().peoplePhoto(taskId, waterId, waterReading, remark, fileId).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("人工拍照", "网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    Log.e("人工拍照", "onResponseUI: "+json );
                    BaseEntity entity = Parser.parserPeoplePhoto(json);
                    if (entity.getSuccess() == true) {
                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                        /*提交成功 清空还原初始控件*/
                        imgPhoto.setImageResource(R.drawable.ic_photo_kuang);
                        imgPhoto.setBackground(getResources().getDrawable(R.drawable.shape_border3));
                        tvlPrompt.setText("请对准您要拍照的物品");
                        edtHouseNumber.setText("");
                        edtHouseName.setText("");
                        edtDoorNumber.setText("");
                        edtPhone.setText("");
                        edtAddres.setText("");
                        edtOldtReade.setText("");
                        edtNewReade.setText("");
                        edtWaterNumber.setText("");
                        edtNewDosage.setText("");
                        edtLongitude.setText("");
                        edtLatude.setText("");
                        edtInstallPosition.setText("");
                        edtRemark.setText("");

                        //跳转
//                        startActivity(new Intent(ArtificialPhotoActivity.this,WatchFragment.class));
                        activityUtils.showToast("提交成功");//抄表成功
                    } else {
                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showDialog("人工拍照", entity.getMessage());
                    }

                }
            });
        }

    }

    /**
     * 定位
     */
    //初始化定位系统
    public void initLocation() {
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
        if (PermissionsUtil.hasPermission(ArtificialPhotoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //成功允许时的操作
            /*定位权限  定位和水表经纬度放在一起*/
            initLocation();//定位初始化
        } else {
            PermissionsUtil.requestPermission(ArtificialPhotoActivity.this, new PermissionListener() {
                @Override//接受
                public void permissionGranted(@NonNull String[] permissions) {
                    initLocation();//定位初始化
                    Toast.makeText(ArtificialPhotoActivity.this, "用户成功授权访问你的地理位置", Toast.LENGTH_LONG).show();
                }

                @Override//拒绝
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(ArtificialPhotoActivity.this, "用户残忍拒访问你的地理位置", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }


    /**
     * 相册和相机访问权限 对话框
     */
    private void requestCemera(String camera, final String granted, final String denied) {

        if (PermissionsUtil.hasPermission(this, camera)) {
            //权限允许成功的操作
            if (IMAGEVIEWSTATUS == 1) {
                //调用相机拍照
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    imgFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagePortrait.jpg";
                    tempFile = new File(imgFilePath);
                    tempUri = Uri.fromFile(tempFile);
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(openCameraIntent, TAKE_PICTURE);
                } else {
                    Toast.makeText(ArtificialPhotoActivity.this, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_SHORT).show();
                }

            } else if (IMAGEVIEWSTATUS == 2) {
                //调用本地相册
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_PICK);
                openAlbumIntent.setType("image/*");//图片类型
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            }

            //Toast.makeText(ArtificialPhotoActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(ArtificialPhotoActivity.this, granted, Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(ArtificialPhotoActivity.this, denied, Toast.LENGTH_LONG).show();
                }
            }, new String[]{camera});
        }
    }

    /**
     * 拍照  对话框显示:弹出对话框不会有黑屏现象（AlertDialog会出现）
     */

    private void setShowDialogPlus() {
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(LayoutInflater.from(this).inflate(R.layout.dialog_context_artifical, null)))
                .setFooter(R.layout.dialog_footer)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.tv_qrcode://扫二微码
                                /**
                                 * 动态获取访问相机  Manifest.permission.CAMERA
                                 */
                                if (PermissionsUtil.hasPermission(ArtificialPhotoActivity.this, Manifest.permission.CAMERA)) {
                                    //成功跳转二维码扫描  成功允许时的操作
                                    startActivityForResult(new Intent(ArtificialPhotoActivity.this, QrCodeScanActivity.class), QrCodeRequest);
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    // Toast.makeText(QrCodeScanActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
                                } else {
                                    PermissionsUtil.requestPermission(ArtificialPhotoActivity.this, new PermissionListener() {
                                        @Override//接受
                                        public void permissionGranted(@NonNull String[] permissions) {
                                            Toast.makeText(ArtificialPhotoActivity.this, "用户成功授权访问相机", Toast.LENGTH_LONG).show();
                                        }

                                        @Override//拒绝
                                        public void permissionDenied(@NonNull String[] permissions) {
                                            Toast.makeText(ArtificialPhotoActivity.this, "用户残忍拒绝访问相机", Toast.LENGTH_LONG).show();
                                        }
                                    }, new String[]{Manifest.permission.CAMERA});
                                }

                                break;

                            case R.id.tv_camera://相机
                                /**
                                 * 动态获取访问摄像头
                                 */
                                IMAGEVIEWSTATUS = 1;
                                requestCemera(Manifest.permission.CAMERA, "用户成功授权访问相机", "用户残忍拒绝访问相机");

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                break;
                            case R.id.tv_photo://相册
                                /**
                                 * 动态获取访问本地的照片 媒体文件内容和文件的权限
                                 */
                                IMAGEVIEWSTATUS = 2;
                                requestCemera(Manifest.permission.WRITE_EXTERNAL_STORAGE, "用户成功授权访问本地照片和媒体文件", "用户残忍拒绝访问本地照片和媒体文件");

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                break;
                            case R.id.tv_no://取消
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择用户返回数据
        if (requestCode == REQUEST_CODE && resultCode == UserSelectActivity.RESULT_CODE) {
            //将返回的数据设置在控件上
            UserSelcetWatchMessage userWatchMsg = (UserSelcetWatchMessage) data.getSerializableExtra("UserWatchMessage");
            edtHouseNumber.setText(userWatchMsg.getCustomerNo());
            edtHouseName.setText(userWatchMsg.getName());
            edtDoorNumber.setText(userWatchMsg.getHouseNumber());
            edtPhone.setText(userWatchMsg.getMobile());
            edtAddres.setText(userWatchMsg.getAddress());
            edtOldtReade.setText(userWatchMsg.getReading());
            edtWaterNumber.setText(userWatchMsg.getWatermeterNo());
            edtInstallPosition.setText(userWatchMsg.getLocation());
            edtLongitude.setText(userWatchMsg.getLongitude() + "");
            edtLatude.setText(userWatchMsg.getLatitude() + "");
        }
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE://相机
                    Log.e("开始对图片进行裁剪处理", "photoCamertPermissions: ");
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    } else {
                        Toast.makeText(ArtificialPhotoActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CHOOSE_PICTURE://相册
                    if (data != null) {
                        startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    }

                    break;
                case CROP_SMALL_PICTURE://图片裁剪后  按确认
                    if (data != null) {
                        setImageToView(data, imgPhoto); // 让刚才选择裁剪得到的图片显示在界面上
                        portraitPath = imagePath;
                        Log.e("路径2：", "uploadPic: " + "从本地获取照片" + portraitPath);
                        okHttpPhotoUpdate(portraitPath);//将图片上传服务器
                        isSelectPhoto = true;//是否已经设置图片在控件上
                        tvlPrompt.setText("");
                    }

                    break;
            }
        }
        /**
         * 将QrCodeScanActivity页面二维码扫描网络请求的信息 发送过来设置在控件上
         */
        //二维码扫描用户返回数据
        if (requestCode == QrCodeRequest && resultCode == QrCodeScanActivity.QrResult_Artifcial) {
            //将返回的数据设置在控件上
            UserSelcetWatchMessage userWatchMsg = (UserSelcetWatchMessage) data.getSerializableExtra("QRCodeResult");
            edtHouseNumber.setText(userWatchMsg.getCustomerNo());
            edtHouseName.setText(userWatchMsg.getName());
            edtDoorNumber.setText(userWatchMsg.getHouseNumber());
            edtPhone.setText(userWatchMsg.getMobile());
            edtAddres.setText(userWatchMsg.getAddress());
            edtOldtReade.setText(userWatchMsg.getReading());
            edtWaterNumber.setText(userWatchMsg.getWatermeterNo());
            edtInstallPosition.setText(userWatchMsg.getLocation());
            edtLongitude.setText(userWatchMsg.getLongitude() + "");
            edtLatude.setText(userWatchMsg.getLatitude() + "");
        }

    }
    /**
     * 后续要改为上传到服务器
     */

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        //imagePath指的是从相册或相机中选取照片点击“确定”时将裁剪的图片复制一份到指定的路径

        imagePath = ImageUtils.savePhoto(this, bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));

        Log.e("路径：", "uploadPic: " + imagePath);

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri != null) {
            tempUri = uri;
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_SMALL_PICTURE);
        }
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
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
