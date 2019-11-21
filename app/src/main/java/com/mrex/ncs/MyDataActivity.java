package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MyDataActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyDataPagerAdapter myDataPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.home_title));

        tabLayout = findViewById(R.id.layout_tab);
        viewPager = findViewById(R.id.vp);

        myDataPagerAdapter = new MyDataPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myDataPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        tabLayout.selectTab(tabLayout.getTabAt(position));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyDataPagerAdapter extends FragmentPagerAdapter {

        Fragment[] arrFragments = new Fragment[2];
        String[] arrPageTitles = new String[]{"내 정보", "예약 현황"};

        public MyDataPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);

            arrFragments[0] = new MyDataFragment();
            arrFragments[1] = new MyReservationFragment();

        }

//        public MyPagerAdapter(@NonNull FragmentManager fm) {
//            super(fm);
//
////            arrFragments[0] = new FragmentHouse();
////            arrFragments[1] = new FragmentBowl();
////            arrFragments[2] = new FragmentLitterBox();
////            arrFragments[3] = new FragmentCatTree();
//        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrPageTitles[position];
        }
    }

}
