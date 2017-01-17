package com.procuratorate.app.async;

/**
 * Created by Qing on 2016/8/4.
 */
public interface OnLoadFinishListener<Response> {
    //加载数据完成
    void onLoadFinish(Class beanClass);
}
