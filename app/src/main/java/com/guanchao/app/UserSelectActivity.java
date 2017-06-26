package com.guanchao.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
/**
 * 用户选择
 */
public class UserSelectActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_user_house_name)
    EditText edtHouseName;//户名
    @BindView(R.id.edt_user_door_number)
    EditText edtDoorNumber;//户号
    @BindView(R.id.btn_user_query)
    Button btnQuery;//查询
    @BindView(R.id.recyView_user_select)
    RecyclerView recylerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;//刷新数据
    @BindView(R.id.rel_user_select_ref)
    RelativeLayout relRefresh;//刷新页面
    private ActivityUtils activityUtils;
    private UserSelectAdapter userSelectdapter;
    private int pagesize = 10;//每页记录数
    private int pagenum = 0;//页数
    private List<UserSelect.ListBean> listBeen;
    public static String watermeterId;//水表ID
    public static int RESULT_CODE = 200;//返回码
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        okHttpUserSelect(pagesize, pagenum, "", "");//用户选择请求


//        Log.e("数据收集", "onCreate: "+listBeen.size() );



        initRefreshDate();//数据刷新
        edtHouseName.addTextChangedListener(textWatcher);
        edtDoorNumber.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edtHouseName.length() == 0 && edtDoorNumber.length() == 0) {
                okHttpUserSelect(pagesize, pagenum, "", "");//用户选择请求
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick({R.id.ig_back, R.id.btn_user_query})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_user_query://查询
                String customerName = edtHouseName.getText().toString().trim();
                String customerNo = edtDoorNumber.getText().toString().trim();
                okHttpUserSelect(pagesize, pagenum, customerName, customerNo);//用户选择"筛选"请求
                Log.e("用户选择", "onResponseUI: " + "筛选成功");
                break;
        }
    }
    //item监听
    private UserSelectAdapter.onNewItemClickListener newItemClickListener = new UserSelectAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
            //获取水表ID
            watermeterId = listBeen.get(postion).getWatermeterId();
            Log.e("watermeterId", "onNewItemClick: " + watermeterId);
            //获取抄表用户信息请求
            okHttpUserSelectWatch();
        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 刷新数据
     */
    int sizeO = 10;
    private void initRefreshDate() {
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pagenum++;
                        sizeO = sizeO + listBeen.size();//每次累计加载的数量
//                       userSelectdapter.addDateList(bean,pagesize);
                        // xRefreshView.setLoadComplete(true);
                        if (sizeO >= count) {//当加载的数量大于等于总记录数时，加载完成，取消加载
                            // xRefreshView.setLoadComplete(true);
                            xRefreshView.stopLoadMore();
                            activityUtils.showToast("没有新数据了");

                        } else {
                            //10  1+
                            okHttpUserSelect(pagesize, pagenum, "", "");
//                            UserSelect.ListBean Bean=new UserSelect.ListBean();
//                            userSelectdapter.insert(Bean,listBeen.size());
                            userSelectdapter.appendData(listBeen, false);
                            userSelectdapter.notifyDataSetChanged();

                        }
                    }
                }, 1000);
            }
        });
    }

    /**
     * 用户选择   网络请求
     */
    private void okHttpUserSelect(int pagesize, int pagenum, String customerName, String customerNo) {
        //获取存储本地的抄表区域areaId
        Watch userWatch = SharePreferencesUtils.getUserWatch();
        String areaId = userWatch.getAreaId();
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().userSelect(pagesize, pagenum, areaId, customerName, customerNo).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility()==View.VISIBLE){
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("用户选择","网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                //Log.e("用户选择", "onResponseUI: " + json);
                BaseEntity<UserSelect> entity = Parser.parserUserSelect(json);
                if (entity.getSuccess() == true) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    //获取总记录数
                    count = entity.getData().getCount();
                  //  Log.e("rrrrrrrrrrrrrrrrrr选择", "onResponseUI: ");
                    listBeen = entity.getData().getList();

                    userSelectdapter = new UserSelectAdapter(UserSelectActivity.this, listBeen);
                    recylerView.setAdapter(userSelectdapter);
                    userSelectdapter.notifyDataSetChanged();

                    Log.e("集合数量", "run: " + listBeen.size());
                    xRefreshView.setPullRefreshEnable(false);
                    xRefreshView.setPullLoadEnable(true);
                    xRefreshView.setPinnedTime(1100);
                    xRefreshView.setMoveForHorizontal(true);
                    userSelectdapter.setCustomLoadMoreView(new XRefreshViewFooter(UserSelectActivity.this));

                    recylerView.setLayoutManager(new LinearLayoutManager(UserSelectActivity.this, LinearLayoutManager.VERTICAL, false));
                    recylerView.setItemAnimator(new DefaultItemAnimator());
                    //设置监听
                    userSelectdapter.setonNewItemClickListener(newItemClickListener);

                   // activityUtils.showToast(entity.getMessage());
                } else {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("用户选择",entity.getMessage());
                }

            }
        });
    }


    /**
     * 用户选择后抄表页面获取用户信息  网络请求
     */
    private void okHttpUserSelectWatch() {
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().userSelectWatchInform(watermeterId).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility()==View.VISIBLE){
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("获取用户信息","网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                //Log.e("用户选择后抄表页面获取用户信息", "onResponseUI: " + json);
                BaseEntity<UserSelcetWatchMessage> entity = Parser.parserUserSelectWatchMsg(json);
                if (entity.getSuccess() == true) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
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

                    Intent intent = new Intent();
                    UserSelcetWatchMessage userSelcetWatchMessage = new UserSelcetWatchMessage(customerNo, customerName, houseNumber, phone, address, oldReading, watermeterNo, location, longitude, latitude);
                    //返回结果
                    setResult(RESULT_CODE, new Intent().putExtra("UserWatchMessage", userSelcetWatchMessage));
                    finish();


                }else {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("获取用户信息",entity.getMessage());
                }
            }
        });

    }

}
