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
import com.procuratorate.app.bean.CodeBean;
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
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 杨绘庆 on 2016/8/1.
 * 司机待执行任务
 */
public class FragmentExecuteWait extends BaseFragment {
    @Bind(R.id.list_wait_dispatch)
    ListViewMore listWaitDispatch;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;

    private View mView;
    private ArrayList<OrderListBean> listData = new ArrayList<>();
    private TaskWaitFrgAdapter adapter ;
    private String userId;
    private int shoe_code = 1;
    private int showNum = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wait_execute,container,false);
        ButterKnife.bind(this, mView);
        init();
        return mView;
    }

    private void init() {
        EventBus.getDefault().register(this);
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


    private AdapterView.OnItemClickListener onItemClickListener  = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), WaitCompleteActivity.class);
            intent.putExtra(Constants.ORDER_REQNO, listData.get(position).reqno);
            if (listData.get(position).status.equals("0")){
                intent.putExtra("tag",0);//开始行程
            }else if (listData.get(position).status.equals("1")){
                intent.putExtra("tag",1);//结束行程
            }
            startActivity(intent);
        }
    };

    private void loadData() {
        listWaitDispatch.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_WAIT_TASK_LIST);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("beginNum",String.valueOf(listData.size()));
        params.addBodyParameter("showNum",String.valueOf(showNum));
        final RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                OrderResult result = (OrderResult) GsonUtil.jsonToBean(data,OrderResult.class);
                if (result!=null){
                    if (result.code.equals(Constants.CODE.SUCCESS)){
                        shoe_code = 1;
                        if (result.list!=null){
                            listData.addAll(result.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listData.size()>0){
                            tvEmpty.setVisibility(View.GONE);
                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        if (listData.size()>=result.total){
                            listWaitDispatch.setLoading(false);
                        }else {
                            listWaitDispatch.setLoading(true);
                        }

                    }else if (result.code.equals(Constants.CODE.NO_POWER)){
                        if (shoe_code!=1){
                            showMessage(result.msg);
                        }
                        tvEmpty.setText(result.msg);
                        shoe_code = 24;
                        listWaitDispatch.setLoading(true);
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

    @Subscribe(threadMode = ThreadMode.PostThread)
    public void onEventMainThread(CodeBean event) {
        if(event!=null){
            if (event.refresh&&event.code==Constants.REFRESH.REF_DRIVER_EXECUTE){
                //提交数据成功刷新页面
                listData.clear();
                loadData();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
