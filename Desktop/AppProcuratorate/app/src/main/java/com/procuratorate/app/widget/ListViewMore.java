package com.procuratorate.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.procuratorate.app.R;

/**
 * Created by Qing on 2016/8/31.
 */
public class ListViewMore extends ListView implements AbsListView.OnScrollListener {

    private boolean loading = false;
    private View footer;
    private ProgressBar bar;
    private TextView tvMsg;

    private int mStartY;
    private int mLastY;
    private int mScrollY=50;

    private OnLoadMoreListener mLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onListener){
        this.mLoadMoreListener = onListener;
    }

    public ListViewMore(Context context) {
        super(context);
        init(context);
    }
    public ListViewMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        footer = LayoutInflater.from(getContext()).inflate(R.layout.list_ui_footer,null);
        bar = (ProgressBar) footer.findViewById(R.id.load_more_bar);
        tvMsg = (TextView) footer.findViewById(R.id.load_more_msg);
        bar.setVisibility(View.GONE);
        addFooterView(footer, null, false);
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (mStartY-mLastY>mScrollY && loading){
                    //上滑
                    if (mLoadMoreListener!=null){
                        mLoadMoreListener.onLoad();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
    //
    public void isLoadingMore(){
        bar.setVisibility(VISIBLE);
        tvMsg.setText("正在加载...");
    }
    //加载结束...
    public void setLoading(boolean isl){
        if (isl){
            tvMsg.setText("上拉加载更多");
        }else {
            loading = false;
            tvMsg.setText("已经全部加载完毕");
        }
        bar.setVisibility(GONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
       if (visibleItemCount==totalItemCount){
           loading = true;
       }else {
           loading = false;
       }
    }

    public interface OnLoadMoreListener{
        void onLoad();
    }
}
