package com.mrex.ncs;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReservationFragment extends Fragment {

    private HomeActivity homeActivity;

    private DatabaseReference reservationRef;
    private Reservation reservation;
    private ArrayList<RVParent> arrListRVParent = new ArrayList<>();
    private ArrayList<RVChild> arrListChild = new ArrayList<>();
    private ManagerAdapter managerAdapter;

    public ReservationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        homeActivity = (HomeActivity) getActivity();

        ExpandableListView elv = view.findViewById(R.id.expandable_lv);
        managerAdapter = new ManagerAdapter(arrListRVParent, getLayoutInflater());
        elv.setAdapter(managerAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        reservationRef = rootRef.child("reservations");

        reservationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrListRVParent.clear();
                managerAdapter.notifyDataSetChanged();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot dds : ds.getChildren()) {
                        reservation = dds.getValue(Reservation.class);

                        arrListChild.add(new RVChild(reservation.getPhone(), reservation.getArea(), reservation.getExpectedTime(),
                                reservation.getPayPrice(), reservation.getPayMethod(), reservation.getPayName(), reservation.getPayDate()));
                        arrListRVParent.add(new RVParent(reservation.getAddress(), reservation.getDate(), arrListChild));

                        Log.e("onDataChange",ds.getKey()+"   "+ dds.getKey()+ "   "+reservation.getAddress()+reservation.getPhone() + reservation.getArea()+ reservation.getExpectedTime()+ reservation.getPayDate());

                        managerAdapter.notifyDataSetChanged();
                    }
                }
//                Collections.sort(arrListRV);
//                Collections.reverse(arrListRV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private class ManagerAdapter extends BaseExpandableListAdapter {

        private ArrayList<RVParent> arrListRVParent;
        private LayoutInflater layoutInflater;

        public ManagerAdapter(ArrayList<RVParent> arrListRVParent, LayoutInflater layoutInflater) {
            this.arrListRVParent = arrListRVParent;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getGroupCount() {
            return arrListRVParent.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return arrListRVParent.get(groupPosition).childs.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return arrListRVParent.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return arrListRVParent.get(groupPosition).childs.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.expandable_parent, null);

            //TODO add isnew image
            RVParent rvParent = (RVParent) getGroup(groupPosition);

            TextView tvDate = convertView.findViewById(R.id.tv_date);
            TextView tvAddress = convertView.findViewById(R.id.tv_address);
            ImageView ivArrow = convertView.findViewById(R.id.iv_arrow);

            tvDate.setText(rvParent.date);
            tvAddress.setText(rvParent.address);
            if (isExpanded) {
                ivArrow.setImageResource(R.drawable.ic_arrow_up_black);
            } else {
                ivArrow.setImageResource(R.drawable.ic_arrow_down_black);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.expandable_child, null);

            ArrayList<RVChild> arrListChild = (ArrayList<RVChild>) getChild(groupPosition, childPosition);
            RVChild rvChild = arrListChild.get(0);

            TextView tvPhone = convertView.findViewById(R.id.tv_phone);
            TextView tvArea = convertView.findViewById(R.id.tv_area);
            TextView tvExpected = convertView.findViewById(R.id.tv_expected_time);
            TextView tvPrice = convertView.findViewById(R.id.tv_price);
            TextView tvPayDate = convertView.findViewById(R.id.tv_pay_date);
            TextView tvPayment = convertView.findViewById(R.id.tv_payment);
            TextView tvPayName = convertView.findViewById(R.id.tv_pay_name);

            tvPhone.setText(rvChild.phone);
            tvArea.setText(rvChild.area);
            tvExpected.setText(rvChild.expected);
            tvPrice.setText(rvChild.price);
            tvPayDate.setText(rvChild.payDate);
            tvPayment.setText(rvChild.payment);
            tvPayName.setText(rvChild.payName);


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    public class RVChild {
        private String phone;
        private String area;
        private String expected;
        private String price;
        private String payDate;
        private String payment;
        private String payName;

        public RVChild() {
        }

        public RVChild(String phone, String area, String expected, String price, String payDate, String payment, String payName) {
            this.phone = phone;
            this.area = area;
            this.expected = expected;
            this.price = price;
            this.payDate = payDate;
            this.payment = payment;
            this.payName = payName;
        }
    }


    public class RVParent implements Comparable<RVParent> {

        private String address;
        private String date;
        private ArrayList<RVChild> childs = new ArrayList<>();

        public RVParent() {
        }

        public RVParent(String address, String date, ArrayList<RVChild> childs) {
            this.address = address;
            this.date = date;
            this.childs = childs;
        }

        @Override
        public int compareTo(RVParent rvParent) {
//
//            try {
//                Date d = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault()).parse(this.date);
//                long date1 = d.getTime();
//                d = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault()).parse(reservation.getDate());
//                long date2 = d.getTime();
//
//                if (date1 == date2) return 0;
//                else if (date1 > date2) return 1;
//                else return -1;
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            return 0;

        }
    }

}
//    public class ManagerAdapter extends RecyclerView.Adapter {
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater inflater = getLayoutInflater();
//            View itemView = inflater.inflate(R.layout.my_reservation_recycler_view, parent, false);
//
//            VHolder vHolder = new VHolder(itemView);
//
//            return vHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            final VHolder vHolder = (VHolder) holder;
//            reservation = arrListRV.get(position);
//
//            vHolder.tvDate.setText(reservation.getDate());
//            vHolder.tvDateList.setText(reservation.getDate());
//            vHolder.tvAddress.setText(reservation.getAddress());
//            vHolder.tvArea.setText(reservation.getArea());
//            vHolder.tvExpectedTime.setText(reservation.getExpectedTime());
//            vHolder.tvPay.setText(reservation.getPayMethod());
//            vHolder.tvPayDate.setText(reservation.getPayDate());
//            vHolder.tvPrice.setText(reservation.getPayPrice());
////            vHolder.tvTime.setText(reservation.getTime());
//            vHolder.tvPhone.setText(reservation.getPhone());
//            vHolder.tvPayName.setText(reservation.getPayName());
//            if (reservation.getPayName().equals("noValue")) {
//                vHolder.llPayName.setVisibility(View.GONE);
//            } else {
//                vHolder.llPayName.setVisibility(View.VISIBLE);
//            }
//            vHolder.rlList.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (vHolder.rlData.getVisibility() == View.VISIBLE) {
//                        vHolder.rlData.setVisibility(View.GONE);
//                        vHolder.ivArrow.setImageResource(R.drawable.ic_arrow_down_black);
//                    } else {
//                        vHolder.rlData.setVisibility(View.VISIBLE);
//                        vHolder.ivArrow.setImageResource(R.drawable.ic_arrow_up_black);
//                    }
//                }
//            });
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return arrListRV.size();
//        }
//
//        public class VHolder extends RecyclerView.ViewHolder {
//
//            private TextView tvDate, tvTime, tvAddress, tvArea, tvExpectedTime, tvPrice, tvPay, tvPayDate, tvPhone, tvDateList, tvPayName;
//            private RelativeLayout rlData, rlList;
//            private LinearLayout llPayName;
//            private ImageView ivArrow;
//
//            public VHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvDate = itemView.findViewById(R.id.tv_date);
//                tvTime = itemView.findViewById(R.id.tv_time);
//                tvAddress = itemView.findViewById(R.id.tv_address);
//                tvArea = itemView.findViewById(R.id.tv_area);
//                tvExpectedTime = itemView.findViewById(R.id.tv_expected_time);
//                tvPrice = itemView.findViewById(R.id.tv_price);
//                tvPay = itemView.findViewById(R.id.tv_payment);
//                tvPayDate = itemView.findViewById(R.id.tv_pay_date);
//                tvPhone = itemView.findViewById(R.id.tv_phone);
//                tvPayName = itemView.findViewById(R.id.tv_pay_name);
//                rlData = itemView.findViewById(R.id.rl_data);
//                rlList = itemView.findViewById(R.id.rl_list);
//                llPayName = itemView.findViewById(R.id.ll_pay_name);
//                tvDateList = itemView.findViewById(R.id.tv_date_list);
//                ivArrow = itemView.findViewById(R.id.iv_arrow);
//
////                itemView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        int position = getLayoutPosition();
////
////                        if (position == 0) {
////
////                        }
////                    }
////                });
//            }
//        }
//
//    }


