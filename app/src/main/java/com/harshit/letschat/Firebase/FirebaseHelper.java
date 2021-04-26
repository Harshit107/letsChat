package com.harshit.letschat.Firebase;

//util class
public class FirebaseHelper {

    public static void addMemberToGroup(String groupKey, String myKey) {
        MyDatabase.groupDetail().child(groupKey).child("member")
                .child(myKey).setValue(System.currentTimeMillis());
    }


}
