package com.mrex.ncs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private HomeActivity homeActivity;

    private String userName;


    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        homeActivity = (HomeActivity) getActivity();

        view.findViewById(R.id.bt_start_chat).setOnClickListener(this);

        SharedPreferences sf = homeActivity.getSharedPreferences("sfUser", MODE_PRIVATE);
        userName = sf.getString("userName", "needSignIn");

        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_start_chat) {
            startActivity(new Intent(homeActivity, ChatActivity.class));
        }
    }
}
