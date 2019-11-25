package com.mrex.ncs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.mrex.ncs.U.userUID;

public class MyReservationFragment extends Fragment {

    private MyDataActivity myDataActivity;

    private DatabaseReference reservationRef;
    private Reservation reservation;
    private ArrayList<Reservation> arrListRV = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyReservationAdapter myReservationAdapter;

    public MyReservationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_reservation, container, false);

        myDataActivity = (MyDataActivity) getActivity();

        recyclerView = view.findViewById(R.id.rv_reservation);
        myReservationAdapter = new MyReservationAdapter();
        recyclerView.setAdapter(myReservationAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        reservationRef = rootRef.child("reservations").child(userUID);

        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrListRV.clear();
                myReservationAdapter.notifyDataSetChanged();
                int pos = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    reservation = ds.getValue(Reservation.class);
                    arrListRV.add(reservation);
                    myReservationAdapter.notifyItemInserted(pos);
                    pos++;

                }
                Collections.sort(arrListRV);
                Collections.reverse(arrListRV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        return view;
    }

    public class MyReservationAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.my_reservation_recycler_view, parent, false);

            VHolder vHolder = new VHolder(itemView);

            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final VHolder vHolder = (VHolder) holder;
            reservation = arrListRV.get(position);

            vHolder.tvDate.setText(reservation.getDate());
            vHolder.tvDateList.setText(reservation.getDate());
            vHolder.tvAddress.setText(reservation.getAddress());
            vHolder.tvArea.setText(reservation.getArea());
            vHolder.tvExpectedTime.setText(reservation.getExpectedTime());
            vHolder.tvPay.setText(reservation.getPayMethod());
            vHolder.tvPayDate.setText(reservation.getPayDate());
            vHolder.tvPrice.setText(reservation.getPayPrice());
            vHolder.tvTime.setText(reservation.getTime());
            vHolder.tvPhone.setText(reservation.getPhone());
            vHolder.tvPayName.setText(reservation.getPayName());
            if (reservation.getPayName().equals("noValue")) {
                vHolder.llPayName.setVisibility(View.GONE);
            } else {
                vHolder.llPayName.setVisibility(View.VISIBLE);
            }
            vHolder.rlList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vHolder.rlData.getVisibility() == View.VISIBLE) {
                        vHolder.rlData.setVisibility(View.GONE);
                        vHolder.ivArrow.setImageResource(R.drawable.ic_arrow_down_black);
                    } else {
                        vHolder.rlData.setVisibility(View.VISIBLE);
                        vHolder.ivArrow.setImageResource(R.drawable.ic_arrow_up_black);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrListRV.size();
        }

        public class VHolder extends RecyclerView.ViewHolder {

            private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice, tvPay, tvPayDate, tvPhone, tvDateList, tvPayName;
            private RelativeLayout rlData, rlList;
            private LinearLayout llPayName;
            private ImageView ivArrow;

            public VHolder(@NonNull View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tv_date);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvAddress = itemView.findViewById(R.id.tv_address);
                tvArea = itemView.findViewById(R.id.tv_area);
                tvExpectedTime = itemView.findViewById(R.id.tv_expected_time);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvPay = itemView.findViewById(R.id.tv_payment);
                tvPayDate = itemView.findViewById(R.id.tv_pay_date);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                tvPayName = itemView.findViewById(R.id.tv_pay_name);
                rlData = itemView.findViewById(R.id.rl_data);
                rlList = itemView.findViewById(R.id.rl_list);
                llPayName = itemView.findViewById(R.id.ll_pay_name);
                tvDateList = itemView.findViewById(R.id.tv_date_list);
                ivArrow = itemView.findViewById(R.id.iv_arrow);

            }
        }

    }

}
