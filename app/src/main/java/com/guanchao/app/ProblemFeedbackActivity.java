package com.guanchao.app;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.guanchao.app.entery.UserSelcetWatchMessage;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.ImageUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.guanchao.app.utils.StatusBarUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
/**
 * 问题反馈
 */
public class ProblemFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_problem_title)
    EditText edtTitle;
    @BindView(R.id.edt_problem_details)
    EditText edtDetails;
    @BindView(R.id.img_up_photo)
    ImageView imgUpPhoto;
    @BindView(R.id.btn_problem_photo)
    Button btnPhoto;
    @BindView(R.id.btn_problem_camear)
    Button btnCamear;
    @BindView(R.id.edt_problem_phone)
    EditText edtPhone;
    @BindView(R.id.btn_problem_ok)
    Button btnProblemOk;
    @BindView(R.id.rel_feedback_loading)
    RelativeLayout relLoading;//刷新页面
    private ActivityUtils activityUtils;
    protected static final int TAKE_PICTURE_feedback = 1;//相机请求码
    protected static final int CHOOSE_PICTURE_feedback = 2;//相册请求码
    private static final int CROP_SMALL_PICTURE_feedback = 3;//图片裁剪后按确认
    public static String imagePaths = "", portraitPaths = "";//图片路径   设置图片路径
    private Uri tempUri;
    private String fileId;//获取文件ID
    private String imgFilePath;
    private File tempFile;
    private boolean isIconStutes = false;
    /*点击相机和相册按钮的状态*/
    private int cameraPhotoStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_feedback);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        activityUtils = new ActivityUtils(this);
/*

        //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        edtTitle.addTextChangedListener(textWatcher);
        edtDetails.addTextChangedListener(textWatcher);
        edtPhone.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (edtTitle.length() == 0 || edtDetails.length() == 0 || edtPhone.length() == 0) {
                btnProblemOk.setEnabled(false);
                btnProblemOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            } else {
                btnProblemOk.setEnabled(true);
                btnProblemOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.ig_back, R.id.btn_problem_photo, R.id.btn_problem_camear, R.id.btn_problem_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_problem_photo://本地相册
                /**
                 * 动态获取访问本地的照片 媒体文件内容和文件的权限
                 */
                cameraPhotoStatus=1;
                requestCemera(Manifest.permission.WRITE_EXTERNAL_STORAGE, "用户成功授权访问本地照片和媒体文件", "用户残忍拒绝访问本地照片和媒体文件");
                break;
            case R.id.btn_problem_camear://相机
                /**
                 * 动态获取访问摄像头
                 */
                cameraPhotoStatus=2;
                requestCemera(Manifest.permission.CAMERA, "用户成功授权访问相机", "用户残忍拒绝访问相机");
                break;
            case R.id.btn_problem_ok://提交
                okHttpProblemFeedBack();
                break;
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE_feedback://相机
                    Log.e("开始对图片进行裁剪处理", "photoCamertPermissions: ");
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    } else {
                        Toast.makeText(ProblemFeedbackActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CHOOSE_PICTURE_feedback://相册
                    if (data != null) {
                        startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    }

                    break;
                case CROP_SMALL_PICTURE_feedback://图片裁剪后  按确认
                    if (data != null) {
                        isIconStutes = true;//图片是否设置的状态
                        setImageToView(data, imgUpPhoto); // 让刚才选择裁剪得到的图片显示在界面上
                        portraitPaths = imagePaths;
                        Log.e("路径2：", "uploadPic: " + "从本地获取照片" + portraitPaths);
                        okHttpPhotoUpdate(portraitPaths);//将图片上传服务器
                        //isSelectPhoto = true;//是否已经设置图片在控件上
                        // tvlPrompt.setText("");
                    }

                    break;
            }
        }
    }

    /**
     * 相册和相机访问权限
     */
    private void requestCemera(String camera, final String granted, final String denied) {
        if (PermissionsUtil.hasPermission(this, camera)) {
            //权限允许成功的操作
            if (cameraPhotoStatus==1){
                //调用本地相册
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_PICK);
                openAlbumIntent.setType("image/*");//图片类型
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE_feedback);
            }else if (cameraPhotoStatus==2){
                //调用相机拍照
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    imgFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagePortrait.jpg";
                    tempFile = new File(imgFilePath);
                    tempUri = Uri.fromFile(tempFile);
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(openCameraIntent, TAKE_PICTURE_feedback);
                } else {
                    Toast.makeText(ProblemFeedbackActivity.this, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(ProblemFeedbackActivity.this, granted, Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(ProblemFeedbackActivity.this, denied, Toast.LENGTH_LONG).show();
                }
            }, new String[]{camera});
        }

    }
        /**
         * 图片上传 网络请求
         */
    private void okHttpPhotoUpdate(String filePath) {
        relLoading.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().photoUpdate(filePath).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relLoading.getVisibility() == View.VISIBLE) {
                    relLoading.setVisibility(View.GONE);
                }
                activityUtils.showDialog("图片上传", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {

                BaseEntity<ImgUpdate> imgUpdate = Parser.parserImgUpdate(json);
                if (imgUpdate.getSuccess() == true) {
                    if (relLoading.getVisibility() == View.VISIBLE) {
                        relLoading.setVisibility(View.GONE);
                    }
                    ImgUpdate imgData = imgUpdate.getData();
                    //获取文件ID
                    fileId = imgData.getFileId();
                    activityUtils.showToast("图片获取并上传成功");
                } else {
                    if (relLoading.getVisibility() == View.VISIBLE) {
                        relLoading.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("图片上传", imgUpdate.getMessage());
                }
            }
        });
    }

    /**
     * 问题反馈  网络请求
     */
    private void okHttpProblemFeedBack() {

        //获取用户的id
        String userId = SharePreferencesUtils.getUser().getId();
        String title = edtTitle.getText().toString().trim();
        String details = edtDetails.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        if (isIconStutes == false) {
            activityUtils.showDialog("问题反馈", "请对反馈的问题拍照");
        } else {
            relLoading.setVisibility(View.VISIBLE);
            OkHttpClientEM.getInstance().problemFeedback(userId, title, details, fileId, phone).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    relLoading.setVisibility(View.GONE);
                    activityUtils.showDialog("问题反馈", "网络异常，请重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    BaseEntity entity = Parser.parserProblemFeedBack(json);
                    if (entity.getSuccess() == true) {
                        relLoading.setVisibility(View.GONE);
                        activityUtils.showToast("提交反馈成功");
                        startActivity(new Intent(ProblemFeedbackActivity.this, MainActivity.class));
                        finish();
                    } else {
                        relLoading.setVisibility(View.GONE);
                        activityUtils.showDialog("问题反馈", entity.getMessage());
                    }

                }
            });

        }
    }

    /**
     * 后续要改为上传到服务器
     */

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        //imagePath指的是从相册或相机中选取照片点击“确定”时将裁剪的图片复制一份到指定的路径

        imagePaths = ImageUtils.savePhoto(this, bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));

        Log.e("路径：", "uploadPic: " + imagePaths);

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
            startActivityForResult(intent, CROP_SMALL_PICTURE_feedback);
        }
    }
}
