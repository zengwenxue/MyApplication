package com.procuratorate.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.CarAllActivity;
import com.procuratorate.app.adapter.MainVpAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.PowerBean;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 杨绘庆 on 2016/8/1.
 * 车辆的调度
 */
public class FragmentDispatch extends Fragment {


    @Bind(R.id.rg_dispatch)
    RadioGroup rgDispatch;
    @Bind(R.id.rb_dispatch_wait)
    RadioButton rbWait;
    @Bind(R.id.rb_dispatch_execute)
    RadioButton rbExecute;
    @Bind(R.id.rb_dispatch_ok)
    RadioButton rbOk;
    @Bind(R.id.vp_dispatch_wait)
    ViewPager vpDispatchWait;
    @Bind(R.id.img_car_location)
    ImageView imgCarLocation;

    private View mView;
    private ArrayList<Fragment> frgAlls = new ArrayList<>();
    private MainVpAdapter adapter;
    private String power;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dispatch,container,false);
        ButterKnife.bind(this, mView);
        initThings();
        init();
        return mView;
    }

    private void init() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        adapter = new MainVpAdapter(manager,frgAlls);
        vpDispatchWait.setAdapter(adapter);
        vpDispatchWait.setOffscreenPageLimit(2);
        rbWait.setChecked(true);
        rgDispatch.setOnCheckedChangeListener(radioChangeLister);
        vpDispatchWait.addOnPageChangeListener(vpChangeListener);
    }

    //vp 和 rg 的联动
    private ViewPager.OnPageChangeListener vpChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            ((RadioButton)rgDispatch.getChildAt(position)).setChecked(true);
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private RadioGroup.OnCheckedChangeListener radioChangeLister = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton rb = (RadioButton)group.getChildAt(i);
                if (rb.isChecked()){
                    vpDispatchWait.setCurrentItem(i);
                    rb.setTextSize(16);
                }else {
                    rb.setTextSize(14);
                }
            }
        }
    };
    @OnClick(R.id.img_car_location)
    void carClick(){
        startActivity(new Intent(getActivity(), CarAllActivity.class));
    }

    /*
    * 调度模块 待调度  待执行 已执行
    * */
    private void initThings() {
        frgAlls.add(new FragmentWaitDispatch());
        frgAlls.add(new FragmentWaitExcute());
        frgAlls.add(new FragmentExecuteOk());
    }

}
