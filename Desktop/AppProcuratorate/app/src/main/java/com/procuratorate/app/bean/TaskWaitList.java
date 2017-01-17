package com.procuratorate.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Qing on 2016/8/15.
 */
public class TaskWaitList implements Parcelable{
    public String id;
    public String reqno;
    public String applyperson;
    public String applydate;
    public String usecardep;
    public String usecarstartdate;
    public String usecarenddate;
    public String beginlocaltion;
    public String destination;
    public String bycarnum;
    public String mobile;
    public String yongcheshiyou;
    public String caryaoqiu;
    public String mark;

    protected TaskWaitList(Parcel in) {
        id = in.readString();
        reqno = in.readString();
        applyperson = in.readString();
        applydate = in.readString();
        usecardep = in.readString();
        usecarstartdate = in.readString();
        usecarenddate = in.readString();
        beginlocaltion = in.readString();
        destination = in.readString();
        bycarnum = in.readString();
        mobile = in.readString();
        yongcheshiyou = in.readString();
        caryaoqiu = in.readString();
        mark = in.readString();
    }

    public static final Creator<TaskWaitList> CREATOR = new Creator<TaskWaitList>() {
        @Override
        public TaskWaitList createFromParcel(Parcel in) {
            return new TaskWaitList(in);
        }

        @Override
        public TaskWaitList[] newArray(int size) {
            return new TaskWaitList[size];
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
        dest.writeString(applyperson);
        dest.writeString(applydate);
        dest.writeString(usecardep);
        dest.writeString(usecarstartdate);
        dest.writeString(usecarenddate);
        dest.writeString(beginlocaltion);
        dest.writeString(destination);
        dest.writeString(bycarnum);
        dest.writeString(mobile);
        dest.writeString(yongcheshiyou);
        dest.writeString(caryaoqiu);
        dest.writeString(mark);
    }
}
