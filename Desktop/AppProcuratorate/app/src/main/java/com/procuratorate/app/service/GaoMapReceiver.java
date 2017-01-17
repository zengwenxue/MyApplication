package com.procuratorate.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;

import org.xutils.http.RequestParams;

/**
 * Created by Qing on 2016/9/2.
 */
public class GaoMapReceiver extends Service implements AMapLocationListener {
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mLocationClient;
    private String userId;
    private String reqno;
    private String jingDu;
    private String weiDu;
    private String address;
    private String carPaiNum;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(1000 * 60*5);//定位间隔时间 5分钟
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
    }

    //启动服务调动的方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getApplicationContext()==null){
            stopSelf();
        }
        Toast.makeText(getApplicationContext(),"开始定位服务",Toast.LENGTH_SHORT).show();
        mLocationClient.startLocation();
        if (intent!=null){
            userId = intent.getStringExtra("userId");
            reqno = intent.getStringExtra("reqNo");
            carPaiNum = intent.getStringExtra("carNum");
        }
        return START_REDELIVER_INTENT;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode()==0){
            double latitude = aMapLocation.getLatitude();//纬度
            double longitude = aMapLocation.getLongitude();//经度
            address = aMapLocation.getAddress();
            jingDu = longitude+"";
            weiDu = latitude+"";
            postLocation();
        }else {
            Toast.makeText(getApplicationContext(),"定位失败,"+aMapLocation.getErrorInfo(),Toast.LENGTH_SHORT).show();
        }
    }


    private void postLocation() {
        if (!StringUtils.isEmpty(carPaiNum)){
            RequestParams params = new RequestParams(Urls.URL_LOCATION_BEGIN);
            params.addBodyParameter("userId",userId);
            params.addBodyParameter("reqNo",reqno);
            params.addBodyParameter("carBrandNum",carPaiNum);
            params.addBodyParameter("jingdu", jingDu);
            params.addBodyParameter("weidu", weiDu);
            params.addBodyParameter("localtion", address);
            RequestClient client = new RequestClient();
            client.onLoadDataPost(params);
            client.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    LoginResult result = (LoginResult) GsonUtil.jsonToBean(data, LoginResult.class);
                    if (result!=null){
                        if (result.code.equals(Constants.CODE.SUCCESS)){
                            //提交位置信息
                        }
                    }
                }
                @Override
                public void error(String err) {
//                    showMessage("失败=="+err);
                }
            });
        }else {
            //车牌号为空
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }
}
