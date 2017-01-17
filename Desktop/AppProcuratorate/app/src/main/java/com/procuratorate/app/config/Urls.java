package com.procuratorate.app.config;

/**
 * Created by Qing on 2016/8/4.
 * 接口
 */
public class Urls {
    /**
     * url
     * */
    public static final String URL_BASE = "http://114.55.52.204:9000/";

    /**
     * 权限
     */
    public static final String URL_POWER =URL_BASE + "app/roleAccess";

    /**
     * 用户登录
     */
    public static final String URL_LOGIN =URL_BASE + "app/useLogin";

    /**
     * 修改密码
     */
    public static final String URL_PASS_CHANGE =URL_BASE + "app/changePwd";

    /**
     * 待调度列表 申请提交
     */
    public static final String URL_APPLY_SUBMIT =URL_BASE + "app/addShenqing";
    /**
     * 申请订单详情
     */
    public static final String URL_APPLY_SUBMIT_MSG =URL_BASE + "app/applyDetail";
    /**
     *  已申请列表
     */
    public static final String URL_APPLY_LIST =URL_BASE + "app/searchDaishenheList";
    /**
     *  审批判断 （通过和不通过）
     */
    public static final String URL_SHEN_PI =URL_BASE + "app/carShenpi";
    /**
     *  审批列表 审批人员
     */
    public static final String URL_SHEN_PI_LIST=URL_BASE + "app/shenPiList";

    /**
     * 待调度列表 未调度
     */
    public static final String URL_DISPATCH_WAIT =URL_BASE + "app/waitCarDispatchList";

    /**
     * 调度参考订单信息
     */
    public static final String URL_DISPATCH_CAN_KAO=URL_BASE + "app/dispatchCanKaoMsg";

    /**
     * 调度信息 （车辆）
     */
    public static final String URL_DISPATCH_CAR= URL_BASE+"app/carDetailMsg";

    /**
     * 调度信息 （司机）
     */
    public static final String URL_DISPATCH_PEOPLE= URL_BASE+"app/carSijiDetailMsg";


    /**
     * 提交调度信息
     */
    public static final String URL_POST_DISPATCH = URL_BASE+"app/addVsBScheDetail";

    /**
     * 待执行列表 （可修改调度）
     */
    public static final String URL_WAIT_EXECUTE = URL_BASE+"app/waitExecuteList";

    /**
     * 已执行列表 （调度历史）
     */
    public static final String URL_OK_EXECUTE = URL_BASE+"app/dispatchOkList";

    /**
     * 调度信息 （曾经提交过的调度信息）
     */
    public static final String URL_DISPATCH_MSG = URL_BASE+"app/CarDispatchMsg";

    /**
     * 车辆位置 （执行任务中的车辆信息）
     */
    public static final String URL_CAR_LOCATION = URL_BASE+"app/carLocationDetails";

    /**
     * 司机待完成任务（未完成任务）
     */
    public static final String URL_WAIT_TASK_LIST = URL_BASE+"app/waitCompleteList";
    /**
     * 司机已完成任务（已完成任务）
     */
    public static final String URL_OK_TASK_LIST = URL_BASE+"app/yetCompleteList";
    /**
     * 任务详情 （订单和调度信息）
     */
    public static final String URL_TASK_DETAIL = URL_BASE+"app/orderDetails";
    /**
     * 待完成任务确认（定位确认）
     */
    public static final String URL_LOCATION_BEGIN= URL_BASE+"app/Journeybegan";
    /**
     * 完成任务确认（结束定位）
     */
    public static final String URL_LOCATION_END = URL_BASE+"app/Journeyend";
    /**
     * 完成任务确认（结束定位）
     */
    public static final String URL_PULL_MSG = URL_BASE+"app/pushMsg";

    /**
     * 更新升级检查
     */
    public static final String API_CHECK_UPDATE = "";
}
