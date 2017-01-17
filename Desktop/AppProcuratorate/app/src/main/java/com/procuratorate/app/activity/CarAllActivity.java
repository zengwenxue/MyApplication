package com.procuratorate.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.adapter.CarListAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.CarDetails;
import com.procuratorate.app.bean.CarMsg;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.widget.ListViewMore;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qing on 2016/9/13.
 */
public class CarAllActivity extends BaseActivity {
    @Bind(R.id.list_car_detail)
    ListViewMore listCars;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slFresh;

    private RequestClient carRequest;
    private ArrayList<CarMsg> list = new ArrayList<>();
    private CarListAdapter adapter;
    private String userId;
    private int showNum = 10;
    private boolean showError = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.fragment_car);
        ButterKnife.bind(this);
        setHeadTitle("车辆位置");
        initThings();
    }
    private void initThings() {
        IConfig iConfig = new IConfig(this);

        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID, "");
        loadData();
        adapter = new CarListAdapter(this);
        adapter.setList(list);
        listCars.setAdapter(adapter);
        listCars.setOnItemClickListener(onItemClickListener);

        listCars.setOnLoadMoreListener(new ListViewMore.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                loadData();
            }
        });
        slFresh.setColorSchemeResources(android.R.color.holo_blue_bright);
        slFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                loadData();
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(),CarLocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.CAR_DETAIL, list.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    //获取数据
    private void loadData() {
        listCars.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_CAR_LOCATION);
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("beginNum", String.valueOf(list.size()));
        params.addBodyParameter("showNum", String.valueOf(showNum));
        RequestClient client = new RequestClient();
        client.onLoadDataPost(params);
        client.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                CarDetails carDetails = (CarDetails) GsonUtil.jsonToBean(data, CarDetails.class);
                if (carDetails!=null){
                    if (carDetails.code.equals(Constants.CODE.SUCCESS)) {
                        //获取数据成功
                        if (carDetails.list != null) {
                            list.addAll(carDetails.list);
                            adapter.notifyDataSetChanged();
                            if (list.size()>0){
                                tvEmpty.setVisibility(View.GONE);
                            }
                        }
                        if (list.size()>=carDetails.total){
                            listCars.setLoading(false);
                        }else {
                            listCars.setLoading(true);
                        }

                    }else if (carDetails.code.equals(Constants.CODE.NO_POWER)){
                        if (showError){
                            showMessage(carDetails.msg);
                        }
                        showError = true;
                        tvEmpty.setText(carDetails.msg);
                        listCars.setLoading(true);
                    }else {
                        listCars.setLoading(true);
                    }
                }
                slFresh.setRefreshing(false);
            }

            @Override
            public void error(String err) {
                listCars.setLoading(true);
                showMessage(err);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
