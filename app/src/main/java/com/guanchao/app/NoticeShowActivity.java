package com.guanchao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.guanchao.app.entery.NoticeAnounce;
import com.guanchao.app.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeShowActivity extends AppCompatActivity {

    @BindView(R.id.tv_show_title)
    TextView tvTitle;
    @BindView(R.id.tv_show_content)
    TextView tvContent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_show);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
       /* //设置状态栏背景颜色
        StatusBarUtil.setStatusBgColor(this, getResources().getColor(R.color.textCursorDrawable), false);
        //设置状态栏字体的颜色true  深色  false 白色
        StatusBarUtil.StatusBarTestColorMode(this, false);
*/
        NoticeAnounce.ListBean noticeShow = (NoticeAnounce.ListBean) getIntent().getSerializableExtra("NoticeShow");
        tvTitle.setText(noticeShow.getTitle());
        tvContent.setText(noticeShow.getContent());
    }

    @OnClick(R.id.toolbar)
    public void onClick() {
        finish();
    }
}
