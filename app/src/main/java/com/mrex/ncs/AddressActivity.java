package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class AddressActivity extends AppCompatActivity {

    static String fullAddress, phone;
    static int area;

    private ViewGroup mapViewContainer;

    private MapView map;
    private String address1, placeName;
    private EditText etAddress2, etArea, etPhone1, etPhone2, etPhone3;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.address_title));

        mapViewContainer = findViewById(R.id.map_view);
        etArea = findViewById(R.id.et_area);
        etPhone1 = findViewById(R.id.et_phone_1);
        etPhone2 = findViewById(R.id.et_phone_2);
        etPhone3 = findViewById(R.id.et_phone_3);

        Intent intent = getIntent();
        address1 = intent.getStringExtra("address");
        placeName = intent.getStringExtra("place");
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);

        TextView tvAddress1 = findViewById(R.id.tv_address1);
        etAddress2 = findViewById(R.id.et_address2);

        tvAddress1.setText(address1 + placeName);
//
//        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        etAddress2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i== EditorInfo.IME_ACTION_DONE)
//                return false;
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        map = new MapView(this);
        mapViewContainer.addView(map);
        map.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng)));
        Log.e("AddressA", lat + "   " + lng);
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeView(map);
        map = null;
    }

    public void next(View view) {
        if (etAddress2.length() == 0) {
            Toast.makeText(this, "상세주소를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;

        } else if (etArea.length() == 0) {
            Toast.makeText(this, "면적을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;

        } else if (etPhone1.length() == 0 || etPhone2.length() == 0 || etPhone3.length() == 0) {
            Toast.makeText(this, "연락처를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;

        }

        phone = etPhone1.getText().toString() + "-" + etPhone2.getText().toString() + "-" + etPhone3.getText().toString();
        fullAddress = address1 + placeName + " " + etAddress2.getText().toString();
        area = Integer.parseInt(etArea.getText().toString());
        Log.e("AddressA:fullAddress: ", fullAddress);
        Log.e("AddressA:area: ", area + "");
        startActivity(new Intent(this, CalendarActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
