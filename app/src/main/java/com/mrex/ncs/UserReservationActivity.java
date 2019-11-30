package com.mrex.ncs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static com.mrex.ncs.HomeActivity.selectedUID;

public class UserReservationActivity extends AppCompatActivity {

    private Reservation reservation;
    private ArrayList<Reservation> arrListRV = new ArrayList<>();
    private UserReservationAdapter userReservationAdapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.reservation_list));

        tvEmpty = findViewById(R.id.tv_empty);

        RecyclerView recyclerView = findViewById(R.id.rv_reservation);
        userReservationAdapter = new UserReservationAdapter();
        recyclerView.setAdapter(userReservationAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference reservationRef = rootRef.child("reservations").child(selectedUID);

        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrListRV.clear();
                userReservationAdapter.notifyDataSetChanged();
                int pos = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    reservation = ds.getValue(Reservation.class);
                    arrListRV.add(reservation);
                    userReservationAdapter.notifyItemInserted(pos);
                    pos++;

                }
                Collections.sort(arrListRV);
                Collections.reverse(arrListRV);
                if (arrListRV.size() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public class UserReservationAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View itemView = inflater.inflate(R.layout.reservation_rv, parent, false);

            VHolder vHolder = new VHolder(itemView);

            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final VHolder vHolder = (VHolder) holder;
            reservation = arrListRV.get(position);

            vHolder.tvChat.setVisibility(View.GONE);
            vHolder.rlUser.setVisibility(View.VISIBLE);
            vHolder.tvUser.setText(reservation.getName());
            vHolder.tvDate.setText(reservation.getDate());
            vHolder.tvAddress.setText(reservation.getAddress());
            vHolder.tvArea.setText(reservation.getArea());
            vHolder.tvCleanType.setText(reservation.getCleanType());
            vHolder.tvPay.setText(reservation.getPayOption());
            vHolder.tvPayDate.setText(reservation.getPayDate());
            vHolder.tvPrice.setText(reservation.getPayPrice());
            vHolder.tvPhone.setText(reservation.getPhone());
            vHolder.tvPayName.setText(reservation.getPayName());
            if (reservation.getPayOption().equals("무통장입금")) {
                vHolder.rlPayName.setVisibility(View.VISIBLE);
            } else {
                vHolder.rlPayName.setVisibility(View.GONE);
            }

            Date nowDate, newDate;
            Calendar calendar = Calendar.getInstance();
            nowDate = calendar.getTime();

            try {
                newDate = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault()).parse(reservation.getPayDate());
                calendar.setTime(newDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                newDate = calendar.getTime();
                if (nowDate.before(newDate)) {
                    vHolder.ivNew.setVisibility(View.VISIBLE);
                    vHolder.rlList.setBackgroundColor(getResources().getColor(R.color.bluegreen_trans5));
                } else {
                    vHolder.ivNew.setVisibility(View.GONE);
                    vHolder.rlList.setBackgroundColor(getResources().getColor(R.color.white));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            vHolder.rlList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

            private TextView tvDate, tvAddress, tvArea, tvCleanType, tvPrice, tvPay, tvPayDate, tvPhone, tvPayName, tvUser, tvChat;
            private RelativeLayout rlData, rlList, rlPayName, rlUser;
            private ImageView ivArrow, ivNew;

            public VHolder(@NonNull View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tv_date);
                tvAddress = itemView.findViewById(R.id.tv_address);
                tvArea = itemView.findViewById(R.id.tv_area);
                tvCleanType = itemView.findViewById(R.id.tv_clean_type);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvPay = itemView.findViewById(R.id.tv_pay_option);
                tvPayDate = itemView.findViewById(R.id.tv_pay_date);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                tvPayName = itemView.findViewById(R.id.tv_pay_name);
                tvUser = itemView.findViewById(R.id.tv_user);
                tvChat = itemView.findViewById(R.id.tv_chat);
                rlData = itemView.findViewById(R.id.rl_data);
                rlList = itemView.findViewById(R.id.rl_list);
                rlPayName = itemView.findViewById(R.id.rl_pay_name);
                rlUser = itemView.findViewById(R.id.rl_user);
                ivArrow = itemView.findViewById(R.id.iv_arrow);
                ivNew = itemView.findViewById(R.id.iv_new);

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();

        if (i == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
