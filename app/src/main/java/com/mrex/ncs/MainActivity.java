package com.mrex.ncs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.maps.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler.sendEmptyMessageDelayed(0, 1500);

        getToken();

        //////////////////////////////
//        String serverUrl = "http://ncservices.dothome.co.kr/pushFM.php";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("MainA:", "requestQueue onResponse:" + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("MainA:", "requestQueue onErrorResponse");
//            }
//        }));

        String key = getKeyHash();
        Log.e("TAG", key);


    }

    public void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("MainA:", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.e("MainA:token:", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                        //토큰 저장
                        SharedPreferences sf = getSharedPreferences("sfUser", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("userToken", token);
                        editor.commit();

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

    public String getKeyHash() {
        PackageInfo packageInfo = getPackageInfo(this, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.e("TAG", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
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

}