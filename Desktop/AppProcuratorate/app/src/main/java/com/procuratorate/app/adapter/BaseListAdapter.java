package com.procuratorate.app.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qing on 2016/8/8.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> mList;

    protected Activity mContext;

    protected ListView mListView;

    public BaseListAdapter(Activity context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public abstract View getView(int position, View convertView, ViewGroup parent);

    //添加数据
    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }
    //清楚数据 清空
    public void clear() {
        this.mList = null;
        notifyDataSetChanged();
    }

    //添加数据 补充
    public void addList(List<T> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }
    public List<T> getList() {
        return mList;
    }
    public void setList(T[] list) {
        List<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setList(arrayList);
    }
    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }
    //删除指定数据
    public void remove(int position) {
        if (mList != null&&mList.size()>0) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }
}
