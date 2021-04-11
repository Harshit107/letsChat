package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.UniversalRecyclerViewAdapter;
import com.harshit.letschat.model.UniversalMessageList;

import java.util.ArrayList;
import java.util.HashMap;

public class UniversalRoom extends AppCompatActivity {

    private static final String TAG = "UniversalChat";
    ImageView back;
    ImageView profilePic;
    ImageView send;

    TextView name;
    TextView lastSeen;
    EditText typeUrMessage;

    RecyclerView recyclerView;
    public UniversalRecyclerViewAdapter universalAdapter;

    ArrayList<UniversalMessageList> list;

    String myName = "";
    String myId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_room);

        init();  //1
        list = new ArrayList<>(); //1234564
        universalAdapter = new UniversalRecyclerViewAdapter(list,getApplicationContext(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(universalAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(typeUrMessage.getText().toString().trim());
                typeUrMessage.setText("");

            }
        });
        getSenderProfile(); //2
        getNewMessage(); //3


    }

    private void getNewMessage() {
        MyDatabase.universalChatRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                if (data.exists()) {
                    String key = data.getKey();
                    String message = "";
                    String time = "";
                    String senderId="";
                    String senderImage = "";
                    String senderName = "";

                    if (data.hasChild("message"))
                        message = data.child("message").getValue().toString();

                    if (data.hasChild("time"))
                        time = data.child("time").getValue().toString();

                    if (data.hasChild("senderName"))
                        senderName = data.child("senderName").getValue().toString();

                    if (data.hasChild("senderImage"))
                        senderImage = data.child("senderImage").getValue().toString();

                    if (data.hasChild("senderId"))
                        senderId = data.child("senderId").getValue().toString();
//                        Log.d(TAG, "Key ="+key+" message =  "+message+" Time = "+time+"\n\n");
//                        list.add();
                    universalAdapter.addNewItem(new UniversalMessageList(message,key,time,senderName,senderImage,senderId, myId));
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                Log.d(TAG,data.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot data) {
                Log.d(TAG,data.toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                Log.d(TAG,data.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,error.toString());

            }
        });



    }

    private void myMessage() {
        //previous
        MyDatabase.universalChatRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //we add 1 data
                list.clear();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //present
        MyDatabase.universalChatRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getSenderProfile() {

        MyDatabase.userDetail().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {

                    if(data.hasChild("name"))
                        myName = data.child("name").getValue().toString();
                    myId = data.getKey();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void sendMessage(final String message) {


        final String messageKey = MyDatabase.universalChatRef().push().getKey();
        if (message.isEmpty())
            Toast.makeText(getApplicationContext(), "Message cannot be Empty", Toast.LENGTH_LONG).show();
        else {

            final HashMap<String, Object> userData = new HashMap<>();
            userData.put("senderName", myName);
            userData.put("message", message);
            userData.put("time","123");
            userData.put("messageId", messageKey);
            userData.put("senderId", myId);
            userData.put("senderImage", "default");
            userData.put("type", "message");

            MyDatabase.universalChatRef().setValue(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            /*
            -> message
                ->name : Harshit
                ->uuid : 123
                -> image :


             */


        }

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

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        MyDatabase.universalChatRef().addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.exists()) {
//                    MessageList message = dataSnapshot.getValue(MessageList.class);
//                    list.add(message);
//                    universalAdapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "No message found", Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//
//    }

}