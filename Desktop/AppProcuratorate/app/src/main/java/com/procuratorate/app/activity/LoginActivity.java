package com.procuratorate.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import org.xutils.http.RequestParams;
import java.util.Set;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by 杨绘庆 on 2016/8/24.
 * 用户登录
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_pass)
    EditText etPass;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_back_pwd)
    TextView tvBackPwd;

    private String mName;
    private String mPass;
    private IConfig iConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_login);
        setHeadTitle("登录");
        setNoBack();
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        iConfig = new IConfig(this);
        mName = iConfig.getStringData(Constants.Login.PARAM_NAME,"");
        if (!StringUtil.isEmpty(mName)){
            etName.setText(mName);
        }
    }
    @OnClick({R.id.btn_login,R.id.tv_back_pwd})
    void loginClick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                initLogin();
                break;
            case R.id.tv_back_pwd:
                startActivity(new Intent(LoginActivity.this,ChangePwdActivity.class));
                break;
        }

    }

    private void initLogin() {
        mName = etName.getText().toString();
        mPass = etPass.getText().toString();
        if (!StringUtil.isEmpty(mName)&&!StringUtil.isEmpty(mPass)){
            RequestParams params = new RequestParams(Urls.URL_LOGIN);
            params.addBodyParameter("name", mName);
            params.addBodyParameter("pass", mPass);
            RequestClient client = new RequestClient();
            client.onLoadDataPost(params);
            client.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    //登录成功 保存用户名和id
                    LoginResult result = (LoginResult) GsonUtil.jsonToBean(data, LoginResult.class);
                    if (result != null) {
                        if (result.code.equals(Constants.CODE.SUCCESS)) {
                            iConfig.setStringData(Constants.Login.PARAM_PWD, mPass);
                            iConfig.setStringData(Constants.Login.PARAM_NAME, mName);
                            iConfig.setStringData(Constants.Login.PARAM_USER_ID, result.userid);
                            iConfig.setStringData(Constants.Login.PARAM_TOKEN, result.token);
                            iConfig.setStringData(Constants.Login.PARAM_PERSON, result.personname);
                            iConfig.setStringData(Constants.Login.PARAM_DEP_NAM, result.depname);
                            iConfig.setStringData(Constants.Login.PARAM_DEP_NO, result.depcode);
                            //极光推送添加标签
                            JPushInterface.setAlias(LoginActivity.this, mName+"", new TagAliasCallback() {
                                @Override
                                public void gotResult(int i, String s, Set<String> set) {
                                }
                            });
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else if (result.code.equals(Constants.CODE.FAIL)){
                            showMessage(result.msg);
                        }
                    }

                }

                @Override
                public void error(String err) {
                    showMessage("登录失败"+err);
                }
            });
        }else {
            showMessage("用户名或密码不能为空...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
