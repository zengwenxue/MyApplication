package com.procuratorate.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.procuratorate.app.R;
import com.procuratorate.app.activity.CarLocationActivity;
import com.procuratorate.app.activity.ShenPiActivity;
import com.procuratorate.app.adapter.ApplyAdapter;
import com.procuratorate.app.adapter.CarListAdapter;
import com.procuratorate.app.adapter.ShenPiAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseFragment;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.ApplyItem;
import com.procuratorate.app.bean.ApplyItemResult;
import com.procuratorate.app.bean.CarDetails;
import com.procuratorate.app.bean.CarMsg;
import com.procuratorate.app.bean.CodeBean;
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
 * Created by 杨绘庆 on 2016/9/13.
 * 车辆审批
 */
public class FragmentCar extends BaseFragment {

    @Bind(R.id.sl_refresh_shen)
    SwipeRefreshLayout slFresh;
    @Bind(R.id.list_shenpi)
    ListViewMore listShenPi;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;

    private String userId;
    private ArrayList<ApplyItem> listDataShen = new ArrayList<>();
    private ShenPiAdapter adapter;
    private int showNum = 10;
    private boolean more = false;
    private int showMsg = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shenpi,container,false);
        ButterKnife.bind(this,view);
        initThings();
        return view;
    }


    private void initThings() {
        IConfig iConfig = new IConfig(getContext());
        EventBus.getDefault().register(this);
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID, "");
        loadData();
        adapter = new ShenPiAdapter(getActivity());
        adapter.setList(listDataShen);
        listShenPi.setAdapter(adapter);
        listShenPi.setOnItemClickListener(onItemClickListener);
        listShenPi.setOnLoadMoreListener(new ListViewMore.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                more = true;
                loadData();
            }
        });
        slFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listDataShen.clear();
                loadData();
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), ShenPiActivity.class);
            intent.putExtra("reqno",listDataShen.get(position).reqno);
            intent.putExtra("status",listDataShen.get(position).shenpistatus);
            intent.putExtra("ding",listDataShen.get(position).dingdanStatus);
            startActivity(intent);
        }
    };

    @Subscribe(threadMode = ThreadMode.PostThread)
    public void onEventMainThread(CodeBean event){
        if (event!=null){
            if (event.refresh&&event.code==Constants.REFRESH.REF_CHECK){
                listDataShen.clear();
                loadData();
            }
        }
    }

    //获取数据
    private void loadData() {
        if (more){
            listShenPi.isLoadingMore();
        }
        RequestParams params = new RequestParams(Urls.URL_SHEN_PI_LIST);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("beginNum", String.valueOf(listDataShen.size()));
        params.addBodyParameter("showNum", String.valueOf(showNum));
        final RequestClient requestClient = new RequestClient();
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                ApplyItemResult result = (ApplyItemResult) GsonUtil.jsonToBean(data,ApplyItemResult.class);
                if (result!=null){
                    if (result.code.equals(Constants.CODE.SUCCESS)){
                        if (result.list!=null){
                            listDataShen.addAll(result.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listDataShen.size()>0){
                            tvEmpty.setVisibility(View.GONE);
                        } else{
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        if (listDataShen.size()>=result.total){
                            listShenPi.setLoading(false);
                        } else{
                            listShenPi.setLoading(true);
                        }
                        showMsg = 1;
                    }else if (result.code.equals(Constants.CODE.NO_POWER)){
                        if (showMsg == 1){
                            showMessage(result.msg);
                            tvEmpty.setText(result.msg);
                        }
                        showMsg = 24;
                    }
                }
                slFresh.setRefreshing(false);
                more = false;
            }

            @Override
            public void error(String err) {
                slFresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
