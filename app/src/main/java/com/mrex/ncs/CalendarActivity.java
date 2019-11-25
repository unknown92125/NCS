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

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.calendar_title));

        CalendarView calendarView = findViewById(R.id.calendar_view);
        TimePicker timePicker = findViewById(R.id.tp);

        Calendar calendar = Calendar.getInstance();

        //날짜선택은 익일부터 다음달까지만 가능
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long minMaxDate = calendar.getTimeInMillis();
        calendarView.setMinDate(minMaxDate);
        calendar.add(Calendar.MONTH, 1);
        minMaxDate = calendar.getTimeInMillis();
        calendarView.setMaxDate(minMaxDate);

        //기본값은 익일 현재 시간으로 설정
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

                year = i;
                month = i1;
                day = i2;
                Log.e("CalendarA", "  " + year + "  " + month + "  " + day);
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                hour = i;
                minute = i1;
                Log.e("CalendarA", "  " + hour + "  " + minute);
            }
        });
    }

    public void next(View view) {

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        Long dateMilli = cal.getTimeInMillis();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault());
        String date = sdfDate.format(dateMilli);
        Log.e("CalendarA", date);

        Intent intent = new Intent(this, CheckActivity.class);
        intent.putExtra("date", date);
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
