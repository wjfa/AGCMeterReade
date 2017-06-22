package com.guanchao.app.fragmet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guanchao.app.R;
import com.guanchao.app.ServiceFaultActivity;
import com.guanchao.app.adapter.ServiceAdapter;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.ServiceRepair;
import com.guanchao.app.entery.User;
import com.guanchao.app.network.OkHttpClientEM;
import com.guanchao.app.network.UICallBack;
import com.guanchao.app.network.parser.Parser;
import com.guanchao.app.utils.ActivityUtils;
import com.guanchao.app.utils.SharePreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class ServiceRepairsFragment extends Fragment {
    View view;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.reylerview_service_repairs)
    RecyclerView reylerview;
    private ServiceAdapter serviceAdapter;
    private List<String> list = new ArrayList<>();
    private ActivityUtils activityUtils;
    private List<ServiceRepair> serviceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_service_repairs, container, false);
        ButterKnife.bind(this, view);
        activityUtils = new ActivityUtils(getActivity());
        //设置一下actionbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        okHttpService();//维修列表  请求
        return view;
    }


    //item监听
    int pos;
    private ServiceAdapter.onNewItemClickListener newItemClickListener = new ServiceAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
            pos=postion;
            //获取成功后返回的信息  设置在故障维修页面的控件上用到
            ServiceRepair service = serviceList.get(postion);
            String callMan = service.getCallMan();//报修人
            String mobile = service.getMobile();//电话
            Object repairTime = service.getRepairTime();//报修时间
            String address = service.getAddress();//地址
            String callContent = service.getCallContent();//报修内容
            //获取维修信息保存  后面维修故障请求会用到
            String repairsId = service.getId();
            Object beforePic = service.getRepairBeforePic();
            Object inPic = service.getRepairInPic();
            Object afterPic = service.getRepairAfterPic();
            ServiceRepair serviceRepair = new ServiceRepair(repairsId, callMan, mobile, repairTime, address, callContent,beforePic,inPic,afterPic);

            startActivity(new Intent(getActivity(), ServiceFaultActivity.class).putExtra("ServiceRepair", serviceRepair));
        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            //Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 维修列表  网络请求
     */
    private void okHttpService() {
        //获取本地存储的登入人ID
        User user = SharePreferencesUtils.getUser();
        String userId = user.getId();
        OkHttpClientEM.getInstance().serviceList(userId).enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                activityUtils.showToast("网络异常，请重试！");
            }

            @Override
            public void onResponseUI(Call call, String json) {
                BaseEntity<List<ServiceRepair>> entity = Parser.parserServiceList(json);
                if (entity.getSuccess() == true) {
                    serviceList = entity.getData();

                    //设置适配器
                    serviceAdapter = new ServiceAdapter(getActivity(), serviceList);
                    reylerview.setAdapter(serviceAdapter);
                    serviceAdapter.notifyDataSetChanged();
                    reylerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    reylerview.setItemAnimator(new DefaultItemAnimator());
                    //设置监听
                    serviceAdapter.setonNewItemClickListener(newItemClickListener);
                    activityUtils.showToast(entity.getMessage());
                } else {
                    activityUtils.showToast(entity.getMessage());
                }
            }
        });
    }
}
