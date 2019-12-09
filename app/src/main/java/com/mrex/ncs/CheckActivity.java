package com.mrex.ncs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.authorization.authcode.KakaoWebViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userUID;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static String KAKAO_HOST = "https://kapi.kakao.com";

    private String date, address, payPrice, cleanType, payOption, payDate, phone;
    private String payName = "0";
    private int area, intPrice;
    private EditText etPayName;

    private RadioButton rbGooglePay;

    private LinearLayout llPayName;
    private SimpleDateFormat sdfDate;

    private PaymentsClient paymentsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ab_check_title));

        paymentsClient = createPaymentsClient(this);

        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvArea = findViewById(R.id.tv_area);
        TextView tvCleanType = findViewById(R.id.tv_clean_type);
        TextView tvPrice = findViewById(R.id.tv_price);
        TextView tvPhone = findViewById(R.id.tv_phone);
        RadioGroup radioGroup = findViewById(R.id.rg);
        etPayName = findViewById(R.id.et_pay_name);
        llPayName = findViewById(R.id.ll_pay_name);
        rbGooglePay = findViewById(R.id.rb_google_pay);

        findViewById(R.id.bt_next_check).setOnClickListener(this);

        address = AddressActivity.fullAddress;
        area = AddressActivity.area;
        phone = AddressActivity.phone;
        cleanType = HomeFragment.cleanType;

        tvPhone.setText(phone);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        intPrice = area * 1000;
        if (intPrice < 20000) {
            intPrice = 20000;
        }
        payPrice = String.format("%,d", intPrice) + "원";
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            possiblyShowGooglePayButton();
        } else {
            rbGooglePay.setVisibility(View.GONE);
        }

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
//                kakaoPayReady();
            }
            if (payOption.equals("구글페이")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    requestPayment();
                }
            }
            //결제 테스트후 결제완료시 실행
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

    // 구글 /////////////////////////////////////////////////////////////////////////////////////////////////


    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    //TODO change gateway and id
    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {
                {
                    put("gateway", "example");
                    put("gatewayMerchantId", "exampleGatewayMerchantId");
                }
            });
        }};
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }


    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }


    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            Log.e("CheckA", "isReadyToPay success: " + task.getResult());
                            rbGooglePay.setVisibility(View.VISIBLE);
//                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.e("isReadyToPay failed", String.valueOf(task.getException()));
                            rbGooglePay.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", "KR");
        transactionInfo.put("currencyCode", "KRW");

        return transactionInfo;
    }


    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Example Merchant");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<JSONObject> getPaymentDataRequest(String price) {
        try {
            JSONObject paymentDataRequest = getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", getMerchantInfo());

//      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
//      JSON object. */
//            paymentDataRequest.put("shippingAddressRequired", true);
//
//            JSONObject shippingAddressParameters = new JSONObject();
//            shippingAddressParameters.put("phoneNumberRequired", false);
//
//            JSONArray allowedCountryCodes = new JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES);
//
//            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
//            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
            return Optional.of(paymentDataRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("CheckA", "RESULT_OK");
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("CheckA", "RESULT_CANCELED");
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Log.e("CheckA", "RESULT_ERROR");
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

//                // Re-enables the Google Pay payment button.
//                mGooglePayButton.setClickable(true);
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        Log.e("CheckA", "handlePaymentSuccess");
        Log.e("CheckA", paymentData.toJson());
        String paymentInformation = paymentData.toJson();
        Log.e("CheckA", paymentInformation);

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

//            String billingName =
//                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
//            Log.d("BillingName", billingName);
//            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG)
//                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     *                   WalletConstants.ERROR_CODE_* constants.
     * @see <a
     * href="https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#constant-summary">
     * Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    // This method is called when the Pay with Google button is clicked.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestPayment() {
        // Disables the button to prevent multiple clicks.

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.

        String price = "1";
//        String price = intPrice + "";


        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }



    // 카카오 /////////////////////////////////////////////////////////////////////////////////////////////////

    private void kakaoPayReady() {
        String addressUrl = KAKAO_HOST + "/v1/payment/ready";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addressUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("CheckA", "onResponse :" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String a = (String) jsonObject.get("next_redirect_app_url");
                    Log.e("CheckA", "next_redirect_app_url :" + a);

                    Intent intent = new Intent(CheckActivity.this, KakaoWebViewActivity.class);
                    intent.putExtra("nextUrl", a);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                JSONParser parser = new JSONParser();
//                Object obj = parser.parse( jsonStr );
//                JSONObject jsonObj = (JSONObject) obj;


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckA", "onErrorResponse :" + error);
                Log.e("CheckA", "onErrorResponse :" + error.networkResponse.statusCode);
                Log.e("CheckA", "onErrorResponse :" + error.networkResponse.headers);
                Log.e("CheckA", "onErrorResponse :" + error.networkResponse);
                Log.e("CheckA", "onErrorResponse :" + error.getLocalizedMessage());
                Log.e("CheckA", "onErrorResponse :" + error.getMessage());
                Log.e("CheckA", "onErrorResponse :" + error.getCause());
                Log.e("CheckA", "onErrorResponse :" + error.toString());

                try {
                    String body = new String(error.networkResponse.data, "UTF-8");
                    Log.e("CheckA", "onErrorResponse :" + body);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Authorization", "KakaoAK " + "dea3ac879015f4932333c8fbb75520a8");
//                params.put("Authorization", "KakaoAK " + getString(R.string.kakao_admin_key));
                headerParams.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return headerParams;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME");
                params.put("partner_order_id", "partner_order_id");
                params.put("partner_user_id", "partner_user_id");
                params.put("item_name", "초코파이");
                params.put("quantity", "1");
                params.put("total_amount", "2200");
                params.put("vat_amount", "200");
                params.put("tax_free_amount", "0");
//                params.put("approval_url", "https://developers.kakao.com/success");
//                params.put("cancel_url", "https://developers.kakao.com/cancel");
//                params.put("fail_url", "https://developers.kakao.com/fail");
                params.put("approval_url", "http://localhost:8080/kakaoPaySuccess");
                params.put("fail_url", "http://localhost:8080/kakaoPaySuccessFail");
                params.put("cancel_url", "http://localhost:8080/kakaoPayCancel");
                return params;
            }
        };

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addressUrl, null, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e("CheckA", "onResponse :" + response.toString());
//                Log.e("CheckA", "onResponse :" + response);
////                parsingAddress(response);
//            }
//
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("CheckA", "onErrorResp:" + error.toString());
//                Log.e("CheckA", "onErrorResp:" + error.networkResponse.statusCode);
//                Log.e("CheckA", "onErrorResp:" + error.networkResponse.data);
//                Log.e("CheckA", "onErrorResp:" + error.networkResponse.headers);
//                Log.e("CheckA", "onErrorResp:" + error.getMessage());
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map headerParams = new HashMap();
//                headerParams.put("Authorization", "KakaoAK " + "dea3ac879015f4932333c8fbb75520a8");
////                params.put("Authorization", "KakaoAK " + getString(R.string.kakao_admin_key));
//                headerParams.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
////                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//                return headerParams;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map params = new HashMap();
//                params.put("cid", "TC0ONETIME");
//                params.put("partner_order_id", "partner_order_id");
//                params.put("partner_user_id", "partner_user_id");
//                params.put("item_name", "초코파이");
//                params.put("quantity", "1");
//                params.put("total_amount", "2200");
//                params.put("vat_amount", "200");
//                params.put("tax_free_amount", "0");
//                params.put("approval_url", "https://developers.kakao.com/success");
//                params.put("cancel_url", "https://developers.kakao.com/cancel");
//                params.put("fail_url", "https://developers.kakao.com/fail");
////                params.put("approval_url", "http://localhost:8080/kakaoPaySuccess");
////                params.put("fail_url", "http://localhost:8080/kakaoPaySuccessFail");
////                params.put("cancel_url", "http://localhost:8080/kakaoPayCancel");
//                return params;
//            }
//        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}
