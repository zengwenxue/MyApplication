package com.procuratorate.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.DispatchWaitActivity;
import com.procuratorate.app.adapter.WaitDispatchAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseFragment;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.bean.OrderListBean;
import com.procuratorate.app.bean.OrderResult;
import com.procuratorate.app.bean.PowerBean;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.utils.StringUtils;
import com.procuratorate.app.widget.ListViewMore;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 杨绘庆 on 2016/8/1.
 * 已调度
 */
public class FragmentExecuteOk extends BaseFragment {
    @Bind(R.id.list_wait_dispatch)
    ListViewMore listWaitDispatch;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;

    private View mView;
    private WaitDispatchAdapter adapter;
    private ArrayList<OrderListBean> dataAll = new ArrayList<>();
    private String userId;
    private int showNum = 10;
    private int show_code = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dispatch_over,container,false);
        ButterKnife.bind(this, mView);
        initThings();
        return mView;
    }

    private void initThings() {
        IConfig iConfig = new IConfig(getContext());
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
//        initPower();
        loadData();
        adapter = new WaitDispatchAdapter(getActivity());
        adapter.setList(dataAll);
        listWaitDispatch.setAdapter(adapter);
        listWaitDispatch.setOnItemClickListener(onItemClickListener);
        listWaitDispatch.setOnLoadMoreListener(new ListViewMore.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                loadData();
            }
        });
        slRefresh.setColorSchemeResources(android.R.color.holo_blue_bright);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataAll.clear();
                loadData();
            }
        });
    }



    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), DispatchWaitActivity.class);
            intent.putExtra(Constants.ORDER_REQNO, dataAll.get(position).reqno);
            intent.putExtra("state", 3);
            startActivity(intent);
        }
    };

    private void loadData() {
        listWaitDispatch.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_OK_EXECUTE);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("beginNum",String.valueOf(dataAll.size()));
        params.addBodyParameter("showNum",String.valueOf(showNum));
        RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                OrderResult order = (OrderResult) GsonUtil.jsonToBean(data, OrderResult.class);
                if (order!=null){
                    if (order.code.equals(Constants.CODE.SUCCESS)){
                        if (order.list!=null){
                            dataAll.addAll(order.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (dataAll.size()>0){
                            tvEmpty.setVisibility(View.GONE);
                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        if (dataAll.size()>=order.total){
                            listWaitDispatch.setLoading(false);
                        }else{
                            listWaitDispatch.setLoading(true);
                        }
                        show_code = 1;
                    }else if (order.code.equals(Constants.CODE.NO_POWER)){
                        if (show_code!=1){
                            showMessage(order.msg);
                        }
                        tvEmpty.setText(order.msg);
                        listWaitDispatch.setLoading(true);
                        show_code = 24;
                    }else {
                        listWaitDispatch.setLoading(true);
                    }
                }
                slRefresh.setRefreshing(false);
            }
            @Override
            public void error(String err) {
                // 获取数据失败
                listWaitDispatch.setLoading(true);
                slRefresh.setRefreshing(false);
            }
        });
    }
}
