package com.procuratorate.app.utils;

import android.text.TextUtils;

/**
 * Created by Qing on 2016/8/1.
 */
public class StringUtils {
    public static boolean isEmpty(String msg){
        if (msg==null||msg.equals(""))
            return true;
        return false;
    }

}
