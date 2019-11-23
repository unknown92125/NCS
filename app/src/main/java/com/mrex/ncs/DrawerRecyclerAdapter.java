package com.mrex.ncs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.mrex.ncs.U.isSignedIn;

public class DrawerRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<HomeActivity.DrawerList> arrListDrawer;
    private HomeActivity.DrawerList drawerList;
    private Context context;
    private DrawerLayout drawerLayout;

    public DrawerRecyclerAdapter(ArrayList<HomeActivity.DrawerList> arrListDrawer, Context context, DrawerLayout drawerLayout) {
        this.arrListDrawer = arrListDrawer;
        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_recycler_view, parent, false);

        VHolder vHolder = new VHolder(view);

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

                    if (!isSignedIn) {
                        context.startActivity(new Intent(context, SignInActivity.class));
                    } else {
                        Intent intent = new Intent(context, MyDataActivity.class);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }

                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

        }
    }
}
