package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.UniversalRecyclerViewAdapter;
import com.harshit.letschat.model.UniversalMessageList;

import java.util.ArrayList;
import java.util.HashMap;

public class UniversalChat extends AppCompatActivity {

    private static final String TAG = "UniversalChat";
    ImageView back;
    ImageView profilePic;
    ImageView send;

    TextView name;
    TextView lastSeen;
    EditText typeUrMessage;

    RecyclerView recyclerView;
    UniversalRecyclerViewAdapter universalAdapter;
    ArrayList<UniversalMessageList> list;

    String senderName = "";
    String senderId = "";
    String senderImage = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_chat);

        init();
        list = new ArrayList<>(); //1234564
        universalAdapter = new UniversalRecyclerViewAdapter(list, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(universalAdapter);
        typeUrMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                send.setEnabled(editable.toString().length() != 0);
            }
        });

        getSenderProfile();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageKey = MyDatabase.universalChatRef().push().getKey();

                HashMap<String, Object> hm = new HashMap<>();
                String myMessage = typeUrMessage.getText().toString().trim();
                long time = System.currentTimeMillis();

                hm.put("message", myMessage);
                hm.put("time", time);
                hm.put("senderName", senderName);
                hm.put("senderId", senderId);
                hm.put("senderImage", senderImage);
                hm.put("messageKey", messageKey);

                typeUrMessage.setText("");
                MyDatabase.universalChatRef().child(messageKey).setValue(hm)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

            }
        });

        getMessage();

    }

    private void getSenderProfile() {

        MyDatabase.userDetail().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {

                    if(data.hasChild("name"))
                        senderName = data.child("name").getValue().toString();
                    senderId = data.getKey();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getMessage() {
        MyDatabase.universalChatRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()) {
                    list.clear();
                    for (DataSnapshot myData : data.getChildren()) {

                        String key = myData.getKey();
                        String message = "";
                        String time = "";
                        String senderId="";
                        String senderImage = "";
                        String senderName = "";

                        if (myData.hasChild("message"))
                            message = myData.child("message").getValue().toString();

                        if (myData.hasChild("time"))
                            time = myData.child("time").getValue().toString();

                        if (myData.hasChild("senderName"))
                            senderName = myData.child("senderName").getValue().toString();

                        if (myData.hasChild("senderImage"))
                            senderImage = myData.child("senderImage").getValue().toString();

                        if (myData.hasChild("senderId"))
                            senderId = myData.child("senderId").getValue().toString();


                        Log.d(TAG, "Key ="+key+" message =  "+message+" Time = "+time+"\n\n");
                        list.add(new UniversalMessageList(message,key,time,senderName,senderImage,senderId));
                    }
                    universalAdapter.renewList(list);
                }
                else{
                    Log.d(TAG, "here");
                    list.clear();
                    universalAdapter.renewList(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        back = findViewById(R.id.back);
        profilePic = findViewById(R.id.profileLogo);
        send = findViewById(R.id.sendBt);
        name = findViewById(R.id.name);
        lastSeen = findViewById(R.id.lastActive);
        typeUrMessage = findViewById(R.id.senderMessage);
        recyclerView = findViewById(R.id.recyclerView);

    }
}