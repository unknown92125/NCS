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

import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userUID;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private final int RC_SIGN_IN = 5001;

    private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice, tvPhone;
    private String date, time, address, minute, expectedTime, payPrice, payMethod, payDate, phone;
    private String depositName = "noValue";
    private Intent intent;
    private int area, hour, price;
    private EditText etDepositName;

    private DatabaseReference reservationRef;
    private RadioGroup radioGroup;
    private LinearLayout llDepositName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.check_title));

        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvAddress = findViewById(R.id.tv_address);
        tvArea = findViewById(R.id.tv_area);
        tvExpectedTime = findViewById(R.id.tv_expected_time);
        tvPrice = findViewById(R.id.tv_price);
        tvPhone = findViewById(R.id.tv_phone);
        radioGroup = findViewById(R.id.rg);
        etDepositName = findViewById(R.id.et_deposit_name);
        llDepositName = findViewById(R.id.ll_deposit_name);
        findViewById(R.id.bt_next_check).setOnClickListener(this);

        address = AddressActivity.fullAddress;
        area = AddressActivity.area;
        phone = AddressActivity.phone;

        tvPhone.setText(phone);

        intent = getIntent();
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

        hour = (area * 10) / 60;
        minute = (area * 10) % 60 + "";
        expectedTime = hour + "시간 " + minute + "분";
        tvExpectedTime.setText(expectedTime);

        price = area * 1000;
        if (price < 20000) {
            price = 20000;
        }
        payPrice = String.format("%,d", price) + "원";
        tvPrice.setText(payPrice);

        tvDate.setText(date);
        tvTime.setText(time);
        tvAddress.setText(address);
        tvArea.setText(area + "평");

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault());
        payDate = sdfDate.format(Calendar.getInstance().getTimeInMillis());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                payMethod = rb.getText().toString();
                if (checkedId == R.id.rb_deposit) {
                    llDepositName.setVisibility(View.VISIBLE);
                } else {
                    llDepositName.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                uploadReservationDB();
                pushReservationFM();
                Toast.makeText(this, "예약이 완료되었습니다", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "예약이 취소되었습니다", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void uploadReservationDB() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        Log.e("CheckA:", userUID);
        reservationRef = rootRef.child("reservations").child(userUID).push();

        Reservation reservation = new Reservation(address, phone, area + "평", date, time, expectedTime, payMethod, payDate, payPrice, depositName);
        reservationRef.setValue(reservation);

        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_next_check) {

            if (!isSignedIn) {
                startActivityForResult(new Intent(this, SignInActivity.class), RC_SIGN_IN);
                return;

            } else {
                if (payMethod.equals("무통장입금")) {
                    if (etDepositName.length() == 0) {
                        Toast.makeText(this, "입금자명을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    depositName = etDepositName.getText().toString();
                }

                uploadReservationDB();
                pushReservationFM();
                Toast.makeText(this, "예약 완료", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pushReservationFM() {
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
                Log.e("CheckA:", "requestQueue onErrorResponse");
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
