package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mrex.ncs.MainActivity.token;
import static com.mrex.ncs.U.userID;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userPW;
import static com.mrex.ncs.U.userToken;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private EditText etID, etPW, etPW2, etPhone, etVeriCode;
    private TextView tvID, tvPW, tvPW2, tvPhone, tvVeriCode;
    private String checkID, newID, newPW, phoneNum, verificationCode;
    private Boolean isDuplicateID = true, isRightPW = false, isSamePW = false, isRightPhone = false, isVerificated = false;
    private DatabaseReference idRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etID = findViewById(R.id.et_id);
        etPW = findViewById(R.id.et_pw);
        etPW2 = findViewById(R.id.et_pw2);
        etPhone = findViewById(R.id.et_phone);
        etVeriCode = findViewById(R.id.et_verification);
        tvID = findViewById(R.id.tv_id);
        tvPW = findViewById(R.id.tv_pw);
        tvPW2 = findViewById(R.id.tv_pw2);
        tvPhone = findViewById(R.id.tv_phone);
        tvVeriCode = findViewById(R.id.tv_verification);

        etID.setOnFocusChangeListener(this);
        etPW.setOnFocusChangeListener(this);
        etPW2.setOnFocusChangeListener(this);
        etPhone.setOnFocusChangeListener(this);
        etVeriCode.setOnFocusChangeListener(this);

        findViewById(R.id.bt_get_verification).setOnClickListener(this);
        findViewById(R.id.bt_sign_up).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.bt_get_verification) {
            PhoneVerify();
        }
        if (i == R.id.bt_sign_up) {
            signUp();
        }

    }

    private void checkDuplicateID() {
        if (etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16) {
            tvID.setText("영문 + 숫자 6 ~ 16자리");
            tvID.setTextColor(getResources().getColor(R.color.red));
            return;
        } else {
            newID = etID.getText().toString();
            Log.e("SignUpA", "newID:" + newID);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = firebaseDatabase.getReference();
            DatabaseReference userRef = rootRef.child("users");
            Log.e("SignUpA", "1");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount() == 0) {
                        Log.e("SignUpA", "if (dataSnapshot.getChildrenCount()==0) 사용가능한 아이디");
                        isDuplicateID = false;
                        tvID.setText("사용가능한 아이디입니다");
                        tvID.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.getValue() != null) {
                                User user = ds.getValue(User.class);

                                checkID = user.getId();
                                Log.e("SignUpA", "checkID" + checkID);
                                if (checkID.equals(newID)) {
                                    Log.e("SignUpA", "중복된 아이디");
                                    isDuplicateID = true;
                                    tvID.setText("중복된 아이디입니다");
                                    tvID.setTextColor(getResources().getColor(R.color.red));
                                    return;
                                } else {
                                    Log.e("SignUpA", "사용가능한 아이디");
                                    isDuplicateID = false;
                                    tvID.setText("사용가능한 아이디입니다");
                                    tvID.setTextColor(getResources().getColor(R.color.blue));
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SignUpA", "DatabaseError: " + databaseError);
                }
            });
        }
        Log.e("SignUpA", "isDuplicateID: " + isDuplicateID);
    }

    private void checkPW() {
        if (etPW.getText().length() != 0) {
            if (etPW.getText().length() < 6 || etPW.getText().length() > 16) {
                tvPW.setText(getString(R.string.password_text));
                tvPW.setVisibility(View.VISIBLE);
                tvPW.setTextColor(getResources().getColor(R.color.red));
                isRightPW = false;
            } else {
                newPW = etPW.getText().toString();
                tvPW.setVisibility(View.INVISIBLE);
                isRightPW = true;
            }
        } else {
            tvPW.setVisibility(View.VISIBLE);
            tvPW.setText("비밀번호를 입력하세요");
            tvPW.setTextColor(getResources().getColor(R.color.red));
            isRightPW = false;
        }
        Log.e("SignUpA", "isRightPW: " + isRightPW);
    }

    private void checkPW2() {
        if (etPW.getText().length() != 0 && etPW2.getText().length() != 0) {
            newPW = etPW.getText().toString();
            String newPW2 = etPW2.getText().toString();
            Log.e("SignUpA", "newPW:" + newPW + "/newPW2:" + newPW2);
            if (newPW.equals(newPW2)) {
                tvPW2.setText(getString(R.string.password_right));
                tvPW2.setTextColor(getResources().getColor(R.color.blue));
                tvPW2.setVisibility(View.VISIBLE);
                isSamePW = true;
            } else {
                tvPW2.setText(getString(R.string.password_wrong));
                tvPW2.setTextColor(getResources().getColor(R.color.red));
                tvPW2.setVisibility(View.VISIBLE);
                isSamePW = false;
            }
        }
        Log.e("SignUpA", "isSamePW: " + isSamePW);
    }

    private void signUp() {

        checkPW();
        checkPW2();
        checkPhone();
        checkVerificationCode();

        Log.e("SignUpA", "id:" + isDuplicateID + "  pw1:" + isRightPW + "  pw2:" + isSamePW + "  phone:" + isRightPhone + "  code:" + isVerificated);

        if (isDuplicateID || !isRightPW || !isSamePW || !isVerificated) {
            return;

        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");

            userID = newID;
            userUID = newID.substring(0, 4) + sdf.format(Calendar.getInstance().getTime());
            userPW = newPW;
            userName = newID;
            if (token != null) {
                userToken = token;
            }

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = firebaseDatabase.getReference();
            idRef = rootRef.child("users").child(userUID);

            User newUser = new User(userUID, userID, userPW, userName, userType, userToken);
            idRef.setValue(newUser);
            uploadToken();

            Intent intentFromSignIn = getIntent();
            setResult(RESULT_OK, intentFromSignIn);

            finish();
        }
    }

    private void uploadToken() {
        Log.e("SignUpA", "uploadToken");

        String serverUrl = "http://ncservices.dothome.co.kr/uploadToken.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SignUpA", "uploadToken onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignUpA", "uploadToken onErrorResponse:" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("userUID", userUID);
                datas.put("userToken", userToken);
                datas.put("userType", userType);

                return datas;
            }
        });

    }

    private void checkVerificationCode() {
        if (etVeriCode.getText().toString().length() == 0) {
            tvVeriCode.setText("인증번호를 입력하세요");
            tvVeriCode.setTextColor(getResources().getColor(R.color.red));
            tvVeriCode.setVisibility(View.VISIBLE);
            isVerificated = false;
        } else {
            String checkCode = etVeriCode.getText().toString();
            Log.e("SignUpA", "checkCode: " + checkCode);
            Log.e("SignUpA", "verificationCode: " + verificationCode);
            if (checkCode.equals(verificationCode)) {
                tvVeriCode.setText("인증번호가 일치합니다");
                tvVeriCode.setTextColor(getResources().getColor(R.color.blue));
                tvVeriCode.setVisibility(View.VISIBLE);
                isVerificated = true;
            } else {
                tvVeriCode.setText("인증번호가 일치하지 않습니다");
                tvVeriCode.setTextColor(getResources().getColor(R.color.red));
                tvVeriCode.setVisibility(View.VISIBLE);
                isVerificated = false;
            }
        }
        Log.e("SignUpA", "isVerificated: " + isVerificated);
    }

    private void checkPhone() {
        if (etPhone.getText().length() != 0) {
            phoneNum = etPhone.getText().toString();
            if (phoneNum.length() < 10 || phoneNum.length() > 11) {
                tvPhone.setVisibility(View.VISIBLE);
                tvPhone.setText("형식에 맞지 않는 번호입니다");
                tvPhone.setTextColor(getResources().getColor(R.color.red));
                isRightPhone = false;
            } else {
                tvPhone.setVisibility(View.INVISIBLE);
                isRightPhone = true;
            }
        } else {
            tvPhone.setVisibility(View.VISIBLE);
            tvPhone.setText("휴대폰번호를 입력하세요");
            tvPhone.setTextColor(getResources().getColor(R.color.red));
            isRightPhone = false;
        }
        Log.e("SignUpA", "isRightPhone: " + isRightPhone);
    }

    private void PhoneVerify() {
        checkPhone();
        if (!isRightPhone) {
            return;
        }
        String phoneNumKR = "+82" + phoneNum;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumKR, 120L /*timeout*/, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // The corresponding whitelisted code above should be used to complete sign-in.
                        Log.e("SignUpA", "onCodeSent");
                        Toast.makeText(SignUpActivity.this, "인증번호가 발송되었습니다", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        verificationCode = phoneAuthCredential.getSmsCode();
                        Log.e("SignUpA", "onVerificationCompleted:" + verificationCode);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("SignUpA", "onVerificationFailed:" + e);
                    }
                });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (!hasFocus) {
            if (i == R.id.et_id) {
                checkDuplicateID();
            }
            if (i == R.id.et_pw) {
                checkPW();
            }
            if (i == R.id.et_pw2) {
                checkPW2();
            }
            if (i == R.id.et_phone) {
                checkPhone();
            }
            if (i == R.id.et_verification) {
                checkVerificationCode();
            }
        }
    }


    //    private void checkDuplicateID() {
//
//        String serverUrl = "http://ncservices.dothome.co.kr/checkDuplicateID.php";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (response.equals("possible")) {
//                    isDuplicateID = false;
//                } else {
//                    isDuplicateID = true;
//                }
//                Log.e("SignUpA", "isDuplicateID:" + isDuplicateID);
//                Log.e("SignUpA", "checkDuplicateID:requestQueue onResponse:" + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("SignUpA", "checkDuplicateID:requestQueue onErrorResponse" + error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                HashMap<String, String> datas = new HashMap<>();
//
//                datas.put("userID", userID);
//
//                Log.e("SignUpA:", "checkDuplicateID:" + "userID:" + userID);
//
//                return datas;
//            }
//        });
//
//    }


}
