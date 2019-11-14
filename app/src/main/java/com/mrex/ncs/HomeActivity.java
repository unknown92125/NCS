package com.mrex.ncs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    static Boolean isLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.iv_home).setOnClickListener(this);
        findViewById(R.id.iv_event).setOnClickListener(this);
        findViewById(R.id.iv_menu).setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_fragments, new HomeFragment());
        fragmentTransaction.commit();

        getLocationPermission();


    }

    private void getLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;

            } else {
                isLocationPermissionGranted = false;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                    Toast.makeText(this, "위치정보제공에 동의하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public void login(View view) {
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_home) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layout_fragments, new HomeFragment());
            fragmentTransaction.commit();
        }
        if (i == R.id.iv_event) {

        }
        if (i == R.id.iv_menu) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layout_fragments, new MenuFragment());
            fragmentTransaction.commit();
        }
    }
}
