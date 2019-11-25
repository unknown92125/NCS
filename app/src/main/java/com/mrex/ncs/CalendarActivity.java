package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {


    private Calendar calendar;
    private Long minMaxDate;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private Long dateMilli, timeMilli;
    private String date, time;
    private SimpleDateFormat sdfDate, sdfTime;
    private int year, month, day, dayOfWeek, hour, minute;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.calendar_title));

        calendarView = findViewById(R.id.calendar_view);
        timePicker = findViewById(R.id.tp);

        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        minMaxDate = calendar.getTimeInMillis();
        calendarView.setMinDate(minMaxDate);
        calendar.add(Calendar.MONTH, 1);
        minMaxDate = calendar.getTimeInMillis();
        calendarView.setMaxDate(minMaxDate);

        sdfDate = new SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault());
        sdfTime = new SimpleDateFormat("a h : mm", Locale.getDefault());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        dateMilli = calendar.getTimeInMillis();
        date = sdfDate.format(dateMilli);
        time = sdfTime.format(dateMilli);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

                year = i;
                month = i1;
                day = i2;

                calendar.set(i, i1, i2);
                dateMilli = calendar.getTimeInMillis();
//                date = sdfDate.format(new Date(dateMilli));
                date = sdfDate.format(dateMilli);
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                hour = i;
                minute = i1;

//                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                timeMilli = calendar.getTimeInMillis();
                time = sdfTime.format(timeMilli);
//                time = sdfTime.format(new Date(timeMilli));
                Log.e("CalendarA:date and time", time);
            }
        });


    }

    public void next(View view) {

        Calendar cal=Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        Long dateMilli = cal.getTimeInMillis();
        sdfDate = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault());
        String date=sdfDate.format(dateMilli);

        intent = new Intent(this, CheckActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
