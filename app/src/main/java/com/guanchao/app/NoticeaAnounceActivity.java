package com.guanchao.app;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.guanchao.app.adapter.NoticeaAnAdapter;
import com.guanchao.app.utils.StatusBarUtil;

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


/**
 * 通知公告
 */
public class NoticeaAnounceActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_style_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;    //支持下拉刷新的ViewGroup
    @BindView(R.id.recyView_notice)
    RecyclerView recylerView;
    private NoticeaAnAdapter noticeaAnAdapter;
    private List<String> notiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticea_anounce);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);

        for (int i = 0; i < 10; i++) {
            notiList.add("" + i);
        }
        noticeaAnAdapter = new NoticeaAnAdapter(this, notiList);
        recylerView.setAdapter(noticeaAnAdapter);
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.setItemAnimator(new DefaultItemAnimator());
        //设置监听
        noticeaAnAdapter.setonNewItemClickListener(newItemClickListener);
        setPtrRefresh();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(true);//是否自动刷新
            }
        }, 1000);


    }


    /**
     * 刷新  加载
     */
    int j=1,k=1;
    private void setPtrRefresh(){
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
                        if (j<3){
                            noticeaAnAdapter.addDate(0,"这是刷新的数据"+j);
                            noticeaAnAdapter.notifyDataSetChanged();
                            mPtrFrameLayout.refreshComplete();
                            j++;
                        }else {
                            mPtrFrameLayout.refreshComplete();
                            Toast.makeText(NoticeaAnounceActivity.this,"没有新数据了",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }

            //加载
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (k<3){
                            //模拟数据
                            noticeaAnAdapter.addDate(notiList.size(),"这是加载的数据"+k);
                            noticeaAnAdapter.notifyDataSetChanged();
                            mPtrFrameLayout.refreshComplete();
                            k++;
                        }else {
                            mPtrFrameLayout.refreshComplete();
                            Toast.makeText(NoticeaAnounceActivity.this,"没有更多内容了",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }

        });
    }
    //item监听
    private NoticeaAnAdapter.onNewItemClickListener newItemClickListener = new NoticeaAnAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
            Toast.makeText(NoticeaAnounceActivity.this, "点击了" + postion, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };


    @OnClick(R.id.ig_back)
    public void onClick() {
        finish();
    }
}
