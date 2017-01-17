package com.procuratorate.app.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.procuratorate.app.R;

/**
 * Created by Qing on 2016/8/16.
 */
public class BaseFragment  extends Fragment{

//    public void showMessage(String msg){
//        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
//    }

    public void showMessage(String msg){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.show_msg,null);
        Toast toast = new Toast(getContext());
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_show_msg);
        tvMsg.setText(msg+"");
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
