package com.procuratorate.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.bean.ApplyItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Qing on 2016/9/12.
 */
public class ApplyAdapter extends BaseListAdapter<ApplyItem> {
    private LayoutInflater inflater;
    public ApplyAdapter(Activity context) {
        super(context);
        this.inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.item_apply_list,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ApplyItem item = mList.get(position);
        if (item!=null){
            holder.tvApplyDep.setText(item.depname);
            holder.tvApplyPerson.setText(item.personname);
            holder.tvApplyTime.setText(item.applydate);
            holder.tvReqNo.setText(item.reqno);
            switch (item.dingdanStatus){
                case 1:
                    holder.tvApplyState.setText("待审核");
                    break;
                case 2:
                    holder.tvApplyState.setText("订单撤销");
                    break;
                case 3:
                    holder.tvApplyState.setText("审核中");
                    break;
                case 4:
                    holder.tvApplyState.setText("审核通过");
                    break;
                case 5:
                    holder.tvApplyState.setText("审核驳回");
                    break;
                case 6:
                    holder.tvApplyState.setText("任务下达");
                    break;
                case 7:
                    holder.tvApplyState.setText("行程进行中");
                    break;
                case 8:
                    holder.tvApplyState.setText("订单结束");
                    break;
                default:
                    holder.tvApplyState.setVisibility(View.GONE);
                    break;
            }

        }
        return convertView;
    }

    static class ViewHolder{
        @Bind(R.id.tv_apply_time)
        TextView tvApplyTime;
        @Bind(R.id.tv_apply_state)
        TextView tvApplyState;
        @Bind(R.id.tv_apply_person)
        TextView tvApplyPerson;
        @Bind(R.id.tv_apply_dep)
        TextView tvApplyDep;
        @Bind(R.id.tv_reqno)
        TextView tvReqNo;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
