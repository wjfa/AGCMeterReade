package com.guanchao.app.fragmet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.GoodsApplyActivity;
import com.guanchao.app.LoginActivity;
import com.guanchao.app.MainActivity;
import com.guanchao.app.NoticeaAnounceActivity;
import com.guanchao.app.ProblemFeedbackActivity;
import com.guanchao.app.QrCodeScanActivity;
import com.guanchao.app.R;
import com.guanchao.app.SignClockActivity;
import com.guanchao.app.application.MyApplication;
import com.guanchao.app.utils.GlideImageLoader;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 首页   继承的接口二维码用得到  相机权限
 */
public class FristFragment extends Fragment{
    View view;
    /**
     * 轮播图
     */
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_first_unlogin)
    TextView tvUnLogin;
    @BindView(R.id.tv_watch)
    TextView tvWatch;
    @BindView(R.id.tv_repairs)
    TextView tvRepairs;
    @BindView(R.id.tv_service_repairs)
    TextView tvServiceRepairs;
    @BindView(R.id.tv_user_message)
    TextView tvUserMessage;
    @BindView(R.id.tv_artifical_photo)
    TextView tvArtificalPhoto;
    public static int select;//设置点击跳转到对应的页面
    /**
     * 二维码
     */
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private DialogPlus dialogOutLogin;
//    private File cache;//创建缓存文件
//    private int SUCCESS_GET_IMAGE=100;//缓存图片线程
   /* Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS_GET_IMAGE) {
                Uri uri = (Uri) msg.obj;
                if (iv_header != null && uri != null) {
                    iv_header.setImageURI(uri);
                }
            }
        }
    };*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frist, container, false);
        ButterKnife.bind(this, view);
        setLunBoTu();
        return view;
    }

    /**
     * 设置轮播图
     */
    private void setLunBoTu() {
        //设置轮播图尺寸
        banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScreenHeight(getActivity()) * 3 / 11));
        //轮播图属性设置
        banner.setImages(MyApplication.images) //设置图片集合（刚打开不会空白）
                .setImageLoader(new GlideImageLoader())//设置图片加载器
                .setBannerTitles(MyApplication.titles)//设置文字标题
                .setOnBannerListener(onBannerListener)//点击轮播图的position
                .setBannerAnimation(Transformer.DepthPage)//设置banner动画效果
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)  //设置banner内置样式（是否需要显示圆形或数字指示器）
                .setDelayTime(2500) //设置轮播时间
                //.isAutoPlay(true)//设置是否自动轮播（默认自动）
                //.isAutoPlay(true)//设置自动轮播，默认为true
                // .setViewPagerIsScroll(true)//设置是否允许手动滑动轮播图（默认true）
                .setIndicatorGravity(BannerConfig.CENTER)  //设置指示器位置（左中右）
                .start();

          /* //创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        if(!cache.exists()){
            cache.mkdirs();
        }
        // 子线程，开启子线程去下载或者去缓存目录找图片，并且返回图片在缓存目录的地址
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //这个URI是图片下载到本地后的缓存目录中的URI
                    for (int i = 0; i < urlList.size(); i++) {
                        Uri uri = ImageUtils.getImageURI(urlList.get(i), cache);
                        Message msg = new Message();
                        msg.what = SUCCESS_GET_IMAGE;
                        msg.obj = uri;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @OnClick({R.id.tv_first_unlogin, R.id.tv_watch, R.id.tv_repairs, R.id.tv_operation, R.id.tv_problem_feedback, R.id.tv_qrcode, R.id.tv_service_repairs, R.id.tv_user_message, R.id.tv_artifical_photo, R.id.tv_noticea})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_watch://抄表
                select = 1;
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra("watch", select));
                getActivity().finish();
                break;
            case R.id.tv_repairs://b报修
                select = 2;
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra("repairs", select));
                getActivity().finish();
                break;
            case R.id.tv_service_repairs://维修
                select = 3;
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra("service_repairs", select));
                getActivity().finish();
                break;
            case R.id.tv_user_message://打卡签到
              startActivity(new Intent(getActivity(), SignClockActivity.class));
                break;
            case R.id.tv_artifical_photo://物资申请
                startActivity(new Intent(getActivity(), GoodsApplyActivity.class));
                break;
            case R.id.tv_noticea://通知公告
                startActivity(new Intent(getActivity(), NoticeaAnounceActivity.class));
                break;
            case R.id.tv_qrcode://二维码扫描
                /**
                 * 动态获取访问相机  Manifest.permission.CAMERA
                 */
                if (PermissionsUtil.hasPermission(getActivity(), Manifest.permission.CAMERA)) {
                    //成功跳转  成功允许时的操作
                    startActivity(new Intent(getActivity(), QrCodeScanActivity.class));
                    // Toast.makeText(QrCodeScanActivity.this, "可以访问摄像头", Toast.LENGTH_LONG).show();
                } else {
                    PermissionsUtil.requestPermission(getActivity(), new PermissionListener() {
                        @Override//接受
                        public void permissionGranted(@NonNull String[] permissions) {
                            Toast.makeText(getActivity(), "用户成功授权访问相机", Toast.LENGTH_LONG).show();
                        }

                        @Override//拒绝
                        public void permissionDenied(@NonNull String[] permissions) {
                            Toast.makeText(getActivity(), "用户残忍拒绝访问相机", Toast.LENGTH_LONG).show();
                        }
                    }, new String[]{Manifest.permission.CAMERA});
                }
                break;
            case R.id.tv_problem_feedback://问题反馈
                startActivity(new Intent(getActivity(), ProblemFeedbackActivity.class));
                break;
            case R.id.tv_first_unlogin://退出登录
                //退出登录
                dialogOutLogin = DialogPlus.newDialog(getActivity())
                        .setContentHolder(new ViewHolder(R.layout.dialog_unlogin))
                        .setFooter(R.layout.dialog_footer)
                        .setGravity(Gravity.BOTTOM)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                                switch (view.getId()) {
                                    case R.id.tv_outlogin://退出登录
                                        // TODO: 2017/6/20 退出登入需要删除密码一下信息    待实现
                                        //删除密码
                                        SharePreferencesUtils.clearNamePass(getActivity());
                                        //清空checkBox的点击状态
                                        SharePreferencesUtils.clearCheckBox(getActivity());

                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        getActivity().finish();
                                        dialogOutLogin.dismiss();
                                        break;
                                    case R.id.tv_no:
                                        if (dialogOutLogin.isShowing()) dialogOutLogin.dismiss();

                                        break;
                                }
                            }
                        })
                        .create();
                dialogOutLogin.show();
                break;
        }
    }


    //设置轮播图的点击监听
    OnBannerListener onBannerListener = new OnBannerListener() {
        @Override
        public void OnBannerClick(int position) {
            Toast.makeText(getActivity(), "你点击了：" + position, Toast.LENGTH_SHORT).show();
        }
    };


    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
       // requestCodeQRCodePermissions();//调用“二维码扫描”方法
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }



    /**
     * 得到屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    @Override
    public void onDestroy() {

        //结束轮播
        banner.stopAutoPlay();
        ButterKnife.bind(getActivity()).unbind();
        super.onDestroy();
    }
}
