package com.procuratorate.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.bean.PullMsg;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Qing on 2016/9/1.
 */
public class PullMsgAdapter extends BaseListAdapter<PullMsg> {
    private LayoutInflater inflater;
    public PullMsgAdapter(Activity context) {
        super(context);
        this.inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.item_pull_msg,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        PullMsg item = mList.get(position);
        if (item!=null){
        }
        holder.tvPullData.setText(item.pushdate);
        holder.tvPullMsg.setText(item.pushmsg);

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.tv_pull_data)
        TextView tvPullData;
        @Bind(R.id.tv_pull_msg)
        TextView tvPullMsg;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
