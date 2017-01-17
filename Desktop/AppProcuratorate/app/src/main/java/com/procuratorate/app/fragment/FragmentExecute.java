package com.procuratorate.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.adapter.MainVpAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 杨绘庆 on 2016/8/1.
 * 司机确认
 */
public class FragmentExecute extends Fragment {

    @Bind(R.id.vp_execute)
    ViewPager vpExecute;
    @Bind(R.id.rg_execute)
    RadioGroup rgExecute;
    @Bind(R.id.rb_execute_wait)
    RadioButton rbWait;
    @Bind(R.id.rb_execute_history)
    RadioButton rbHistory;

    private ArrayList<Fragment> pager = new ArrayList<>();
    private MainVpAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_execute,container,false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        pager.add(new FragmentExecuteWait());
        pager.add(new FragmentExecuteHistory());
        FragmentManager manager = getActivity().getSupportFragmentManager();
        adapter = new MainVpAdapter(manager,pager);
        vpExecute.setAdapter(adapter);
        vpExecute.addOnPageChangeListener(pageChangeListener);
        rgExecute.setOnCheckedChangeListener(checkedChangeListener);
        rbWait.setChecked(true);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            ((RadioButton)rgExecute.getChildAt(position)).setChecked(true);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton rb = (RadioButton) group.getChildAt(i);
                if (rb.isChecked()){
                    vpExecute.setCurrentItem(i);
                    rb.setTextSize(16);
                }else {
                    rb.setTextSize(14);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
