package com.guanchao.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.guanchao.app.R;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.UserSelect;
import com.guanchao.app.entery.Watch;
import com.guanchao.app.utils.DensityUtil;

import java.util.List;

import static com.guanchao.app.network.HttpUtilsApi.serviceList;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class UserSelectAdapter extends BaseRecyclerAdapter<UserSelectAdapter.MyViewHolder> {


    private List<UserSelect.ListBean> serviceList;
    private Context context;
    private int largeCardHeight, smallCardHeight;
    private UserSelect.ListBean userSevice;

    public UserSelectAdapter(Context context, List<UserSelect.ListBean> serviceList) {
        this.serviceList = serviceList;
        this.context = context;
        //根据手机的分辨率从 dp 的单位 转成为 px(像素)
        largeCardHeight = DensityUtil.dip2px(context, 150);
        smallCardHeight = DensityUtil.dip2px(context, 100);
    }


    //添加集合数据
    public void addDateList(UserSelect.ListBean listBean, int pos) {
        serviceList.add(pos, listBean);
        //更新，不是notifyDataSetChanged();
        notifyItemChanged(pos);
        notifyItemInserted(pos);
        notifyItemRangeChanged(pos, serviceList.size());
    }

    public void insert(UserSelect.ListBean listBean, int position) {
        insert(serviceList, listBean, position);
    }

    //添加数据并判断是否清空
    public void appendData(UserSelect.ListBean t,boolean isClearOld){
        if(t==null)
            return;
        if(isClearOld)
            serviceList.clear();
        serviceList.add(t);
    }
    //添加集合数据并判断是否清空�
    public void appendData(List<UserSelect.ListBean> data,boolean isClearOld){
        if(data==null)
            return;
        if(isClearOld)
            serviceList.clear();
        serviceList.addAll(data);
    }

    public void clear(){
        serviceList.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_select_user, parent, false);
        MyViewHolder vh = new MyViewHolder(v, true);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position, boolean isItem) {
        // TODO: 2017/3/15 先对整体布局进行点击监听

        userSevice = serviceList.get(position);
        holder.tvHouseNumber.setText("户号：" + userSevice.getCustomerNo());
        holder.tvDoorName.setText("户名：" + userSevice.getName());
        holder.tvDoorNumber.setText("门牌号：" + userSevice.getHouseNumber());
        holder.tvWaterNumber.setText("水表钢号：" + userSevice.getWatermeterNo());
        holder.tvposition.setText("地址：" + userSevice.getAddress());

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            holder.rootView.getLayoutParams().height = position % 2 != 0 ? largeCardHeight : smallCardHeight;
        }

        if (myListener != null) {
            //点击监听
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onNewItemClick(v, position);
                }
            });
            //长按监听
            holder.btnSelect.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myListener.OnNewItemLongClick(v, position);
                    return true;
                }
            });
        }
    }


    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view, false);
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {

        return serviceList.size();
    }

    /**
     * 创建自定义item布局类优化
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        public View rootView;//父布局
        private TextView tvHouseNumber;
        private TextView tvDoorName;
        private TextView tvDoorNumber;
        private TextView tvWaterNumber;
        private TextView tvposition;
        private Button btnSelect;

        public MyViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {


                rootView = itemView
                        .findViewById(R.id.card_view);
                tvHouseNumber = (TextView) itemView.findViewById(R.id.tv_item_user_door_number);
                tvDoorName = (TextView) itemView.findViewById(R.id.tv_item_user_door_name);
                tvDoorNumber = (TextView) itemView.findViewById(R.id.tv_item_user_house_number);
                tvWaterNumber = (TextView) itemView.findViewById(R.id.tv_item_user_water_number);
                tvposition = (TextView) itemView.findViewById(R.id.tv_item_user_select_position);
                btnSelect = (Button) itemView.findViewById(R.id.btn_user_select);

            }
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