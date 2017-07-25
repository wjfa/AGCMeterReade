package com.guanchao.app.network.parser;

import android.content.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanchao.app.entery.BaseEntity;
import com.guanchao.app.entery.GoodsApply;
import com.guanchao.app.entery.GoodsApplyModel;
import com.guanchao.app.entery.ImgUpdate;
import com.guanchao.app.entery.NoticeAnounce;
import com.guanchao.app.entery.ServiceRepair;
import com.guanchao.app.entery.User;
import com.guanchao.app.entery.UserSelcetWatchMessage;
import com.guanchao.app.entery.UserSelect;
import com.guanchao.app.entery.Watch;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class Parser {


    //请求成功或失败  内容
   /* public static BaseEntity parserStateCode(String json) {
        BaseEntity result = GsonUtils.parseJsonWithGson(json, BaseEntity.class);
        return result;
    }*/
    //请求成功或失败  内容
   /* public static BaseEntity parserStateCode(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken<BaseEntity>() {
        }.getType());

        return entity;

    }*/
    //登入时解析json数据
    public static BaseEntity<User> parserLogin(String json){
        Gson gson = new Gson();
        BaseEntity<User> entity = gson.fromJson(json, new TypeToken<BaseEntity<User>>() {
        }.getType());

        return entity;

    }
    //抄表任务解析json数据（列表）
    public static BaseEntity<List<Watch>> parserWatch(String json){
        Gson gson = new Gson();
        BaseEntity<List<Watch>> entity = gson.fromJson(json, new TypeToken<BaseEntity<List<Watch>>>() {
        }.getType());
        return entity;

    }
    //用户报修解析json数据
    public static BaseEntity parserUserRepairs(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken<BaseEntity>() {
        }.getType());
        return entity;

    }

    //用户选择解析json数据(列表)
    public static  BaseEntity<UserSelect> parserUserSelect(String json){
        Gson gson = new Gson();
        BaseEntity<UserSelect> entity = gson.fromJson(json, new TypeToken< BaseEntity<UserSelect>>() {
        }.getType());
        return entity;

    }
    /* //用户选择“查询”解析json数据(查询)
     public static  BaseEntity<UserSelect.PdBean> parserUserSelectQuery(String json){
         Gson gson = new Gson();
         BaseEntity<UserSelect.PdBean> entity = gson.fromJson(json, new TypeToken< BaseEntity<UserSelect.PdBean>>() {
         }.getType());
         return entity;

     }*/
    //用户选择后抄表页面获取用户信息(列表)
    public static  BaseEntity<UserSelcetWatchMessage> parserUserSelectWatchMsg(String json){
        Gson gson = new Gson();
        BaseEntity<UserSelcetWatchMessage> entity = gson.fromJson(json, new TypeToken< BaseEntity<UserSelcetWatchMessage>>() {
        }.getType());
        return entity;

    }
    //设置水表经纬度（人工拍照）
    public static  BaseEntity parserWaterLatLutde(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }
    //图片上传（人工拍照）
    public static  BaseEntity<ImgUpdate> parserImgUpdate(String json){
        Gson gson = new Gson();
        BaseEntity<ImgUpdate> entity = gson.fromJson(json, new TypeToken< BaseEntity<ImgUpdate>>() {
        }.getType());
        return entity;

    }
    //人工拍照
    public static  BaseEntity parserPeoplePhoto(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }
    //维修列表
    public static  BaseEntity<List<ServiceRepair>> parserServiceList(String json){
        Gson gson = new Gson();
        BaseEntity<List<ServiceRepair>> entity = gson.fromJson(json, new TypeToken< BaseEntity<List<ServiceRepair>>>() {
        }.getType());
        return entity;

    }
    //故障维修
    public static  BaseEntity parserServiceFault(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }
    //用户通知
    public static NoticeAnounce parserNoticeAnounce(String json){
        Gson gson = new Gson();
        NoticeAnounce entity = gson.fromJson(json, new TypeToken< NoticeAnounce>() {
        }.getType());
        return entity;

    }
    //上班签到和下班签退
    public static BaseEntity parserSignClickWork(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }
    //上班签到和下班签退
    public static BaseEntity parserProblemFeedBack(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }

    //物资申请（物品）
    public static List<GoodsApply> parserGoodsApply(String json){
        Gson gson = new Gson();
        List<GoodsApply> entity = gson.fromJson(json, new TypeToken< List<GoodsApply>>() {
        }.getType());

        return entity;
    }
    //物资申请（物品）
    public static List<GoodsApplyModel> parserGoodsApplyModel(String json){
        Gson gson = new Gson();
        List<GoodsApplyModel> entity = gson.fromJson(json, new TypeToken< List<GoodsApplyModel>>() {
        }.getType());

        return entity;
    }
    //上班签到和下班签退
    public static BaseEntity parserGoodsApplyCommit(String json){
        Gson gson = new Gson();
        BaseEntity entity = gson.fromJson(json, new TypeToken< BaseEntity>() {
        }.getType());
        return entity;

    }
}
/*class GsonUtils {
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }
}*/
