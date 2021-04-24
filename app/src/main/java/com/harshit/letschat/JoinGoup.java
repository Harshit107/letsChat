package com.harshit.letschat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;

import es.dmoral.toasty.Toasty;

public class JoinGoup extends AppCompatActivity {

    EditText groupId;
    ImageView back;
    ImageView next;
    ProgressDialog pbar;
    TextView errorMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        pbar = new ProgressDialog(this);
        pbar.setCancelable(false);
        pbar.setCanceledOnTouchOutside(false);
        pbar.setMessage("Finding Group Detail..");

        errorMessage = findViewById(R.id.errorMessage);
        groupId = findViewById(R.id.groupId);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myGroupId = groupId.getText().toString();

                if(myGroupId.isEmpty()){
                    groupId.setError("Enter Valid Id");
                    Toasty.error(getApplicationContext(), "Enter Valid Id").show();
                    return;
                }
                pbar.show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                        .child("groups")
                        .child("detail")
                        .child(myGroupId);

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        if(data.exists()) {
                            updateMyGroup(myGroupId);
                        }
                        else{
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText("Group Not Found");
                            groupId.setError("Enter Valid Id");
                            Toasty.error(getApplicationContext(), "Group Id Not Found").show();
                            pbar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pbar.dismiss();
                        Toasty.error(getApplicationContext(), error.getMessage()).show();

                    }
                });


            }
        });




    }

    private void updateMyGroup(String myGroupId) {

        MyDatabase.myGroups().child(myGroupId).setValue(System.currentTimeMillis()+"")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        errorMessage.setText("");
                        errorMessage.setVisibility(View.GONE);
                        Toasty.success(getApplicationContext(),"Group Joined successfully").show();
                        pbar.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        pbar.dismiss();
                    }
                });
    }
}
