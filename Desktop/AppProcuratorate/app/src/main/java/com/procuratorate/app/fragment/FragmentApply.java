package com.procuratorate.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.activity.ApplyAddActivity;
import com.procuratorate.app.activity.ApplyChangeDelActivity;
import com.procuratorate.app.adapter.ApplyAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseFragment;
import com.procuratorate.app.base.IConfig;
import com.procuratorate.app.bean.ApplyItem;
import com.procuratorate.app.bean.ApplyItemResult;
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
 * Created by 杨绘庆 on 2016/9/12.
 * 申请Fragment
 */
public class FragmentApply extends BaseFragment {
    @Bind(R.id.img_apply)
    ImageView imgApply;
    @Bind(R.id.list_apply_data)
    ListViewMore listApplyMe;
    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slFresh;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;

    private String userId;
    private ArrayList<ApplyItem> listApply = new ArrayList<>();
    private ApplyAdapter adapter;
    private int showNum = 10;
    private boolean more = false;
    private int showMsg = 1;
    private String token;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply,container,false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }

    private void init() {
        IConfig iConfig = new IConfig(getContext());
        EventBus.getDefault().register(this);
        userId = iConfig.getStringData(Constants.Login.PARAM_USER_ID, "");
        token = iConfig.getStringData(Constants.Login.PARAM_TOKEN,"");
        loadData();
        adapter = new ApplyAdapter(getActivity());
        adapter.setList(listApply);
        listApplyMe.setAdapter(adapter);
        listApplyMe.setOnLoadMoreListener(new ListViewMore.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                more = true;
                loadData();
            }
        });
        listApplyMe.setOnItemClickListener(onItemClickListener);
        slFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listApply.clear();
                loadData();
                adapter.notifyDataSetChanged();
            }
        });
    }
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), ApplyAddActivity.class);
            intent.putExtra("state",1);
            intent.putExtra(Constants.ORDER_REQNO,listApply.get(position).reqno);
            intent.putExtra("ding",listApply.get(position).dingdanStatus);
            startActivity(intent);
        }
    };
    @OnClick({R.id.img_apply})
    void applyClick(View view){
        switch (view.getId()){
            case R.id.img_apply:
                Intent intent = new Intent(getActivity(), ApplyAddActivity.class);
                intent.putExtra("state",0);
                startActivity(intent);
                break;
        }
    }

    private void loadData() {
        if (more){
            listApplyMe.isLoadingMore();
        }
        RequestParams params = new RequestParams(Urls.URL_APPLY_LIST);
        params.addBodyParameter("userId",userId);
        params.addBodyParameter("beginNum", String.valueOf(listApply.size()));
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
                            listApply.addAll(result.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listApply.size()>0){
                            tvEmpty.setVisibility(View.GONE);
                        }else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        if (listApply.size()>=result.total){
                            listApplyMe.setLoading(false);
                        }else {
                            listApplyMe.setLoading(true);
                        }
                        showMsg = 1;
                    }else if (result.code.equals(Constants.CODE.NO_POWER)){
                        if (showMsg!=1){
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
                listApplyMe.setLoading(true);
                slFresh.setRefreshing(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.PostThread)
    public void onEvent(CodeBean bean){
        if (bean!=null){
            if (bean.refresh&&bean.code==Constants.REFRESH.REF_APPLY){
                listApply.clear();
                loadData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
