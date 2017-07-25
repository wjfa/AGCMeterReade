package com.guanchao.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.ImgUpdate;
import com.guanchao.app.entery.ServiceRepair;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.ImageUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.R.attr.path;
import static com.guanchao.app.ArtificialPhotoActivity.CHOOSE_PICTURE;
import static com.guanchao.app.ArtificialPhotoActivity.TAKE_PICTURE;

/**
 * 故障维修
 */
public class ServiceFaultActivity extends AppCompatActivity {


    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_photo_before)
    ImageView imgPhotoBefore;//维修前
    @BindView(R.id.img_photo_now)
    ImageView imgPhotoNow;//维修中
    @BindView(R.id.img_photo_after)
    ImageView imgPhotoAfter;//维修后
    @BindView(R.id.edt_service_repairs_name)
    EditText edtRepairsName;//报修人
    @BindView(R.id.edt_service_phone)
    EditText edtServicePhone;//电话
    @BindView(R.id.edt_service_time)
    EditText edtServiceTime;//时间
    @BindView(R.id.edt_service_addres)
    EditText edtServiceAddres;//地址
    @BindView(R.id.edt_repairs_content)
    EditText edtRepairsContent;//报修内容

    @BindView(R.id.edt_service_content)
    EditText edtServiceContent;//维修内容
    @BindView(R.id.btn_service_ok)
    Button btnServiceOk;
    @BindView(R.id.activity_service_repairs)
    RelativeLayout activityServiceRepairs;
    @BindView(R.id.tv_prmopt1)
    TextView tvPrmopt1;
    @BindView(R.id.tv_prmopt2)
    TextView tvPrmopt2;
    @BindView(R.id.tv_prmopt3)
    TextView tvPrmopt3;
    @BindView(R.id.rel_service_fault_ref)
    RelativeLayout relRefresh;//刷新页面
    private ServiceRepair serviceRepair;
    private Uri tempUri;
    private ActivityUtils activityUtils;
    //private boolean isSelectPhoto;//判断是否设置图片在控件上
    private int controlsStaute;//设置点击三个照片的控件状态
    private int IMAGEVIEWSTATUS;//设置点击相机或相册的状态
    //判断图片是否展示（来设置按顺序拍照，不然会提示）
    private boolean isPicShow1 = false;
    private boolean isPicShow2 = false;
    private boolean isPicShow3 = false;
    protected static final int TAKE_PICTURES_fault = 1;//相机请求码
    protected static final int CHOOSE_PICTURES_fault = 2;//相册请求码
    private static final int CROP_PICTURE_fault = 3;//图片裁剪后按确认
    public static String imgPathm = "", repairsBefore = "", repairsIN = "", repairsAfter = "";//图片路径   设置图片路径

    private String imgFilePath;
    private File tempFile;
    private String beforePicId;//维修前图片id
    private String inPicId;//维修中图片id
    private String afterPicId;//维修后图片id
    private static int isCommitSucess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_fault);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        initControls();
    }

    //设置控件信息controls
    private void initControls() {
        //意图跳转后获取 信息
        serviceRepair = (ServiceRepair) getIntent().getSerializableExtra("ServiceRepair");
        edtRepairsName.setText(serviceRepair.getCallMan());
        edtServicePhone.setText(serviceRepair.getMobile());
        edtServiceTime.setText(serviceRepair.getRepairTime() + "");
        edtServiceAddres.setText(serviceRepair.getAddress());
        edtRepairsContent.setText(serviceRepair.getCallContent());

        edtServiceContent.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (edtServiceContent.length() != 0) {
                //设置button可点击;
                btnServiceOk.setEnabled(true);
                btnServiceOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            } else {
                btnServiceOk.setEnabled(false);
                btnServiceOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            }
        }
    };

    /**
     * 获取剪切后的图片意图展示在控件上
     *
     * @param data
     * @param imageView
     */
    protected void setImageToView(Intent data, ImageView imageView) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    @OnClick({R.id.ig_back, R.id.img_photo_before, R.id.img_photo_now, R.id.img_photo_after, R.id.btn_service_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back://返回
                finish();
                break;
            case R.id.img_photo_before://维修前
                controlsStaute = 1;
                setShowDialogPlus();
                break;
            case R.id.img_photo_now://维修中
                if (isPicShow1 == true) {//必须第一个拍过照片才可以拍第二张
                    controlsStaute = 2;
                    setShowDialogPlus();
                } else {
                    activityUtils.showDialog("上传图片", "请先进行维修前拍照");
                }

                break;
            case R.id.img_photo_after://维修后
                if (isPicShow1 == true && isPicShow2 == true) {//同理
                    controlsStaute = 3;
                    setShowDialogPlus();
                } else {
                    activityUtils.showDialog("上传图片", "请先进行维修前和维修中拍照");
                }

                break;
            case R.id.btn_service_ok://保存
                okHttpServiceFault();
                break;
        }
    }

    /**
     * 图片上传　　网络请求
     */
    private void okHttpPhotoUpdate(String path) {
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().photoUpdate(path).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility() == View.VISIBLE) {
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("图片上传", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Log.e("图片上传", "onResponseUI: " + json);
                BaseEntity<ImgUpdate> imgUpdate = Parser.parserImgUpdate(json);
                if (imgUpdate.getSuccess() == true) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    //获取文件ID 后面提交维修时需要
                    if (controlsStaute == 1) {
                        beforePicId = imgUpdate.getData().getFileId();
                    } else if (controlsStaute == 2) {
                        inPicId = imgUpdate.getData().getFileId();
                    } else if (controlsStaute == 3) {
                        afterPicId = imgUpdate.getData().getFileId();
                    }
                    activityUtils.showToast("图片上传成功");
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
     * 维修故障　　网络请求
     */
    private void okHttpServiceFault() {
        String id = SharePreferencesUtils.getUser().getId();
        String repairsContent = edtServiceContent.getText().toString().trim();
        if (isPicShow1 == false || isPicShow2 == false || isPicShow3 == false) {
            activityUtils.showDialog("故障维修", "请对维修前,中,后的维修情况进行拍照");
        } else {
            relRefresh.setVisibility(View.VISIBLE);
            OkHttpClientEM.getInstance().serviceFault(id, beforePicId, inPicId, afterPicId, repairsContent).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("故障维修", "网络异常，请稍后重试  " + "");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    Log.e("故障维修", "onResponseUI: " + json);
                    BaseEntity entity = Parser.parserServiceFault(json);
                    if (entity.getSuccess() == true) {
                        isCommitSucess = 1;

                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                       /*提交成功 返回上一页*/
                        finish();
//                        startActivity(new Intent(ServiceFaultActivity.this,ServiceRepairsFragment.class));
//                        finish();
                        activityUtils.showToast(entity.getMessage());
                    } else {
                        if (relRefresh.getVisibility() == View.VISIBLE) {
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showDialog("故障维修", entity.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 相册和相机访问权限
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
                    String pathName = "HeadPortrait.jpg";
                    tempUri = Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory() + "/" + pathName));
                    // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(openCameraIntent, TAKE_PICTURES_fault);
                } else {
                    Toast.makeText(ServiceFaultActivity.this, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_SHORT).show();
                }

            } else if (IMAGEVIEWSTATUS == 2) {
                //调用本地相册
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_PICK);
                openAlbumIntent.setType("image/*");//图片类型
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURES_fault);
            }

            //Toast.makeText(ArtificialPhotoActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(ServiceFaultActivity.this, granted, Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(ServiceFaultActivity.this, denied, Toast.LENGTH_LONG).show();
                }
            }, new String[]{camera});
        }
    }

    /**
     * 拍照  对话框显示:弹出对话框不会有黑屏现象（AlertDialog会出现）
     */
    private void setShowDialogPlus() {
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(LayoutInflater.from(this).inflate(R.layout.dialog_context, null)))
                .setFooter(R.layout.dialog_footer)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
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
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE://相机
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE://相册
                    if (data != null) {
                        startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    }

                    break;
                case CROP_PICTURE_fault://图片裁剪后  按确认
                    if (data != null) {
                        if (controlsStaute == 1) {
                            isPicShow1 = true;
                            setImageToView(data, imgPhotoBefore); // 让刚才选择裁剪得到的图片显示在界面上
                            repairsBefore = imgPathm;
                            okHttpPhotoUpdate(repairsBefore);//将图片上传服务器
                            tvPrmopt1.setText("");
                            // Log.e("图片路径", "onActivityResult: "+repairsBefore );
                        } else if (controlsStaute == 2) {
                            isPicShow2 = true;
                            setImageToView(data, imgPhotoNow);
                            repairsIN = imgPathm;
                            okHttpPhotoUpdate(repairsIN);//将图片上传服务器
                            tvPrmopt2.setText("");
                        } else if (controlsStaute == 3) {
                            isPicShow3 = true;
                            setImageToView(data, imgPhotoAfter);
                            repairsAfter = imgPathm;
                            tvPrmopt3.setText("");
                            //isSelectPhoto=true;
                            okHttpPhotoUpdate(repairsAfter);//将图片上传服务器
                        }

                        // Log.e("路径2：", "uploadPic: " + portraitPath);
                        //okHttpPhotoUpdate(portraitPath);
                    }

                    break;
            }
        }
    }

    /**
     * 后续要改为上传到服务器
     */
    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        //imagePath指的是从相册或相机中选取照片点击“确定”时将裁剪的图片复制一份到指定的路径

        imgPathm = ImageUtils.savePhoto(this, bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("路径：", "uploadPic: " + imgPathm);

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
            startActivityForResult(intent, CROP_PICTURE_fault);
        }
    }
}
