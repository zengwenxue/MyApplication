package com.procuratorate.app.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import org.xutils.http.RequestParams;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by 杨绘庆 on 2016/8/24.
 * 修改密码
 */
public class ChangePwdActivity extends BaseActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_old_pass)
    EditText etOldPass;
    @Bind(R.id.et_new_pass)
    EditText etNewPass;

    private String phone,passNew,passOld;
    private String name;
    private IConfig iConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //出现软键盘 屏幕上移
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_change_pwd);
        setHeadTitle("修改密码");
        setHeadSize(16);
        ButterKnife.bind(this);
        iConfig = new IConfig(this);
        name = iConfig.getStringData(Constants.Login.PARAM_NAME, "");
        etPhone.setText(name+"");
        etOldPass.setFocusable(true);
        etOldPass.requestFocus();
    }

    @OnClick(R.id.btn_sure)
    void sureClick(){
        phone = etPhone.getText().toString();
        passNew = etNewPass.getText().toString();
        passOld = etOldPass.getText().toString();
        if (StringUtil.isEmpty(passOld)){
            showMessage("请输入旧密码");
            return;
        }
        if (StringUtil.isEmpty(passNew)){
            showMessage("请输入新密码");
            return;
        }
        //提交新密码
        RequestParams params = new RequestParams(Urls.URL_PASS_CHANGE);
        params.addBodyParameter("mobile",name);
        params.addBodyParameter("oldPass",passOld);
        params.addBodyParameter("newPass", passNew);
        RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                LoginResult model = (LoginResult) GsonUtil.jsonToBean(data,LoginResult.class);
                if (model.code.equals(Constants.CODE.SUCCESS)){
                    iConfig.setStringData(Constants.Login.PARAM_PWD,passNew);
                    finish();
                }
                showMessage(model.msg);
            }
            @Override
            public void error(String err) {
                showMessage(err);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
