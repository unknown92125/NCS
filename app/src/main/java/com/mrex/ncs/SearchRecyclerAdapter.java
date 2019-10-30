package com.mrex.ncs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<AddressData> arrListData;
    private AddressData addressData;
    private Context context;
    private FragmentManager fragmentManager;
    private Intent intent;

    public SearchRecyclerAdapter(ArrayList<AddressData> arrListData, Context context, FragmentManager fragmentManager) {
        this.arrListData = arrListData;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.search_recycler_view, parent, false);

        VHolder vHolder = new VHolder(itemView);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        addressData = arrListData.get(position);

        vHolder.tvPlaceName.setText(addressData.getPlaceName());
        vHolder.tvAddress.setText(addressData.getAddress());

    }

    @Override
    public int getItemCount() {
        return arrListData.size();
    }

    class VHolder extends RecyclerView.ViewHolder {

        private TextView tvPlaceName, tvAddress;

        public VHolder(@NonNull View itemView) {
            super(itemView);

            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            tvAddress = itemView.findViewById(R.id.tv_address1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    addressData = arrListData.get(position);

                    intent = new Intent(context, AddressActivity.class);
                    intent.putExtra("place", addressData.getPlaceName());
                    intent.putExtra("address", addressData.getAddress());
                    intent.putExtra("lat", addressData.getLat());
                    intent.putExtra("lng", addressData.getLng());
                    context.startActivity(intent);

                }
            });

        }
    }
}
