package com.procuratorate.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.procuratorate.app.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Qing on 2016/9/18.
 */
public class DataPickerView extends LinearLayout{
    private String lable;//单位
    private NumberPicker npYear ;
    private NumberPicker npMonth ;
    private NumberPicker npDay ;
    private NumberPicker npHour ;
    private NumberPicker npMinute ;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private ArrayList<Integer> dataYear = new ArrayList<>();
    private int mill;


    public DataPickerView(Context context) {
        super(context);
        init(context);
    }
    public DataPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        mill = c.get(Calendar.MILLISECOND);
        View view = View.inflate(context, R.layout.data_choice, null);
        npYear = (NumberPicker) view.findViewById(R.id.np_year);
        npMonth = (NumberPicker) view.findViewById(R.id.np_mouth);
        npDay = (NumberPicker) view.findViewById(R.id.np_day);
        npHour = (NumberPicker) view.findViewById(R.id.np_hour);
        npMinute = (NumberPicker) view.findViewById(R.id.np_minute);
        initData();
        initChange();
        addView(view);
    }

    private void initChange() {
        npYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                initDay();
            }
        });

        npMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                initDay();
            }
        });
    }

    public String dataTime(){
        return npYear.getValue()+"-"+npMonth.getValue()+"-"+npDay.getValue()+
                " "+npHour.getValue()+":"+npMinute.getValue()+":"+mill/17;
    }

    private void initData() {
        npYear.setMaxValue(2200);
        npYear.setMinValue(2000);
        npYear.setValue(year);

        npMonth.setMaxValue(12);
        npMonth.setMinValue(1);
        npMonth.setValue(month+1);

        npDay.setMinValue(1);
        initDay();
        npDay.setValue(day);

        npHour.setMaxValue(23);
        npHour.setMinValue(0);
        npHour.setValue(hour);

        npMinute.setMaxValue(59);
        npMinute.setMinValue(0);
        npMinute.setValue(minute);
    }
    private boolean runN = false;

    private void initDay() {
        year = npYear.getValue();
        month = npMonth.getValue();
        if (year%400==0&&year%4==0){
            //2yue29天
            runN = true;
        }else {
            runN = false;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                npDay.setMaxValue(31);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                npDay.setMaxValue(30);
                break;
            case 2:
                if (runN){
                    npDay.setMaxValue(29);
                }else {
                    npDay.setMaxValue(28);
                }
                break;

        }
    }



}
