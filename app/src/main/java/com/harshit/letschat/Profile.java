package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;

import java.util.HashMap;

public class Profile extends AppCompatActivity {


    private static final String TAG = "Profile";
    EditText name;
    EditText status;
    TextView email;
    ImageView back;
    ImageView edit;

    ProgressDialog pbar;
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.nameEt);
        status = findViewById(R.id.status);
        email = findViewById(R.id.emailEt);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        pbar = new ProgressDialog(this);
        pbar.setMessage("Please wait...");
        pbar.setCanceledOnTouchOutside(false);
        pbar.setCancelable(false);
        pbar.show();

        disableEt();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       getUserDataFromDatabase();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag){
                    pbar.show();
                    MyDatabase.userDetail().child("name").setValue(name.getText().toString());
                    MyDatabase.userDetail().child("status").setValue(status.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pbar.dismiss();
                            flag = false;
                            edit.setImageResource(R.drawable.ic_baseline_edit_24);
                            name.setBackgroundResource(R.color.transparent);
                            status.setBackgroundResource(R.color.transparent);
                            disableEt();
                            getUserDataFromDatabase();
                        }
                    });

                }
                else {
                    enableEt();
                    name.setBackgroundResource(R.drawable.curve_cor);
                    status.setBackgroundResource(R.drawable.curve_cor);
                    edit.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    flag = true;
                }
            }
        });

    }

    private void getUserDataFromDatabase() {
        MyDatabase.userDetail().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data != null) {
                    if(data.hasChild("name")){
                        String myDataName = data.child("name").getValue().toString();
                        Log.d(TAG,myDataName);
                        name.setText(myDataName);

                    }
                    if(data.hasChild("email")){
                        String myDataEmail = data.child("email").getValue().toString();
                        email.setText(myDataEmail);
                    }
                    if(data.hasChild("status")){
                        String myDataStatus = data.child("status").getValue().toString();
                        status.setText(myDataStatus);
                    }
                    edit.setVisibility(View.VISIBLE);
                    pbar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbar.dismiss();
            }
        });
    }

    public void enableEt() {
        name.setEnabled(true);
        status.setEnabled(true);
    }
    public void disableEt() {
        name.setEnabled(false);
        status.setEnabled(false);
        edit.setVisibility(View.INVISIBLE);
    }

}