package com.guanchao.app.fragmet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.MainActivity;
import com.guanchao.app.R;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.User;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户报修
 */
public class RepairsFragment extends Fragment {
    View view;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_user_repairs_name)
    EditText edtUserName;
    @BindView(R.id.edt_user_repairs_phone)
    EditText edtUserPhone;
    @BindView(R.id.edt_user_repairs_email)
    EditText edtUserAddres;
    @BindView(R.id.edt_user_repairs_context)
    EditText edtUserContext;//输入内容
    @BindView(R.id.btn_user_repairs_ok)
    Button btnOk;
    @BindView(R.id.rel_repairs_ref)
    RelativeLayout relRefresh;//刷新页面
    public static int stutesCommitOK;//点击保存后设置跳转到维修页面
    private ActivityUtils activityUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repairs, container, false);
        ButterKnife.bind(this, view);
        activityUtils=new ActivityUtils(getActivity());
        intControl();

        return view;
    }
    private void intControl(){
        edtUserName.addTextChangedListener(textWatcher);
        edtUserPhone.addTextChangedListener(textWatcher);
        edtUserAddres.addTextChangedListener(textWatcher);
        edtUserContext.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (edtUserName.length()==0||edtUserPhone.length()==0||edtUserAddres.length()==0||edtUserContext.length()==0){
                btnOk.setEnabled(false);
                btnOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            }else {
                //设置button可点击
                btnOk.setEnabled(true);
                btnOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @OnClick(R.id.btn_user_repairs_ok)
    public void onClick() {
        okHttpUserRepairs();
    }

    /**
     * 用户报修网络上传
     */
    private void okHttpUserRepairs(){
        String callMan=edtUserName.getText().toString().trim();
        String mobile=edtUserPhone.getText().toString().trim();
        String addres=edtUserAddres.getText().toString().trim();
        String callContent=edtUserContext.getText().toString().trim();
        relRefresh.setVisibility(View.VISIBLE);
        if (!"".equals(callMan)||!"".equals(mobile)||!"".equals(addres)||!"".equals(callContent)) {
            //获取本地存储的登入人ID
            User user = SharePreferencesUtils.getUser();
            String repairStaffID = user.getId();
            OkHttpClientEM.getInstance().userRepairs(callMan,mobile,addres,callContent,repairStaffID).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relRefresh.getVisibility()==View.VISIBLE){
                        relRefresh.setVisibility(View.GONE);
                    }
                    activityUtils.showDialog("用户报修","网络异常，请稍后重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {
                    //{"data":null,"message":"报修成功","success":true}
                    BaseEntity baseEntity = Parser.parserUserRepairs(json);
                    if (baseEntity.getSuccess()==true){
                        stutesCommitOK=1;//设置点击保存后跳转到维修页面  MainActivity用到
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                        activityUtils.showToast("提交成功");
                    }else {
                        if (relRefresh.getVisibility()==View.VISIBLE){
                            relRefresh.setVisibility(View.GONE);
                        }
                        activityUtils.showDialog("用户报修",baseEntity.getMessage());
                    }

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        ButterKnife.bind(getActivity()).unbind();
        super.onDestroy();
    }
}
