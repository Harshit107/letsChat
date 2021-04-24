package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.harshit.letschat.Firebase.MyDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CreateGroup extends AppCompatActivity {

    EditText groupName;
    ImageView back;
    ImageView next;
    TextView link;
    LinearLayout shareLayout;
    ImageView share;
    ProgressDialog pbar;
    String myGroupLink = "https://join.letschat/";
    String groupGlobalKey = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        pbar = new ProgressDialog(this);
        pbar.setCancelable(false);
        pbar.setCanceledOnTouchOutside(false);
        pbar.setMessage("Creating group...");

        groupName = findViewById(R.id.groupName);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        share = findViewById(R.id.shareImage);
        shareLayout = findViewById(R.id.shareLayout);
        link = findViewById(R.id.link);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mGroupName = groupName.getText().toString();
                //group name should not be empty
                if(mGroupName.isEmpty()) {
                    Toasty.error(getApplicationContext(),"Please Enter Group Name", Toasty.LENGTH_LONG).show();
                    groupName.setError("Field Required");
                    return;
                }
                pbar.show();
                String uniqueKey = MyDatabase.groupDetail().push().getKey();//provide unique Id ->123456
                groupGlobalKey = uniqueKey;
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("admin", FirebaseAuth.getInstance().getUid());
                hashMap.put("name", mGroupName);
                MyDatabase.groupDetail().child(uniqueKey).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MyDatabase.myGroups().child(uniqueKey).setValue(System.currentTimeMillis())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        shareLayout.setVisibility(View.VISIBLE);
                                        Toasty.success(getApplicationContext(), "Group created Successfully", Toast.LENGTH_LONG).show();
                                            link.setText(myGroupLink+uniqueKey);
                                            //String myLink = https://join.group.letschat/uniqueId
//                                            Integer link = myLink.lastIndexOf("/"); 20
//                                            String groupUniqueKey = link.substring(21,link.length())
                                        pbar.dismiss();

                                    }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pbar.dismiss();
                            }
                        });
                    }
                });
            }
        });

        //share link
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLinkWithOtherApp(link.getText().toString());
            }
        });
    }

    private void shareLinkWithOtherApp(String myGroupLink) {

        //Intent Action
        //Intent type
        //Intent data
        //Intent Extra
        String link = "Join Group using code : "+groupGlobalKey+"\n Or\n"+"Join through link : \n";
        Intent it = new Intent();
        it.setAction(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_SUBJECT,"Choose One");
        it.putExtra(Intent.EXTRA_TEXT,link + myGroupLink);
        startActivity(it);
    }

}