package com.guanchao.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.adapter.GoodsApplyAdapter;
import com.guanchao.app.adapter.GoodsApplyModelAdapter;
import com.guanchao.app.adapter.GoodsApplyNumberAdapter;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.GoodsApply;
import com.guanchao.app.entery.GoodsApplyModel;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.guanchao.app.R.id.btn_goods_commit;


public class GoodsApplyActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(btn_goods_commit)
    Button btnCommit;
    @BindView(R.id.list_view_goods)
    ListView listViewGoods;
    @BindView(R.id.list_view_modle)
    ListView listViewModle;
    @BindView(R.id.list_view_number)
    ListView listViewNumber;
    @BindView(R.id.edt_goods_note)
    EditText edtNote;
    @BindView(R.id.tv_goods_prompt)
    TextView tvPrompt;
    @BindView(R.id.rel_goods_apply_ef)
    RelativeLayout relLogin;//刷新页面
    private int MaxLen = 80;//最大输入数
    private ActivityUtils activityUtils;
    private GoodsApplyAdapter goodsApplyAdapter;//物品名称
    private GoodsApplyModelAdapter modelApplyAdapter;//型号
    private GoodsApplyNumberAdapter numberAdapter;//数量
    private List<GoodsApply> goodsList;//物品名称集合
    private List<GoodsApplyModel> modelList;//型号集合
    private String typeId;
    private String goodsId;
    private int goodsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_apply);
        ButterKnife.bind(this);

        activityUtils = new ActivityUtils(this);
        /*//设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        tvPrompt.setText("还可输入 " + MaxLen + "字");
        edtNote.addTextChangedListener(textWatcher);

        okHttpGoodsApply();//物品请求

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //实现输入数字的提示
            Editable note = edtNote.getText();
            tvPrompt.setText("还可输入 " + (MaxLen - note.toString().length()) + "字");
            if ((note.toString().length() > MaxLen)) {
                int selEndIndex = Selection.getSelectionEnd(note);
                String str = note.toString();
                //截取新字符串
                String newStr = str.substring(0, MaxLen);
                edtNote.setText(newStr);
                note = edtNote.getText();

                //新字符串的长度
                int newLen = note.length();
                //旧光标位置超过字符串长度
                if (selEndIndex > newLen) {
                    selEndIndex = note.length();
                }
                //设置新光标所在的位置
                Selection.setSelection(note, selEndIndex);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://物资名称
                    goodsApplyAdapter = new GoodsApplyAdapter(getApplicationContext(), -1);
                    listViewGoods.setAdapter(goodsApplyAdapter);
                    listViewGoods.setOnItemClickListener(onItemClickListener);

                    String jsonGoods = (String) msg.obj;
                    goodsList = Parser.parserGoodsApply(jsonGoods);
                    //Log.e("物资申请", "onResponseUI: " + jsonGoods);
                    for (int i = 0; i < goodsList.size(); i++) {
                        goodsApplyAdapter.addDate(goodsList.get(i));
                        goodsApplyAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2://型号
                    modelApplyAdapter = new GoodsApplyModelAdapter(getApplicationContext(), -1);
                    listViewModle.setAdapter(modelApplyAdapter);
                    listViewModle.setOnItemClickListener(onItemClickListener);

                    String jsonModel = (String) msg.obj;
                    modelList = Parser.parserGoodsApplyModel(jsonModel);
                    //Log.e("物资申请", "onResponseUI: " + jsonModel);

                    for (int i = 0; i < modelList.size(); i++) {
                        modelApplyAdapter.addDate(modelList.get(i));
                        modelApplyAdapter.notifyDataSetChanged();
                    }
                    break;

                case 3://物资提交
                    String jsonCommit = (String) msg.obj;
                    BaseEntity entity = Parser.parserGoodsApplyCommit(jsonCommit);
                    if (entity.getSuccess() == true) {
                        if (relLogin.getVisibility() == View.VISIBLE) {
                            relLogin.setVisibility(View.GONE);//隐藏刷新页面
                            btnCommit.setEnabled(true);//登入获取焦点
                        }
                        Log.e("物资申请", "onResponseUI: " + jsonCommit);
                        //刷新适配器还原
                        edtNote.setText("");
                        activityUtils.showToast("物资申请成功");
                    } else {
                        if (relLogin.getVisibility() == View.VISIBLE) {
                            relLogin.setVisibility(View.GONE);//隐藏刷新页面
                            btnCommit.setEnabled(true);//登入获取焦点
                        }
                        activityUtils.showDialog("物资申请", entity.getMessage());
                    }
                    break;
            }
        }
    };


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.list_view_goods://物资名称
                    //设置点击item背景颜色
                    goodsApplyAdapter.setSeclectionBgColor(position);
                    goodsApplyAdapter.notifyDataSetChanged();
                    //获取物品名称返回的typeid
                    typeId = goodsList.get(position).getTypeId();
                    okHttpGoodsApplyModel(typeId);

                    //清空数量List
                    //numberAdapter.clear();
                    break;
                case R.id.list_view_modle://型号
                    //设置点击item背景颜色
                    modelApplyAdapter.setSeclectionBgColor(position);
                    modelApplyAdapter.notifyDataSetChanged();
                    //获取型号返回的goodsId
                    goodsId = modelList.get(position).getGoodsId();

                    // numberList=new ArrayList<>();
                    numberAdapter = new GoodsApplyNumberAdapter(getApplicationContext(), -1);
                    listViewNumber.setAdapter(numberAdapter);
                    listViewNumber.setOnItemClickListener(onItemClickListener);
                    for (int k = 1; k < 31; k++) {
                        numberAdapter.addDate("" + k);
                        numberAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.list_view_number://数量
                    //设置点击item背景颜色
                    numberAdapter.setSeclectionBgColor(position);
                    numberAdapter.notifyDataSetChanged();
                    //获取数量
                    goodsNumber = (int) parent.getItemIdAtPosition(position) + 1;
                    break;
            }

        }
    };

    /*//item监听
    private GoodsApplyAdapter.onNewItemClickListener newItemClickListener = new GoodsApplyAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {

        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };
*/
    @OnClick({R.id.ig_back, btn_goods_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case btn_goods_commit://提交
                okHttpGoodsApplyCommit();
                break;
        }
    }

    /**
     * 物资申请(物品)  网络请求
     */
    private void okHttpGoodsApply() {

        OkHttpClientEM.getInstance().goodsApplyGoods().enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                activityUtils.showDialog("物资申请", "网络异常，请稍后");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 物资申请(型号)  网络请求
     */
    private void okHttpGoodsApplyModel(String typeId) {
        OkHttpClientEM.getInstance().goodsApplyGoodsModel(typeId).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                activityUtils.showDialog("物资申请", "网络异常，请稍后");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 物资申请(提交)  网络请求
     */
    private void okHttpGoodsApplyCommit() {
        Log.e("数据", "okHttpGoodsApplyCommit:   物品id" + goodsId + "数量：" + goodsNumber);
        String userId = SharePreferencesUtils.getUser().getId();
        String note = edtNote.getText().toString().trim();

        relLogin.setVisibility(View.VISIBLE);//展示刷新页面
        btnCommit.setEnabled(false);//登入失去焦点
        OkHttpClientEM.getInstance().goodsApplyGoodsCommit(userId, goodsId, goodsNumber, note).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                if (relLogin.getVisibility() == View.VISIBLE) {
                    if (relLogin.getVisibility() == View.VISIBLE) {
                        relLogin.setVisibility(View.GONE);//隐藏刷新页面
                        btnCommit.setEnabled(true);//登入获取焦点
                    }
                    btnCommit.setEnabled(true);//登入获取焦点
                }
                activityUtils.showDialog("物资申请", "网络异常，请稍后");
            }

            @Override
            public void onResponseUI(Call call, String json) {

                Message msg = new Message();
                msg.what = 3;
                msg.obj = json;
                handler.sendMessage(msg);
            }
        });
    }

}
