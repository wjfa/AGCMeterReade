package com.guanchao.app.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.guanchao.app.R;

public class MyMusicService extends Service {

    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取音乐资源
        mp = MediaPlayer.create(this, R.raw.pm4);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();// 释放资源。让资源得到释放;;
            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mp.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }
}
