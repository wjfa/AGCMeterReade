package com.guanchao.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.guanchao.app.fragmet.WatchFragment;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.DensityUtil;
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
    @BindView(R.id.tv_select_type)
    TextView tvSelectType;//筛选
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
    private PopupWindow popupWindow;//用来显示popupwindow的parent
    private int type = 0;//0：未抄  1：已超
    private ImageView imgNo;
    private TextView noWatch;
    private TextView yesWatch;
    private int yesWatchStatus=1;//判断是否已抄状态，如果点击已抄 则提示不可再抄（默认 1未抄  2已抄）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        //type  默认未抄   1：以抄
        okHttpUserSelect(pagesize, pagenum, "", "", WatchFragment.newStrTime, type);//用户选择请求
//        Log.e("数据收集", "onCreate: "+listBeen.size() );
        initRefreshDate();//数据刷新
        edtHouseName.addTextChangedListener(textWatcher);
        edtDoorNumber.addTextChangedListener(textWatcher);
        showPopWindow();//筛选对话框
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edtHouseName.length() == 0 && edtDoorNumber.length() == 0) {
                okHttpUserSelect(pagesize, pagenum, "", "", WatchFragment.newStrTime, type);//用户选择请求
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @OnClick({R.id.ig_back, R.id.btn_user_query, R.id.tv_select_type})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_user_query://查询
                String customerName = edtHouseName.getText().toString().trim();
                String customerNo = edtDoorNumber.getText().toString().trim();
                okHttpUserSelect(pagesize, pagenum, customerName, customerNo, WatchFragment.newStrTime, type);//用户选择"筛选"请求
                Log.e("用户选择", "onResponseUI: " + "筛选成功");
                break;
            case R.id.tv_select_type://筛选 未抄或已抄
                //设置控件颜色
//                tvSelectType.setTextColor(getResources().getColor(R.color.yellow2));
//                tvSelectType.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.ic_screening2), null);

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    setPopWindowbackgroundAlpha(1.0f);//设置背景正常
                } else {
                    //点击弹出pop对话框(用来显示popupwindow的parent  ID，以该位置为参照物设置具体位置)

                    popupWindow.showAsDropDown(tvSelectType);
                    setPopWindowbackgroundAlpha(0.8f);//设置背景变暗
                    //默认选中未抄用户  设置已抄和未抄数
                    noWatch.setText("未抄"+"("+(Integer.valueOf(WatchFragment.households)-Integer.valueOf(WatchFragment.finisheNumber))+")");
                    yesWatch.setText("已抄"+"("+Integer.valueOf(WatchFragment.finisheNumber)+")");

                }
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
            //获取抄表用户基本信息请求
            if (yesWatchStatus==1){
                okHttpUserSelectWatch();
            }else if (yesWatchStatus==2){
                activityUtils.showDialog("用户抄表","该用户已抄过表，不可再抄");

            }
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
    int sizeO = 10;//改值要和 pagesize = 10;每页记录数  一致

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
                        if (sizeO > count) {//当加载的数量大于等于总记录数时，加载完成，取消加载
                            // xRefreshView.setLoadComplete(true);
                            xRefreshView.stopLoadMore();
                            activityUtils.showToast("没有新数据了");

                        } else {
                            //10  1+
                            okHttpUserSelect(pagesize, pagenum, "", "", WatchFragment.newStrTime, type);
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
    private void okHttpUserSelect(int pagesize, int pagenum, String customerName, String customerNo, String month, int type) {
        //获取存储本地的抄表区域areaId
        Watch userWatch = SharePreferencesUtils.getUserWatch();
        String areaId = userWatch.getAreaId();
        relRefresh.setVisibility(View.VISIBLE);
        OkHttpClientEM.getInstance().userSelect(pagesize, pagenum, areaId, customerName, customerNo, month, type).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relRefresh.getVisibility() == View.VISIBLE) {
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("用户选择", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
//                Log.e("用户选择", "onResponseUI: " + json);
                BaseEntity<UserSelect> entity = Parser.parserUserSelect(json);
                if (entity.getSuccess() == true) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
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
                    Log.e("用户选择成功", "onResponseUI: " + json);
                    // activityUtils.showToast(entity.getMessage());
                } else {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("用户选择", entity.getMessage());
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
                if (relRefresh.getVisibility() == View.VISIBLE) {
                    relRefresh.setVisibility(View.GONE);
                }
                activityUtils.showDialog("获取用户信息", "网络异常，请稍后重试");
            }

            @Override
            public void onResponseUI(Call call, String json) {
//                Log.e("用户选择后抄表页面获取用户信息", "onResponseUI: " + json);
                BaseEntity<UserSelcetWatchMessage> entity = Parser.parserUserSelectWatchMsg(json);
                if (entity.getSuccess() == true) {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
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


                } else {
                    if (relRefresh.getVisibility() == View.VISIBLE) {
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("获取用户信息", entity.getMessage());
                }
            }
        });
    }

    /**
     * 显示"筛选"对话框
     */
    private void showPopWindow() {
        View popview = LayoutInflater.from(this).inflate(R.layout.popup_window_select, null);
        //WRAP_CONTENT:是.xml中的布局宽、高，wrap_content包裹
        popupWindow = new PopupWindow(popview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.getContentView().setFocusable(true);//为是否可以获得焦点
        //设置可触摸PopupWindow之外的地方关闭
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.dialogWindowAnim_style);//设置动画

        //设置宽度
       // popupWindow.setWidth(150);

        noWatch = (TextView) popview.findViewById(R.id.tv_no_watch);
        yesWatch = (TextView) popview.findViewById(R.id.tv_yes_wacth);
        imgNo = (ImageView) popview.findViewById(R.id.image_no_watch);
        final ImageView imgYes = (ImageView) popview.findViewById(R.id.image_yes_watch);

        //设置默认标记未抄
        imgNo.setImageResource(R.drawable.ic_screening_logo2);
        //未抄
        noWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesWatchStatus=1;//判断是否已抄状态，如果点击已抄 则提示不可再抄
                //设置控件
                tvSelectType.setText("未抄用户");
                tvSelectType.setPadding(0,0,5,0);
                //tvSelectType.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null);

                imgNo.setImageResource(R.drawable.ic_screening_logo2);
                imgYes.setImageResource(R.drawable.ic_screening_logo);
                type = 0;
                okHttpUserSelect(pagesize, pagenum, "", "", WatchFragment.newStrTime, type);
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                    setPopWindowbackgroundAlpha(1.0f);//设置背景正常
                }
            }
        });
        //已抄
        yesWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesWatchStatus=2;//判断是否已抄状态，如果点击已抄 则提示不可再抄
                //设置控件
                tvSelectType.setText("已抄用户");
                tvSelectType.setPadding(0,0,5,0);
                //tvSelectType.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null);
                imgYes.setImageResource(R.drawable.ic_screening_logo2);
                imgNo.setImageResource(R.drawable.ic_screening_logo);
                type = 1;
                okHttpUserSelect(pagesize, pagenum, "", "", WatchFragment.newStrTime, type);
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                    setPopWindowbackgroundAlpha(1.0f);//设置背景正常
                }
            }
        });
        //点击其它地方对话框关闭的时候，将背景还原
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                setPopWindowbackgroundAlpha(1.0f);//设置背景正常
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void setPopWindowbackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = bgAlpha;
        this. getWindow().setAttributes(params);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

}
