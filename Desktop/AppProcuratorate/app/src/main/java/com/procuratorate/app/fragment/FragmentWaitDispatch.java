package com.procuratorate.app.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.DispatchWaitActivity;
import com.procuratorate.app.activity.MainActivity;
import com.procuratorate.app.adapter.WaitDispatchAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseFragment;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.CarDispatch;
import com.procuratorate.app.bean.CodeBean;
import com.procuratorate.app.bean.LoginResult;
import com.procuratorate.app.bean.OrderListBean;
import com.procuratorate.app.bean.OrderResult;
import com.procuratorate.app.bean.PowerBean;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;
import com.procuratorate.app.widget.ListViewMore;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by 杨绘庆 on 2016/8/1.
 * 待调度
 */
public class FragmentWaitDispatch extends BaseFragment {

    @Bind(R.id.list_wait_dispatch)
    ListViewMore listWaitDispatch;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;


    private View mView;
    private ArrayList<OrderListBean> listAll = new ArrayList<>();
    private WaitDispatchAdapter adapter;
    private String userID;
    private int showNum = 10;
    private int show_code = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wait_dispatch,container,false);
        ButterKnife.bind(this, mView);
        initThings();
        return mView;
    }
    //获取订单信息
    private void initThings() {
        EventBus.getDefault().register(this);
        IConfig iConfig = new IConfig(getContext());
        userID = iConfig.getStringData(Constants.Login.PARAM_USER_ID,"");
        loadData();
        if (listAll.size()<=0)  //是否有数据
            tvEmpty.setVisibility(View.VISIBLE);
        else
            tvEmpty.setVisibility(View.GONE);
        adapter = new WaitDispatchAdapter(getActivity());
        adapter.setList(listAll);
        listWaitDispatch.setAdapter(adapter);
        listWaitDispatch.setOnItemClickListener(onItemClickListener);
        //
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
                listAll.clear();
                loadData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadData() {
        listWaitDispatch.isLoadingMore();
        RequestParams params = new RequestParams(Urls.URL_DISPATCH_WAIT);
        params.addBodyParameter("userId",userID);
        params.addBodyParameter("showNum",String.valueOf(showNum));
        params.addBodyParameter("beginNum", String.valueOf(listAll.size()));
        RequestClient request = new RequestClient();
        request.onLoadDataPost(params);
        request.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                //获取的数据
                OrderResult order = (OrderResult) GsonUtil.jsonToBean(data, OrderResult.class);
                if (order != null) {
                    if (order.code.equals(Constants.CODE.SUCCESS)) {
                        if (order.list != null) {
                            listAll.addAll(order.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listAll.size() >= order.total) {
                            listWaitDispatch.setLoading(false);
                        }else {
                            listWaitDispatch.setLoading(true);
                        }
                        if (listAll.size() > 0) {
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        show_code = 1;
                    } else if (order.code.equals(Constants.CODE.NO_POWER)) {
                        listWaitDispatch.setLoading(true);
                        if (show_code != 1) {
                            showMessage(order.msg);
                        }
                        tvEmpty.setText(order.msg);
                        show_code = 24;
                    }else {
                        listWaitDispatch.setLoading(true);
                    }
                }
                slRefresh.setRefreshing(false);
            }

            @Override
            public void error(String err) {
                listWaitDispatch.setLoading(true);
                slRefresh.setRefreshing(false);
            }
        });
    }


    private int delete=-1;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), DispatchWaitActivity.class);
            intent.putExtra(Constants.ORDER_REQNO, listAll.get(position).reqno);
            intent.putExtra("state", 1);
            delete=position;
            startActivity(intent);
        }
    };

    @Subscribe(threadMode = ThreadMode.PostThread)
    public void onEventMainThread(CodeBean event) {
        if(event!=null){
            if (event.refresh&&event.code==Constants.REFRESH.REF_WAIT_DISPATCH){
                //提交数据成功刷新页面,更新状态
                if (listAll.size()>0){
                    listAll.remove(delete);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
