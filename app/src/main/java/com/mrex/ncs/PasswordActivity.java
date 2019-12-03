package com.mrex.ncs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.concurrent.TimeUnit;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextInputEditText etID, etPW, etPW2, etVeriCode;
    private TextInputLayout tilID, tilPW, tilPW2, tilVeriCode;
    private String checkID, uid, newPW, verificationCode, phone;
    private Boolean isRightID = false, isVerificated = false, isRightPW = false, isSamePW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("비밀번호 변경");

        etID = findViewById(R.id.et_id);
        etPW = findViewById(R.id.et_pw);
        etPW2 = findViewById(R.id.et_pw2);
        etVeriCode = findViewById(R.id.et_verification);

        tilID = findViewById(R.id.til_id);
        tilPW = findViewById(R.id.til_pw);
        tilPW2 = findViewById(R.id.til_pw2);
        tilVeriCode = findViewById(R.id.til_verification);

        etID.setOnFocusChangeListener(this);
        etPW.setOnFocusChangeListener(this);
        etPW2.setOnFocusChangeListener(this);
        etVeriCode.setOnFocusChangeListener(this);

        findViewById(R.id.bt_check_id).setOnClickListener(this);
        findViewById(R.id.bt_check_code).setOnClickListener(this);
        findViewById(R.id.bt_change_pw).setOnClickListener(this);


    }

    private void checkID() {

        if (etID.getText().toString().length() == 0) {
            tilID.setError("아이디를 입력하세요");
            tilID.setErrorIconDrawable(R.drawable.ic_close_red);
            return;
        } else if (etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16) {
            tilID.setError("잘못된 아이디입니다");
            tilID.setErrorIconDrawable(R.drawable.ic_close_red);
            return;
        }

        checkID = etID.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference().child("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    Log.e("SignInA", "userid:" + user.getId() + "  checkid:" + checkID +
                            "  결과: " + (checkID.equals(user.getId())));

                    if (checkID.equals(user.getId())) {
                        Log.e("SignInA", "right id");

                        phone = user.getPhone();
                        uid = user.getUid();
                        isRightID = true;
                        break;

                    }
                }

                if (isRightID) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PasswordActivity.this);
                    builder.setMessage("회원가입시 입력한 번호로 인증\n" + "( " + phone + " )")
                            .setPositiveButton("인증번호 받기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PhoneVerify();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else {
                    tilID.setErrorIconDrawable(R.drawable.ic_close_red);
                    tilID.setError("입력한 아이디를 찾을수 없습니다");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_check_id) {
            checkID();
        }
        if (i == R.id.bt_check_code) {
            checkVerificationCode();
        }
        if (i == R.id.bt_change_pw) {
            changePassword();
        }
    }

    private void changePassword() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(uid);
        userRef.child("pw").setValue(newPW);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordActivity.this);
        builder.setMessage("인증번호가 발송되었습니다")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();

        String phoneNumKR = "+82" + phone;

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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (!hasFocus) {

            if (i == R.id.et_pw) {
                checkPW();
            }
            if (i == R.id.et_pw2) {
                checkPW2();
            }

        } else {
            if (i == R.id.et_id) {
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
            if (i == R.id.et_verification) {
                tilVeriCode.setError(null);
                tilVeriCode.setErrorIconDrawable(null);
                tilVeriCode.setHelperText(null);
            }
        }
    }
}
