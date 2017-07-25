package com.guanchao.app.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

/**
 * Created by 王建法 on 2017/7/18.
 */

public class BoundAnimation extends Animation {
    private View view;
    private int targetHeight;
    private int originalHeight;//imageview最初的高度
    private int totalHeight;
    public BoundAnimation(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        originalHeight = view.getHeight();
        totalHeight = targetHeight-originalHeight;
        setDuration(400);
        setInterpolator(new OvershootInterpolator());
    }

    /**
     *
     * @param interpolatedTime 动画执行的进度 默认是0--1 或者百分比 比如他是0.5表示动画执行了50%
     * @param t
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        int resetHeight = (int) (originalHeight+totalHeight*interpolatedTime);
        view.getLayoutParams().height = resetHeight;
        view.requestLayout();
    }
}