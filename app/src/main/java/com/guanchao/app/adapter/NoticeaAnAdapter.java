package com.guanchao.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanchao.app.R;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.NoticeAnounce;
import com.guanchao.app.entery.Watch;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class NoticeaAnAdapter extends RecyclerView.Adapter<NoticeaAnAdapter.MyViewHolder> {

    private List<NoticeAnounce.ListBean> noticeaList = new ArrayList<>();
    private Context context;

    public NoticeaAnAdapter(Context context, List<NoticeAnounce.ListBean> noticeaList) {
        this.context = context;
        this.noticeaList = noticeaList;


    }

    public void addDate(int pos, NoticeAnounce.ListBean data) {
        noticeaList.add(pos, data);
        //更新，不是notifyDataSetChanged();
        notifyItemChanged(pos);
        notifyItemInserted(pos);
        notifyItemRangeChanged(pos, noticeaList.size());
    }

    //添加数据并判断是否清空
    public void appendData(NoticeAnounce.ListBean data, boolean isClearOld) {
        if (data == null)
            return;
        if (isClearOld)
            noticeaList.clear();
        noticeaList.add(noticeaList.size(), data);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notice_anounce, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO: 2017/3/15 先对整体布局进行点击监听

        NoticeAnounce.ListBean noticeAnounce = noticeaList.get(position);
        String title = noticeAnounce.getTitle();
        String content = noticeAnounce.getContent();
        String createDate = noticeAnounce.getCreateDate();
        NoticeAnounce.ListBean bean = new NoticeAnounce.ListBean(title, content, createDate);
        holder.tvTitle.setText(bean.getTitle());
        holder.tvContent.setText(bean.getContent());
        holder.tvTime.setText(bean.getCreateDate());


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
    }

    @Override
    public int getItemCount() {
        return noticeaList.size()==0?0:noticeaList.size();
    }

    /**
     * 创建自定义item布局类优化
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout releal;
        private ImageView inco;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvTime;
//        private TextView tvCheckMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            releal = (RelativeLayout) itemView.findViewById(R.id.rel_notice);
            inco = (ImageView) itemView.findViewById(R.id.img_notice_photo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_notice_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_item_notive_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_item_notice_time);
//            tvCheckMore = (TextView) itemView.findViewById(R.id.tv_item_notice_more);

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
    public onNewItemClickListener myListener;

    //对外提供一个监听方法
    public void setonNewItemClickListener(onNewItemClickListener listener) {
        this.myListener = listener;
    }
}