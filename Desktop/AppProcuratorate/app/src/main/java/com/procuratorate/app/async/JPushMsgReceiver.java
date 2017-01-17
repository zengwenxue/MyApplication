package com.procuratorate.app.async;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Qing on 2016/8/25.
 */
public class JPushMsgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())){
            //收到通知
        }else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            //点击通知消息的处理
        }
    }
}
