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
import com.procuratorate.app.activity.WaitCompleteActivity;
import com.procuratorate.app.adapter.TaskWaitFrgAdapter;
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
 * 司机已完成任务
 */
public class FragmentExecuteHistory extends BaseFragment {


    @Bind(R.id.list_wait_dispatch)
    ListViewMore listWaitDispatch;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;

    private View mView;
    private ArrayList<OrderListBean> listData = new ArrayList<>();
    private TaskWaitFrgAdapter adapter;
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
        adapter = new TaskWaitFrgAdapter(getActivity());
        adapter.setList(listData);
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
                listData.clear();
                loadData();
            }
        });
    }



    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), WaitCompleteActivity.class);
            intent.putExtra(Constants.ORDER_REQNO, listData.get(position).reqno);
            intent.putExtra("tag",2);
            startActivity(intent);
        }
    };
    /**
     * 获取完成列表信息
     */
    private void loadData() {
        listWaitDispatch.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_OK_TASK_LIST);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("showNum",String.valueOf(showNum));
        params.addBodyParameter("beginNum",String.valueOf(listData.size()));
        RequestClient client = new RequestClient();
        client.onLoadDataPost(params);
        client.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                OrderResult result = (OrderResult) GsonUtil.jsonToBean(data, OrderResult.class);
                if (result!=null){
                    if (result.code.equals(Constants.CODE.SUCCESS)){
                        show_code = 1;
                        if (result.list!=null){
                            listData.addAll(result.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (result.list.size()>0){
                            tvEmpty.setVisibility(View.GONE);

                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            slRefresh.setVisibility(View.GONE);
                        }
                        if (listData.size()>=result.total){
                            listWaitDispatch.setLoading(false);
                        }else {
                            listWaitDispatch.setLoading(true);
                        }
                    }else if (result.code.equals(Constants.CODE.NO_POWER)){
                        if (show_code!=1){
                            showMessage(result.msg);
                        }
                        tvEmpty.setText(result.msg);
                        listWaitDispatch.setLoading(true);
                        show_code=24;
                    }else {
                        listWaitDispatch.setLoading(true);
                    }
                }
                slRefresh.setRefreshing(false);
            }
            @Override
            public void error(String err) {
                slRefresh.setRefreshing(false);
                listWaitDispatch.setLoading(true);
            }
        });
    }
}
