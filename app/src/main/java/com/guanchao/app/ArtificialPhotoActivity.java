package com.guanchao.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.LoudnessEnhancer;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.R.attr.gravity;
import static com.guanchao.app.fragmet.WatchFragment.taskId;
import static java.lang.Integer.parseInt;

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
    private boolean isSet = false;
    private static int REQUEST_CODE = 100;
    private int IMAGEVIEWSTATUS;//设置控件状态
    protected static final int TAKE_PICTURE = 1;//相机请求码
    protected static final int CHOOSE_PICTURE = 2;//相册请求码
    private static final int CROP_SMALL_PICTURE = 3;//图片裁剪后按确认
    private static final int GETPICTURE = 4;//图片复制
    public static String imagePath = "", portraitPath = "";//图片路径   设置图片路径
    private ActivityUtils activityUtils;
    private Uri tempUri;
    private String fileId;
    private boolean  isSelectPhoto;//是否已经设置图片在控件上

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

            if (edtHouseName.length() == 0 || edtNewReade.length() == 0|| edtRemark.length() == 0||isSelectPhoto==false) {// isSelectPhoto:false  未设置图片在控件上
                btnOk.setClickable(false);
                btnOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            } else {
                //设置button可点击;
                btnOk.setClickable(true);
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

    @OnClick({R.id.ig_back, R.id.edt_artific_now_amouter, R.id.tv_artific_select, R.id.tv_artific_get_position, R.id.tv_artific_photo_collect, R.id.btn_aretific_ok})
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
                break;
            case R.id.tv_artific_photo_collect://拍照
                IMAGEVIEWSTATUS = 1;
                setShowDialogPlus();
                break;
            case R.id.btn_aretific_ok://保存
                okHttpWaterLatLtude();//设置水表经纬度 网络请求
                okHttpPeoplePhoto();//人工拍照 网络请求
                break;
        }
    }

    /**
     * 图片上传 网络请求
     */
    private void okHttpPhotoUpdate(String filePath) {

        OkHttpClientEM.getInstance().photoUpdate(filePath).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                activityUtils.showToast("网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {

                BaseEntity<ImgUpdate> imgUpdate = Parser.parserImgUpdate(json);
                if (imgUpdate.getSuccess() == true) {
                    ImgUpdate imgData = imgUpdate.getData();
                    //获取文件ID
                    fileId = imgData.getFileId();
                    activityUtils.showToast(imgUpdate.getMessage());
                } else {
                    activityUtils.showToast(imgUpdate.getMessage());
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
        if ("".equals(longtude) || "".equals(latude)) {
            return;
        } else {
            OkHttpClientEM.getInstance().setWaterLatLtude(watermeterId, longtude, latude).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    activityUtils.showToast("网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    BaseEntity entity = Parser.parserWaterLatLutde(json);
                    if (entity.getSuccess() == true) {
                        activityUtils.showToast(entity.getMessage());//查询成功
                    } else {
                        activityUtils.showToast(entity.getMessage());
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
        if (edtHouseNumber.getText().toString().length() == 0 || edtHouseName.getText().toString().length() == 0 || edtDoorNumber.getText().toString().length() == 0
                || edtPhone.getText().toString().length() == 0 || edtAddres.getText().toString().length() == 0 || edtOldtReade.getText().toString().length() == 0
                || edtWaterNumber.getText().toString().length() == 0 || edtLongitude.getText().toString().length() == 0
                || edtLatude.getText().toString().length() == 0 || edtInstallPosition.getText().toString().length() == 0) {
            activityUtils.showToast("信息不完整");
        } else if (waterReading.length() == 0 || edtNewDosage.getText().toString().length() == 0 || edtRemark.getText().toString().length() == 0) {
            activityUtils.showToast("请补全信息");
        } else {
            OkHttpClientEM.getInstance().peoplePhoto(taskId, waterId, waterReading, remark, fileId).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    activityUtils.showToast("网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    BaseEntity entity = Parser.parserPeoplePhoto(json);
                    if (entity.getSuccess() == true) {
                        activityUtils.showToast(entity.getMessage());//抄表成功
                    } else {
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
                                activityUtils.showToast("相机");
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                String pathName = "HeadPortrait.jpg";
                                tempUri = Uri.fromFile(new File(Environment
                                        .getExternalStorageDirectory() + "/" + pathName));
                                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                                startActivityForResult(openCameraIntent, TAKE_PICTURE);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                break;
                            case R.id.tv_photo://相册
                                Intent openAlbumIntent = new Intent(
                                        Intent.ACTION_PICK);
                                openAlbumIntent.setType("image/*");//图片类型
                                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
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
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
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
                        // Log.e("路径2：", "uploadPic: " + portraitPath);
                        okHttpPhotoUpdate(portraitPath);//将图片上传服务器
                        isSelectPhoto=true;//是否已经设置图片在控件上
                        tvlPrompt.setText("");
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

        imagePath = ImageUtils.savePhoto(bitmap, Environment
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

}
