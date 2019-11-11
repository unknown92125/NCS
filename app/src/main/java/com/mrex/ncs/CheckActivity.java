package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CheckActivity extends AppCompatActivity {

    private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice;
    private String date, time, address, minute;
    private Intent intent;
    private int area, hour, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvAddress = findViewById(R.id.tv_address);
        tvArea = findViewById(R.id.tv_area);
        tvExpectedTime = findViewById(R.id.tv_expected_time);
        tvPrice = findViewById(R.id.tv_price);

        address = AddressActivity.fullAddress;
        area = AddressActivity.area;

        intent = getIntent();
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

        hour = (area * 10) / 60;
        minute = (area * 10) % 60 + "";
        tvExpectedTime.setText(hour + "시간 " + minute + "분");

        price = area * 1000;
        if (price < 20000) {
            price = 20000;
        }
        tvPrice.setText(String.format("%,d", price) + "원");


        tvDate.setText(date);
        tvTime.setText(time);
        tvAddress.setText(address);
        tvArea.setText(area + "평");


    }

    public void back(View view) {
        finish();
    }

    public void next(View view) {
    }
}
