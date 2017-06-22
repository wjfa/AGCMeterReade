package com.guanchao.app.entery;

/**
 * Created by 王建法 on 2017/6/14.
 */

public class User {

    /**
     * areaId : 1
     * createDate : 2016-06-01
     * createUser : guxw
     * deptId : 1
     * email : 723169884@qq.com
     * id : 1
     * job : 抄水工
     * mobile : 15335192608
     * name : 顾小伟
     * password : e10adc3949ba59abbe56e057f20f883e
     * st : 1
     * userId : guxw
     */
    private String areaId;
    private String createDate;
    private String createUser;
    private String deptId;
    private String email;
    private String id;  // --用户ID
    private String job;  //--职位
    private String mobile;  //--手机
    private String name;  //用户名
    private String password;
    private String st;
    private String userId;  // —工号

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
