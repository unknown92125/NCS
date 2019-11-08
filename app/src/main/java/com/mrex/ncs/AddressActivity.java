package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class AddressActivity extends AppCompatActivity {

    static String fullAddress;

    private ViewGroup mapViewContainer;

    private MapView map;
    private Intent intent;
    private TextView tvAddress1;
    private String address1;
    private EditText etAddress2;
    private String placeName;
    private double lat;
    private double lng;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address);

        mapViewContainer = findViewById(R.id.map_view);

        intent = getIntent();
        address1 = intent.getStringExtra("address");
        placeName = intent.getStringExtra("place");
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);

        tvAddress1 = findViewById(R.id.tv_address1);
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
        Log.e("TAG", "AA onResume");
        map = new MapView(this);
        mapViewContainer.addView(map);
        map.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng)));
        Log.e("TAG", lat + "   " + lng);
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
        Log.e("TAG", "AA onPause");
        mapViewContainer.removeView(map);
        map = null;
    }

    public void back(View view) {
        finish();
    }

    public void next(View view) {
        fullAddress = etAddress2.getText().toString();
        startActivity(new Intent(this, CalendarActivity.class));
    }
}
