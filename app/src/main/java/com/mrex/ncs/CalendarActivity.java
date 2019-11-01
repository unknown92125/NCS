package com.mrex.ncs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);





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
    }
}
