package com.procuratorate.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Qing on 2016/8/12.
 */
public class CarMsg implements Parcelable{
    public String id;
    public String carbrandnum;
    public String jingdu;
    public String weidu;
    public String localtion;
    public String locationdate;
    public String personname;

    protected CarMsg(Parcel in) {
        id = in.readString();
        carbrandnum = in.readString();
        jingdu = in.readString();
        weidu = in.readString();
        localtion = in.readString();
        locationdate = in.readString();
        personname = in.readString();
    }

    public static final Creator<CarMsg> CREATOR = new Creator<CarMsg>() {
        @Override
        public CarMsg createFromParcel(Parcel in) {
            return new CarMsg(in);
        }

        @Override
        public CarMsg[] newArray(int size) {
            return new CarMsg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(carbrandnum);
        dest.writeString(jingdu);
        dest.writeString(weidu);
        dest.writeString(localtion);
        dest.writeString(locationdate);
        dest.writeString(personname);
    }
}
