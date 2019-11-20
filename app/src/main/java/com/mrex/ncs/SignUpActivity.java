package com.mrex.ncs;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private EditText etID, etPW, etPW2, etPhone, etVeriCode;
    private String userID, checkID;
    private Boolean isDuplicateID;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etID = findViewById(R.id.et_id);
        etPW = findViewById(R.id.et_pw);
        etPW2 = findViewById(R.id.et_pw2);
        etPhone = findViewById(R.id.et_phone);
        etVeriCode = findViewById(R.id.et_verification);

        checkDuplicateID();
    }

    private void checkDuplicateID() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        DatabaseReference userRef = rootRef.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot dds : ds.getChildren()) {
                        user = dds.getValue(User.class);
                        checkID = user.getId();
                        if (checkID.equals(userID)) {
                            isDuplicateID = true;
                        } else isDuplicateID = false;

                        //TODO

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
