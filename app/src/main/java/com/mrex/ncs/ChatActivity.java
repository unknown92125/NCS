package com.mrex.ncs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity implements ChildEventListener {

    private ArrayList<MessageItem> arrListMessage = new ArrayList<>();

    private EditText et;
    private ListView listView;
    private ChatAdapter chatAdapter;

    private String userUID;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        et = findViewById(R.id.et_chat);

        listView = findViewById(R.id.list_view_chat);
        chatAdapter = new ChatAdapter(arrListMessage, getLayoutInflater());
        listView.setAdapter(chatAdapter);

        SharedPreferences sf = getSharedPreferences("sfUser", MODE_PRIVATE);
        userUID = sf.getString("userUID", "needSignIn");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        chatRef = rootRef.child("chat").child(userUID);

        chatRef.addChildEventListener(this);


    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);

        arrListMessage.add(messageItem);

        chatAdapter.notifyDataSetChanged();
        listView.setSelection(arrListMessage.size() - 1);
    }

    public void sendMessage(View view) {

        String name = "user";
        String message = et.getText().toString();

        Calendar calendar = Calendar.getInstance();
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        MessageItem messageItem = new MessageItem(name, message, time);

        chatRef.push().setValue(messageItem);

        et.setText("");

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }


    public class ChatAdapter extends BaseAdapter {

        ArrayList<MessageItem> arrListMessage;
        LayoutInflater layoutInflater;

        public ChatAdapter(ArrayList<MessageItem> arrListMessage, LayoutInflater layoutInflater) {
            this.arrListMessage = arrListMessage;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return arrListMessage.size();
        }

        @Override
        public Object getItem(int position) {
            return arrListMessage.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            TextView tvName, tvMsg, tvTime;

            MessageItem messageItem = arrListMessage.get(position);

            View itemView = null;

            if (messageItem.getName().equals(getString(R.string.app_name_kr))) {
                itemView = layoutInflater.inflate(R.layout.manager_chat_box, viewGroup, false);
                tvName = itemView.findViewById(R.id.tv_name_chat);
                tvMsg = itemView.findViewById(R.id.tv_manager_chat);
                tvTime = itemView.findViewById(R.id.tv_time_chat);

                tvName.setText(messageItem.getName());
                tvMsg.setText(messageItem.getMessage());
                tvTime.setText(messageItem.getTime());

            } else {
                itemView = layoutInflater.inflate(R.layout.user_chat_box, viewGroup, false);
                tvMsg = itemView.findViewById(R.id.tv_user_chat);
                tvTime = itemView.findViewById(R.id.tv_time_chat);

                tvMsg.setText(messageItem.getMessage());
                tvTime.setText(messageItem.getTime());

            }

            return itemView;
        }


    }


    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
