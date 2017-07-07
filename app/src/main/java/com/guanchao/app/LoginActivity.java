package com.guanchao.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.User;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.guanchao.app.utils.StatusBarUtil;
import com.guanchao.app.utils.SystemBarCompat;
import com.guanchao.app.utils.SystemStatusCompat;

import java.io.IOException;
import java.security.MessageDigest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.icu.lang.UProperty.NAME;

/**
 * 登入页面
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edt_user)
    TextView edtUser;
    @BindView(R.id.edt_password)
    TextView edtPassword;
    @BindView(R.id.cbox_login)
    CheckBox coxLogin;//自动登入
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.rel_login_ref)
    RelativeLayout relLogin;//刷新页面
    private ActivityUtils activityUtils;
    private static boolean isCBox = false;
    private static boolean checkBoxMSg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(LoginActivity.this);

        //SystemStatusCompat.compat(this);
//         SystemStatusCompat.compat(this, getResources().getColor(R.color.color_yes_click));
//        SystemStatusCompat.setStatusBarBg(this, R.color.white);
//        //设置状态栏字体的颜色
//        SystemBarCompat.setFlymeStatusBarDarkIcon(this, true);
        //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.white), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, true);

        init();

    }

    private void init() {
        //获取本地存储的手机号和密码  15335192608  密码：123456
        String[] msg = SharePreferencesUtils.getUserMsg(LoginActivity.this);
        edtUser.setText(msg[0]);
        edtPassword.setText(msg[1]);

        //设置CheckBox
        coxLogin.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coxLogin.isChecked()) {
                    //保存存储点击状态
                    isCBox = true;
                    SharePreferencesUtils.savaCheckBox(LoginActivity.this, isCBox);

                    // OkHttpLogin();
                } else {
                    isCBox = false;
                    SharePreferencesUtils.savaCheckBox(LoginActivity.this, isCBox);

                }
            }
        });
        //获取本地点击的信息  并判断如果true直接登入
        checkBoxMSg = SharePreferencesUtils.getCheckBox(LoginActivity.this);
        coxLogin.setChecked(checkBoxMSg);//设置CheckBox是否选中
        if (checkBoxMSg) {//如果选中  则自动登入
            OkHttpLogin();
        }else {
            edtPassword.setText("");//密码为空
        }
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        OkHttpLogin();
    }

    /**
     * 登入  网络请求
     */
    private void OkHttpLogin() {
        final String username = edtUser.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        String passmd5 = MD5(password);//密码需要用MD5加密  32位
        Log.e("加密", passmd5);

        if ("".equals(username)) {
            activityUtils.showDialog("用户登入", "用户名不能为空");
        } else if ("".equals(password)) {
            activityUtils.showDialog("用户登入", "密码不能为空");
        } else {
            relLogin.setVisibility(View.VISIBLE);//展示刷新页面
            OkHttpClientEM.getInstance().login(username, passmd5).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    if (relLogin.getVisibility() == View.VISIBLE) {
                        relLogin.setVisibility(View.GONE);//隐藏刷新页面
                    }
                    activityUtils.showDialog("用户登入", "网络异常，请重试");
                }

                @Override
                public void onResponseUI(Call call, String json) {

                    //解析json
                    BaseEntity<User> watch = Parser.parserLogin(json);
                    if (watch.getSuccess() == true) {
                        //成功后保存手机号和密码（该密码是通过加密的，所以不能从返回的数据中进行保存）
                        SharePreferencesUtils.savaUserMsg(LoginActivity.this, username, password);

                        if (relLogin.getVisibility() == View.VISIBLE) {
                            relLogin.setVisibility(View.GONE);//隐藏刷新页面
                        }
                        String id = watch.getData().getId();
                        //将用户信息保存到本地配置里
                        User watchSh = new User(id);
                        SharePreferencesUtils.setUser(watchSh);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.dialog_enter_anim_top_bottom, R.anim.translate_right_to_left);
                        finish();
                        //   activityUtils.showToast(watch.getMessage());
                    } else {
                        if (relLogin.getVisibility() == View.VISIBLE) {
                            relLogin.setVisibility(View.GONE);//隐藏刷新页面
                        }
                        activityUtils.showDialog("用户登入", watch.getMessage());
                    }

                }
            });
        }
    }

    /**
     * MD5密码加密，32位
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }





}
