package com.guanchao.app.entery;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class Watch {
    /**
     * areaId : 151
     * areaName : 万桥十组
     * cbUser : 1
     * cbUserName : 顾小伟
     * createTime : 2017-05-18 16:18:36.0
     * creator : 1
     * creatorName : 顾小伟
     * finished : 7
     * households : 101
     * month : 201704
     * st : 1
     * taskId : 100036
     */

    private String areaId;  //--区域ID
    private String areaName;  //--区域名称
    private String cbUser;  //--抄表人ID
    private String cbUserName;  //--抄表人名称
    private String createTime;
    private String creator;
    private String creatorName;
    private String finished;  //--完成数
    private String households;  //--户数
    private String month;  //--月份
    private String st;
    private String taskId;  //--任务编号

    public Watch(String areaId) {
        this.areaId = areaId;
    }

    public Watch() {

    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCbUser() {
        return cbUser;
    }

    public void setCbUser(String cbUser) {
        this.cbUser = cbUser;
    }

    public String getCbUserName() {
        return cbUserName;
    }

    public void setCbUserName(String cbUserName) {
        this.cbUserName = cbUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public String getHouseholds() {
        return households;
    }

    public void setHouseholds(String households) {
        this.households = households;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
