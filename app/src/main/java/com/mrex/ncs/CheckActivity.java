package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.mrex.ncs.U.userUID;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private String date, address, expectedTime, payPrice, payMethod, payDate, phone;
    private String payName = "noValue";
    private int area;
    private EditText etPayName;

    private LinearLayout llPayName;
    private SimpleDateFormat sdfDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.check_title));

        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvArea = findViewById(R.id.tv_area);
        TextView tvExpectedTime = findViewById(R.id.tv_expected_time);
        TextView tvPrice = findViewById(R.id.tv_price);
        TextView tvPhone = findViewById(R.id.tv_phone);
        RadioGroup radioGroup = findViewById(R.id.rg);
        etPayName = findViewById(R.id.et_pay_name);
        llPayName = findViewById(R.id.ll_pay_name);
        findViewById(R.id.bt_next_check).setOnClickListener(this);

        address = AddressActivity.fullAddress;
        area = AddressActivity.area;
        phone = AddressActivity.phone;

        tvPhone.setText(phone);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        int hour = (area * 10) / 60;
        String minute = (area * 10) % 60 + "";
        expectedTime = hour + "시간 " + minute + "분";
        tvExpectedTime.setText(expectedTime);

        int price = area * 1000;
        if (price < 20000) {
            price = 20000;
        }
        payPrice = String.format("%,d", price) + "원";
        tvPrice.setText(payPrice);

        tvDate.setText(date);
        tvAddress.setText(address);
        tvArea.setText(area + "평");

        sdfDate = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault());


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                payMethod = rb.getText().toString();
                if (checkedId == R.id.rb_pay) {
                    llPayName.setVisibility(View.VISIBLE);
                } else {
                    llPayName.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_next_check) {
            if (payMethod.equals("무통장입금")) {
                if (etPayName.length() == 0) {
                    Toast.makeText(this, "입금자명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                payName = etPayName.getText().toString();
            }
            payDate = sdfDate.format(Calendar.getInstance().getTimeInMillis());
            uploadReservationDB();
            pushReservationFM();
        }
    }

    private void uploadReservationDB() {
        Log.e("CheckA", "uploadReservationDB");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference reservationRef = rootRef.child("reservations").child(userUID).push();

        Reservation reservation = new Reservation(address, date, phone, area + "평", expectedTime, payPrice, payMethod, payName, payDate);
        reservationRef.setValue(reservation);

        Toast.makeText(this, "예약이 완료되었습니다", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();

    }

    private void pushReservationFM() {
        Log.e("CheckA", "pushReservationFM");
        String serverUrl = "http://ncservices.dothome.co.kr/pushReservationFM.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("CheckA:", "requestQueue onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckA:", "requestQueue onErrorResponse:" + error);
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
