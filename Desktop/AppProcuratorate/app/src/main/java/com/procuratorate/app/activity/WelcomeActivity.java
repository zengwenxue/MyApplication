package com.procuratorate.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.procuratorate.app.R;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.utils.StringUtils;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by 杨绘庆 on 2016/8/24.
 * 欢迎页
 */
public class WelcomeActivity extends Activity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        IConfig iConfig = new IConfig(this);
        name = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (StringUtils.isEmpty(name))
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    else
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}
