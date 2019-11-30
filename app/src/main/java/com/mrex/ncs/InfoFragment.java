package com.mrex.ncs;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userType;

public class InfoFragment extends Fragment implements View.OnClickListener {

    private HomeActivity homeActivity;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    public InfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        homeActivity = (HomeActivity) getActivity();

        fragmentManager = getFragmentManager();

        view.findViewById(R.id.bt_chat).setOnClickListener(this);
        view.findViewById(R.id.bt_call).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_chat) {
            homeActivity.getSupportActionBar().setTitle(getString(R.string.ab_chat_title));
            homeActivity.ivHome.setImageResource(R.drawable.ic_home_white);
            homeActivity.ivChat.setImageResource(R.drawable.ic_chat_bubble_blue);
            homeActivity.ivReservation.setImageResource(R.drawable.ic_assignment_white);
            homeActivity.ivMenu.setImageResource(R.drawable.ic_phone_white);

            if (!isSignedIn) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_fragments, new ChatSignInFragment());
                fragmentTransaction.commit();

            } else if (userType.equals("manager")) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_fragments, new ManagerChatFragment());
                fragmentTransaction.commit();

            } else {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_fragments, new ChatFragment());
                fragmentTransaction.commit();
            }
        }
        if (i == R.id.bt_call) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + "010-2437-3056"));
            startActivity(callIntent);
        }
    }
}
