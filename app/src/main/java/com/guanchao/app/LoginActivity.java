package com.guanchao.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.User;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;
import com.guanchao.app.utils.SystemBarCompat;
import com.guanchao.app.utils.SystemStatusCompat;

import java.io.IOException;
import java.security.MessageDigest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edt_user)
    TextView edtUser;
    @BindView(R.id.edt_password)
    TextView edtPassword;
    @BindView(R.id.cbox_login)
    CheckBox cboxLogin;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private ActivityUtils activityUtils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(LoginActivity.this);
        edtUser.setText("15335192608");
        edtPassword.setText("123456");
        //SystemStatusCompat.compat(this);
//         SystemStatusCompat.compat(this, getResources().getColor(R.color.color_yes_click));
        SystemStatusCompat.setStatusBarBg(this,R.color.white);
        //设置状态栏字体的颜色
        SystemBarCompat.setFlymeStatusBarDarkIcon(this,true);
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        setLogin();
//        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }


    private void setLogin() {
        String username = edtUser.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passmd5 = MD5(password);//密码需要用MD5加密  32位
        Log.e("加密", passmd5);

        if ("".equals(username)) {
            activityUtils.showToast("用户名不能为空");
        } else if ("".equals(password)) {
            activityUtils.showToast("密码不能为空");
        } else {
            OkHttpClientEM.getInstance().login(username, passmd5).enqueue(new UICallBack() {
                @Override
                public void onFailureUI(Call call, IOException e) {
                    activityUtils.showToast("网络异常，请重试！");
                }

                @Override
                public void onResponseUI(Call call, String json) {

                    //解析json
                    BaseEntity<User> watch = Parser.parserLogin(json);
                    if (watch.getSuccess() == true) {
                        String id = watch.getData().getId();
                        //将用户信息保存到本地配置里
                        User watchSh = new User(id);
                        SharePreferencesUtils.setUser(watchSh);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        activityUtils.showToast(watch.getMessage());
                    }else {
                        activityUtils.showToast(watch.getMessage());
                    }

                }
            });
        }
    }

    // MD5加密，32位
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

}
