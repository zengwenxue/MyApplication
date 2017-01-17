package com.procuratorate.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.DispatchWaitActivity;
import com.procuratorate.app.adapter.WaitExecuteAdapter;
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
 * 待执行 （可选调度）
 */
public class FragmentWaitExcute extends BaseFragment {

    @Bind(R.id.list_wait_dispatch)
    ListViewMore listWaitDispatch;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;

    private WaitExecuteAdapter adapter;
    private View mView;
    private ArrayList<OrderListBean> listAlls = new ArrayList<>();
    private IConfig iConfig;
    private String userId;
    private int showNum = 10;
    private int show_code = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wait_execute,container,false);
        ButterKnife.bind(this, mView);
        initThings();
        return mView;
    }

    private void initThings() {
        iConfig = new IConfig(getActivity());
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID, "");
        loadData();
        adapter = new WaitExecuteAdapter(getActivity());
        adapter.setList(listAlls);
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
                listAlls.clear();
                loadData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), DispatchWaitActivity.class);
            intent.putExtra(Constants.ORDER_REQNO, listAlls.get(position).reqno);
            intent.putExtra("state", 2);
            startActivity(intent);
        }
    };

    /**
     * 获取已调度的信息
     */
    private void loadData() {
        listWaitDispatch.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_WAIT_EXECUTE);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("beginNum",String.valueOf(listAlls.size()));
        params.addBodyParameter("showNum",String.valueOf(showNum));
        RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                OrderResult order = (OrderResult) GsonUtil.jsonToBean(data, OrderResult.class);
                if (order!=null){
                    if (order.code.equals(Constants.CODE.SUCCESS)){
                        show_code = 1;
                        if (order.list!=null){
                            listAlls.addAll(order.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listAlls.size()>0)
                            tvEmpty.setVisibility(View.GONE);
                        else{
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        if (listAlls.size()>=order.total){
                            listWaitDispatch.setLoading(false);
                        }else {
                            listWaitDispatch.setLoading(true);
                        }
                    }else if (order.code.equals(Constants.CODE.NO_POWER)){
                        //避免初次提示消息
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
                slRefresh.setRefreshing(false);
                listWaitDispatch.setLoading(true);
            }
        });
    }
}
