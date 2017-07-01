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
import com.guanchao.app.entery.Watch;

import java.util.List;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class NoticeaAnAdapter extends RecyclerView.Adapter<NoticeaAnAdapter.MyViewHolder> {

    private List<String> noticeaList;
    private Context context;

    public NoticeaAnAdapter(Context context, List<String> noticeaList) {
        this.noticeaList = noticeaList;
        this.context = context;
    }

    public void addDate(int pos,String data){
        noticeaList.add(pos,data);
        notifyItemChanged(pos);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notice_anounce, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO: 2017/3/15 先对整体布局进行点击监听

        holder.tvTitle.setText("标题：  " + noticeaList.get(position));
       /* holder.tvPosition.setText("区域：" + watch.getAreaName());
        holder.tvHouseNumber.setText("户数：" + watch.getHouseholds());
        holder.tvCopied.setText("已抄：" + watch.getFinished());
*/

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
        return noticeaList.size();
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