package com.guanchao.app.network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.R.attr.id;
import static android.R.attr.type;
import static com.guanchao.app.UserSelectActivity.watermeterId;

/**
 * Created by 王建法 on 2017/5/8.
 */

public class OkHttpClientEM {
    private Gson gson;

    private OkHttpClient okHttpClient;

    private OkHttpClientEM() {
        //日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //将拦截器放到okhttp中
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)//将拦截器放到okhttp中
                .build();

        //实例化
        gson = new Gson();
    }

    //单实例
    private static OkHttpClientEM okHttpClientEM;

    public static synchronized OkHttpClientEM getInstance() {
        if (okHttpClientEM == null) {
            okHttpClientEM = new OkHttpClientEM();
        }
        return okHttpClientEM;
    }


    /**
     * 登录
     * post
     *
     * @param username 用户名
     * @param password 密码
     */
    public Call login(String username, String password) {
        //表单形式构建请求体
       /* RequestBody requestBody = new FormBody.Builder()
                .add("mobile", username)
                .add("password", password)//密码需要用MD5加密  32位
                .build();*/

        //构建请求  http://218.92.200.222:8081/dtcb/mobileLogin?mobile=15335192608&password=e10adc3949ba59abbe56e057f20f883e
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.loginUrl + "mobile=" + username + "&" + "password=" + password)
                .get()
                .build();
        return okHttpClient.newCall(request);
    }

    /**
     * 抄表任务
     * post
     *
     * @param cbUser 用户ID
     * @param month  月份
     */
    public Call watchTask(String cbUser, String month) {
        //表单形式构建请求体
       /* RequestBody requestBody = new FormBody.Builder()
                .add("cbUser", cbUser)
                .add("month", month)
                .build();*/

        //构建请求  http://218.92.200.222:8081/dtcb/task/taskList4cbUser?cbUser=1&month=201704
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.watchTask + "cbUser=" + cbUser + "&" + "month=" + month)
                .get()
                .build();
        // Log.e("", HttpUtilsApi.httpUrl + HttpUtilsApi.watchTask + "cbUser=" + cbUser + "&" + "month=" + month);
        return okHttpClient.newCall(request);
    }

    /**
     * 用户报修
     * post
     *
     * @param callMan     报修人
     * @param mobile      手机
     * @param address     地址
     * @param callContent 报修内容
     * @param repairStaff 维修人：当前登陆人
     */
    public Call userRepairs(String callMan, String mobile, String address, String callContent, String repairStaff) {
        //表单形式构建请求体
       /* RequestBody requestBody = new FormBody.Builder()
                .add("cbUser", cbUser)
                .build();*/

        //构建请求  http://218.92.200.222:8081/dtcb/repair/mobileInsert?callMan=guxiaowei&mobile=15335192608&address=nanjing&callContent=broken&repairStaff=1
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.userRepairs + "callMan=" + callMan + "&" + "mobile=" + mobile + "&" + "address=" + address + "&" + "callContent=" + callContent + "&" + "repairStaff=" + repairStaff)
                .get()
                .build();
        //Log.e("", HttpUtilsApi.httpUrl + HttpUtilsApi.serviceList + "repairStaff=" + cbUser);
        return okHttpClient.newCall(request);
    }

    /**
     * 用户选择
     * post
     *
     * @param pagesize     每页记录数
     * @param pagenum      页数
     * @param groups       小组 即抄表任务中的areaId
     * @param customerName 户名
     * @param customerNo   户号
     * @param month        月份
     * @param type         是否抄表  0：未抄  1：已抄
     */
    public Call userSelect(int pagesize, int pagenum, String groups, String customerName, String customerNo, String month, int type) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/customer/userList?pagesize=5&pagenum=0&groups=142&name=%E4%B8%87%E4%B8%BA%E4%B8%9C&customerNo=0004000128
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.userSelect + "pagesize=" + pagesize + "&" + "pagenum=" + pagenum + "&" + "groups=" + groups + "&" + "name=" + customerName + "&" + "customerNo=" + customerNo + "&" + "month=" + month + "&" + "type=" + type)
                .get()
                .build();
//       Log.e("查询", HttpUtilsApi.httpUrl + HttpUtilsApi.userSelect + "pagesize=" + pagesize + "&" + "pagenum=" + pagenum + "&" + "groups=" + groups + "&" + "name=" + customerName + "&" + "customerNo=" + customerNo+ "&" + "month=" + month+ "&" + "type=" + type);
        return okHttpClient.newCall(request);
    }

    /**
     * 用户选择后获取用户抄表信息
     * post
     *
     * @param watermeterId 水表ID
     */
    public Call userSelectWatchInform(String watermeterId) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/customer/customerInfo4cb?watermeterId=14084
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId)
                .get()
                .build();

//        Log.e("", "userSelectWatchInform: "+ HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId );
        return okHttpClient.newCall(request);
    }

    /**
     * 设置水表经纬度（人工拍照）
     * post
     *
     * @param watermeterId 水表ID
     * @param longitude    经度
     * @param latitude     纬度
     */
    public Call setWaterLatLtude(String watermeterId, String longitude, String latitude) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/watermeter/updateLatitudeAndLongitude?watermeterId=14084&longitude=1&latitude=2
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.setWaterLatLtude + "watermeterId=" + watermeterId + "&" + "longitude=" + longitude + "&" + "latitude=" + latitude)
                .get()
                .build();

        //Log.e("", "userSelectWatchInform: "+ HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId );
        return okHttpClient.newCall(request);
    }

    /**
     * 图片上传(人工拍照)
     * post  无参数
     *
     * @param filePath 图片文件路径
     */
    public Call photoUpdate(String filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//添加类型
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody requestBody = builder.build();

        //构建请求  http://218.92.200.222:8081//dtcb/file/upload
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.photoUpdate)
                .post(requestBody)
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 人工拍照
     * post
     *
     * @param taskId       任务ID
     * @param watermeterId 水表ID
     * @param curReading   水表读数
     * @param remark       备注
     * @param fileID       文件Id
     */
    public Call peoplePhoto(String taskId, String watermeterId, String curReading, String remark, String fileID) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/watermeter/updateLatitudeAndLongitude?watermeterId=14084&longitude=1&latitude=2
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.peoplePhoto + "taskId=" + taskId + "&" + "watermeterId=" + watermeterId + "&" + "curReading=" + curReading + "&" + "remark=" + remark + "&" + "fileId=" + fileID)
                .get()
                .build();

        Log.e("", "userSelectWatchInform: "+ HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId );
        return okHttpClient.newCall(request);
    }


    /**
     * 维修列表
     * post
     *
     * @param repairStaff 维修人ID（当前登入人）
     */
    public Call serviceList(String repairStaff) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/repair/repairTask?repairStaff=1
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.serviceList + "repairStaff=" + repairStaff)
                .get()
                .build();

        //Log.e("", "userSelectWatchInform: "+ HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId );
        return okHttpClient.newCall(request);
    }

    /**
     * 图片上传(故障维修)
     * post  无参数
     *
     * @param filePath 图片文件路径
     */
    public Call photoUpdate2(List<File> filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//添加类型
        //创建File
        for (File path : filePath) {
            builder.addFormDataPart("img", path.getName(), RequestBody.create(MediaType.parse("image/png"), path));
            // Log.e("文件图片：", "" + path);
        }

        RequestBody requestBody = builder.build();

        //构建请求  http://218.92.200.222:8081//dtcb/file/upload
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.photoUpdate)
                .post(requestBody)
                .build();

        // Log.e("图片上传", HttpUtilsApi.httpUrl + HttpUtilsApi.photoUpdate);
        return okHttpClient.newCall(request);
    }

    /**
     * 维修故障
     * post
     *
     * @param id：维修ID，
     * @param repairBeforePic：维修前图片ID，必填
     * @param repairAfterPic：维修后图片ID，    必填
     * @param repairInPic：维修中图片ID        必填
     * @param repairContent：维修内容         必填
     */
    public Call serviceFault(String id, String repairBeforePic, String repairInPic, String repairAfterPic, String repairContent) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/repair/repair?id=27&repairBeforePic=123&repairAfterPic=124&repairInPic=124&repairContent=qwer
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.serviceFault + "id=" + id + "&" + "repairBeforePic=" + repairBeforePic + "&" + "repairAfterPic=" + repairAfterPic + "&" + "repairInPic=" + repairInPic + "&" + "repairContent=" + repairContent)
                .get()
                .build();

        //Log.e("", "userSelectWatchInform: "+ HttpUtilsApi.httpUrl + HttpUtilsApi.userSelectGetWacthInform + "watermeterId=" + watermeterId );
        return okHttpClient.newCall(request);
    }

    /**
     * 通知公告
     * post
     *
     * @param type：     类型，
     * @param pagesize： 每页的个数
     * @param pagenum：  页面
     */
    public Call noticeAnounce(int type, int pagenum, int pagesize) {
        //表单形式构建请求体

        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.noticeAnounce + "type=" + type + "&" + "pagesize=" + pagesize + "&" + "pagenum=" + pagenum)
                .get()
                .build();
        //Log.e("通知公告", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.noticeAnounce + "type=" + type + "&" + "pagesize=" + pagesize + "&" + "pagenum=" + pagenum  );
        return okHttpClient.newCall(request);
    }

    /**
     * 上班打卡
     * post
     *
     * @param workDate：       日期
     * @param userId：         用户的id 登入时返回的id
     * @param lateReason：     迟到理由  如果没有就填空leaveLongitude
     * @param signLongitude： 经度
     * @param signLatitude：  纬度
     */
    public Call signClickGoWork(String workDate, String userId, String lateReason, double signLongitude, double signLatitude) {
        //表单形式构建请求体

        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.signClickGoWork + "workDate=" + workDate + "&" + "userId=" + userId + "&" + "lateReason=" + lateReason + "&" + "signLongitude=" + signLongitude + "&" + "signLatitude=" + signLatitude)
                .get()
                .build();
//         Log.e("上班打卡", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.signClickGoWork  + "workDate=" + workDate + "&" + "userId=" + userId + "&" + "lateReason=" + lateReason + "&" + "signLongitude=" + signLongitude + "&" + "signLatitude=" + signLatitude);
        return okHttpClient.newCall(request);
    }

    /**
     * 下班打卡
     * post
     *
     * @param workDate：       日期
     * @param userId：         用户的id 登入时返回的id
     * @param leaveReason：     早退理由  如果没有就填空
     * @param leaveLongitude： 经度
     * @param leaveLatitude：  纬度
     */
    public Call signClickDownWork(String workDate, String userId, String leaveReason, double leaveLongitude, double leaveLatitude) {
        //表单形式构建请求体

        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.signClickDownWork + "workDate=" + workDate + "&" + "userId=" + userId + "&" + "leaveReason=" + leaveReason + "&" + "leaveLongitude=" + leaveLongitude + "&" + "leaveLatitude=" + leaveLatitude)
                .get()
                .build();
//        Log.e("打卡", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.signClickDownWork+ "workDate=" + workDate + "&" + "userId=" + userId + "&" + "leaveReason=" + leaveReason + "&" + "leaveLongitude=" + leaveLongitude + "&" + "leaveLatitude=" + leaveLatitude);
        return okHttpClient.newCall(request);
    }

    /**
     * 问题反馈
     * post
     *
     * @param userId：  用户的id 登入时返回的id
     * @param title：   标题
     * @param content： 问题描述
     * @param fileId：  上传照片后返回的fileId
     * @param contact： 联系方式
     */
    public Call problemFeedback(String userId, String title, String content, String fileId, String contact) {
        //表单形式构建请求体
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.problemFeedback + "userId=" + userId + "&" + "title=" + title + "&" + "content=" + content + "&" + "fileId=" + fileId + "&" + "contact=" + contact)
                .get()
                .build();
        //Log.e("打卡", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.problemFeedback + "userId=" + userId + "&" + "title=" + title + "&" + "content=" + content+ "&" + "fileId=" + fileId + "&" + "contact=" + contact );
        return okHttpClient.newCall(request);
    }


    /**
     * 物资申请(物品)
     * post
     */
    public Call goodsApplyGoods() {
        //表单形式构建请求体
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.goodsApplyGoods)
                .get()
                .build();
        //Log.e("打卡", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.goodsApplyGoods );
        return okHttpClient.newCall(request);
    }

    /**
     * 物资申请(型号)
     * post
     *
     * @param typeId： 物资申请(物品)请求的返回typeId
     */
    public Call goodsApplyGoodsModel(String typeId) {
        //表单形式构建请求体
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.goodsApplyModel + "typeId=" + typeId)
                .get()
                .build();
        //Log.e("打卡", ": "+ HttpUtilsApi.httpUrl + HttpUtilsApi.goodsApplyModel + "typeId=" + typeId   );
        return okHttpClient.newCall(request);
    }

    /**
     * 物资申请（提交）
     * post
     *
     * @param applyMan：      申请人（用户的id），
     * @param goodsId：       物资申请(型号)返回的goodsId
     * @param goodsQuanlity： 数量  remark
     * @param remark：        备注
     */
    public Call goodsApplyGoodsCommit(String applyMan, String goodsId, int goodsQuanlity, String remark) {
        //表单形式构建请求体
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.goodsApplyCommit + "applyMan=" + applyMan + "&" + "goodsId=" + goodsId + "&" + "goodsQuanlity=" + goodsQuanlity + "&" + "remark=" + remark)
                .get()
                .build();
        //Log.e("通知公告", ": "+ HttpUtilsApi.httpUrl +  HttpUtilsApi.goodsApplyCommit + "applyMan=" + applyMan + "&" + "goodsId=" + goodsId + "&" + "goodsQuanlity=" + goodsQuanlity + "&" + "remark=" + remark );
        return okHttpClient.newCall(request);
    }


    /**
     * 用户选择
     * post
     *
     * @param groups     小组 即抄表任务中的areaId
     * @param customerNo 户号
     */
    public Call userSelectQrCode(String groups, String customerNo) {
        //表单形式构建请求体

        //构建请求  http://218.92.200.222:8081/dtcb/customer/userList?pagesize=5&pagenum=0&groups=142&name=%E4%B8%87%E4%B8%BA%E4%B8%9C&customerNo=0004000128
        Request request = new Request.Builder()
                .url(HttpUtilsApi.httpUrl + HttpUtilsApi.userSelect + "groups=" + groups + "&" + "customerNo=" + customerNo )
                .get()
                .build();
//        Log.e("查询", HttpUtilsApi.httpUrl + HttpUtilsApi.userSelect + "groups=" + groups + "&" + "customerNo=" + customerNo);
        return okHttpClient.newCall(request);
    }
}
