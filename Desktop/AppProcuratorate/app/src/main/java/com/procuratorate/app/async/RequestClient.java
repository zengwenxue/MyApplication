package com.procuratorate.app.async;

import android.app.AlertDialog;
import android.content.Context;

import com.procuratorate.app.bean.CarDetails;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qing on 2016/8/4.
 * XUtils获取数据
 */
public class RequestClient {
    private OnLoadData onLoadData;
    public void setLoadDataFinish(OnLoadData onLoadData){
        this.onLoadData = onLoadData;
    }
    private Context context;
    public  void onLoadDataPost(final RequestParams params){
        //下载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        onLoadData.success(s);
                    }
                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        onLoadData.error(throwable.getMessage());
                    }
                    @Override
                    public void onCancelled(CancelledException e) {

                    }
                    @Override
                    public void onFinished() {

                    }
                });
            }
        }).start();


    }

    public interface OnLoadData{
        void success(String data);
        void error(String err);
    }


}
