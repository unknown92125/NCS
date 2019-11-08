package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {


    private Calendar calendar;
    private Long minMaxDate;
    private CalendarView calendarView;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendar_view);
        timePicker = findViewById(R.id.tp);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        minMaxDate=calendar.getTimeInMillis();
        calendarView.setMinDate(minMaxDate);
        calendar.add(Calendar.MONTH,1);
        minMaxDate=calendar.getTimeInMillis();
        calendarView.setMaxDate(minMaxDate);


    }

//    public class EventDecorator implements DayViewDecorator {
//
//        private final int color;
//        private final HashSet<CalendarDay> dates;
//
//        public EventDecorator(int color, Collection<CalendarDay> dates) {
//            this.color = color;
//            this.dates = new HashSet<>(dates);
//        }
//
//        @Override
//        public boolean shouldDecorate(CalendarDay day) {
//            return dates.contains(day);
//        }
//
//        @Override
//        public void decorate(DayViewFacade view) {
//            view.addSpan(new DotSpan(5, color));
//        }
//    }//EventDecorator

    public void back(View view) {
        finish();
    }

    public void next(View view) {
        String date;
        int year, month, day, hour, min;

        calendarView.getDate();

        startActivity(new Intent(this, CheckActivity.class));
    }
}
