package com.procuratorate.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Qing on 2016/8/10.
 */
public class IConfig {
    private SharedPreferences mSharedPreferences;

    private String filename = "myLoadData";

    private Context context;

    private SharedPreferences.Editor editor = null;

    public IConfig(Context context) {
        this.context = context;
        mSharedPreferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    //存储数据
    public void setStringData(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }
    public String getStringData(String key,String value){
        return mSharedPreferences.getString(key,value);
    }

}
