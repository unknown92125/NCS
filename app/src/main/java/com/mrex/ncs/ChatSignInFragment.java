package com.mrex.ncs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.mrex.ncs.SignInActivity.RC_SIGN_IN;


public class ChatSignInFragment extends Fragment implements View.OnClickListener {

    private HomeActivity homeActivity;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private SharedPreferences sf;


    public ChatSignInFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_sign_in, container, false);

        homeActivity = (HomeActivity) getActivity();
        fragmentManager = homeActivity.getSupportFragmentManager();
        sf = homeActivity.getSharedPreferences("sfUser", MODE_PRIVATE);

        view.findViewById(R.id.bt_sign_in).setOnClickListener(this);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (sf.getString("userType", "needSignIn").equals("manager")) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layout_fragments, new ManagerChatFragment());
                    fragmentTransaction.commit();
                } else {
                    Log.e("ChatSIF::", "onActivityResult");
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layout_fragments, new ChatFragment());
                    fragmentTransaction.commit();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_sign_in) {
            startActivityForResult(new Intent(homeActivity, SignInActivity.class), RC_SIGN_IN);
        }
    }
}
