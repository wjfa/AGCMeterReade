package com.guanchao.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.ImgUpdate;
import com.guanchao.app.entery.ServiceRepair;
import com.guanchao.app.fragmet.ServiceRepairsFragment;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.ImageUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.R.attr.path;
import static com.guanchao.app.ArtificialPhotoActivity.CHOOSE_PICTURE;
import static com.guanchao.app.ArtificialPhotoActivity.TAKE_PICTURE;
import static com.guanchao.app.ArtificialPhotoActivity.imagePath;
import static com.guanchao.app.ArtificialPhotoActivity.portraitPath;

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
    @BindView(R.id.tv_service_content)
    TextView tvSContent;//维修内容提示
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
    private int controlsStaute;//设置控件状态
    protected static final int TAKE_PICTURES = 1;//相机请求码
    protected static final int CHOOSE_PICTURES = 2;//相册请求码
    private static final int CROP_PICTURE = 3;//图片裁剪后按确认
    public static String imgPath = "", repairsBefore = "", repairsIN = "", repairsAfter = "";//图片路径   设置图片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_repairs);
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
        // 取控件当前的布局参数
        ViewGroup.LayoutParams layoutParams = tvSContent.getLayoutParams();
        layoutParams.height = 380;// 当控件的高强制设成300象素
        tvSContent.setLayoutParams(layoutParams);// 使设置好的布局参数应用到控件
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edtServiceContent.length() != 0) {
                //设置button可点击;
                btnServiceOk.setClickable(true);
                btnServiceOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            } else {
                btnServiceOk.setClickable(false);
                btnServiceOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            }
            if (edtServiceContent.length() != 0) {
                tvSContent.setHint("");

            } else {
                tvSContent.setHint("暂无维修内容");
            }
            if (edtServiceContent.length() > 220) {//当输入的长度>180时设置控件高度300px像素
                ViewGroup.LayoutParams layoutParams2 = edtServiceContent.getLayoutParams();
                layoutParams2.height = 380;// 当控件的高强制设成300象素
                edtServiceContent.setLayoutParams(layoutParams2);// 使设置好的布局参数应用到控件
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
                controlsStaute = 2;
                setShowDialogPlus();
                break;
            case R.id.img_photo_after://维修后
                controlsStaute = 3;
                setShowDialogPlus();
                break;
            case R.id.btn_service_ok://保存
                okHttpServiceFault();
                break;
        }
    }

    /**
     * 图片上传　　网络请求
     */
    private void okHttpPhotoUpdate(List<File> path) {
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().photoUpdate2(path).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility()==View.VISIBLE){
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showToast("网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Log.e("图片上传2", "onResponseUI: " + json);
                BaseEntity<ImgUpdate> imgUpdate = Parser.parserImgUpdate(json);
                if (imgUpdate.getSuccess() == true) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    //获取文件ID
                    //activityUtils.showToast(imgUpdate.getMessage());
                } else {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showToast(imgUpdate.getMessage());
                }
            }
        });
    }

    /**
     * 维修故障　　网络请求
     */
    private void okHttpServiceFault() {
        //获取维修ID
        String id = serviceRepair.getId();
        String beforePic = serviceRepair.getRepairBeforePic() + "";
        String inPic = serviceRepair.getRepairInPic() + "";
        String afterPic = serviceRepair.getRepairAfterPic() + "";
        String repairsContent = edtServiceContent.getText().toString().trim();
        relRefresh.setVisibility(View.VISIBLE);
        if (repairsContent.length() != 0) {
            OkHttpClientEM.getInstance().serviceFault(id, beforePic, inPic, afterPic, repairsContent).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showToast("网络异常，请稍后重试  " + "");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    BaseEntity entity = Parser.parserServiceFault(json);
                    if (entity.getSuccess() == true) {
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
//                        startActivity(new Intent(ServiceFaultActivity.this,MainActivity.class));
//                        finish();
                        //activityUtils.showToast(entity.getMessage());
                    } else {
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showToast(entity.getMessage());
                    }
                }
            });
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
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                String pathName = "HeadPortrait.jpg";
                                tempUri = Uri.fromFile(new File(Environment
                                        .getExternalStorageDirectory() + "/" + pathName));
                                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                                startActivityForResult(openCameraIntent, TAKE_PICTURES);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                break;
                            case R.id.tv_photo://相册
                                Intent openAlbumIntent = new Intent(
                                        Intent.ACTION_PICK);
                                openAlbumIntent.setType("image/*");//图片类型
                                startActivityForResult(openAlbumIntent, CHOOSE_PICTURES);
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
                case CROP_PICTURE://图片裁剪后  按确认
                    if (data != null) {
                        if (controlsStaute == 1) {
                            setImageToView(data, imgPhotoBefore); // 让刚才选择裁剪得到的图片显示在界面上
                            repairsBefore = imgPath;
                            tvPrmopt1.setText("");
                            // Log.e("图片路径", "onActivityResult: "+repairsBefore );
                        } else if (controlsStaute == 2) {
                            setImageToView(data, imgPhotoNow);
                            repairsIN = imgPath;
                            tvPrmopt2.setText("");
                        } else if (controlsStaute == 3) {
                            setImageToView(data, imgPhotoAfter);
                            repairsAfter = imgPath;
                            tvPrmopt3.setText("");
                            //isSelectPhoto=true;
                            //设置第三张图片后上传
                            // TODO: 2017/6/18 判断三张图片都存在后上传
                            //图片文件集合
                            List<File> fileList = new ArrayList<>();
                            fileList.add(new File(repairsBefore));
                            fileList.add(new File(repairsIN));
                            fileList.add(new File(repairsAfter));
                            okHttpPhotoUpdate(fileList);//将图片上传服务器
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

        imgPath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("路径：", "uploadPic: " + imgPath);

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
            startActivityForResult(intent, CROP_PICTURE);
        }
    }
}
