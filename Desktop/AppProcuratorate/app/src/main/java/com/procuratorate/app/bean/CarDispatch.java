package com.procuratorate.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Qing on 2016/8/15.
 */
public class CarDispatch implements Parcelable{
    public String id;
    public String userid;
    public String reqno;
    public String carbrandnum;
    public String anpaisiji;
    public String mobile;
    public String bycarnum;
    public int status;

    public CarDispatch() {
    }

    protected CarDispatch(Parcel in) {
        id = in.readString();
        userid = in.readString();
        reqno = in.readString();
        carbrandnum = in.readString();
        anpaisiji = in.readString();
        mobile = in.readString();
        bycarnum = in.readString();
        status = in.readInt();
    }

    public static final Creator<CarDispatch> CREATOR = new Creator<CarDispatch>() {
        @Override
        public CarDispatch createFromParcel(Parcel in) {
            return new CarDispatch(in);
        }

        @Override
        public CarDispatch[] newArray(int size) {
            return new CarDispatch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userid);
        dest.writeString(reqno);
        dest.writeString(carbrandnum);
        dest.writeString(anpaisiji);
        dest.writeString(mobile);
        dest.writeString(bycarnum);
        dest.writeInt(status);
    }
}
