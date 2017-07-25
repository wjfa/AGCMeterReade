package com.guanchao.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.guanchao.app.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeShowMsgActivity extends AppCompatActivity {

    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_show_msg);
        ButterKnife.bind(this);

      /*  //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        if (QrCodeScanActivity.QrCode ==1) {
//
            Log.e("扫描结果22", "onScanQRCodeSuccess: "+getIntent().getStringExtra("QrcReslut"));
            tvMsg.setText("户号："+getIntent().getStringExtra("QrcReslut"));
        } else if (QrCodeScanActivity.QrCode == 2) {
            tvMsg.setText(getIntent().getStringExtra("qcResultError"));

        } else if (QrCodeScanActivity.QrCode == 3) {
            tvMsg.setText(getIntent().getStringExtra("photoResult"));

        }
    }

    @OnClick(R.id.ig_back)
    public void onClick() {
        startActivity(new Intent(QrCodeShowMsgActivity.this, QrCodeScanActivity.class));
        finish();
    }

    //调用系统返回键按钮  返回跳转到上一页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(QrCodeShowMsgActivity.this, QrCodeScanActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
