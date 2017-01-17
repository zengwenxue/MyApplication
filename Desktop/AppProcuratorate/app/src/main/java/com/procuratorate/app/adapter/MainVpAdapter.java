package com.procuratorate.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Qing on 2016/8/1.
 */
public class MainVpAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> pager;
    public MainVpAdapter(FragmentManager fm,ArrayList<Fragment> pager) {
        super(fm);
        this.pager = pager;
    }

    @Override
    public Fragment getItem(int position) {
        return pager.get(position);
    }

    @Override
    public int getCount() {
        return pager.size();
    }
}
