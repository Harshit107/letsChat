package com.harshit.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.harshit.letschat.Firebase.MyDatabase;

import java.util.HashMap;

public class CreateGroup extends AppCompatActivity {

    EditText groupName;
    ImageView back;
    ImageView next;
    TextView link;
    LinearLayout shareLayout;
    ImageView share;

    String myGroupLink = "https://join.group.letschat/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

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
                String uniqueKey = MyDatabase.groupDetail().push().getKey();//provide unique Id ->123456
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("admin", FirebaseAuth.getInstance().getUid());
                hashMap.put("name", mGroupName);
                MyDatabase.groupDetail().child(uniqueKey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MyDatabase.myGroups().child(uniqueKey).setValue(System.currentTimeMillis())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        shareLayout.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Group created Successfully", Toast.LENGTH_LONG).show();
                                        link.setText(myGroupLink+uniqueKey);

                                    }
                        });
                    }
                });
            }
        });

    }
}