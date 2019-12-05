package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.HashMap;
import java.util.Map;

import static com.mrex.ncs.MainActivity.token;
import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userID;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userPW;
import static com.mrex.ncs.U.userToken;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final int RC_SIGN_IN = 9001;
    public static final int RC_SIGN_UP = 9002;
    public static final int RC_CHANGE_PW = 9003;

    private String checkID, checkPW;
    private TextInputEditText etID, etPW;
    private TextInputLayout tilID, tilPW;

    private DatabaseReference idRef;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    ///////////////////

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ab_sign_in_title));

        etID = findViewById(R.id.et_id);
        etPW = findViewById(R.id.et_pw);
        tilID = findViewById(R.id.til_id);
        tilPW = findViewById(R.id.til_pw);

        findViewById(R.id.bt_google_login).setOnClickListener(this);
        findViewById(R.id.bt_sign_up).setOnClickListener(this);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.tv_find_pw).setOnClickListener(this);

        etID.setOnFocusChangeListener(this);
        etPW.setOnFocusChangeListener(this);

        /////////////////////// Google
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        ////////////////////// Kakao
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        ///////////////////////

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_UP) {
            //회원가입 결과
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == RC_CHANGE_PW){
            //비밀번호 변경
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
            }
        }

        // 구글 //////////////////////////////

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.e("SignInA", " Google Sign In was successful, authenticate with Firebase");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("SignInA", "Google sign in failed");
                // ...
            }
        }

        // 카카오 ///////////////////////////
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (hasFocus) {
            if (i == R.id.et_id) {
                tilID.setError(null);
                tilID.setErrorIconDrawable(null);
            }
            if (i == R.id.et_pw) {
                tilPW.setError(null);
                tilPW.setErrorIconDrawable(null);
            }
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bt_google_login) {
            signIn();
        }
        if (i == R.id.bt_sign_up) {
            startActivityForResult(new Intent(this, SignUpActivity.class), RC_SIGN_UP);
        }
        if (i == R.id.bt_login) {
            checkIDAndPW();
        }
        if (i == R.id.tv_find_pw) {
            startActivityForResult(new Intent(this, PasswordActivity.class), RC_CHANGE_PW);
        }
    }

    private void checkIDAndPW() {
        Log.e("SignInA", "checkIDAndPW");

        if (etID.getText().toString().length() == 0) {
            tilID.setError("아이디를 입력하세요");
            tilID.setErrorIconDrawable(R.drawable.ic_close_red);
            return;
        } else if (etPW.getText().toString().length() == 0) {
            tilPW.setError("비밀번호를 입력하세요");
            tilPW.setErrorIconDrawable(R.drawable.ic_close_red);
            return;
        } else if (etID.getText().toString().length() < 6 || etID.getText().toString().length() > 16 ||
                etPW.getText().toString().length() < 6 || etPW.getText().toString().length() > 16) {
            tilID.setError("아이디 또는 비밀번호가 틀립니다");
            tilID.setErrorIconDrawable(R.drawable.ic_close_red);
            return;
        }

        checkID = etID.getText().toString();
        checkPW = etPW.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        userRef = rootRef.child("users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    Log.e("SignInA", "userid:" + user.getId() + "  checkid:" + checkID +
                            "  userpw:" + user.getPw() + "  checkpw:" + checkPW +
                            "  결과: " + (checkID.equals(user.getId()) && checkPW.equals(user.getPw())));

                    if (checkID.equals(user.getId()) && checkPW.equals(user.getPw())) {
                        Log.e("SignInA", "right id and pw");

                        userUID = user.getUid();
                        userID = user.getId();
                        userPW = user.getPw();
                        userName = user.getName();
                        userType = user.getType();
                        userToken = user.getToken();
                        isSignedIn = true;

                        setResult(RESULT_OK);

                        if (token != null) {
                            if (!userToken.equals(token)) {
                                Log.e("SignInA", "checkIDAndPW: if(!userToken.equals(token))");
                                userToken = token;
                                Log.e("SignInA", userToken + "   " + token);
                                userRef.child(userUID).child("token").setValue(userToken);
                                uploadToken();
                                saveUserDataSF();
                            } else {
                                saveUserDataSF();
                            }
                        } else {
                            //token == null
                            saveUserDataSF();
                        }

                        break;
                    }
                }
                if (!isSignedIn) {
                    Log.e("SignInA", "wrong id or pw");
                    tilID.setError("아이디 또는 비밀번호가 틀립니다");
                    tilID.setErrorIconDrawable(R.drawable.ic_close_red);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadUserData() {
        Log.e("SignInA", "loadUserData");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        idRef = rootRef.child("users").child(userUID);

        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Log.e("SignInA", "loadUserData: if (dataSnapshot.getValue() != null)");
                    User user = dataSnapshot.getValue(User.class);
                    userID = user.getId();
                    userPW = user.getPw();
                    userName = user.getName();
                    userType = user.getType();
                    userToken = user.getToken();
                    isSignedIn = true;

                    setResult(RESULT_OK);

                    if (token != null) {
                        if (!userToken.equals(token)) {
                            Log.e("SignInA", "loadUserData: if(!userToken.equals(token))");
                            userToken = token;
                            idRef.child("token").setValue(userToken);
                            uploadToken();
                            saveUserDataSF();
                        } else {
                            saveUserDataSF();
                        }
                    } else {
                        //token == null
                        saveUserDataSF();
                    }
                } else {
                    //New User
                    if (token != null) {
                        userToken = token;
                    }
                    User newUser = new User(userUID, userID, userPW, userName, "0", userType, userToken);
                    idRef.setValue(newUser);
                    isSignedIn = true;

                    setResult(RESULT_OK);

                    uploadToken();
                    saveUserDataSF();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SignInA", "loadUserData: onCancelled:" + databaseError);
            }
        });
    }

    ///////////////////////////////////////////////

    private void uploadToken() {
        Log.e("SignInA", "uploadToken");
        String serverUrl = "http://ncservices.dothome.co.kr/uploadToken.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SignInA", "uploadToken onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignInA", "uploadToken onErrorResponse:" + error);
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

    private void saveUserDataSF() {
        Log.e("SignInA", "saveUserDataSF");
        SharedPreferences sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putString("userUID", userUID);
        editor.putString("userID", userID);
        editor.putString("userPW", userPW);
        editor.putString("userName", userName);
        editor.putString("userToken", userToken);
        editor.putString("userType", userType);
        editor.putBoolean("isSignedIn", true);

        editor.apply();

        finish();
    }

    // 구글 로그인 /////////////////////////////////////////////////

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("SignInA", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("SignInA", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            userUID = user.getUid();
                            userName = "g" + userUID.substring(userUID.length() - 8);
                            Log.e("SignInA", "id:" + userUID + "  name:" + userName);

                            loadUserData();

                        } else {
                            Log.e("SignInA", "signInWithCredential:failure");
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("SignInA", "google sign out complete");
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e("SignInA", "onStart:");
    }

    // 카카오 로그인 /////////////////////////////////////////////////

    private class SessionCallback implements ISessionCallback {
        //로그인 성공
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        //로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SignInA", "SessionCallback:onSessionOpenFailed");
        }

        //사용자 정보 요청
        protected void requestMe() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("SignInA", "failed to get user info. msg=" + errorResult);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SignInA", "requestMe:onSessionClosed");
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.e("SignInA", "카카오로그인:requestMe:onSuccess");

                    userUID = result.getId() + "";
                    userName = "k" + userUID.substring(userUID.length() - 8);
                    Log.e("SignInA", "userName : " + userName);

                    loadUserData();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
