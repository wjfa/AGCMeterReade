package com.guanchao.app.network;

/**
 * Created by 王建法 on 2017/4/27.
 *
 * URL接口
 */

public class HttpUtilsApi {

    public static final String httpUrl = "http://218.92.200.222:8081/";

    //登录
    public static final String loginUrl="dtcb/mobileLogin?";
    //用户退出
    public static final String userOutLogin="Info/outLogin";

    //抄表任务（列表）
    public static final String watchTask="dtcb/task/taskList4cbUser?";
    //用户报修
    public static final String userRepairs="dtcb/repair/mobileInsert?";
    //选择用户（列表）
    public static final String userSelect="dtcb/customer/userList?";
    //选择用户后获取用户信息
    public static final String userSelectGetWacthInform="dtcb/customer/customerInfo4cb?";
    //设置水表经纬度
    public static final String setWaterLatLtude="dtcb/watermeter/updateLatitudeAndLongitude?";
    //图片上传
    public static final String photoUpdate="/dtcb/file/upload";
    //人工拍照
    public static final String peoplePhoto="dtcb/watermeter/cb?";

    //维修列表
    public static final String serviceList="dtcb/repair/repairTask?";
    //维修故障
    public static final String serviceFault="dtcb/repair/repair?";

    //通知公告
    public static final String noticeAnounce="dtcb/notice/noticeList?";
    //上班签到
    public static final String signClickGoWork="dtcb/attendance/sign?";
    //下班签退
    public static final String signClickDownWork="dtcb/attendance/leave?";

    //问题反馈
    public static final String problemFeedback="dtcb/feedback/feedback?";

    //物资申请(货物)
    public static final String goodsApplyGoods="dtcb/goodsType/queryAllGoodsType";

    //物资申请(型号)
    public static final String goodsApplyModel="dtcb/goods/queryGoodsByTypeId?";
    //物资申请
    public static final String goodsApplyCommit="dtcb/goodsApply/save?";


}
