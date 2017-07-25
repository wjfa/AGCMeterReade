package com.guanchao.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.guanchao.app.adapter.NoticeaAnAdapter;
import com.guanchao.app.adapter.NoticeaAnceAdapter;
import com.guanchao.app.entery.NoticeAnounce;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.StatusBarUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import okhttp3.Call;

import static android.R.id.list;
import static android.R.string.no;
import static com.guanchao.app.R.id.view;
import static com.guanchao.app.network.HttpUtilsApi.noticeAnounce;


/**
 * 通知公告
 */
public class NoticeaAnounceActivity extends AppCompatActivity {
    public static long lastRefreshTime;
    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_style_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;   //支持下拉刷新的ViewGroup
    @BindView(R.id.recyView_notice)
    RecyclerView recylerView;
    @BindView(R.id.rel_notice_loading)
    RelativeLayout relLoading;//刷新页面
    private NoticeaAnAdapter noticeaAnAdapter;
    private ActivityUtils activityUtils;
    private int pagenum = 0;//pagenum：  页面
    private int pagesize = 4;//pagesize：   每页的个数
    private List<NoticeAnounce.ListBean> listBeanList;
    private Handler handler;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticea_anounce);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

     /*   //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);*/
        okHttpNoticeAnounce(pagenum);
        setPtrRefresh();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String json = (String) msg.obj;
                NoticeAnounce noticeAnounce = Parser.parserNoticeAnounce(json);
                count = noticeAnounce.getCount();
                listBeanList = noticeAnounce.getList();
                if (noticeaAnAdapter == null) {
                    noticeaAnAdapter = new NoticeaAnAdapter(NoticeaAnounceActivity.this, listBeanList);
                    recylerView.setAdapter(noticeaAnAdapter);
                    recylerView.setLayoutManager(new LinearLayoutManager(NoticeaAnounceActivity.this, LinearLayoutManager.VERTICAL, false));
                    recylerView.setItemAnimator(new DefaultItemAnimator());
                    //设置监听
                    noticeaAnAdapter.setonNewItemClickListener(newItemClickListener);
                    Log.e("适配器的数量1", "handleMessage: " + noticeaAnAdapter.getItemCount());
                } else {
                    for (int i = 0; i < listBeanList.size(); i++) {
                        NoticeAnounce.ListBean bean = listBeanList.get(i);
                        noticeaAnAdapter.addDate(noticeaAnAdapter.getItemCount(), bean);
                        noticeaAnAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }


    //item监听
    private NoticeaAnAdapter.onNewItemClickListener newItemClickListener = new NoticeaAnAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
           /* if (view instanceof TextView) {
                return;
            }*/
            //Toast.makeText(NoticeaAnounceActivity.this, "点击了" + postion, Toast.LENGTH_SHORT).show();
            Log.e("点击", "onNewItemClick: " + postion);
            NoticeAnounce.ListBean bean = listBeanList.get(postion);
            Intent intent = new Intent(NoticeaAnounceActivity.this, NoticeShowActivity.class);
            intent.putExtra("NoticeShow", bean);
            startActivity(intent);

        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 刷新  加载
     */
    int sizeO = 4;//改值要和 pagesize ;每页记录数  一致

    private void setPtrRefresh() {
        // MaterialHeader 风格
        MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
        header.setPadding(0, PtrLocalDisplay.dp2px(50), 0, PtrLocalDisplay.dp2px(50));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            //刷新
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.refreshComplete();
                    }
                }, 1000);
            }

            //加载
            @Override
            public void onLoadMoreBegin(final PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pagenum++;
                        sizeO = sizeO + listBeanList.size();//每次累计加载的数量

                        Log.e("数据", "onResponseUI: " + listBeanList.size());
                        if (sizeO > count) {
                            mPtrFrameLayout.refreshComplete();
                            activityUtils.showToast("客官别急，没有更多数据了");
                        } else {
                            okHttpNoticeAnounce(pagenum);
                            mPtrFrameLayout.refreshComplete();

                        }

                    }
                }, 1000);
            }

        });
    }

    /**
     * 通知公告  网络请求
     */
    private void okHttpNoticeAnounce(int pagenum) {
        relLoading.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().noticeAnounce(1, pagenum, pagesize).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                relLoading.setVisibility(View.GONE);
                activityUtils.showDialog("通知公告", "网络异常，请重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                relLoading.setVisibility(View.GONE);//隐藏
                Message msg = new Message();
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    @OnClick(R.id.ig_back)
    public void onClick() {
        finish();
    }
}
