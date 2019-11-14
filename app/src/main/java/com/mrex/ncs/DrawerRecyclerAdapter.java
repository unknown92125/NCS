package com.mrex.ncs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DrawerRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<HomeActivity.DrawerList> arrListDrawer;
    private HomeActivity.DrawerList drawerList;
    private Context context;

    public DrawerRecyclerAdapter(ArrayList<HomeActivity.DrawerList> arrListDrawer, Context context) {
        this.arrListDrawer = arrListDrawer;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.menu_recycler_view, parent, false);

        VHolder vHolder = new VHolder(itemView);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        drawerList = arrListDrawer.get(position);

        vHolder.iv.setImageResource(drawerList.getImage());
        vHolder.tv.setText(drawerList.getMenuText());

    }

    @Override
    public int getItemCount() {
        return arrListDrawer.size();
    }

    class VHolder extends RecyclerView.ViewHolder {

        private ImageView iv;
        private TextView tv;

        public VHolder(@NonNull View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv_icon);
            tv = itemView.findViewById(R.id.tv_menu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();

                }
            });

        }
    }
}
