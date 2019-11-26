package com.mrex.ncs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import static android.content.Context.MODE_PRIVATE;
import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userName;


public class MyDataFragment extends Fragment implements View.OnClickListener {

    private MyDataActivity myDataActivity;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public MyDataFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_data, container, false);
        myDataActivity = (MyDataActivity) getActivity();

        TextView tvNickname = view.findViewById(R.id.tv_nickname);
        tvNickname.setText(userName);
        view.findViewById(R.id.bt_sign_out).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(myDataActivity, gso);
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void signOut() {
        Log.e("MyDataF", "signOut");
        SharedPreferences sf = myDataActivity.getSharedPreferences("sfUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        editor.putBoolean("isSignedIn", false);
        editor.apply();

        isSignedIn = false;

        //////////////////////////////////////////////////////////////////////////////
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(myDataActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("MyDataF", "google sign out complete");
                    }
                });
        //////////////////////////////////////////////////////////////////////////////
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.e("MyDataF", "kakao:onCompleteLogout");
            }
        });

        myDataActivity.startActivity(new Intent(myDataActivity, HomeActivity.class));
        myDataActivity.finishAffinity();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_sign_out) {
            signOut();
        }
    }
}
