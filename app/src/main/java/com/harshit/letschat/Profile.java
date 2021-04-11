package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harshit.letschat.Firebase.MyDatabase;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

public class Profile extends AppCompatActivity {


    private static final String TAG = "Profile";
    EditText name;
    EditText status;
    TextView email;
    ImageView back;
    ImageView edit;
    ImageView profileImage;
    ImageView profile_img_btn;
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
        profileImage = findViewById(R.id.profile_img);
        profile_img_btn = findViewById(R.id.profile_img_btn);
        pbar = new ProgressDialog(this);
        pbar.setMessage("Please wait...");
        pbar.setCanceledOnTouchOutside(false);
        pbar.setCancelable(false);
//        pbar.show();

        disableEt();  //1
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserDataFromDatabase();  //2


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag) {
                    pbar.show();
                    MyDatabase.userDetail().child("name").setValue(name.getText().toString());
                    MyDatabase.userDetail().child("status").setValue(status.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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

                } else {
                    enableEt();
                    name.setBackgroundResource(R.drawable.curve_cor);
                    status.setBackgroundResource(R.drawable.curve_cor);
                    edit.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    flag = true;
                }
            }
        });

        profile_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

    }

    private void getUserDataFromDatabase() {
//        MyDatabase.userDetail().keepSynced(true);
        MyDatabase.userDetail().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data != null) {
                    if (data.hasChild("name")) {
                        String myDataName = data.child("name").getValue().toString();
                        Log.d(TAG, myDataName);
                        name.setText(myDataName);

                    }
                    if (data.hasChild("email")) {
                        String myDataEmail = data.child("email").getValue().toString();
                        email.setText(myDataEmail);
                    }
                    if (data.hasChild("status")) {
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



    //
    public void selectImage() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("image/*");
        startActivityForResult(new Intent(Intent.createChooser(it, "Choose Image")), 120);
    }

    //
    public void checkPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), new String(Manifest.permission.READ_EXTERNAL_STORAGE)) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        else {
            selectImage();
        }

    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 && data != null && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            Log.d(TAG, "User");
            uploadImageToStorage(imageUri);

        }
    }

    //
    void uploadImageToStorage(Uri uri){
        pbar.show();
        StorageReference sRef = FirebaseStorage.getInstance().getReference()
                .child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child("profile");
        sRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pbar.dismiss();
                        Log.d(TAG, uri.toString());
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}