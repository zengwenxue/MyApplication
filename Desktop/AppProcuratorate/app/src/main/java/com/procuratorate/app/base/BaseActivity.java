package com.procuratorate.app.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.utils.StringUtils;

/**
 * Created by Qing on 2016/7/29.
 */
public class BaseActivity extends AppCompatActivity {

    private RelativeLayout rlTiTle;
    private ImageView imgBack;
    private TextView tvTiltle;
    private TextView tvRight;
    private LinearLayout llContent;
    public IConfig iConfig;
    public StringUtils StringUtil = new StringUtils();
    public String userId;
    public RequestClient requestClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_title_activity);
        iConfig = new IConfig(this);
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
        requestClient = new RequestClient();
        rlTiTle = (RelativeLayout) findViewById(R.id.base_title);
        imgBack = (ImageView) findViewById(R.id.icon_title_back);
        tvTiltle = (TextView) findViewById(R.id.tv_title_center);
        tvRight = (TextView) findViewById(R.id.tv_title_right);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        setHeadSize(16);//设置标题字体大小
        //activity切换动画
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBack();
            }
        });
    }

    public void finishBack(){
        finish();
    }

    public void setHeadTitle(String headTilte) {
        rlTiTle.setVisibility(View.VISIBLE);
        tvTiltle.setText(headTilte);
    }
    public void setHeadSize( float size){
        tvTiltle.setTextSize(size);
    }

    public void setNoBack(){
        imgBack.setVisibility(View.GONE);
    }

    public void setContentViewThis(int layoutResID) {
        View view  = LayoutInflater.from(this).inflate(layoutResID,null);
        llContent.addView(view);
    }

    public void showMessageNormal(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public void showMessage(String msg){
        View view = LayoutInflater.from(this).inflate(R.layout.show_msg,null);
        Toast toast = new Toast(this);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_show_msg);
        tvMsg.setText(msg+"");
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
