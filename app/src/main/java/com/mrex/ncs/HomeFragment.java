package com.mrex.ncs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ArrayList<HomeList> arrListHome = new ArrayList<>();
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private RecyclerView recyclerView;

    private ArrayList<Integer> arrListImg = new ArrayList<>();
    private HomePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private int page = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        arrListHome.add(new HomeList(R.drawable.move, getString(R.string.home_1_1), getString(R.string.home_1_2)));
        arrListHome.add(new HomeList(R.drawable.home, getString(R.string.home_2_1), getString(R.string.home_2_2)));
        arrListHome.add(new HomeList(R.drawable.stairs, getString(R.string.home_3_1), getString(R.string.home_3_2)));
        arrListHome.add(new HomeList(R.drawable.office, getString(R.string.home_4_1), getString(R.string.home_4_2)));

        recyclerView = view.findViewById(R.id.recycler_view);
        homeRecyclerAdapter = new HomeRecyclerAdapter(arrListHome, getActivity());
        recyclerView.setAdapter(homeRecyclerAdapter);

        ////////////////////////////////////////////////////////////////////////////

        arrListImg.add(R.drawable.pager_img_1);
        arrListImg.add(R.drawable.pager_img_2);
        arrListImg.add(R.drawable.pager_img_3);

        viewPager = view.findViewById(R.id.pager);
        pagerAdapter = new HomePagerAdapter(arrListImg, getLayoutInflater());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CirclePageIndicator circlePageIndicator = view.findViewById(R.id.circle_indicator);
        circlePageIndicator.setViewPager(viewPager);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (pagerAdapter.getCount() - 1 == page) {
                page = 0;
            } else page++;

            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, 3000);

        }
    };


}
