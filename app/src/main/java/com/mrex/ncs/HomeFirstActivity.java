package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeFirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_first);

    }

    public void reservation(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

    public void back(View view) {
        finish();
    }
}


