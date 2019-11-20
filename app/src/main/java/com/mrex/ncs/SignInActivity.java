package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private String userName;
    private String userUID;
    private String userID;
    private String userPW;
    private String userToken;

    private DatabaseReference iDRef;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    ///////////////////

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.sign_in_title));

        findViewById(R.id.bt_google_login).setOnClickListener(this);
        findViewById(R.id.bt_ok).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //////////////////////

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        ///////////////////////

        getToken();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.e("SignInA:", "onActivityResult sign in");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("SignInA:", "Google sign in failed");
                // ...
            }
        }

        /////////////////////////////

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e("SignInA:", "onStart:");

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("SignInA:", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("SignInA:", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userUID = user.getUid();
                            userName = user.getDisplayName();
                            Log.e("SignInA:", "id:" + userUID + "  name:" + userName);

                            uploadDB();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("SignInA:", "signInWithCredential:failure");

                        }

                        // ...
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
                        Log.e("SignInA:", "google sign out complete");
                    }
                });
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bt_google_login) {
            signIn();
        } else if (i == R.id.bt_ok) {
            startActivity(new Intent(this, SignUpActivity.class));
        }

//         else if (i == R.id.bt_google_logout) {
//            signOut();
//        } else if (i == R.id.bt_kakao_logout) {
//            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//                @Override
//                public void onCompleteLogout() {
//                    Log.e("SignInA:", "kakao:onCompleteLogout");
//                }
//            });
//        }
    }

    ///////////////////////////////////////////////

    private void uploadDB() {

        saveUserDataSF();
        uploadToken();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        iDRef = rootRef.child("users").child(userUID);

        iDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    User setUser = new User(userUID, userName, userID, userPW);
                    iDRef.setValue(setUser);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Intent intentFromReservation = getIntent();
        setResult(RESULT_OK, intentFromReservation);
        finish();

    }

    private void uploadToken() {

        Log.e("SignInA:", "uploadToken()");
        String serverUrl = "http://ncservices.dothome.co.kr/uploadToken.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SignInA:uploadToken", "requestQueue onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SignInA:uploadToken", "requestQueue onErrorResponse");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("userUID", userUID);
                datas.put("userToken", userToken);
                if (userID.equals("manager")) {
                    datas.put("userType", "manager");
                } else {
                    datas.put("userType", "user");
                }

                Log.e("SignInA:", "getParams:" + "userUID:" + userUID + "/userToken:" + userToken);

                return datas;
            }
        });

    }

    private void saveUserDataSF() {
        SharedPreferences sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putString("userUID", userUID);
        editor.putString("userName", userName);
        editor.putString("userToken", userToken);

        editor.commit();
    }

    ///////////////////////////////////////////////////

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        //로그인 성공
        @Override
        public void onSessionOpened() {
            Log.e("SignInA:", "SessionCallback:onSessionOpened");
            requestMe();
        }

        //로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SignInA:", "SessionCallback:onSessionOpenFailed");
        }

        //사용자 정보 요청
        protected void requestMe() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("SignInA:", "failed to get user info. msg=" + errorResult);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SignInA:", "requestMe:onSessionClosed");
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.e("SignInA:", "requestMe:onSuccess");

                    Log.e("SignInA:", "getId:" + result.getId() + "   getKakaoAccount:" + result.getKakaoAccount()
                            + "  getProperties:" + result.getProperties() + "  getGroupUserToken:" + result.getGroupUserToken());

                    userUID = result.getId() + "";
                    userName = result.getProperties().get("nickname");
//                    String email=result.getKakaoAccount().getEmail();
                    Log.e("SignInA:", "id:" + userUID + "  name:" + userName);

                    uploadDB();

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("SignInA:", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        userToken = task.getResult().getToken();

                        Log.e("SignInA:token:", userToken);

                    }
                });
    }


}
