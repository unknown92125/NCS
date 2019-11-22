package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher {

    private EditText etID, etPW, etPW2, etPhone, etVeriCode;
    private Button btSignUp;
    private TextView tvID, tvPW;
    private String userID, checkID, userPW, checkPW, phoneNum, verificationCode;
    private Boolean isDuplicateID = true, isSamePW = false, isVerificated = false;
    private User user;

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
        tvPW = findViewById(R.id.tv_pw2);

        etID.setOnFocusChangeListener(this);
        etID.addTextChangedListener(this);
        etPW.addTextChangedListener(this);
        etPW2.addTextChangedListener(this);

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
//                Log.e("SignInA:", "isDuplicateID:" + isDuplicateID);
//                Log.e("SignInA:", "checkDuplicateID:requestQueue onResponse:" + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("SignInA:", "checkDuplicateID:requestQueue onErrorResponse" + error);
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

    private void checkDuplicateID() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference userRef = rootRef.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        user = ds.getValue(User.class);
                        checkID = user.getId();
                        if (checkID.equals(userID)) {
                            isDuplicateID = true;
                        } else isDuplicateID = false;

                        //TODO

                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == etID) {
            if (!hasFocus || etID.getText().toString().length() != 0) {

                userID = etID.getText().toString();
                Log.e("SignUpA:", "userID:" + userID);

                checkDuplicateID();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (etPW.getText().toString().length() != 0 && etPW2.getText().toString().length() != 0) {
            userPW = etPW.getText().toString();
            String userPW2 = etPW2.getText().toString();
            Log.e("SignUpA:", "userPW:" + userPW + "/userPW2:" + userPW2);
            if (userPW.equals(userPW2)) {
                tvPW.setText(getString(R.string.password_right));
                tvPW.setTextColor(getResources().getColor(R.color.blue));
                isSamePW = true;
            } else {
                tvPW.setText(getString(R.string.password_wrong));
                tvPW.setTextColor(getResources().getColor(R.color.red));
                isSamePW = false;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void signUp(View view) {

        if (isDuplicateID) {
            Toast.makeText(this, "회원가입실패:아이디중복", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isSamePW) {
            Toast.makeText(this, "회원가입실패:비밀번호다름", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isVerificated) {
            Toast.makeText(this, "회원가입실패:핸드폰번호인증필요", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intentFromSignIn = getIntent();
        intentFromSignIn.putExtra("userID", userID);
        intentFromSignIn.putExtra("userPW", userPW);
        setResult(RESULT_OK, intentFromSignIn);
        finish();

    }

    public void getVerificationCode(View view) {
        if (etPhone.getText().toString().length() == 0) {
            Toast.makeText(this, "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        phoneNum = etPhone.getText().toString();
        PhoneVerify();
    }

    public void checkVerificationCode(View view) {
        if (etVeriCode.getText().toString().length() == 0) {
            Toast.makeText(this, "인증번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        String checkCode = etVeriCode.getText().toString();
        Log.e("SignUpA:", "checkCode:" + checkCode + "/veriCode:" + verificationCode);
        if (checkCode.equals(verificationCode)) {
            Toast.makeText(this, "인증완료", Toast.LENGTH_SHORT).show();
            isVerificated = true;
        } else {
            Toast.makeText(this, "인증실패", Toast.LENGTH_SHORT).show();
            isVerificated = false;
        }

    }

    public void PhoneVerify() {
        // [START auth_test_phone_verify]
        String phoneNumKR = "+82" + phoneNum;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumKR, 60L /*timeout*/, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                        Log.e("MainA:", "onVerificationCompleted:");
                        // The corresponding whitelisted code above should be used to complete sign-in.

                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        verificationCode = phoneAuthCredential.getSmsCode();
                        Log.e("MainA:", "onVerificationCompleted:" + phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // ...
                        Log.e("MainA:", "onVerificationFailed:" + e);
                    }

                });
        // [END auth_test_phone_verify]
    }


}
