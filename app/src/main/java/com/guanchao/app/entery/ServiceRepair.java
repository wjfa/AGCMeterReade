package com.guanchao.app.entery;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 王建法 on 2017/6/18.
 */

public class ServiceRepair implements Serializable{



    /**
     * address : 1
     * assignmentStaff : null
     * assignmentTime : null
     * callContent : 1
     * callMan : 1
     * createTime : null
     * createUser : null
     * id : 1
     * mobile : 1
     * repairAfterPic : null
     * repairBeforePic : null
     * repairContent : null
     * repairInPic : null
     * repairStaff : null
     * repairTime : null
     * st : 1
     */

    private String address;  // --地址
    private Object assignmentStaff;
    private Object assignmentTime;
    private String callContent;  //--报修内容
    private String callMan;     // --报修人
    private Object createTime;
    private Object createUser;
    private String id;    //--维修ID
    private String mobile;   //手机
    private Object repairAfterPic;  //维修后图片ID
    private Object repairBeforePic;  //维修前图片ID
    private Object repairContent;
    private Object repairInPic;   //维修中图片ID
    private Object repairStaff;
    private Object repairTime;  //维修时间
    private String st;

    public ServiceRepair() {
    }

    public ServiceRepair(String id,String callMan, String mobile, Object repairTime, String address, String callContent,Object repairBeforePic,Object repairInPic,Object repairAfterPic) {
        this.id = id;
        this.callMan = callMan;
        this.mobile = mobile;
        this.repairTime = repairTime;
        this.address = address;
        this.callContent = callContent;
        this.repairBeforePic = repairBeforePic;
        this.repairInPic = repairInPic;
        this.repairAfterPic = repairAfterPic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getAssignmentStaff() {
        return assignmentStaff;
    }

    public void setAssignmentStaff(Object assignmentStaff) {
        this.assignmentStaff = assignmentStaff;
    }

    public Object getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(Object assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public String getCallContent() {
        return callContent;
    }

    public void setCallContent(String callContent) {
        this.callContent = callContent;
    }

    public String getCallMan() {
        return callMan;
    }

    public void setCallMan(String callMan) {
        this.callMan = callMan;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    public Object getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Object createUser) {
        this.createUser = createUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Object getRepairAfterPic() {
        return repairAfterPic;
    }

    public void setRepairAfterPic(Object repairAfterPic) {
        this.repairAfterPic = repairAfterPic;
    }

    public Object getRepairBeforePic() {
        return repairBeforePic;
    }

    public void setRepairBeforePic(Object repairBeforePic) {
        this.repairBeforePic = repairBeforePic;
    }

    public Object getRepairContent() {
        return repairContent;
    }

    public void setRepairContent(Object repairContent) {
        this.repairContent = repairContent;
    }

    public Object getRepairInPic() {
        return repairInPic;
    }

    public void setRepairInPic(Object repairInPic) {
        this.repairInPic = repairInPic;
    }

    public Object getRepairStaff() {
        return repairStaff;
    }

    public void setRepairStaff(Object repairStaff) {
        this.repairStaff = repairStaff;
    }

    public Object getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(Object repairTime) {
        this.repairTime = repairTime;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

}
