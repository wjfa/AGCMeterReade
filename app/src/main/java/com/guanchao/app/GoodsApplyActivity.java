package com.guanchao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.editable;

public class GoodsApplyActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_goods_name)
    EditText edtName;
    @BindView(R.id.edt_goods_apply)
    EditText edtApply;
    @BindView(R.id.edt_goods_time)
    EditText edtTime;
    @BindView(R.id.edt_goods_unit)
    EditText edtUnit;
    @BindView(R.id.edt_goods_note)
    EditText edtNote;
    @BindView(R.id.tv_goods_prompt)
    TextView tvPrompt;
    @BindView(R.id.btn_goods_commit)
    Button btnCommit;
    private int MaxLen = 200;//最大输入数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_apply);
        ButterKnife.bind(this);

        tvPrompt.setText("还可输入 " + MaxLen + "字");
        edtNote.addTextChangedListener(textWatcher);
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

            if (edtName.length() != 0 || edtApply.length() != 0 || edtTime.length() != 0 || edtUnit.length() != 0) {
                btnCommit.setEnabled(true);
                btnCommit.setBackgroundColor(getResources().getColor(R.color.color_yes_click));
            } else {
                btnCommit.setEnabled(false);
                btnCommit.setBackgroundColor(getResources().getColor(R.color.color_no_click));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.ig_back, R.id.btn_goods_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.btn_goods_commit:
                break;
        }
    }
}
