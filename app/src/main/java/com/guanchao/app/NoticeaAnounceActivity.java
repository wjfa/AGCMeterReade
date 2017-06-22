package com.guanchao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeaAnounceActivity extends AppCompatActivity {

    @BindView(R.id.ig_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyView_notice)
    RecyclerView recylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticea_anounce);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ig_back)
    public void onClick() {
        finish();
    }
}
