package com.guanchao.app.entery;

import java.io.Serializable;

/**
 * Created by 王建法 on 2017/6/15.
 */

public class UserSelcetWatchMessage implements Serializable{


    /**
     * address : 东台南沈
     * customerNo : 0004001172
     * houseNumber : 17215
     * id : 17215
     * latitude : null
     * location : 水表安装位置
     * longitude : null
     * mobile : 13800017215
     * name : 杨仁江
     * reading : 0
     * watermeterId : 15131
     * watermeterNo : 115131
     */

    private String address; //-用户地址
    private String customerNo; //--户号
    private String houseNumber; //--门牌号
    private String id;  //--用户ID
    private Object latitude;// --水表纬度
    private String location;  //--安装位置
    private Object longitude; //--水表经度
    private String mobile; // --手机
    private String name; //--姓名
    private String reading; //--水表读数
    private String watermeterId; //--水表ID
    private String watermeterNo;  //--水表钢号

    public UserSelcetWatchMessage() {
    }

    public UserSelcetWatchMessage(String customerNo, String name, String houseNumber, String mobile, String address, String reading, String watermeterNo, String location, Object longitude, Object latitude) {
        this.customerNo = customerNo;
        this.name = name;
        this.houseNumber = houseNumber;
        this.mobile = mobile;
        this.address = address;
        this.reading = reading;
        this.watermeterNo = watermeterNo;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
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

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getWatermeterId() {
        return watermeterId;
    }

    public void setWatermeterId(String watermeterId) {
        this.watermeterId = watermeterId;
    }

    public String getWatermeterNo() {
        return watermeterNo;
    }

    public void setWatermeterNo(String watermeterNo) {
        this.watermeterNo = watermeterNo;
    }
}

