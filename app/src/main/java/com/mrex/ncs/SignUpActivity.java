package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.concurrent.TimeUnit;

import static com.mrex.ncs.U.userID;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userPW;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher {

    private EditText etID, etPW, etPW2, etPhone, etVeriCode;
    private TextView tvID, tvPW;
    private String checkID, newID, newPW, phoneNum, verificationCode;
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

        etPW.addTextChangedListener(this);
        etPW2.addTextChangedListener(this);
        etID.setOnFocusChangeListener(this);
        etID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkDuplicateID();
                return true;
            }
        });

    }

    private void checkDuplicateID() {

        if (etID.getText().toString().equals("noValue") ||
                etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16) {
            tvID.setText("영문 + 숫자 6 ~ 16자리");
            tvID.setTextColor(getResources().getColor(R.color.red));
            return;
        }

        newID = etID.getText().toString();
        Log.e("SignUpA:", "newID:" + newID);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference userRef = rootRef.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    checkID = user.getId();
                    if (checkID.equals(newID)) {
                        isDuplicateID = true;
                        tvID.setText("중복된 아이디입니다");
                        tvID.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        isDuplicateID = false;
                        tvID.setText("사용가능한 아이디입니다");
                        tvID.setTextColor(getResources().getColor(R.color.blue));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        checkDuplicateID();
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (etID.getText().toString().equals("noValue") ||
                etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16) {
            tvID.setText("6 ~ 16자리");
            tvID.setTextColor(getResources().getColor(R.color.red));
            return;
        } else {
            newPW = etPW.getText().toString();
            String newPW2 = etPW2.getText().toString();
            Log.e("SignUpA:", "newPW:" + newPW + "/newPW2:" + newPW2);
            if (newPW.equals(newPW2)) {
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


    public void signUp(View view) {

        if (isDuplicateID) {
            Toast.makeText(this, "중복된 아이디입니다", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isSamePW) {
            Toast.makeText(this, "비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isVerificated) {
            Toast.makeText(this, "인증번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");

        userID = newID;
        userUID = userID.substring(0, 4) + sdf.format(Calendar.getInstance().getTime());
        userPW = newPW;
        userName = newID;
        userType = "user";

        Intent intentFromSignIn = getIntent();
        setResult(RESULT_OK, intentFromSignIn);
        finish();

    }

    public void getVerificationCode(View view) {
        if (etPhone.getText().toString().length() == 0) {
            Toast.makeText(this, "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "인증번호를 발송하였습니다", Toast.LENGTH_SHORT).show();
        view.setClickable(false);
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
            Toast.makeText(this, "인증이 완료 되었습니다", Toast.LENGTH_SHORT).show();
            isVerificated = true;
        } else {
            Toast.makeText(this, "인증번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
            isVerificated = false;
        }

    }

    public void PhoneVerify() {
        String phoneNumKR = "+82" + phoneNum;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumKR, 120L /*timeout*/, TimeUnit.SECONDS,
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
                        Log.e("MainA:", "onVerificationFailed:" + e);
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

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


}
