package com.guanchao.app.entery;

import java.util.List;

/**
 * Created by 王建法 on 2017/6/15.
 */

public class UserSelect {



    /**
     * count : 185
     * end : 0
     * entityOrField : true
     * list : [{"address":"东台南沈","customerNo":"0004000642","houseNumber":"16685","id":"16685","name":"万为干","watermeterId":"14601","watermeterNo":"114601"},{"address":"东台南沈","customerNo":"0004000641","houseNumber":"16684","id":"16684","name":"常秋香","watermeterId":"14600","watermeterNo":"114600"},{"address":"东台南沈","customerNo":"0004000640","houseNumber":"16683","id":"16683","name":"陈兰","watermeterId":"14599","watermeterNo":"114599"},{"address":"东台南沈","customerNo":"0004000639","houseNumber":"16682","id":"16682","name":"陈桂英","watermeterId":"14598","watermeterNo":"114598"},{"address":"东台南沈","customerNo":"0004000638","houseNumber":"16681","id":"16681","name":"何复林","watermeterId":"14597","watermeterNo":"114597"},{"address":"东台南沈","customerNo":"0004000637","houseNumber":"16680","id":"16680","name":"贾红英","watermeterId":"14596","watermeterNo":"114596"},{"address":"东台南沈","customerNo":"0004000636","houseNumber":"16679","id":"16679","name":"陈长凤","watermeterId":"14595","watermeterNo":"114595"},{"address":"东台南沈","customerNo":"0004000635","houseNumber":"16678","id":"16678","name":"鲁传华","watermeterId":"14594","watermeterNo":"114594"}]
     * pagenum : 1
     * pagesize : 8
     * pd : {"pagesize":"8","name":"","groups":"146","pagenum":"1","customerNo":""}
     * start : 8
     */

    private int count;  //-总记录数
    private int end;
    private boolean entityOrField;
    private int pagenum;//--页数
    private int pagesize; //--每页记录数
    private PdBean pd;
    private int start;
    private List<ListBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isEntityOrField() {
        return entityOrField;
    }

    public void setEntityOrField(boolean entityOrField) {
        this.entityOrField = entityOrField;
    }

    public int getPagenum() {
        return pagenum;
    }

    public void setPagenum(int pagenum) {
        this.pagenum = pagenum;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public PdBean getPd() {
        return pd;
    }

    public void setPd(PdBean pd) {
        this.pd = pd;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class PdBean {
        /**
         * pagesize : 8
         * name :
         * groups : 146
         * pagenum : 1
         * customerNo :
         */

        private String pagesize;//--每页记录数
        private String name;  //户名
        private String groups;
        private String pagenum;  //--页码
        private String customerNo;//户号

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroups() {
            return groups;
        }

        public void setGroups(String groups) {
            this.groups = groups;
        }

        public String getPagenum() {
            return pagenum;
        }

        public void setPagenum(String pagenum) {
            this.pagenum = pagenum;
        }

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }
    }

    public static class ListBean {
        /**
         * address : 东台南沈
         * customerNo : 0004000642
         * houseNumber : 16685
         * id : 16685
         * name : 万为干
         * watermeterId : 14601
         * watermeterNo : 114601
         */

        private String address;  //--用户地址
        private String customerNo;  //--户号
        private String houseNumber;  // --门牌号
        private String id;  // --用户ID
        private String name;  //--户名
        private String watermeterId;  //--水表ID
        private String watermeterNo;  //--水表钢号

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
}
