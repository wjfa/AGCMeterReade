package com.guanchao.app.fragmet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guanchao.app.ArtificialPhotoActivity;
import com.guanchao.app.LoginActivity;
import com.guanchao.app.MainActivity;
import com.guanchao.app.NoticeaAnounceActivity;
import com.guanchao.app.ProblemFeedbackActivity;
import com.guanchao.app.R;
import com.guanchao.app.ServiceFaultActivity;
import com.guanchao.app.utils.ImageUtils;
import com.jorge.circlelibrary.ImageCycleView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.path;
import static com.guanchao.app.MainActivity.selected;

/**
 * 首页
 */
public class FristFragment extends Fragment {
    View view;
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
     * 轮播图
     */
    @BindView(R.id.cyaleview_route_driver)
    ImageCycleView imageCycleView;
    //装在数据的集合  文字描述
    ArrayList<String> imageDescList = new ArrayList<>();
    //装在数据的集合  图片地址
    ArrayList<String> urlList = new ArrayList<>();
    private DialogPlus dialogOutLogin;
    private File cache;//创建缓存文件
    private int SUCCESS_GET_IMAGE=100;//缓存图片线程
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

        initAddDateImg();
        return view;
    }
    /**
     * 初始化数据和集合图片
     */
    private void initAddDateImg() {

        urlList.clear();
        // 选择切换类型
        //CYCLE_VIEW_NORMAL  CYCLE_VIEW_THREE_SCALE   CYCLE_VIEW_ZOOM_IN   可以随意选择
        imageCycleView.setCycle_T(ImageCycleView.CYCLE_T.CYCLE_VIEW_THREE_SCALE);

        /**添加数据*/
        urlList.add("http://www.chinadaily.com.cn/hqgj/jryw/201111/ae8a18be37c0c1cf17b8b9ed3ae4704f.jpg");
        urlList.add("http://img62.gkzhan.com/9/20140514/635356583091575827975.jpg");
        urlList.add("http://a1.att.hudong.com/18/00/01300001234740130562000839970.jpg");
        urlList.add("http://gd.people.com.cn/NMediaFile/2016/0408/LOCAL201604080923000120428122275.jpg");

        /**添加文字*/
        imageDescList.add("业务员正在抄表1");
        imageDescList.add("业务员正在抄表2");
        imageDescList.add("领导正在开会及指导");
        imageDescList.add("一名业务员正在维修");



       /* //创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        if(!cache.exists()){
            cache.mkdirs();
        }

        // 子线程，开启子线程去下载或者去缓存目录找图片，并且返回图片在缓存目录的地址
        ;
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


        initCarsuelView(imageDescList, urlList);//轮播图
    }

    /**
     * 初始化轮播图
     */
    public void initCarsuelView(ArrayList<String> imageDescList, ArrayList<String> urlList) {
        //getScreenHeight(getActivity()) * 3 / 20   设置轮播图的高度
        LinearLayout.LayoutParams cParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScreenHeight(getActivity()) * 3 / 11);
        imageCycleView.setLayoutParams(cParams);
        ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                /**实现点击事件*/
                //Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                /**在此方法中，显示图片，可以用自己的图片加载库，也可以用（Imageloader）*/
                ImageLoader.getInstance().displayImage(imageURL, imageView);
            }
        };
        /**设置数据*/
        imageCycleView.setImageResources(imageDescList, urlList, mAdCycleViewListener);
        // 是否隐藏底部
        imageCycleView.hideBottom(false);
        imageCycleView.startImageCycle();//循环播放间隔
        //imageCycleView.pushImageCycle();
    }

    @OnClick({R.id.tv_first_unlogin,R.id.tv_watch, R.id.tv_repairs, R.id.tv_operation,  R.id.tv_problem_feedback, R.id.tv_service_repairs, R.id.tv_user_message, R.id.tv_artifical_photo, R.id.tv_noticea})
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
                //startActivity(new Intent(getActivity(),ServiceFaultActivity.class));
                break;
            case R.id.tv_artifical_photo://物资申请
                // startActivity(new Intent(getActivity(),ArtificialPhotoActivity.class));
                break;

            case R.id.tv_noticea://通知公告
                startActivity(new Intent(getActivity(),NoticeaAnounceActivity.class));
                break;
            case R.id.tv_operation://操作手册
                // startActivity(new Intent(getActivity(),ArtificialPhotoActivity.class));
                break;
            case R.id.tv_problem_feedback://问题反馈
                 startActivity(new Intent(getActivity(),ProblemFeedbackActivity.class));
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
                                switch (view.getId()){
                                    case R.id.tv_outlogin://退出登录
                                        // TODO: 2017/6/20 退出登入需要删除一下信息    待实现
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
        super.onDestroy();
//        if (cache.getPath()!=null){
//            cache.delete();
//        }
    }
}
