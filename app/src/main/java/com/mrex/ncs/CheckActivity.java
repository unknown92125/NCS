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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userUID;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private String date, address, payPrice, cleanType, payOption, payDate, phone;
    private String payName = "0";
    private int area;
    private EditText etPayName;

    private LinearLayout llPayName;
    private SimpleDateFormat sdfDate;

    private BillingClient billingClient;
    private SkuDetails skuDetails;
    private String cleaningPay = "청소예약", cleaningPrice;

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

//    {
//
//
//
//    private void googlePayBilling() {
//
//        billingClient = BillingClient.newBuilder(this).setListener(this).build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(BillingResult billingResult) {
//                if (billingResult.getResponseCode() == OK) {
//                    // The BillingClient is ready. You can query purchases here.
//
//                    List<String> skuList = new ArrayList<>();
//                    skuList.add(cleaningPay);
//                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
//                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
//                    billingClient.querySkuDetailsAsync(params.build(),
//                            new SkuDetailsResponseListener() {
//                                @Override
//                                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
//                                    // Process the result.
//                                    if (billingResult.getResponseCode() == OK && skuDetailsList != null) {
//                                        for (SkuDetails skuDetails : skuDetailsList) {
//                                            String sku = skuDetails.getSku();
//                                            String price = skuDetails.getPrice();
//
//                                            if (cleaningPay.equals(sku)) {
//                                                cleaningPrice = price;
//                                            }
//                                        }
//                                    }
//                                }
//                            });
//
//                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
//                            .setSkuDetails(skuDetails)
//                            .build();
//                    BillingResult responseCode = billingClient.launchBillingFlow(CheckActivity.this, flowParams);
//
//                }
//            }
//
//
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        });
//
//
//    }
//
//    void handlePurchase(Purchase purchase) {
//        if (purchase.getPurchaseState() == PURCHASED) {
//            // Grant entitlement to the user.
//
//            ConsumeParams consumeParams =
//                    ConsumeParams.newBuilder()
//                            .setPurchaseToken(purchase.getPurchaseToken())
//                            .build();
//            billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
//                @Override
//                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
//                    if (billingResult.getResponseCode() == OK) {
//                        // Handle the success of the consume operation.
//                        // For example, increase the number of coins inside the user's basket.
//                    }
//                }
//            });
//        }
//
//    }
//
//    @Override
//    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
//        if (billingResult.getResponseCode() == OK
//                && purchases != null) {
//            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
//            }
//        } else if (billingResult.getResponseCode() == USER_CANCELED) {
//            // Handle an error caused by a user cancelling the purchase flow.
//        } else {
//            // Handle any other error codes.
//        }
//    }
//    }

}
