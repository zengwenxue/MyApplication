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
import com.procuratorate.app.bean.ApplyMsg;
import com.procuratorate.app.bean.ApplyMsgResult;
import com.procuratorate.app.bean.BaseModel;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;

import org.xutils.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyChangeDelActivity extends BaseActivity {

    @Bind(R.id.tv_department)
    TextView tvDepartment;
    @Bind(R.id.tv_data_now)
    TextView tvDataNow;
    @Bind(R.id.tv_apply_person)
    TextView tvApplyPerson;
    @Bind(R.id.et_car_num_peo)
    EditText etCarNumPeo;
    @Bind(R.id.tv_phone_num)
    TextView tvPhoneNum;

    @Bind(R.id.et_car_require)
    EditText etCarRequire;
    @Bind(R.id.et_mark)
    EditText etMark;
    @Bind(R.id.tv_result_apply)
    TextView tvApplyResult;
    @Bind(R.id.tv_person_shen)
    TextView tvPersonShen;
    @Bind(R.id.tv_idea)
    TextView tvIdea;
    @Bind(R.id.et_origin)
    EditText etOrigin;
    @Bind(R.id.et_destination)
    EditText etDestination;
    @Bind(R.id.tv_why_car)
    EditText tvWhyCar;
    @Bind(R.id.tv_begin_time)
    EditText tvBeginTime;
    @Bind(R.id.tv_end_time)
    EditText tvEndTime;
    @Bind(R.id.btn_revoke_apply)
    Button btnRevokeApply;

    private ApplyMsgResult resultApply;
    private String reqNo;
    private int state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_apply_details);
        ButterKnife.bind(this);
        setHeadTitle("申请详情");
        init();
    }

    private void init() {
        Intent intent = getIntent();
        reqNo = intent.getStringExtra(Constants.ORDER_REQNO);
        loadOrder();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1==1){
                tvDataNow.setText(resultApply.applydate);
                tvDepartment.setText(resultApply.depname);
                tvPersonShen.setText(resultApply.shenpiperson);
                tvIdea.setText(resultApply.shenheyijian);
                tvApplyResult.setText(resultApply.shenpistatus);
                etCarNumPeo.setText(resultApply.bycarnum+"");
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
    private void loadOrder() {
        RequestParams params = new RequestParams(Urls.URL_APPLY_SUBMIT_MSG);
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("reqno", reqNo);
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
                        showMessage("失败:"+resultApply.msg);
                    }
                }
            }

            @Override
            public void error(String err) {
                showMessage("失败："+err);
            }
        });
    }

    @OnClick({R.id.btn_adopt_shenpi,R.id.btn_pass_shenpi})
    void shenAgain(View v){
        switch (v.getId()){
            case R.id.btn_adopt_shenpi:
                state = 1;
                btnAgain();
                break;
            case R.id.btn_pass_shenpi:
                state = 2;
                btnAgain();
                break;
        }
    }

    //撤销申请
    private void btnAgain() {
        ApplyMsg msg = new ApplyMsg();
        RequestParams params = new RequestParams(Urls.URL_APPLY_SUBMIT);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("reqNo", reqNo);
        params.addBodyParameter("state", String.valueOf(state));
        /**
         * ///////////////////////////////////////////////////////////////
         */
        if (state==1){
            params.addBodyParameter("msg", "新的订单");
        }
        RequestClient requestClient = new RequestClient();
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                BaseModel model = (BaseModel) GsonUtil.jsonToBean(data,BaseModel.class);
                if (model!=null){
                    if (model.code.equals(Constants.CODE.SUCCESS)){
                        showMessage("订单已撤销");
                    }
                }
            }
            @Override
            public void error(String err) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
