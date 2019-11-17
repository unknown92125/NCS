package com.mrex.ncs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class MyDataFragment extends Fragment implements View.OnClickListener {

    private MyDataActivity myDataActivity;
    private TextView tvNickname;

    public MyDataFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_data, container, false);
        myDataActivity = (MyDataActivity) getActivity();

        SharedPreferences sf = myDataActivity.getSharedPreferences("sfUser", MODE_PRIVATE);
        String userName = sf.getString("userName", "needSignIn");

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvNickname.setText(userName);
        view.findViewById(R.id.bt_sign_out).setOnClickListener(this);

        return view;
    }

    private void signOut() {
        SharedPreferences sf = myDataActivity.getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putString("userName", "needSignIn");
        editor.commit();

        myDataActivity.startActivity(new Intent(myDataActivity, HomeActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_sign_out) {
            signOut();
        }
    }
}
