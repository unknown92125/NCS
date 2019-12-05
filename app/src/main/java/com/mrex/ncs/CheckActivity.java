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
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userUID;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private static String KAKAO_HOST = "https://kapi.kakao.com";

    private String date, address, payPrice, cleanType, payOption, payDate, phone;
    private String payName = "0";
    private int area;
    private EditText etPayName;

    private LinearLayout llPayName;
    private SimpleDateFormat sdfDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ab_check_title));

        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvArea = findViewById(R.id.tv_area);
        TextView tvCleanType = findViewById(R.id.tv_clean_type);
        TextView tvPrice = findViewById(R.id.tv_price);
        TextView tvPhone = findViewById(R.id.tv_phone);
        RadioGroup radioGroup = findViewById(R.id.rg);
        etPayName = findViewById(R.id.et_pay_name);
        llPayName = findViewById(R.id.ll_pay_name);
        findViewById(R.id.bt_next_check).setOnClickListener(this);

        address = AddressActivity.fullAddress;
        area = AddressActivity.area;
        phone = AddressActivity.phone;
        cleanType = HomeFragment.cleanType;

        tvPhone.setText(phone);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

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
                payOption = rb.getText().toString();
                if (checkedId == R.id.rb_pay) {
                    llPayName.setVisibility(View.VISIBLE);
                } else {
                    llPayName.setVisibility(View.GONE);
                }
            }
        });

        /////////////////////////////////////////////////////////////


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_next_check) {
            if (payOption.equals("무통장입금")) {
                if (etPayName.length() == 0) {
                    Toast.makeText(this, "입금자명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                payName = etPayName.getText().toString();
            }
            if (payOption.equals("카카오페이")) {
                kakaoPayReady();
            }
            //결제 테스트후 주석 제거
//            payDate = sdfDate.format(Calendar.getInstance().getTimeInMillis());
//            uploadReservationDB();
//            pushReservationFM();
        }
    }

    private void uploadReservationDB() {
        Log.e("CheckA", "uploadReservationDB");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference reservationRef = rootRef.child("reservations").child(userUID).push();

        Reservation reservation = new Reservation(userUID, userName, address, date, phone, area + "평", cleanType, payPrice, payOption, payName, payDate);
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
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("address", address);

//                Log.e("ChatA", "uploadToken:" + "userUID:" + userUID + "   userID:" + userID + "   userPW:" + userPW + "   userName:" + userName + "   userToken:" + userToken + "   userType:" + userType);

                return datas;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private void kakaoPayReady() {
        String addressUrl = KAKAO_HOST + "/v1/payment/ready";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addressUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("CheckA", "onResponse :" + response.toString());
//                parsingAddress(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckA", "onErrorResp:" + error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map params = new HashMap();
                params.put("Authorization", "KakaoAK " + "dea3ac879015f4932333c8fbb75520a8");
//                params.put("Authorization", "KakaoAK " + getString(R.string.kakao_admin_key));
                params.put("Content-Type", "application/json; charset=utf-8");
//                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map params = new HashMap();
                params.put("cid", "TC0ONETIME");
                params.put("partner_order_id", "partner_order_id");
                params.put("partner_user_id", "partner_user_id");
                params.put("item_name", "청소예약");
                params.put("quantity", "1");
                params.put("total_amount", "22000");
                params.put("tax_free_amount", "0");
                params.put("approval_url", "https://developers.kakao.com/success");
                params.put("cancel_url", "https://developers.kakao.com/cancel");
                params.put("fail_url", "https://developers.kakao.com/fail");
//                params.put("approval_url", "http://localhost:8080/kakaoPaySuccess");
//                params.put("cancel_url", "http://localhost:8080/kakaoPayCancel");
//                params.put("fail_url", "http://localhost:8080/kakaoPaySuccessFail");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}
