package com.procuratorate.app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.bean.OrderListBean;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Qing on 2016/8/8.
 */
public class WaitExecuteAdapter extends  BaseListAdapter<OrderListBean> {
    private LayoutInflater inflater;
    public WaitExecuteAdapter(Activity context) {
        super(context);
        this.inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.item_wait_dispatch,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        OrderListBean item = mList.get(position);
        if (item!=null){
            holder.tvCarDepartment.setText("用车部门："+item.usecardep);
            holder.tvCarUseTime.setText("开始时间："+item.usecarstartdate);
            if (item.status!=null){
                if (item.status.equals("0")){
                    holder.tvState.setVisibility(View.VISIBLE);
                    holder.tvState.setText("未执行");
                    holder.tvState.setTextColor(Color.RED);
                }else if (item.status.equals("1")){
                    holder.tvState.setText("执行中");
                    holder.tvState.setTextColor(Color.GREEN);
                    holder.tvState.setVisibility(View.VISIBLE);
                }else {
                    holder.tvState.setVisibility(View.GONE);
                }
            }

        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_car_department)
        TextView tvCarDepartment;
        @Bind(R.id.tv_car_use_time)
        TextView tvCarUseTime;
        @Bind(R.id.tv_dispatch_state)
        TextView tvState;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
