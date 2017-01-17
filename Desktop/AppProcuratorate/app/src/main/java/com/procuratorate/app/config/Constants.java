package com.procuratorate.app.config;

/**
 * Created by Qing on 2016/8/9.
 */
public class Constants {
    /**
     * 响应码 成功
     */
    public static final class CODE{

        public static final String SUCCESS = "1";

        public static final String FAIL = "0";


        public static final String ABNORMAL = "25";// 异常


        public static final String NO_POWER = "24";
    }

    public static final class POWER{
        public static final String DISPATCH = "3";

        public static final String DRIVER = "4";
    }

    /**
     * 用户
     */
    public static final class Login {
        public static final String PARAM_TOKEN = "token";

        public static final String PARAM_USER_ID = "userId";

        public static final String PARAM_NAME = "name";

        public static final String PARAM_PWD = "pwd";

        public static final String PARAM_PERSON="person";

        public static final String PARAM_DEP_NO="depcode";

        public static final String PARAM_DEP_NAM="depname";
    }
    /**
     * 订单信息
     */
    public static final String ORDER_DETAIL = "order";
    /**
     * 订单号
     */
    public static final String ORDER_REQNO = "reqno";


    /**
     * 车辆详情信息
     */
    public static final String CAR_DETAIL = "car";

    /**
     * 刷新 对应的表示  刷新界面
     */
    public static final class REFRESH{
        public static final int REF_APPLY=10;
        public static final int REF_CHECK=20;
        public static final int REF_WAIT_DISPATCH=30;
        public static final int REF_DRIVER_EXECUTE=40;
    }
}
