package com.td.oldplay.bean;

/**
 * Created by my on 2017/7/24.
 */

public class RechargeInfo {

    /**
     * account :
     * addTime : {"date":22,"day":2,"hours":11,"minutes":50,"month":7,"nanos":0,"seconds":9,"time":1503373809000,"timezoneOffset":-480,"year":117}
     * detail : 消费
     * formatTime : 2017.08.22 11:50:09
     * id : 2
     * money : 300
     * orderId : 2
     * type : 2
     * userId : 1
     */

   public String account;
   public String detail;
   public String formatTime;
   public String id;
   public String money;
   public String orderId;
   public int type;  // 类型(0:充值1:提现2:消费3:直播)
   public  String typeDetail;
   public String userId;

   public int payType; //支付方式 0:支付宝,1:微信



}
