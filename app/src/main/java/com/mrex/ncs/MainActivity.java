package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        getToken();// -> loadUserData

//        handler.sendEmptyMessageDelayed(0, 2000);

//        String key = getKeyHash();
//        Log.e("MainA:", "KeyHash: " + key);

    }

    private void loadUserData() {
        Log.e("HomeA", "loadUserData");

        sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        isSignedIn = sf.getBoolean("isSignedIn", false);

        if (isSignedIn) {
            Log.e("HomeA", "if (isSignedIn)");
            userUID = sf.getString("userUID", "noValue");

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = firebaseDatabase.getReference();
            idRef = rootRef.child("users").child(userUID);

            idRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        Log.e("HomeA", "if (dataSnapshot.getValue() != null)");
                        User user = dataSnapshot.getValue(User.class);
                        userID = user.getId();
                        userPW = user.getPw();
                        userName = user.getName();
                        userType = user.getType();
                        userToken = user.getToken();

                        if (token != null) {
                            if (!userToken.equals(token)) {
                                Log.e("HomeA", "if(!userToken.equals(token))");
                                userToken = token;
                                uploadToken();// -> saveUserDataSF
                            } else {
                                saveUserDataSF();
                            }
                        } else {
                            //token == null
                            saveUserDataSF();
                        }
                    } else {
                        //dataSnapshot.getValue() == null
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            //isSignedIn == false
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    private void saveUserDataSF() {
        Log.e("HomeA", "saveUserDataSF");
        sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putString("userUID", userUID);
        editor.putString("userID", userID);
        editor.putString("userPW", userPW);
        editor.putString("userName", userName);
        editor.putString("userToken", token);
        editor.putString("userType", userType);
        editor.putBoolean("isSignedIn", true);

        editor.apply();

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    private void uploadToken() {
        Log.e("HomeA", "uploadToken");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        idRef = rootRef.child("users").child(userUID);

        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Log.e("HomeA", "if (dataSnapshot.getValue()!=null)");
                    idRef.child("token").setValue(token);
                } else {
                    //dataSnapshot.getValue()==null
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /////////////////////////////////////////////

        String serverUrl = "http://ncservices.dothome.co.kr/uploadToken.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("HomeA", "uploadToken onResponse:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HomeA", "uploadToken onErrorResponse:" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("userUID", userUID);
                datas.put("userToken", userToken);
                datas.put("userType", userType);

                Log.e("HomeA:", "uploadToken getParams:" + "userUID:" + userUID + "   userID:" + userID + "   userPW:" + userPW + "   userName:" + userName + "   userType:" + userType + "   userToken:" + userToken);

                return datas;
            }
        });

        saveUserDataSF();

    }

    public void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("HomeA", "getTokenFailed:", task.getException());

                            loadUserData();

                        } else {
                            // Get new Instance ID token
                            token = task.getResult().getToken();
                            Log.e("HomeA:getToken:", token);

                            loadUserData();
                        }
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

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            finish();
//        }
//    };

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