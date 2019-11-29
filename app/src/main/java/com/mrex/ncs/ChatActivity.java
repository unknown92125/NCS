package com.mrex.ncs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.mrex.ncs.FMService.isChatForeground;
import static com.mrex.ncs.ManagerChatFragment.selectedUID;
import static com.mrex.ncs.U.userName;
import static com.mrex.ncs.U.userType;
import static com.mrex.ncs.U.userUID;

public class ChatActivity extends AppCompatActivity implements ChildEventListener {

    private ArrayList<MessageItem> arrListMessage = new ArrayList<>();

    private EditText et;
    private ListView listView;
    private ChatAdapter chatAdapter;

    private String chatUID, message;
    private Boolean isPreviousTypeSame = false, isNextTypeSame = false, isNextTimeSame = false;
    private int etHeight;

    private DatabaseReference chatRef;
    private MessageItem messageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.ab_chat_title));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        et = findViewById(R.id.et_chat);

        listView = findViewById(R.id.list_view_chat);
        chatAdapter = new ChatAdapter(arrListMessage, getLayoutInflater());
        listView.setAdapter(chatAdapter);
        etHeight = et.getHeight();
        et.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (etHeight != bottom) {
                    listView.setSelection(arrListMessage.size() - 1);
                }
            }
        });

        if (userType.equals("user")) {
            chatUID = userUID;
        } else {
            chatUID = selectedUID;

            //when started activity from HomeActivity(chat notification)
            if (getIntent().getExtras() != null) {
                Log.e("ChatA", " if (getIntent().getExtras() != null)");
                Intent intent = getIntent();
                chatUID = intent.getStringExtra("chatUID");
                Log.e("ChatA", "chatUID: "+chatUID);
            }
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        chatRef = rootRef.child("chat").child(chatUID);

        chatRef.addChildEventListener(this);

    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//        Log.e("ChatA", "onChildAdded");
//        Log.e("ChatA", dataSnapshot.getKey());
        messageItem = dataSnapshot.getValue(MessageItem.class);
        arrListMessage.add(messageItem);
        chatAdapter.notifyDataSetChanged();
        listView.setSelection(arrListMessage.size() - 1);
    }

    public void sendMessage(View view) {
        Log.e("ChatA", "sendMessage");

        message = et.getText().toString();

        Calendar calendar = Calendar.getInstance();
        Long timeMilli = calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("a h:mm", Locale.getDefault());
        String time = sdf.format(timeMilli);

        if (userType.equals("manager")) {
            messageItem = new MessageItem("세상의 모든 청소", message, time, userType, timeMilli);
        } else {
            messageItem = new MessageItem(userName, message, time, userType, timeMilli);
        }
        chatRef.push().setValue(messageItem);

        pushChatFM();

        et.setText("");
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatForeground = true;
        Log.e("ChatA", "onResume isChatForeground = true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isChatForeground = false;
        Log.e("ChatA", "onPause isChatForeground = false");
    }

    private void pushChatFM() {

        String serverUrl = "http://ncservices.dothome.co.kr/pushChatFM.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("ChatA", "onResponse:"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ChatA", "onErrorResponse:"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> datas = new HashMap<>();
                datas.put("userUID", chatUID);
                datas.put("message", message);
                datas.put("userName", userName);

//                Log.e("ChatA", "uploadToken:" + "userUID:" + userUID + "   userID:" + userID + "   userPW:" + userPW + "   userName:" + userName + "   userToken:" + userToken + "   userType:" + userType);

                return datas;
            }
        });

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
            MessageItem previousMessage;
            if (position != 0) {
                previousMessage = arrListMessage.get(position - 1);
                if (previousMessage.getType().equals(messageItem.getType())) {
                    isPreviousTypeSame = true;
                } else {
                    isPreviousTypeSame = false;
                }
            }

            MessageItem nextMessage;
            if (position < arrListMessage.size() - 1) {
                nextMessage = arrListMessage.get(position + 1);
                if (nextMessage.getType().equals(messageItem.getType())) {
                    isNextTypeSame = true;
                } else {
                    isNextTypeSame = false;
                }
                if (nextMessage.getTime().equals(messageItem.getTime())) {
                    isNextTimeSame = true;
                } else {
                    isNextTimeSame = false;
                }
            }

            View itemView = null;

            if (messageItem.getType().equals("manager")) {
                itemView = layoutInflater.inflate(R.layout.manager_chat_box, viewGroup, false);
                tvName = itemView.findViewById(R.id.tv_name_chat);
                tvMsg = itemView.findViewById(R.id.tv_manager_chat);
                tvTime = itemView.findViewById(R.id.tv_time_chat);

                tvName.setText(getString(R.string.app_name_kr));
                tvMsg.setText(messageItem.getMessage());
                tvTime.setText(messageItem.getTime());
                if (position != 0) {
                    if (isPreviousTypeSame) {
                        tvName.setVisibility(View.GONE);
                    } else {
                        tvName.setVisibility(View.VISIBLE);
                    }
                }
                if (position < arrListMessage.size() - 1) {
                    if (isNextTimeSame) {
                        tvTime.setVisibility(View.GONE);
                        if (!isNextTypeSame) {
                            tvTime.setVisibility(View.VISIBLE);
                        } else {
                            tvTime.setVisibility(View.GONE);
                        }
                    } else {
                        tvTime.setVisibility(View.VISIBLE);
                    }
                }

            } else {
                itemView = layoutInflater.inflate(R.layout.user_chat_box, viewGroup, false);
                tvMsg = itemView.findViewById(R.id.tv_user_chat);
                tvTime = itemView.findViewById(R.id.tv_time_chat);

                tvMsg.setText(messageItem.getMessage());
                tvTime.setText(messageItem.getTime());
                if (position < arrListMessage.size() - 1) {
                    if (isNextTimeSame) {
                        tvTime.setVisibility(View.GONE);
                        if (!isNextTypeSame) {
                            tvTime.setVisibility(View.VISIBLE);
                        } else {
                            tvTime.setVisibility(View.GONE);
                        }
                    } else {
                        tvTime.setVisibility(View.VISIBLE);
                    }
                }
            }
            return itemView;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
