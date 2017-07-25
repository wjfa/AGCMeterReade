package com.guanchao.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.R;
import com.guanchao.app.entery.NoticeAnounce;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 王建法 on 2017/7/10.
 */

public class NoticeaAnceAdapter extends MyBaseAdapter<NoticeAnounce.ListBean> {
    public NoticeaAnceAdapter(Context context) {
        super(context);
    }

    @Override
    public View getMyView(final int position, View convertView, ViewGroup parent) {

        MyViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_notice_anounce, null);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        NoticeAnounce.ListBean noticeAnounce = myList.get(position);
        holder.tvTitle.setText(noticeAnounce.getTitle());
        holder.tvContent.setText(noticeAnounce.getContent());
        holder.tvTime.setText(noticeAnounce.getCreateDate());

        holder.releal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (myListener != null) {
            //点击监听
            holder.releal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onNewItemClick(v, position);
                }
            });
            //长按监听
            holder.releal.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myListener.OnNewItemLongClick(v, position);
                    return true;
                }
            });
        }
        return convertView;
    }


    class MyViewHolder {

        private RelativeLayout releal;
        private ImageView inco;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvTime;

        public MyViewHolder(View view) {
            releal = (RelativeLayout) view.findViewById(R.id.rel_notice);
            inco = (ImageView) view.findViewById(R.id.img_notice_photo);
            tvTitle = (TextView) view.findViewById(R.id.tv_item_notice_title);
            tvContent = (TextView) view.findViewById(R.id.tv_item_notive_content);
            tvTime = (TextView) view.findViewById(R.id.tv_item_notice_time);
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
    public NoticeaAnceAdapter.onNewItemClickListener myListener;

    //对外提供一个监听方法
    public void setonNewItemClickListener(NoticeaAnceAdapter.onNewItemClickListener listener) {
        this.myListener = listener;
    }
}