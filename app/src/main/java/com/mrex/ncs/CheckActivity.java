package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CheckActivity extends AppCompatActivity {

    private TextView tvDate, tvTime, tvAddress, tvArea;
    private String date, time, address, area;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvAddress = findViewById(R.id.tv_address);
        tvArea = findViewById(R.id.tv_area);

        intent = getIntent();
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

        tvDate.setText(date);
        tvTime.setText(time);

    }

    public void back(View view) {
        finish();
    }

    public void next(View view) {
    }
}
