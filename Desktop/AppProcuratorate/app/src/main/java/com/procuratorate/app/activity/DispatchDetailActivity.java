package com.procuratorate.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.procuratorate.app.R;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.bean.CarDispatch;
import com.procuratorate.app.bean.CarTypeResult;
import com.procuratorate.app.bean.DriverMsgResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;
import org.xutils.http.RequestParams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * created by 杨绘庆 on 2016/8/28.
 * 车辆详细调度
 */
public class DispatchDetailActivity extends BaseActivity {

    @Bind(R.id.btn_dispatch_car_sure)
    Button btnSure;
    @Bind(R.id.lv_car_msg)
    ListView lvCarMsg;
    @Bind(R.id.lv_driver_msg)
    ListView lvDriverMsg;
    @Bind(R.id.rl_car_msg)
    LinearLayout rlCarMsg;
    @Bind(R.id.rl_driver_msg)
    LinearLayout rlDriverMsg;
    @Bind(R.id.tv_car_msg)
    TextView tvCarMsg;
    @Bind(R.id.tv_car_num_peo)
    TextView tvCarNumPeo;
    @Bind(R.id.tv_driver_check)
    TextView tvDriverCheck;
    @Bind(R.id.tv_driver_num_check)
    TextView tvDriverNumCheck;
    @Bind(R.id.ib_delete_close)
    ImageButton iconClose;

    private ArrayList<Map<String,String>> dataCar = new ArrayList<>();
    private ArrayList<Map<String,String>> dataPeople = new ArrayList<>();
    private SimpleAdapter adapterCar;
    private String carN;
    private String carP;
    private SimpleAdapter adapterDriver;
    private String personname;
    private String mobil;
    private String perid;
    private boolean lvCarShow = false;
    private boolean driverShow = false;
    private Map<String, String> mapPerson;
    private String defaultSiJi;
    private String defaultMobile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle(null);
        setContentViewThis(R.layout.activity_dispatch_detail);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        getWindow().setLayout(width/5*4,height/3*2);
        ButterKnife.bind(this);
        init();
    }
    private ArrayList<CarDispatch> resultList = new ArrayList<>();
    private boolean isDefault = true;
    private void init() {
        Intent intent = getIntent();
        resultList = intent.getParcelableArrayListExtra("againMsg");
        loadCarData();
        adapterCar = new SimpleAdapter(this,dataCar, R.layout.item_spinner_car,
                new String[]{"cartype","zaikenum","defaultsiji","mobile"},
                new int[]{R.id.car_type,R.id.car_max_num,R.id.tv_driver,R.id.tv_driver_mobile});
        lvCarMsg.setAdapter(adapterCar);
        lvCarMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> car = dataCar.get(position);
                carN = car.get("cartype");
                carP = car.get("zaikenum");
                tvCarMsg.setText("" + carN);
                tvCarNumPeo.setText("" + carP);
                lvCarMsg.setVisibility(View.GONE);
                lvCarShow = false;
                defaultSiJi = car.get("defaultsiji");
                defaultMobile = car.get("mobile");
                isDefault = true;
                loadPeople();
            }
        });
        adapterDriver = new SimpleAdapter(this,dataPeople, R.layout.item_spinner_people,
                new String[]{"personname","mobil"},
                new int[]{R.id.car_person,R.id.car_person_num});
        lvDriverMsg.setAdapter(adapterDriver);
        lvDriverMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> car = dataPeople.get(position);
                personname = car.get("personname");
                mobil = car.get("mobil");
                perid = car.get("id");
                tvDriverCheck.setText(personname);
                tvDriverNumCheck.setText(mobil);
                lvDriverMsg.setVisibility(View.GONE);
                driverShow = false;
            }
        });
    }

    /**
     * 司机信息
     */
    private void loadPeople() {
        if (!StringUtils.isEmpty(carN)){
            dataPeople.clear();
            RequestParams params = new RequestParams(Urls.URL_DISPATCH_PEOPLE);
            params.addBodyParameter("carBrandNum",carN);
            RequestClient request = new RequestClient();
            request.onLoadDataPost(params);
            request.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    //司机
                    DriverMsgResult result = (DriverMsgResult) GsonUtil.jsonToBean(data,DriverMsgResult.class);
                    if (result.list!=null){
                        for (int i = 0; i < result.list.size(); i++) {
                            mapPerson = new HashMap<String, String>();
                            mapPerson.put("personname", result.list.get(i).personname);
                            mapPerson.put("mobil", result.list.get(i).mobile);
                            mapPerson.put("id", result.list.get(i).userid);
                            dataPeople.add(mapPerson);
                        }

                        personname=dataPeople.get(0).get("personname");
                        mobil = dataPeople.get(0).get("mobil");
                        perid = dataPeople.get(0).get("id");
                        adapterDriver.notifyDataSetChanged();
                        tvDriverCheck.setText(defaultSiJi); //默认司机信息
                        tvDriverNumCheck.setText(defaultMobile);
                    }
                }
                @Override
                public void error(String err) {
                }
            });
        }else {
            showMessage("未选择车辆");
        }
    }

    /**
     * 车辆信息
     */
    private void loadCarData() {
        RequestParams params = new RequestParams(Urls.URL_DISPATCH_CAR);
        params.addBodyParameter("userId","10002");
        final RequestClient requestCar = new RequestClient();
        requestCar.onLoadDataPost(params);
        requestCar.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                CarTypeResult result = (CarTypeResult) GsonUtil.jsonToBean(data,CarTypeResult.class);
                if (result.list!=null){
                    for (int i = 0; i < result.list.size(); i++) {
                        Map<String,String> mapCar = new HashMap<String, String>();
                        mapCar.put("cartype",result.list.get(i).carbrandnum);
                        mapCar.put("zaikenum",result.list.get(i).zaikenum);
                        mapCar.put("id",result.list.get(i).id);
                        mapCar.put("defaultsiji",result.list.get(i).defaultsiji);
                        mapCar.put("mobile",result.list.get(i).mobile);
                        dataCar.add(i, mapCar);
                    }
                    adapterCar.notifyDataSetChanged();
                }
            }

            @Override
            public void error(String err) {

            }
        });
    }

    private boolean isAgain(CarDispatch dis){
        for (int i = 0; i < resultList.size() ; i++) {
            if (resultList.get(i).mobile.equals(dis.mobile)||
                    resultList.get(i).carbrandnum.equals(dis.carbrandnum)){
                return true;
            }
        }

        return false;
    }
    @OnClick({R.id.btn_dispatch_car_sure,R.id.rl_car_msg,R.id.rl_driver_msg,R.id.ib_delete_close})
    void sureClick(View v){
        switch (v.getId()){
            case R.id.btn_dispatch_car_sure:
                //确认返回的数据对象
                personname = tvDriverCheck.getText().toString();
                mobil = tvDriverNumCheck.getText().toString();
                CarDispatch dispatchs = new CarDispatch();
                dispatchs.id = "Yang";
                dispatchs.carbrandnum = carN;
                dispatchs.anpaisiji = personname;
                dispatchs.bycarnum = carP;
                dispatchs.mobile = mobil;
                dispatchs.userid = perid;
                if (StringUtil.isEmpty(carN)){
                    showMessage("未选择车辆");
                }else if (isAgain(dispatchs)){
                    showMessage("请勿选择已选中的车辆或者司机");
                } else {
                    EventBus.getDefault().post(dispatchs);
                    finish();
                }
                break;
            case R.id.rl_car_msg:
                if (driverShow){
                    lvDriverMsg.setVisibility(View.GONE);
                    driverShow=false;
                }
                if (lvCarShow)
                    lvCarMsg.setVisibility(View.GONE);
                else
                    lvCarMsg.setVisibility(View.VISIBLE);
                lvCarShow=!lvCarShow;
                break;
            case R.id.rl_driver_msg:
                if (StringUtils.isEmpty(carN)){
                    showMessage("未选择车辆");
                }else {
                    if (driverShow){
                        lvDriverMsg.setVisibility(View.GONE);
                    }
                    else{
                        lvDriverMsg.setVisibility(View.VISIBLE);
                    }
                    driverShow = !driverShow;
                }
                break;
            case R.id.ib_delete_close:
                finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
