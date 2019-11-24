package com.mrex.ncs;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private ArrayList<HomeList> arrListHome = new ArrayList<>();
    private HomeList homeList;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private RecyclerView recyclerView;

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

        arrListHome.add(new HomeList(R.drawable.move, getString(R.string.home_1_1), getString(R.string.home_1_2)));
        arrListHome.add(new HomeList(R.drawable.home, getString(R.string.home_2_1), getString(R.string.home_2_2)));
        arrListHome.add(new HomeList(R.drawable.office, getString(R.string.home_3_1), getString(R.string.home_3_2)));
        arrListHome.add(new HomeList(R.drawable.stairs, getString(R.string.home_4_1), getString(R.string.home_4_2)));

        recyclerView = view.findViewById(R.id.rv_home);
        homeRecyclerAdapter = new HomeRecyclerAdapter(arrListHome, getLayoutInflater());
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

    public class HomeRecyclerAdapter extends RecyclerView.Adapter {

       private ArrayList<HomeList> arrListHome;
       private LayoutInflater layoutInflater;

        public HomeRecyclerAdapter(ArrayList<HomeList> arrListHome, LayoutInflater layoutInflater) {
            this.arrListHome = arrListHome;
            this.layoutInflater = layoutInflater;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.home_recycler_view, parent, false);

            VHolder vHolder = new VHolder(itemView);

            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VHolder vHolder = (VHolder) holder;
            homeList = arrListHome.get(position);

            Glide.with(getActivity()).load(homeList.getImage()).into(vHolder.imageView);
            vHolder.tvTop.setText(homeList.getTextTop());
            vHolder.tvBottom.setText(homeList.getTextBottom());

        }

        @Override
        public int getItemCount() {
            return arrListHome.size();
        }

        class VHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView tvTop, tvBottom;

            public VHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.iv_icon);
                tvTop = itemView.findViewById(R.id.tv_top);
                tvBottom = itemView.findViewById(R.id.tv_bottom);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getLayoutPosition();

                        if (position == 0) {
                            startActivity(new Intent(getActivity(), HomeFirstActivity.class));
                        }

                    }
                });

            }
        }
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
            View page = inflater.inflate(R.layout.home_pager, null);
            ImageView iv = page.findViewById(R.id.iv_pager);
            iv.setImageResource(arrListImg.get(position));
            container.addView(page);

            return page;
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

    public class HomeList {
        private int image;
        private String textTop, textBottom;

        public HomeList(int image, String textTop, String textBottom) {
            this.image = image;
            this.textTop = textTop;
            this.textBottom = textBottom;
        }

        public int getImage() {
            return image;
        }

        public String getTextTop() {
            return textTop;
        }

        public String getTextBottom() {
            return textBottom;
        }
    }

}
