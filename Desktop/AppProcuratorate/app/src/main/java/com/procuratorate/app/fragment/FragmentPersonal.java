package com.procuratorate.app.fragment;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.AboutUsActivity;
import com.procuratorate.app.activity.ActivityManagerMe;
import com.procuratorate.app.activity.ChangePwdActivity;
import com.procuratorate.app.activity.LoginActivity;
import com.procuratorate.app.activity.MessageCenterActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.utils.StringUtils;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 个人中心
 * Created by 杨绘庆 on 2016/8/11.
 */
public class FragmentPersonal extends Fragment {

    @Bind(R.id.tv_show_name)
    TextView showName;
    @Bind(R.id.btn_exit)
    Button btnExit;
    @Bind(R.id.img_center_msg)
    ImageView imgCenterMsg;

    private IConfig iConfig;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal,null);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        iConfig = new IConfig(getContext());
        String name = iConfig.getStringData(Constants.Login.PARAM_NAME,"");
        if (!StringUtils.isEmpty(name))
            showName.setText(name);

    }

    @OnClick({R.id.rl_change_pwd,R.id.btn_exit,R.id.rl_about_us,R.id.img_center_msg})
    void centerClick(View v){
        switch (v.getId()){
            case R.id.img_center_msg:
                startActivity(new Intent(getActivity(), MessageCenterActivity.class));
                break;
            case R.id.rl_about_us:
                Intent intentA = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intentA);
                break;
            case R.id.rl_change_pwd:
                Intent intentC = new Intent(getActivity(), ChangePwdActivity.class);
                startActivity(intentC);
                break;
            case R.id.btn_exit:
                //退出
                iConfig.setStringData(Constants.Login.PARAM_TOKEN,"");
                iConfig.setStringData(Constants.Login.PARAM_USER_ID,"");
                iConfig.setStringData(Constants.Login.PARAM_PWD, "");
                ActivityManagerMe.getInstance().finishAllActivity();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                JPushInterface.setAlias(getActivity(), "", new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                    }
                });
                getActivity().finish();
                break;
        }

    }
}
