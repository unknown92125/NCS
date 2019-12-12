package com.mrex.ncs;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public static String cleanType;

    private HomeActivity homeActivity;

    private ArrayList<Integer> arrListImg = new ArrayList<>();
    private HomePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private int page = 0;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeActivity = (HomeActivity) getActivity();

        view.findViewById(R.id.cv_1).setOnClickListener(this);
        view.findViewById(R.id.cv_2).setOnClickListener(this);
        view.findViewById(R.id.cv_3).setOnClickListener(this);
        view.findViewById(R.id.cv_4).setOnClickListener(this);

        ////////////////////////////////////////////////////

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
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.cv_1) {
            startActivity(new Intent(homeActivity, HomeFirstActivity.class));
            cleanType = "이사청소";
        }
        if (i == R.id.cv_2) {
            startActivity(new Intent(homeActivity, HomeSecondActivity.class));
            cleanType = "입주청소";
        }
        if (i == R.id.cv_3) {
            startActivity(new Intent(homeActivity, HomeThirdActivity.class));
        }
        if (i == R.id.cv_4) {
            startActivity(new Intent(homeActivity, HomeFourthActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 2000);
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

    public class HomePagerAdapter extends PagerAdapter {

        private ArrayList<Integer> arrListImg;
        private LayoutInflater inflater;

        public HomePagerAdapter(ArrayList<Integer> arrListImg, LayoutInflater inflater) {
            this.arrListImg = arrListImg;
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return arrListImg.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View viewPage = inflater.inflate(R.layout.home_pager, null);
            ImageView iv = viewPage.findViewById(R.id.iv_pager);
            iv.setImageResource(arrListImg.get(position));
            container.addView(viewPage);


//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (page == 0) {
//                        startActivity(new Intent(homeActivity, CleanTip1Activity.class));
//                    }
//                    if (page == 1) {
//                        startActivity(new Intent(homeActivity, CleanTip2Activity.class));
//                    }
//                }
//            });

            return viewPage;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }


}
