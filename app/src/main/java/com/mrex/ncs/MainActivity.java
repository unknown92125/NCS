package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userID;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userPW;
import static com.mrex.ncs.U.userToken;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class MainActivity extends AppCompatActivity {

    public static String token;
    private SharedPreferences sf;
    private DatabaseReference idRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.sendEmptyMessageDelayed(0, 1500);

        getToken();
        loadUserData();

//        String key = getKeyHash();
//        Log.e("MainA:", "KeyHash: " + key);

    }

    private void loadUserData() {

        sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        userUID = sf.getString("userUID", "noValue");
        isSignedIn = sf.getBoolean("isSignedIn", false);

        if (!isSignedIn) {
            return;
        } else if (userUID.equals("noValue")) {
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        idRef = rootRef.child("users").child(userUID);
        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userID = user.getId();
                    userPW = user.getPw();
                    userName = user.getName();
                    userType = user.getType();
                    userToken = user.getToken();

                    if ((!userName.equals(getString(R.string.app_name_kr))) && userType.equals("manager")) {
                        userName = getString(R.string.app_name_kr);
                        idRef.child("name").setValue(getString(R.string.app_name_kr));
                    }
                }

                Log.e("HomeA:loadUserData:", "userUID:" + userUID + "   userID:" + userID + "   userPW" + userPW + "   userName" + userName + "   userType" + userType + "   userToken" + userToken + "   isSignedIn" + isSignedIn);
                if (userToken==null || !userToken.equals(token)) {
                    Log.e("HomeA:loadUserData:", "!userToken.equals(token)");
                    userToken = token;
                    uploadToken();
                }
                saveUserDataSF();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void saveUserDataSF() {
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
    }

    private void uploadToken() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        idRef = rootRef.child("users").child(userUID);

        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    idRef.child("token").setValue(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /////////////////////////////////////////////

        Log.e("HomeA:", "uploadToken()");
        String serverUrl = "http://ncservices.dothome.co.kr/uploadToken.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("userUID", userUID);
                datas.put("userToken", userToken);
                datas.put("userType", userType);

                Log.e("HomeA:", "uploadToken:" + "userUID:" + userUID + "   userID:" + userID + "   userPW:" + userPW + "   userName:" + userName + "   userToken:" + userToken + "   userType:" + userType);

                return datas;
            }
        });

    }

    public void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("HomeA:", "getTokenFailed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.e("HomeA:token:", token);

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    };

//    public String getKeyHash() {
//        PackageInfo packageInfo = getPackageInfo(this, PackageManager.GET_SIGNATURES);
//        if (packageInfo == null)
//            return null;
//
//        for (Signature signature : packageInfo.signatures) {
//            try {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
//            } catch (NoSuchAlgorithmException e) {
//                Log.e("TAG", "Unable to get MessageDigest. signature=" + signature, e);
//            }
//        }
//        return null;
//    }

}