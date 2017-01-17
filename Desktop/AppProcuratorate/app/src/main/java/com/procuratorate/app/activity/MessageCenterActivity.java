package com.procuratorate.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.adapter.PullMsgAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.bean.PullMsg;
import com.procuratorate.app.bean.PullMsgResult;
import com.procuratorate.app.config.Constants;
import com.procuratorate.app.config.Urls;
import com.procuratorate.app.utils.GsonUtil;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by 杨绘庆 on 2016/9/2.
 * 消息中心
 */
public class MessageCenterActivity extends BaseActivity {

    @Bind(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @Bind(R.id.list_pull_msg)
    ListView listPullMsg;
    @Bind(R.id.tv_empty_show)
    TextView tvEmpty;

    private ArrayList<PullMsg> listMsg = new ArrayList<>();
    private PullMsgAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_message_center);
        ButterKnife.bind(this);
        ActivityManagerMe.getInstance().addActivity(this);
        setHeadTitle("消息中心");
        init();

    }

    private void init() {
        loadData();
        adapter = new PullMsgAdapter(this);
        adapter.setList(listMsg);
        listPullMsg.setAdapter(adapter);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listMsg.clear();
                loadData();
            }
        });
    }

    /**
     * 订单信息
     */
    private void loadData() {
        RequestParams params = new RequestParams(Urls.URL_PULL_MSG);
        params.addBodyParameter("userId", userId);
        requestClient.onLoadDataPost(params);
        requestClient.setLoadDataFinish(new RequestClient.OnLoadData() {
            @Override
            public void success(String data) {
                PullMsgResult pullMsgResult = (PullMsgResult) GsonUtil.jsonToBean(data,PullMsgResult.class);
                if (pullMsgResult!=null){
                    if (pullMsgResult.code.equals(Constants.CODE.SUCCESS)){
                        if (pullMsgResult.list!=null){
                            listMsg.addAll(pullMsgResult.list);
                            adapter.notifyDataSetChanged();
                        }
                        if (listMsg.size()>0){
                            tvEmpty.setVisibility(View.GONE);
                        }
                    }else {
                        showMessage(pullMsgResult.msg);
                    }
                }
                slRefresh.setRefreshing(false);
            }

            @Override
            public void error(String err) {
                slRefresh.setRefreshing(false);
            }
        });
    }

    //消息推送点击进来, 返回的时候返回主页面
    @Override
    public void finishBack() {
        super.finishBack();
        if (ActivityManagerMe.getActivity(MainActivity.class)==null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ActivityManagerMe.getActivity(MainActivity.class)==null){
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
