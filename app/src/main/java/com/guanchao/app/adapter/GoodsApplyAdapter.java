package com.guanchao.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.GoodsApplyActivity;
import com.guanchao.app.R;
import com.guanchao.app.entery.GoodsApply;
import com.guanchao.app.entery.NoticeAnounce;

import static com.guanchao.app.R.drawable.choose_item_no_select;

/**
 * Created by 王建法 on 2017/7/10.
 */

public class GoodsApplyAdapter extends MyBaseAdapter<GoodsApply> {
    private int clickTemp = -1;//刚进入时临时的点击背景  -1是无背景  0是第一个index设置默认点击背景

    public GoodsApplyAdapter(Context context, int clickTemp) {
        super(context);
        this.clickTemp = clickTemp;
    }
    //标识选择的Item设置背景
    public void setSeclectionBgColor(int position) {
        clickTemp = position;
    }
    @Override
    public View getMyView(final int position, View convertView, ViewGroup parent) {

        MyViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_goods_apply, null);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        //设置物品到控件上
        GoodsApply goodsApply = myList.get(position);
        holder.goods.setText(goodsApply.getTypeName());

        // 根据是否被选择来切换item的背景
        if (clickTemp == position) {
            holder.goods.setBackgroundResource(R.drawable.choose_item_selecteds);
        } else {
            holder.goods.setBackgroundResource(R.drawable.shape_border4);
        }

      /*  if (myListener != null) {
            //点击监听
            holder.goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onNewItemClick(v, position);
                }
            });
            //长按监听
            holder.goods.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myListener.OnNewItemLongClick(v, position);
                    return true;
                }
            });
        }*/
        return convertView;
    }


    class MyViewHolder {


        public TextView goods;

        public MyViewHolder(View view) {
            goods = (TextView) view.findViewById(R.id.tv_apply_item);
        }


    }

    /**
     * Recycler的适配器中没有Item的监听方法，因此需要自己定义监听接口去实现监听效果
     * 点击监听接口
     */
    public interface onNewItemClickListener {
        void onNewItemClick(View view, int postion); //点击时的方法

        void OnNewItemLongClick(View view, int postion);
    }

    //声明监听接口对象
    public GoodsApplyAdapter.onNewItemClickListener myListener;

    //对外提供一个监听方法
    public void setonNewItemClickListener(GoodsApplyAdapter.onNewItemClickListener listener) {
        this.myListener = listener;
    }
}