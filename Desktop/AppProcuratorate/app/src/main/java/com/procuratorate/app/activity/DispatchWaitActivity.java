package com.procuratorate.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.adapter.BaseListAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.BaseModel;
import com.procuratorate.app.bean.CarDispatch;
import com.procuratorate.app.bean.CarDispatchResult;
import com.procuratorate.app.bean.CodeBean;
import com.procuratorate.app.bean.DeleteMsgPull;
import com.procuratorate.app.bean.OrderDetail;
import com.procuratorate.app.bean.PostCarDispatch;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;
import com.procuratorate.app.widget.ScrollListView;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 杨绘庆 on 2016/8/18.
 * 车辆调度
 */
public class DispatchWaitActivity extends BaseActivity {

    @Bind(R.id.ll_order_detail)
    LinearLayout orderMsg;
    @Bind(R.id.tv_car_dispatch_num)
    TextView tvDispatchNum;
    @Bind(R.id.icon_add_dispatch)
    ImageButton iconAdd;
    @Bind(R.id.list_dispatch_result)
    ScrollListView listViewResult;
    @Bind(R.id.tv_empty_show)
    TextView tvEmptyShow;
    @Bind(R.id.btn_dispatch_sure)
    Button btnSubmit;
    @Bind(R.id.tv_car_department)
    TextView tvDepartment;
    @Bind(R.id.tv_total_num)
    TextView tvTotalNum;
    @Bind(R.id.tv_use_start_date)
    TextView tvStartTime;
    @Bind(R.id.tv_use_end_date)
    TextView tvEndDate;
    @Bind(R.id.tv_apply_person)
    TextView tvPerson;
    @Bind(R.id.tv_mobil)
    TextView tvMobil;
    @Bind(R.id.tv_begin_location)
    TextView tvBeginLocation;
    @Bind(R.id.tv_end_location)
    TextView tvEndLocation;
    @Bind(R.id.tv_the_car_for)
    TextView tvCarFor;
    @Bind(R.id.tv_the_car_mark)
    TextView tvCarMark;


    private ArrayList<CarDispatch> resultList = new ArrayList<>();
    private ArrayList<CarDispatch> resultOld = new ArrayList<>();
    private ArrayList<DeleteMsgPull> delMsgList =new ArrayList<>();
    private DispatchAdapter adapter;
    private int countCar = 0;
    private int state;
    private String reqno;

    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_dispatch_wait);
        ButterKnife.bind(this);
        setHeadTitle("车辆调度");
        init();
    }

    private void init() {
        IConfig iConfig = new IConfig(this);
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
        Intent intent = getIntent();
        if (intent!=null){
            state = intent.getIntExtra("state",-1);
            reqno = intent.getStringExtra(Constants.ORDER_REQNO);
        }
        loadOrderData();
        switch (state){
            case 1:
                setHeadTitle("车辆调度");
                break;
            case 2:
                setHeadTitle("待执行调度");
                loadDispatch();
                break;
            case 3:
                setHeadTitle("已完成调度");
                loadDispatch();
                iconAdd.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                break;
        }
        EventBus.getDefault().register(this);
        adapter = new DispatchAdapter(this);
        adapter.setList(resultList);
        listViewResult.setAdapter(adapter);
    }

    //获取订单信息
    private void loadOrderData() {
        if (!StringUtils.isEmpty(reqno)){
            RequestParams params = new RequestParams(Urls.URL_DISPATCH_CAN_KAO);
            params.addBodyParameter("reqNo",reqno);
            params.addBodyParameter("userId",userId);
            if (state==3){
                params.addBodyParameter("flag","1");
            }
            RequestClient client = new RequestClient();
            client.onLoadDataPost(params);
            client.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    OrderDetail order = (OrderDetail) GsonUtil.jsonToBean(data,OrderDetail.class);
                    if (order!=null){
                        if (order.code.equals(Constants.CODE.SUCCESS)){
                            if (!StringUtil.isEmpty(order.usecardep))
                                tvDepartment.setText("部门："+order.usecardep);
                            if (!StringUtil.isEmpty(order.bycarnum))
                                tvTotalNum.setText("人数："+order.bycarnum);
                            if (!StringUtil.isEmpty(order.usecarstartdate))
                                tvStartTime.setText("开始时间："+order.usecarstartdate);
                            if (!StringUtil.isEmpty(order.usecarenddate))
                                tvEndDate.setText("结束时间："+order.usecarenddate);
                            if (!StringUtil.isEmpty(order.applyperson))
                                tvPerson.setText("申请人："+order.applyperson);
                            if (!StringUtil.isEmpty(order.mobile))
                                tvMobil.setText("电话："+order.mobile);
                            if (!StringUtil.isEmpty(order.beginlocaltion))
                                tvBeginLocation.setText("出发地："+order.beginlocaltion);
                            if (!StringUtil.isEmpty(order.destination))
                                tvEndLocation.setText("目的地："+order.destination);
                            if (!StringUtil.isEmpty(order.yongcheshiyou))
                                tvCarFor.setText(order.yongcheshiyou);
                            if (!StringUtil.isEmpty(order.caryaoqiu))
                                tvCarMark.setText(order.caryaoqiu);
                        }
                    }
                }
                @Override
                public void error(String err) {

                }
            });
        }
    }


    //获取调度的订单
    private void loadDispatch() {
        tvEmptyShow.setVisibility(View.GONE);
        RequestParams params = new RequestParams(Urls.URL_DISPATCH_MSG);
        if (state==3){
            params.addBodyParameter("", reqno);
        }
        params.addBodyParameter("reqNo", reqno);
        params.addBodyParameter("userId", userId);
        final RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                //订单信息
                CarDispatchResult result = (CarDispatchResult) GsonUtil.jsonToBean(data, CarDispatchResult.class);
                if (result.list != null) {
                    if (result.code.equals(Constants.CODE.SUCCESS)){
                        resultList.clear();
                        resultList.addAll(result.list);
                        resultOld.addAll(resultList);
                        adapter.notifyDataSetChanged();
                        countCar = resultList.size();
                        tvDispatchNum.setText("车辆:" + countCar + "辆");
                    }else {
                        showMessageNormal(result.msg);
                    }
                }
            }

            @Override
            public void error(String err) {
            }
        });

    }

    @OnClick({R.id.icon_add_dispatch,R.id.btn_dispatch_sure})
    void clickShow(View v){
        switch (v.getId()){
            case R.id.icon_add_dispatch:
                //添加车辆
                Intent intent = new Intent(this,DispatchDetailActivity.class);
                intent.putParcelableArrayListExtra("againMsg", resultList);
                startActivity(intent);
                break;
            case R.id.btn_dispatch_sure:
                submitDispatch();
                break;
        }
    }

    /**
     * 提交数据
     */
    private void submitDispatch() {
        if (resultList.size()<=0){
            showMessage("请选择车辆调度！！！");
        }else{
            ArrayList<PostCarDispatch> carPost = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                CarDispatch carDispatch = resultList.get(i);
                PostCarDispatch car = new PostCarDispatch();
                car.anpaiSiji = carDispatch.userid;
                car.byCarNum = carDispatch.bycarnum;
                car.carBrandNum = carDispatch.carbrandnum;
                car.mobile = carDispatch.mobile;
                car.status = carDispatch.status;
                carPost.add(car);
            }
            String delMsg = GsonUtil.objectToJson(delMsgList);
            String msg = GsonUtil.objectToJson(carPost);
            RequestParams params = new RequestParams(Urls.URL_POST_DISPATCH);
            params.addBodyParameter("userId",userId);
            params.addBodyParameter("reqNo", reqno);
            params.addBodyParameter("msg", msg);
            params.addBodyParameter("deleteMsg",delMsg);
            RequestClient client = new RequestClient();
            client.onLoadDataPost(params);
            client.setLoadDataFinish(new RequestClient.OnLoadData() {
                @Override
                public void success(String data) {
                    //提交数据成功
                    BaseModel baseModel = (BaseModel) GsonUtil.jsonToBean(data, BaseModel.class);
                    if (baseModel!=null){
                        if (baseModel.code.equals(Constants.CODE.SUCCESS)) {
                            showMessage(baseModel.msg);
                            CodeBean con = new CodeBean();
                            con.refresh = true;
                            con.code = Constants.REFRESH.REF_WAIT_DISPATCH;
                            EventBus.getDefault().post(con);
                            finish();
                        }else {
                            showMessageNormal(baseModel.msg);
                        }
                    }
                }

                @Override
                public void error(String err) {
                    showMessage("失败"+err);
                }
            });
        }

    }
    /**
     * 返回的数据 （调用的车辆）
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.PostThread)
    public void onEventMainThread(CarDispatch event) {
        if(event!=null){
            countCar+=1;
            event.status=0;
            tvEmptyShow.setVisibility(View.GONE);
            tvDispatchNum.setText("车辆:"+countCar+"辆");
            resultList.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    public class DispatchAdapter extends BaseListAdapter<CarDispatch>{

        private LayoutInflater inflater;

        public DispatchAdapter(Activity context) {
            super(context);
            this.inflater = context.getLayoutInflater();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null){
                convertView = inflater.inflate(R.layout.item_detail_dispatch,null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            final CarDispatch item = mList.get(position);
            holder.carNum.setText("车载：" + item.bycarnum + "人");
            holder.carPeople.setText("司机："+item.anpaisiji);
            holder.carType.setText("车牌："+item.carbrandnum);
            holder.phoneNum.setText("电话：" + item.mobile);
            holder.iconLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //记录曾经调度过 现在删除的数据mobile
                    for (CarDispatch car : resultOld) {
                        if (item.id.equals(car.id)) {
                            DeleteMsgPull p = new DeleteMsgPull();
                            p.mobile = car.mobile;
                            delMsgList.add(p);
                        }
                    }
                    //删除数据
                    remove(position);
                    countCar -= 1;
                    tvDispatchNum.setText("车辆：" + countCar + "辆");
                    if (countCar == 0)
                        tvEmptyShow.setVisibility(View.VISIBLE);

                }
            });
            if (item.status<2){
                if (item.status==0) {
                    holder.driverSure.setText("未确认");
                    holder.driverSure.setTextColor(Color.RED);
                }
                else if (item.status==1) {
                    holder.driverSure.setText("已确认");
                    holder.driverSure.setTextColor(Color.GREEN);
                    holder.iconLess.setClickable(false);
                }
            }else {
                holder.driverSure.setVisibility(View.GONE);
            }
            if (state==3)
                holder.iconLess.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder{
            @Bind(R.id.tv_car_type)
            TextView carType;
            @Bind(R.id.tv_car_num)
            TextView carNum;
            @Bind(R.id.tv_car_people)
            TextView carPeople;
            @Bind(R.id.tv_phone_num)
            TextView phoneNum;
            @Bind(R.id.icon_less)
            ImageView iconLess;
            @Bind(R.id.tv_driver_sure)
            TextView driverSure;
            public  ViewHolder(View v){
                ButterKnife.bind(this, v);}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}