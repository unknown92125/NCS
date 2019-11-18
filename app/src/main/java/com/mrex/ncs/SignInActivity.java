package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    public static String userName;
    public static String userID;

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
        findViewById(R.id.bt_google_logout).setOnClickListener(this);
        findViewById(R.id.bt_kakao_logout).setOnClickListener(this);

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
                            userID = user.getUid();
                            userName = user.getDisplayName();
                            Log.e("SignInA:", "id:" + userID + "  name:" + userName);

                            uploadUserDB();

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

                    }
                });
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bt_google_login) {
            signIn();
        } else if (i == R.id.bt_google_logout) {
            signOut();
        } else if (i == R.id.bt_kakao_logout) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Log.e("SignInA:", "kakao:onCompleteLogout");
                }
            });
        }
    }

    ///////////////////////////////////////////////

    private void uploadUserDB() {
        saveID();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        iDRef = rootRef.child("users").child(userID);

        iDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    User setUser = new User(userID, userName, "", "", 0);
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

    private void saveID() {
        SharedPreferences sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putString("userID", userID);
        editor.putString("userName", userName);
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

                    userID = result.getId() + "";
                    userName = result.getProperties().get("nickname");
//                    String email=result.getKakaoAccount().getEmail();
                    Log.e("SignInA:", "id:" + userID + "  name:" + userName);

                    uploadUserDB();

                    if (result.hasSignedUp() == OptionalBoolean.FALSE) {
                        Log.e("SignInA:", "result.hasSignedUp() == OptionalBoolean.FALSE");
                    } else {
                        Log.e("SignInA:", "else result.hasSignedUp() == OptionalBoolean.FALSE");
                    }
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


}
