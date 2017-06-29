package com.guanchao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 问题反馈
 */
public class ProblemFeedbackActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_problem_title)
    EditText edtTitle;
    @BindView(R.id.edt_problem_details)
    EditText edtDetails;
    @BindView(R.id.img_up_photo)
    ImageView imgUpPhoto;
    @BindView(R.id.btn_problem_photo)
    Button btnPhoto;
    @BindView(R.id.btn_problem_camear)
    Button btnCamear;
    @BindView(R.id.edt_problem_phone)
    EditText edtPhone;
    @BindView(R.id.btn_problem_ok)
    Button btnProblemOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_feedback);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        edtTitle.addTextChangedListener(textWatcher);
        edtDetails.addTextChangedListener(textWatcher);

    }
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (edtTitle.length()!=0||edtDetails.length()!=0||edtPhone.length()!=0){
                btnProblemOk.setEnabled(true);
                btnProblemOk.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            }else {
                btnProblemOk.setEnabled(false);
                btnProblemOk.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @OnClick({R.id.ig_back, R.id.btn_problem_photo, R.id.btn_problem_camear, R.id.btn_problem_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_problem_photo://本地相册
                break;
            case R.id.btn_problem_camear://相机
                break;
            case R.id.btn_problem_ok://提交
                break;
        }
    }
}
