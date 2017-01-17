package com.procuratorate.app.activity;


import android.content.Intent;
import android.os.Bundle;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.procuratorate.app.R;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.bean.CarMsg;
import com.procuratorate.app.config.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 杨绘庆 on 2016/8/22.
 */
public class CarLocationActivity extends BaseActivity {

    @Bind(R.id.map_car)
    MapView mapCar;
    private Marker marker;
    private AMap aMap;
    private String name;
    private String location;
    private double longitude;
    private double latitude;
    private String carTye;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_car_location);
        ButterKnife.bind(this);
        mapCar.onCreate(savedInstanceState);
        setHeadTitle("车辆位置");
        init();
    }


    private void init() {
        //获取坐标和位置
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            CarMsg car = bundle.getParcelable(Constants.CAR_DETAIL);
            if (car!=null){
                name = car.personname;
                location = car.localtion;
                carTye = car.carbrandnum;
                longitude = Double.valueOf(car.jingdu);
                latitude = Double.valueOf(car.weidu);
            }
        }
        //添加标记 显示位置
        initMap();
    }

    private void initMap() {
        aMap = mapCar.getMap();
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
        MarkerOptions markerOptions =  new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title("司机："+name+"\n"+
                "车辆："+carTye+"\n"+
                "位置："+location);
        marker = aMap.addMarker(markerOptions);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapCar.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapCar.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapCar.onDestroy();
        ButterKnife.unbind(this);
    }
}
