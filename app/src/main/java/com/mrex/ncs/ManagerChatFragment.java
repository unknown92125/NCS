package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import static com.mrex.ncs.HomeActivity.selectedUID;

public class ManagerChatFragment extends Fragment {

    private ArrayList<MessageItem> arrListMessageItem = new ArrayList<>();
    private ArrayList<MessageItem> arrListLastMessage = new ArrayList<>();
    private ArrayList<String> arrListUserName = new ArrayList<>();
    private ArrayList<String> arrListUID = new ArrayList<>();
    private String userChatName;

    private MessageItem messageItem;

    private ManagerChatRecyclerAdapter managerChatRecyclerAdapter;

    public ManagerChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_chat, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference chatRef = rootRef.child("chat");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrListUserName.clear();
                arrListLastMessage.clear();
                arrListUID.clear();
                managerChatRecyclerAdapter.notifyDataSetChanged();
//                int pos = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    arrListMessageItem.clear();
                    arrListUID.add(ds.getKey());
                    Log.e("MCF:ds", "getKey:" + ds.getKey() + "/getChildrenCount:" + ds.getChildrenCount() + "/getChildren:" + ds.getChildren());
                    for (DataSnapshot dds : ds.getChildren()) {
                        messageItem = dds.getValue(MessageItem.class);
                        if (!messageItem.getType().equals("manager")) {
                            userChatName = messageItem.getName();
                        }
                        arrListMessageItem.add(messageItem);
                        Log.e("MCF:dds", "getName:" + messageItem.getName() + "/getMessage:" + messageItem.getMessage());

                    }
                    arrListUserName.add(userChatName);
                    Collections.sort(arrListMessageItem);
                    Log.e("MCF:", "size:" + arrListMessageItem.size());
                    messageItem = arrListMessageItem.get(arrListMessageItem.size() - 1);
                    arrListLastMessage.add(messageItem);
                    Log.e("MCF:", messageItem.getName() + "/" + messageItem.getMessage());
//                    managerChatRecyclerAdapter.notifyItemInserted(pos);
//                    pos++;
                }
                managerChatRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv_manager_chat);
        managerChatRecyclerAdapter = new ManagerChatRecyclerAdapter(arrListLastMessage);
        recyclerView.setAdapter(managerChatRecyclerAdapter);

        return view;
    }

    public class ManagerChatRecyclerAdapter extends RecyclerView.Adapter {

        ArrayList<MessageItem> arrListLastMessage;

        public ManagerChatRecyclerAdapter(ArrayList<MessageItem> arrListLastMessage) {
            this.arrListLastMessage = arrListLastMessage;
        }

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
            userChatName = arrListUserName.get(position);

            MessageItem messageItem = arrListLastMessage.get(position);

            vHolder.tvName.setText(userChatName);
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
                        selectedUID = arrListUID.get(position);
                        Log.e("MCF:", selectedUID + "");
                        startActivity(new Intent(getActivity(), ChatActivity.class));
                    }
                });
            }
        }
    }


}