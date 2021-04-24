package com.harshit.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.GroupChatRecyclerViewAdapter;
import com.harshit.letschat.adapter.UniversalRecyclerViewAdapter;
import com.harshit.letschat.model.UniversalMessageList;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {

    private static final String TAG = "GroupChat";
    ImageView back;
    CircleImageView profilePic;
    ImageView send;
    TextView name;
    TextView lastSeen;
    EditText typeUrMessage;
    RecyclerView recyclerView;
    public GroupChatRecyclerViewAdapter groupAdapter;
    ArrayList<UniversalMessageList> list;
    String myName = "";
    String myId = "";

    //
    Intent get;
    String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat );

        get = getIntent();
        groupId = get.getStringExtra("groupId");



        init();  //1
        MyDatabase.groupChat().child(groupId).keepSynced(true);

        findGroupDetail(groupId);

        list = new ArrayList<>(); //1234564
        groupAdapter = new GroupChatRecyclerViewAdapter(list,getApplicationContext(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);

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

    private void findGroupDetail(String groupId) {
        MyDatabase.groupDetail().child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    //forName
                    if(data.child("name").getValue() != null){
                        String groupName = data.child("name").getValue().toString();
                       //to set group name
                        name.setText(groupName);

                    }
                    //forImage
                    if(data.child("image").getValue() != null){
                        String groupImage = data.child("image").getValue().toString(); //image in form of link
                        try {
                            Glide.with(getApplicationContext())
                                    .load(groupImage)
                                    .placeholder(R.drawable.logo)
                                    .into(profilePic);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void getNewMessage() {
        MyDatabase.groupChat().child(groupId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                if (data.exists()) {
                    String key = data.getKey();
                    String message = "";
                    String time = "";
                    String senderId="";
                    String senderImage = "";
                    String senderName = "";
                    String type = "message";

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

                    if (data.hasChild("type"))
                        type = data.child("type").getValue().toString();
//                        Log.d(TAG, "Key ="+key+" message =  "+message+" Time = "+time+"\n\n");
//                        list.add();
                    groupAdapter.addNewItem(new UniversalMessageList(message,key,time,senderName,senderImage,senderId, myId, type));
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

        final String messageKey = MyDatabase.groupChat().child(groupId).push().getKey();
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

            MyDatabase.groupChat().child(groupId).child(messageKey).setValue(userData)
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

}