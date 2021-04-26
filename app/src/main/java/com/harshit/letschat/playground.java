package com.harshit.letschat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//rough work
class playground {

    //upload data to a particular Node
    public static void main(String[] args) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();//root node reference
        mRef.child("xyz").child("xyz");     //move ref to particular node

        mRef.setValue("value to set");   // Set String value

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("child","abc");            //set multiple value at a time

        //get data from database at particular node

        //one time (not connected)
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //connect to particular node
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //call every time when data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ////connect to particular node -> more specific
        mRef.addChildEventListener(new ChildEventListener() {
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






}
