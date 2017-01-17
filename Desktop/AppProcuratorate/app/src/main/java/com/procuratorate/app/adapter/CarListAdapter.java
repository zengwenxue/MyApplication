package com.procuratorate.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procuratorate.app.R;
import com.procuratorate.app.bean.CarDetails;
import com.procuratorate.app.bean.CarMsg;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Qing on 2016/8/9.
 */
public class CarListAdapter extends BaseListAdapter<CarMsg> {
    private LayoutInflater inflater;
    public CarListAdapter(Activity context) {
        super(context);
        this.inflater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.item_car_list,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        CarMsg item = mList.get(position);
        if (item!=null){
            holder.carLocation.setText("位置："+item.localtion);
            holder.carNum.setText("车牌："+item.carbrandnum);
            holder.carPeople.setText("司机："+item.personname);
        }

        return convertView;
    }

    static class ViewHolder{
        @Bind(R.id.tv_car_num)
        TextView carNum;
        @Bind(R.id.tv_car_people)
        TextView carPeople;
        @Bind(R.id.tv_car_location)
        TextView carLocation;
        public ViewHolder(View v){
            ButterKnife.bind(this,v);
        }
    }
}
