package com.procuratorate.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.bean.ApplyMsgResult;
import com.procuratorate.app.bean.BaseModel;
import com.procuratorate.app.bean.CodeBean;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;

import org.xutils.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ShenPiActivity extends BaseActivity {
    @Bind(R.id.tv_department)
    TextView tvDepartment;
    @Bind(R.id.tv_data_now)
    TextView tvDataNow;
    @Bind(R.id.tv_apply_person)
    TextView tvApplyPerson;
    @Bind(R.id.et_car_num_peo)
    TextView tvCarNumPeo;
    @Bind(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @Bind(R.id.et_car_require)
    TextView etCarRequire;
    @Bind(R.id.et_mark)
    TextView etMark;
    @Bind(R.id.et_origin)
    TextView etOrigin;
    @Bind(R.id.et_destination)
    TextView etDestination;
    @Bind(R.id.tv_why_car)
    TextView tvWhyCar;
    @Bind(R.id.tv_begin_time)
    TextView tvBeginTime;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    @Bind(R.id.et_idea)
    EditText etIdea;
    @Bind(R.id.btn_adopt_shenpi)
    Button btnAdoptShen;
    @Bind(R.id.btn_pass_shenpi)
    Button btnPassShen;
    @Bind(R.id.tv_ding_state)
    TextView tvDingState;
    @Bind(R.id.tv_result)
    TextView tvResult;

    private ApplyMsgResult resultApply;
    private String reqno;
    private String shenIdea;
    private int status;
    private int ding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_shen_pi);
        setHeadTitle("车辆审批");
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        tvDepartment.setFocusable(true);
        tvDepartment.setFocusableInTouchMode(true);
        tvDepartment.requestFocus();
        Intent intent = getIntent();
        if (intent!=null){
            reqno = intent.getStringExtra("reqno");
            status = intent.getIntExtra("status",6);
            ding = intent.getIntExtra("ding",0);
        }
        if (status==1||status==2){
            btnAdoptShen.setVisibility(View.GONE);
            btnPassShen.setVisibility(View.GONE);
            etIdea.setVisibility(View.GONE);
        }
        if (status==1){
            tvResult.setText("审核通过");
        } else if (status==2){
            tvResult.setText("审核不通过");
        } else if (status==0){
            tvResult.setText("待审批");
        }

        switch (ding){
            case 1:
                tvDingState.setText("待审批");
                break;
            case 2:
                tvDingState.setText("订单撤销");
                break;
            case 3:
                tvDingState.setText("审核中");
                break;
            case 4:
                tvDingState.setText("审核通过");
                break;
            case 5:
                tvDingState.setText("审核驳回");
                break;
            case 6:
                tvDingState.setText("任务下达");
                break;
            case 7:
                tvDingState.setText("行程进行中");
                break;
            case 8:
                tvDingState.setText("订单结束");
                break;
        }
        loadData();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1==1){
                tvDepartment.setText(resultApply.depname);
                tvApplyPerson.setText(resultApply.personname);
                tvDataNow.setText(resultApply.applydate);
                if (!StringUtils.isEmpty(resultApply.bycarnum+"")){
                    tvCarNumPeo.setText(resultApply.bycarnum+"");
                }
                etOrigin.setText(resultApply.beginlocaltion);
                etDestination.setText(resultApply.destination);
                tvBeginTime.setText(resultApply.usecarstartdate);
                tvEndTime.setText(resultApply.usecarenddate);
                tvPhoneNum.setText(resultApply.mobile);
                tvWhyCar.setText(resultApply.yongcheshiyou);
                etCarRequire.setText(resultApply.caryaoqiu);
                etMark.setText(resultApply.mark);
            }
        }
    };

    private void loadData() {
        RequestParams params = new RequestParams(Urls.URL_APPLY_SUBMIT_MSG);
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("reqno", reqno);
        RequestClient requestClient = new RequestClient();
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                resultApply = (ApplyMsgResult) GsonUtil.jsonToBean(data, ApplyMsgResult.class);
                if (resultApply!=null){
                    if (resultApply.code.equals(Constants.CODE.SUCCESS)){
                        Message message = new Message();
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }else {
                        showMessage(resultApply.msg);
                    }
                }
            }

            @Override
            public void error(String err) {
                showMessage(err);
            }
        });
    }

    @OnClick({R.id.btn_adopt_shenpi,R.id.btn_pass_shenpi})
    void shenOk(View v){
        shenIdea = etIdea.getText().toString().trim();
        switch (v.getId()){
            case R.id.btn_adopt_shenpi:
                shenOkSubmit(1); //审批通过
                break;
            case R.id.btn_pass_shenpi://审批不通过，给出不通过的审批理由...
                if (StringUtils.isEmpty(shenIdea)){
                    showMessage("请填写审批意见");
                } else{
                    shenOkSubmit(2);
                }
                break;
        }
    }

    //提交审批 1.通过 2.不通过
    private void shenOkSubmit(int state) {
        RequestParams params = new RequestParams(Urls.URL_SHEN_PI);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("reqNo",reqno);
        params.addBodyParameter("idea",shenIdea);
        params.addBodyParameter("state",String.valueOf(state));
        RequestClient requestClient = new RequestClient();
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                BaseModel model = (BaseModel) GsonUtil.jsonToBean(data,BaseModel.class);
                if (model!=null){
                    if ( model.code.equals(Constants.CODE.SUCCESS)){
                        showMessage(model.msg);
                        CodeBean bean = new CodeBean();
                        bean.refresh = true;
                        bean.code = Constants.REFRESH.REF_CHECK;
                        EventBus.getDefault().post(bean);//返回刷新
                        finish();
                    }
                }
            }
            @Override
            public void error(String err) {
                showMessage("失败："+err);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
