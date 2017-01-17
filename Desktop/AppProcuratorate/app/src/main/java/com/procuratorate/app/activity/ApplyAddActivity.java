package com.procuratorate.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.bean.ApplyMsg;
import com.procuratorate.app.bean.ApplyMsgResult;
import com.procuratorate.app.bean.BaseModel;
import com.procuratorate.app.bean.CodeBean;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;
import com.procuratorate.app.widget.DataPickerView;

import org.xutils.http.RequestParams;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 车辆申请
 */
public class ApplyAddActivity extends BaseActivity {

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
    TextView tvBeginTime;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    @Bind(R.id.btn_revoke_apply)
    Button btnRevokeApply;
    @Bind(R.id.btn_submit_apply)
    Button btnSubmitApply;

    String person;
    private String applyMobile;
    private String applyPerson;
    private String msgSubmit;
    private String reqNo;
    private int state;
    private int shen;
    private ApplyMsgResult resultApply;
    private String dateMe;
    private int ding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_apply);
        setHeadTitle("车辆申请");
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent!=null){
            reqNo = intent.getStringExtra(Constants.ORDER_REQNO);
            shen = intent.getIntExtra("state",-1);
            ding = intent.getIntExtra("ding",0);
        }
        if (shen==1){
            //查看申请的车辆
            state = 1;
            loadOrder();
            btnSubmitApply.setText("修改申请");
            btnRevokeApply.setVisibility(View.VISIBLE);
            //待审核 审核中 都可以修改订单
            if (ding==1||ding==3){
                btnSubmitApply.setVisibility(View.VISIBLE);
            }else {
                btnSubmitApply.setVisibility(View.GONE);
            }
            //审批 待审核 审核中 通过 都可以撤销 其他情况不能
            if (ding==1||ding==3||ding==4){
                btnRevokeApply.setVisibility(View.VISIBLE);
            }else {
                btnRevokeApply.setVisibility(View.GONE);
            }
        }else if (shen==0){
            //初次申请车辆
            btnSubmitApply.setText("提交申请");
            Calendar calendar = Calendar.getInstance();
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH)+1;
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int h = calendar.get(Calendar.HOUR);
            int mi = calendar.get(Calendar.MINUTE);
            tvDataNow.setText(m+"-"+d+" "+h+":"+mi);
            state = 0;
        }
        tvDepartment.setFocusable(true);
        tvDepartment.setFocusableInTouchMode(true);
        tvDepartment.requestFocus();
        tvDepartment.setText(iConfig.getStringData(Constants.Login.PARAM_DEP_NAM, ""));
        applyMobile = iConfig.getStringData(Constants.Login.PARAM_NAME,"");
        tvPhoneNum.setText(applyMobile);
        applyPerson = iConfig.getStringData(Constants.Login.PARAM_PERSON,"");
        tvApplyPerson.setText(applyPerson);
        tvBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataC(tvBeginTime);
            }
        });
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataC(tvEndTime);
            }
        });
    }

    /**
     * 日期选择
     */
    private void dataC (final TextView text){
        View body = LayoutInflater.from(this).inflate(R.layout.time_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(body);
        final DataPickerView dataPickerView = (DataPickerView) body.findViewById(R.id.data_picker);
        Button btnCancel = (Button) body.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) body.findViewById(R.id.btn_ok);
        final AlertDialog dialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = dataPickerView.dataTime();
                text.setText(s);
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1==1){
                tvApplyPerson.setText(resultApply.personname);
                tvDataNow.setText(resultApply.applydate);
                tvDepartment.setText(resultApply.depname);
                tvPersonShen.setText(resultApply.shenpiperson);
                tvIdea.setText(resultApply.shenheyijian);
                switch (resultApply.dingdanstatus){
                    case 1:
                        tvApplyResult.setText("待审核");//审批状态
                        break;
                    case 2:
                        tvApplyResult.setText("订单撤销");//审批状态
                        break;
                    case 3:
                        tvApplyResult.setText("审核中");//审批状态
                        break;
                    case 4:
                        tvApplyResult.setText("审核通过");//审批状态
                        break;
                    case 5:
                        tvApplyResult.setText("审核驳回");//审批状态
                        break;
                    case 6:
                        tvApplyResult.setText("任务下达");//审批状态
                        break;
                    case 7:
                        tvApplyResult.setText("行程进行中");//审批状态
                        break;
                    case 8:
                        tvApplyResult.setText("订单结束");//审批状态
                        break;

                }

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
                       showMessage("获取申请单失败:"+resultApply.msg);
                   }
                }
            }

            @Override
            public void error(String err) {
                showMessage("失败："+err);
            }
        });
    }

    @OnClick({R.id.btn_submit_apply,R.id.btn_revoke_apply})
    void applySubmit(View v){
        switch (v.getId()){
            case R.id.btn_submit_apply:
                btnSubmit();
                break;
            case R.id.btn_revoke_apply:
                state = 2;
                btnSubmit();
                break;
        }

    }

    //提交申请
    private void btnSubmit() {
        ApplyMsg applyMsg = new ApplyMsg();
        applyMsg.personname = userId;
        applyMsg.depname = iConfig.getStringData(Constants.Login.PARAM_DEP_NO,"");
        applyMsg.applydate = tvDataNow.getText().toString();
        //日期：
        String timeBegin = tvBeginTime.getText().toString();    //开始时间
        String timeEnd = tvEndTime.getText().toString();    //结束时间
        applyMsg.beginlocaltion = etOrigin.getText().toString();
        applyMsg.destination = etDestination.getText().toString();
        if (!StringUtils.isEmpty(etCarNumPeo.getText().toString())){
            applyMsg.bycarnum = Integer.valueOf(etCarNumPeo.getText().toString());
        }
        if (state==0){
            applyMsg.mobile = applyMobile;
        }else {
            applyMsg.mobile = tvPhoneNum.getText().toString();
        }
        applyMsg.yongcheshiyou = tvWhyCar.getText().toString();
        applyMsg.caryaoqiu = etCarRequire.getText().toString();
        applyMsg.mark = etMark.getText().toString();
        applyMsg.shenheyijian = tvIdea.getText().toString();
        applyMsg.shenpistatus = tvApplyResult.getText().toString();
        applyMsg.shenpiperson = tvPersonShen.getText().toString();
        msgSubmit = GsonUtil.objectToJson(applyMsg);
        RequestParams params = new RequestParams(Urls.URL_APPLY_SUBMIT);
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("msg", msgSubmit);
        params.addBodyParameter("reqNo", reqNo);
        params.addBodyParameter("state", String.valueOf(state));
        params.addBodyParameter("usecarstartdate", timeBegin);
        params.addBodyParameter("usecarenddate", timeEnd);
        RequestClient requestClient = new RequestClient();
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                BaseModel result = (BaseModel) GsonUtil.jsonToBean(data,BaseModel.class);
                if (result!=null){
                    if (result.code.equals(Constants.CODE.SUCCESS)){
                        showMessage("申请成功");
                        CodeBean bean = new CodeBean();
                        bean.code = Constants.REFRESH.REF_APPLY;
                        bean.refresh = true;
                        EventBus.getDefault().post(bean);
                        finish();
                    }else {
                        showMessage(result.msg);
                    }
                }
            }
            @Override
            public void error(String err) {
                showMessage("失败"+err);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
