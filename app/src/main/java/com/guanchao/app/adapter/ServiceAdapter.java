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
import com.guanchao.app.entery.ServiceRepair;

import java.util.List;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<ServiceRepair> serviceList;
    private Context context;

    public ServiceAdapter(Context context, List<ServiceRepair> serviceList) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_service_repairs, parent, false));
        return myViewHolder;
    }

    int stCompete;

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO: 2017/3/15 先对整体布局进行点击监听
        ServiceRepair service = serviceList.get(position);

        //获取是否维修完成
        for (int i = 0; i < serviceList.size(); i++) {
            stCompete = Integer.valueOf(service.getSt());

            if (stCompete == 1||stCompete == 2) {//如果等于3时设置未完成
                holder.icon.setImageResource(R.mipmap.ic_no_complete);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_complete);

            }
        }
        holder.people.setText("报修人：" + service.getCallMan());
        holder.tvPhone.setText("电话：" + service.getMobile());
        holder.tvPosition.setText("地址：" + service.getAddress());
        if (myListener != null) {
            //点击监听
            holder.relService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onNewItemClick(v, position);
                }
            });
            //长按监听
            holder.relService.setOnLongClickListener(new View.OnLongClickListener() {
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
        return serviceList.size();
    }

    /**
     * 创建自定义item布局类优化
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relService;
        private ImageView icon;
        private TextView people;
        private TextView tvPhone;
        private TextView tvPosition;

        public MyViewHolder(View itemView) {
            super(itemView);
            //对item整体设置监听
            relService = (RelativeLayout) itemView.findViewById(R.id.rel_service_p);
            icon = (ImageView) itemView.findViewById(R.id.img_service_photo);
            people = (TextView) itemView.findViewById(R.id.tv_item_service_peploe);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_item_service_phone);
            tvPosition = (TextView) itemView.findViewById(R.id.tv_item_service_emile);

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