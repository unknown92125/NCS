package com.mrex.ncs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class HomePagerAdapter extends PagerAdapter {

    private  ArrayList<Integer> arrListImg;
    private    LayoutInflater inflater;

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
