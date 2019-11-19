package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CheckActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 5001;

    private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice, tvPhone;
    private String userID, date, time, address, minute, expectedTime, payPrice, payMethod, payDate, phone;
    private String depositName = "needDepositName";
    private Intent intent;
    private int area, hour, price;
    private EditText etDepositName;

    private SharedPreferences sf;

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

    public void next(View view) {
        sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        String userName = sf.getString("userName", "needSignIn");

        if (userName.equals("needSignIn")) {
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

            userID = sf.getString("userID", "needSignIn");
            uploadReservationDB();
            pushFM();
            Toast.makeText(this, "예약 완료", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                userID = sf.getString("userID", "needSignIn");
                uploadReservationDB();
                Toast.makeText(this, "예약 완료", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "예약 취소", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void uploadReservationDB() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        reservationRef = rootRef.child("reservations").child(userID).push();

        Reservation reservation = new Reservation(address, phone, area + "평", date, time, expectedTime, payMethod, payDate, "N", payPrice, depositName);
        reservationRef.setValue(reservation);

        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        startActivity(new Intent(this, HomeActivity.class));
//        finish();
        finishAffinity();

    }

    private void pushFM() {
        String serverUrl = "http://ncservices.dothome.co.kr/pushFM.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("MainA:", "requestQueue onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainA:", "requestQueue onErrorResponse");
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
