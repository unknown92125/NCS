package com.mrex.ncs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import static com.mrex.ncs.U.userPhone;
import static com.mrex.ncs.U.userToken;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private TextInputEditText etID, etPW, etPW2, etPhone, etVeriCode;
    private TextInputLayout tilID, tilPW, tilPW2, tilPhone, tilVeriCode;
    private String checkID, newID, newPW, phoneNum, verificationCode;
    private Boolean isDuplicateID = true, isRightPW = false, isSamePW = false, isRightPhone = false, isVerificated = false;
    private DatabaseReference idRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.sign_up));

        etID = findViewById(R.id.et_id);
        etPW = findViewById(R.id.et_pw);
        etPW2 = findViewById(R.id.et_pw2);
        etPhone = findViewById(R.id.et_phone);
        etVeriCode = findViewById(R.id.et_verification);

        tilID = findViewById(R.id.til_id);
        tilPW = findViewById(R.id.til_pw);
        tilPW2 = findViewById(R.id.til_pw2);
        tilPhone = findViewById(R.id.til_phone);
        tilVeriCode = findViewById(R.id.til_verification);

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
            etID.clearFocus();
            etPW.clearFocus();
            etPW2.clearFocus();
            etPhone.clearFocus();
            etVeriCode.clearFocus();

            signUp();
        }

    }

    private void checkDuplicateID() {
        if (etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16) {
            tilID.setErrorIconDrawable(R.drawable.ic_close_red);
            tilID.setError("6~16자의 영문 소문자와 숫자만 사용 가능합니다");
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
                        tilID.setError(null);
                        tilID.setErrorIconDrawable(null);

                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.getValue() != null) {
                                User user = ds.getValue(User.class);

                                checkID = user.getId();
                                Log.e("SignUpA", "checkID" + checkID);
                                if (checkID.equals(newID)) {
                                    Log.e("SignUpA", "중복된 아이디");
                                    isDuplicateID = true;
                                    tilID.setErrorIconDrawable(R.drawable.ic_close_red);
                                    tilID.setError("중복된 아이디입니다");
                                    return;

                                } else {
                                    Log.e("SignUpA", "사용가능한 아이디");
                                    isDuplicateID = false;
                                    tilID.setError(null);
                                    tilID.setErrorIconDrawable(null);

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
                tilPW.setErrorIconDrawable(R.drawable.ic_close_red);
                tilPW.setError("6~16자의 영문 소문자와 숫자만 사용 가능합니다");
                isRightPW = false;
            } else {
                newPW = etPW.getText().toString();
                tilPW.setError(null);
                tilPW.setErrorIconDrawable(null);
                isRightPW = true;
            }
        } else {
            tilPW.setErrorIconDrawable(R.drawable.ic_close_red);
            tilPW.setError("비밀번호를 입력하세요");
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
                tilPW2.setError(null);
                tilPW2.setErrorIconDrawable(null);
                isSamePW = true;
            } else {
                tilPW2.setErrorIconDrawable(R.drawable.ic_close_red);
                tilPW2.setError(getString(R.string.password_wrong));
                isSamePW = false;
            }
        }
        Log.e("SignUpA", "isSamePW: " + isSamePW);
    }

    private void checkPhone() {
        if (etPhone.getText().length() != 0) {
            phoneNum = etPhone.getText().toString();
            if (!phoneNum.startsWith("01") || phoneNum.length() < 10 || phoneNum.length() > 11) {
                tilPhone.setErrorIconDrawable(R.drawable.ic_close_red);
                tilPhone.setError("형식에 맞지 않는 번호입니다");
                isRightPhone = false;
            } else {
                tilPhone.setError(null);
                tilPhone.setErrorIconDrawable(null);
                isRightPhone = true;
            }
        } else {
            tilPhone.setErrorIconDrawable(R.drawable.ic_close_red);
            tilPhone.setError("휴대폰번호를 입력하세요");
            isRightPhone = false;
        }
        Log.e("SignUpA", "isRightPhone: " + isRightPhone);
    }

    private void signUp() {

//        checkDuplicateID();
//        checkPW();
//        checkPW2();
//        checkPhone();
//        checkVerificationCode();

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

            User newUser = new User(userUID, userID, userPW, userName, phoneNum, userType, userToken);
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
            tilVeriCode.setErrorIconDrawable(R.drawable.ic_close_red);
            tilVeriCode.setError("인증번호를 입력하세요");
            isVerificated = false;
        } else {
            String checkCode = etVeriCode.getText().toString();
            Log.e("SignUpA", "checkCode: " + checkCode);
            Log.e("SignUpA", "verificationCode: " + verificationCode);
            if (checkCode.equals(verificationCode)) {
                tilVeriCode.setError(null);
                tilVeriCode.setErrorIconDrawable(null);
                tilVeriCode.setHelperText("인증번호가 일치합니다");
                isVerificated = true;
            } else {
                tilVeriCode.setErrorIconDrawable(R.drawable.ic_close_red);
                tilVeriCode.setError("인증번호가 일치하지 않습니다");
                isVerificated = false;
            }
        }
        Log.e("SignUpA", "isVerificated: " + isVerificated);
    }

    private void PhoneVerify() {
        checkPhone();
        if (!isRightPhone) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage("인증번호가 발송되었습니다")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();

        String phoneNumKR = "+82" + phoneNum;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumKR, 120L /*timeout*/, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // The corresponding whitelisted code above should be used to complete sign-in.
                        Log.e("SignUpA", "onCodeSent");
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
        } else {
            if (i == R.id.et_id) {
                tilID.setHelperText(null);
                tilID.setError(null);
                tilID.setErrorIconDrawable(null);
            }
            if (i == R.id.et_pw) {
                tilPW.setError(null);
                tilPW.setErrorIconDrawable(null);
            }
            if (i == R.id.et_pw2) {
                tilPW2.setError(null);
                tilPW2.setErrorIconDrawable(null);
            }
            if (i == R.id.et_phone) {
                tilPhone.setError(null);
                tilPhone.setErrorIconDrawable(null);
            }
            if (i == R.id.et_verification) {
                tilVeriCode.setError(null);
                tilVeriCode.setErrorIconDrawable(null);
                tilVeriCode.setHelperText(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
