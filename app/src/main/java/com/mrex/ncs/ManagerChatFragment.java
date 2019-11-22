package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ManagerChatFragment extends Fragment {

    ArrayList<MessageItem> arrListMessageItem = new ArrayList<>();
    ArrayList<MessageItem> arrListLastMessage = new ArrayList<>();
    ArrayList<String> arrListUID = new ArrayList<>();

    RecyclerView recyclerView;
    ManagerChatRecyclerAdapter managerChatRecyclerAdapter;

    public ManagerChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_chat, container, false);

        recyclerView = view.findViewById(R.id.rv_manager_chat);
        managerChatRecyclerAdapter = new ManagerChatRecyclerAdapter();
        recyclerView.setAdapter(managerChatRecyclerAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference chatRef = rootRef.child("chat");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrListMessageItem.clear();
                arrListLastMessage.clear();
                arrListUID.clear();
                managerChatRecyclerAdapter.notifyDataSetChanged();
                int pos = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    arrListUID.add(ds.getKey());
                    for (DataSnapshot dds : ds.getChildren()) {
                        MessageItem messageItem = dds.getValue(MessageItem.class);
                        arrListMessageItem.add(messageItem);
                    }
                    Collections.sort(arrListMessageItem);
                    MessageItem messageItem = arrListMessageItem.get(arrListMessageItem.size() - 1);
                    arrListLastMessage.add(messageItem);
                    managerChatRecyclerAdapter.notifyItemInserted(pos);
                    pos++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class ManagerChatRecyclerAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.manager_chat_recycler_view, parent, false);

            VHolder vHolder = new VHolder(itemView);

            return vHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            VHolder vHolder = (VHolder) holder;
            MessageItem messageItem = arrListLastMessage.get(position);

            vHolder.tvName.setText(messageItem.getName());
            vHolder.tvMessage.setText(messageItem.getMessage());
            vHolder.tvTime.setText(messageItem.getTime());
        }

        @Override
        public int getItemCount() {
            return arrListLastMessage.size();
        }

        public class VHolder extends RecyclerView.ViewHolder {

            private TextView tvName, tvMessage, tvTime;

            public VHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                tvMessage = itemView.findViewById(R.id.tv_message);
                tvTime = itemView.findViewById(R.id.tv_time);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getLayoutPosition();
                        String selectedUID = arrListUID.get(position);
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("SelectedUID", selectedUID);
                        startActivity(intent);

                    }
                });
            }
        }
    }


}
