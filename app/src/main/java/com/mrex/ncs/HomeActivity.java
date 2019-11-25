package com.mrex.ncs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import static com.mrex.ncs.U.isSignedIn;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userType;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    static Boolean isLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ArrayList<DrawerList> arrListDrawer = new ArrayList<>();
    private TextView tvName;
    private ImageView ivHome, ivChat, ivMenu, ivReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.home_title));

        getLocationPermission();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nv);
        tvName = findViewById(R.id.tv_name);
        ivHome = findViewById(R.id.iv_home);
        ivChat = findViewById(R.id.iv_chat);
        ivMenu = findViewById(R.id.iv_menu);
        ivReservation = findViewById(R.id.iv_reservation);

        arrListDrawer.add(new DrawerList(R.drawable.ic_person, "내 정보"));
        arrListDrawer.add(new DrawerList(R.drawable.ic_assignment_black, "예약 확인"));

        RecyclerView recyclerView = findViewById(R.id.rv_navigation);
        DrawerRecyclerAdapter drawerRecyclerAdapter = new DrawerRecyclerAdapter(arrListDrawer, this, drawerLayout);
        recyclerView.setAdapter(drawerRecyclerAdapter);

        ivHome.setOnClickListener(this);
        ivChat.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        ivReservation.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_fragments, new HomeFragment());
        fragmentTransaction.commit();

        navigationView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(navigationView);
                return false;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isSignedIn) {
            Log.e("HomeA", "onResume: if (!isSignedIn)");
            tvName.setText("");
        } else {
            tvName.setText(userName);

            if (userType.equals("manager")) {
                Log.e("HomeA", "onResume: if (userType.equals(\"manager\"))");
                ivReservation.setVisibility(View.VISIBLE);
            } else {
                ivReservation.setVisibility(View.GONE);
            }
        }

        //if started activity from chat Notification then show ManagerChatFragment and start ChatActivity
        if (getIntent().getExtras() != null) {
            Log.e("HomeA", "onResume: if (getIntent().getExtras() != null)");
            Intent intent = getIntent();
            String goTo = intent.getStringExtra("goTo");

            if (goTo != null) {
                Log.e("HomeA", goTo);

                if (isSignedIn) {
                    Log.e("HomeA", "onResume: if (isSignedIn)");

                    if (goTo.equals("chat")) {
                        getSupportActionBar().setTitle(getString(R.string.chat_title));

                        ivHome.setImageResource(R.drawable.ic_home_white);
                        ivChat.setImageResource(R.drawable.ic_chat_bubble_blue);
                        ivReservation.setImageResource(R.drawable.ic_assignment_white);
                        ivMenu.setImageResource(R.drawable.ic_menu_white);

                        Intent intent1 = new Intent(this, ChatActivity.class);
                        intent1.putExtra("chatUID", intent.getStringExtra("chatUID"));

                        if (userType.equals("manager")) {
                            Log.e("HomeA", "onResume: if (userType.equals(\"manager\"))");
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.layout_fragments, new ManagerChatFragment());
                            fragmentTransaction.commit();

                            startActivity(intent1);

                        } else if (userType.equals("user")) {
                            Log.e("HomeA", "onResume: else if (userType.equals(\"user\"))");
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.layout_fragments, new ChatFragment());
                            fragmentTransaction.commit();

                            startActivity(intent1);

                        }
                    } else if (goTo.equals("reservation")) {
                        getSupportActionBar().setTitle(getString(R.string.reservation_list));

                        ivHome.setImageResource(R.drawable.ic_home_white);
                        ivChat.setImageResource(R.drawable.ic_home_white);
                        ivReservation.setImageResource(R.drawable.ic_chat_bubble_blue);
                        ivMenu.setImageResource(R.drawable.ic_menu_white);

                        if (userType.equals("manager")) {
                            Log.e("HomeA", "onResume: if (userType.equals(\"manager\"))");
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.layout_fragments, new ReservationFragment());
                            fragmentTransaction.commit();

                        }
                    }
                } else {
                    //signedIn == false
                    startActivity(new Intent(this, SignInActivity.class));
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        toggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
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
    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.iv_home) {
            getSupportActionBar().setTitle(getString(R.string.home_title));
            ivHome.setImageResource(R.drawable.ic_home_blue);
            ivChat.setImageResource(R.drawable.ic_chat_bubble_white);
            ivReservation.setImageResource(R.drawable.ic_assignment_white);
            ivMenu.setImageResource(R.drawable.ic_menu_white);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layout_fragments, new HomeFragment());
            fragmentTransaction.commit();
        }
        if (i == R.id.iv_chat) {
            getSupportActionBar().setTitle(getString(R.string.chat_title));
            ivHome.setImageResource(R.drawable.ic_home_white);
            ivChat.setImageResource(R.drawable.ic_chat_bubble_blue);
            ivReservation.setImageResource(R.drawable.ic_assignment_white);
            ivMenu.setImageResource(R.drawable.ic_menu_white);

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
        if (i == R.id.iv_reservation) {
            ivHome.setImageResource(R.drawable.ic_home_white);
            ivChat.setImageResource(R.drawable.ic_chat_bubble_white);
            ivReservation.setImageResource(R.drawable.ic_assignment_blue);
            ivMenu.setImageResource(R.drawable.ic_menu_white);

            if (userType.equals("manager")) {
                getSupportActionBar().setTitle(getString(R.string.reservation_list));
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_fragments, new ReservationFragment());
                fragmentTransaction.commit();
            }
        }
        if (i == R.id.iv_menu) {
            getSupportActionBar().setTitle(getString(R.string.menu_title));
            ivHome.setImageResource(R.drawable.ic_home_white);
            ivChat.setImageResource(R.drawable.ic_chat_bubble_white);
            ivReservation.setImageResource(R.drawable.ic_assignment_white);
            ivMenu.setImageResource(R.drawable.ic_menu_blue);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layout_fragments, new MenuFragment());
            fragmentTransaction.commit();
        }

    }


    public class DrawerList {
        int image;
        String menuText;

        public DrawerList(int image, String menuText) {
            this.image = image;
            this.menuText = menuText;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getMenuText() {
            return menuText;
        }

        public void setMenuText(String menuText) {
            this.menuText = menuText;
        }
    }
}
