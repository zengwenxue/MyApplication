package com.procuratorate.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Qing on 2016/8/9.
 */
public class OrderListBean implements Parcelable{
    public String id;
    public String reqno;
    public String usecardep;
    public String usecarstartdate;
    public String usecarenddate;
    public String status;


    protected OrderListBean(Parcel in) {
        id = in.readString();
        reqno = in.readString();
        usecardep = in.readString();
        usecarstartdate = in.readString();
        usecarenddate = in.readString();
        status = in.readString();
    }

    public static final Creator<OrderListBean> CREATOR = new Creator<OrderListBean>() {
        @Override
        public OrderListBean createFromParcel(Parcel in) {
            return new OrderListBean(in);
        }

        @Override
        public OrderListBean[] newArray(int size) {
            return new OrderListBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(reqno);
        dest.writeString(usecardep);
        dest.writeString(usecarstartdate);
        dest.writeString(usecarenddate);
        dest.writeString(status);
    }
}
