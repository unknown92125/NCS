package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.mrex.ncs.SignInActivity.userID;

public class CheckActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 5001;

    private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice;
    private String date, time, address, minute;
    private Intent intent;
    private int area, hour, price;

    private DatabaseReference reservationRef;

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
        startActivityForResult(new Intent(this, SignInActivity.class), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
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
        reservationRef = rootRef.child("reservations").push();

        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Reservation reservation = new Reservation(userID, address, "01012345678", area + "평", date, time, "N");
                reservationRef.setValue(reservation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startActivity(new Intent(this, HomeActivity.class));
        finish();

    }
}
