package com.harshit.letschat.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyDatabase {

    static DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static DatabaseReference userDetail () {
        return mRef.child("users").child("detail").child(mAuth.getUid());
    }

}
