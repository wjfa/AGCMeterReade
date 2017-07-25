package com.guanchao.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by 王建法 on 2017/7/18.
 *
 * 仿QQ空间listview反弹效果
 */

public class BoundScrollView extends  ListView {

    private static final String TAG = "CustomListView";
    private ImageView iv_header_view;
    private int maxImgHeight;//图片的最大高度
    private int originalHeight;//imageview 图片对应的原始高度

    public BoundScrollView(Context context) {
        super(context);
    }

    public BoundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoundScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 当listview滑动到头的时候被调用
     * @param deltaX Change in X in pixels
     * @param deltaY Change in Y in pixels
     * @param scrollX Current X scroll value in pixels before applying deltaX
     * @param scrollY Current Y scroll value in pixels before applying deltaY
     * @param scrollRangeX Maximum content scroll range along the X axis
     * @param scrollRangeY Maximum content scroll range along the Y axis
     * @param maxOverScrollX Number of pixels to overscroll by in either direction
     *          along the X axis.
     * @param maxOverScrollY Number of pixels to overscroll by in either direction
     *          along the Y axis. 滑动到头部最多还可以滑动多少
     * @param isTouchEvent true if this scroll operation is the result of a touch event.
     * @return true if scrolling was clamped to an over-scroll boundary along either
     *          axis, false otherwise. isTouch true就是手慢慢的滑动到头部 false表示一瞬间滑动到头部有惯性的

     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if(deltaY<0&&isTouchEvent){
            //当前的高度+拖动的距离  动态改变imageview的高度
            int resetHeight = iv_header_view.getHeight()-deltaY;
            if(resetHeight>maxImgHeight){
                resetHeight = maxImgHeight;
            }
            iv_header_view.getLayoutParams().height = resetHeight;
            iv_header_view.requestLayout();//重新 layout --->onLayout去动态计算他的高度
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                BoundAnimation resetHeightAnimation  = new BoundAnimation(iv_header_view,originalHeight);
                startAnimation(resetHeightAnimation);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setView(ImageView view) {
        this.iv_header_view = view;
        this.originalHeight = view.getHeight();
        maxImgHeight = iv_header_view.getDrawable().getIntrinsicHeight();
    }
}