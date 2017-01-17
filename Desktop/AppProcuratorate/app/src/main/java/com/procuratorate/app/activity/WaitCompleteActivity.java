package com.procuratorate.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.CodeBean;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.bean.TaskWaitListResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.service.GaoMapReceiver;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;

import org.xutils.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by 杨绘庆 on 2016/8/24.
 * 司机 - 订单信息
 */
public class WaitCompleteActivity extends BaseActivity {

    @Bind(R.id.tv_person)
    TextView tvPerson;
    @Bind(R.id.tv_department)
    TextView tvDep;
    @Bind(R.id.tv_begin)
    TextView tvBegin;
    @Bind(R.id.tv_end)
    TextView tvEnd;
    @Bind(R.id.tv_beginlocaltion)
    TextView tvBegLocation;
    @Bind(R.id.tv_destination)
    TextView tvDestination;
    @Bind(R.id.tv_use_carNum)
    TextView tvUserNum;
    @Bind(R.id.tv_per_mobil)
    TextView tvMobile;
    @Bind(R.id.tv_tongXing)
    TextView tvTongXing;
    @Bind(R.id.et_mark)
    TextView etMark;
    @Bind(R.id.btn_submit)
    Button btnSub;

    private String reqno;
    private int tag;
    private String carPaiNum;
    private String userId;
    private String address;
    private String jingDu;
    private String weiDu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_wait_complete);
        ButterKnife.bind(this);
        setHeadTitle("订单详情");
        init();
    }
    private void init() {
        IConfig iConfig = new IConfig(this);
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
        Intent intent = getIntent();
        reqno = intent.getStringExtra(Constants.ORDER_REQNO);
        tag = intent.getIntExtra("tag",1);
        if (tag==0)
            btnSub.setText("开始行程");
        else if (tag==1)
            btnSub.setText("结束行程");
        else
            btnSub.setVisibility(View.GONE);
        loadData();
    }

    /**
     * 订单详情
     */
    private void loadData() {
        if (!StringUtils.isEmpty(reqno)){
            RequestParams params = new RequestParams(Urls.URL_TASK_DETAIL);
            params.addBodyParameter("reqNo",reqno);
            params.addBodyParameter("userId",userId);
            RequestClient clientd = new RequestClient();
            clientd.onLoadDataPost(params);
            clientd.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    TaskWaitListResult result = (TaskWaitListResult) GsonUtil.jsonToBean(data, TaskWaitListResult.class);
                    if (result != null) {
                        if (!StringUtil.isEmpty(result.applyperson))
                            tvPerson.setText("联系人:" + result.applyperson);
                        if (!StringUtil.isEmpty(result.usecardep))
                            tvDep.setText("部门：" + result.usecardep);
                        if (!StringUtil.isEmpty(result.usecarstartdate))
                            tvBegin.setText("开始时间：" + result.usecarstartdate);
                        if (!StringUtil.isEmpty(result.usecarenddate))
                            tvEnd.setText("结束时间：" + result.usecarenddate);
                        if (!StringUtil.isEmpty(result.beginlocaltion))
                            tvBegLocation.setText("出发地：" + result.beginlocaltion);
                        if (!StringUtil.isEmpty(result.destination))
                            tvDestination.setText("目的地:" + result.destination);
                        if (!StringUtil.isEmpty(result.bycarnum))
                            tvUserNum.setText("人数：" + result.bycarnum);
                        if (!StringUtil.isEmpty(result.mobile))
                            tvMobile.setText("电话：" + result.mobile);
                        if (!StringUtil.isEmpty(result.friend))
                            tvTongXing.setText("同行司机：" + result.friend);
                        if (!StringUtil.isEmpty(result.carbrandnum))
                            carPaiNum = result.carbrandnum;
                        if (!StringUtil.isEmpty(result.mark))
                            etMark.setText(result.mark);
                    }
                }

                @Override
                public void error(String err) {

                }
            });
        }
    }
    @OnClick(R.id.btn_submit)
    void submit(){
        Intent mapService = new Intent(this,GaoMapReceiver.class);
        mapService.putExtra("userId",userId);
        mapService.putExtra("reqNo", reqno);
        mapService.putExtra("carNum", carPaiNum);
        if (tag==0){
            //开始定位服务
            startService(mapService);
        }else if (tag==1){
            //结束定位
            stopService(mapService);
            endLocation();
        }
        CodeBean bean = new CodeBean();
        bean.refresh = true;
        bean.code = Constants.REFRESH.REF_DRIVER_EXECUTE;
        EventBus.getDefault().post(bean);
    }



    private void endLocation() {
        RequestParams params = new RequestParams(Urls.URL_LOCATION_END);
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("reqNo", reqno);
        RequestClient client = new RequestClient();
        client.onLoadDataPost(params);
        client.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                LoginResult result = (LoginResult) GsonUtil.jsonToBean(data, LoginResult.class);
                if (result != null) {
                    if (result.code.equals(Constants.CODE.SUCCESS)) {
                        finish();
                        showMessage("行程结束");
                    }
                }
            }

            @Override
            public void error(String err) {
                showMessage("失败因为:"+err);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }



}
