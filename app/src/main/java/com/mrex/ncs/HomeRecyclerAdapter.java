package com.mrex.ncs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter {

    private   ArrayList<HomeList> arrListHome;
    private    HomeList homeList;
    private   Context context;

    public HomeRecyclerAdapter(ArrayList<HomeList> arrListHome, Context context) {
        this.arrListHome = arrListHome;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.home_recycler_view, parent, false);

        VHolder vHolder = new VHolder(itemView);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        homeList = arrListHome.get(position);

        Glide.with(context).load(homeList.getImage()).into(vHolder.imageView);
        vHolder.tvTop.setText(homeList.getTextTop());
        vHolder.tvBottom.setText(homeList.getTextBottom());

    }

    @Override
    public int getItemCount() {
        return arrListHome.size();
    }

    class VHolder extends RecyclerView.ViewHolder {

        private   ImageView imageView;
        private  TextView tvTop, tvBottom;
        private    Intent intent;


        public VHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_icon);
            tvTop = itemView.findViewById(R.id.tv_top);
            tvBottom = itemView.findViewById(R.id.tv_bottom);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getLayoutPosition();

                    if (position==0){
                        intent=new Intent(context, HomeFirstActivity.class);
                        context.startActivity(intent);
                    }

                }
            });

        }
    }
}
