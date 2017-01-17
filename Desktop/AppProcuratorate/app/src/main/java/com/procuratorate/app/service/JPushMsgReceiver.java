package com.procuratorate.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.procuratorate.app.activity.MessageCenterActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Qing on 2016/9/2.
 */
public class JPushMsgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // 在这里可以做些统计，或者做些其他工作 收到通知的時候
            // 获取未读取的数量
        }else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
//            String message = bundle.getString(JPushInterface.EXTRA_ALERT);
            Intent intentTO = new Intent(context, MessageCenterActivity.class);  //自定义打开的界面
            intentTO.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentTO);
        }
    }
}
