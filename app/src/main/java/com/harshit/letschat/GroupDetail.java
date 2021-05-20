package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.MyGroupDetailAdapter;
import com.harshit.letschat.model.GroupList;
import com.harshit.util.ChatpageHelper;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class GroupDetail extends AppCompatActivity {

    ImageView groupImage, editImage;
    ProgressDialog pbar;
    String groupId;
    TextView groupName;
    TextView createdOn;
    String myGroupLink = "https://join.letschat.com/";
    TextView shareLink;
    TextView exitGroup;
    TextView addMember;

    //group member
    RecyclerView recyclerView;
    MyGroupDetailAdapter adapter;
    ArrayList<GroupList> lists;
    String myGroupImage;
    String groupAdmin = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        Intent get = getIntent();
        groupId = get.getStringExtra("groupId");

        pbar = new ProgressDialog(this);
        pbar.setMessage("Please wait...");
        pbar.setCanceledOnTouchOutside(false);
        pbar.setCancelable(false);

        groupImage = findViewById(R.id.groupImage);
        editImage = findViewById(R.id.editImage);
        groupName = findViewById(R.id.groupName);
        createdOn = findViewById(R.id.createdOn);
        shareLink = findViewById(R.id.shareLink);
        addMember = findViewById(R.id.addMember);
        exitGroup = findViewById(R.id.exit);

        getGroupDataFromDatabase();

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGroupLink();
            }
        });

        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitUserFromGroup();
            }
        });

        //show member
        recyclerView = findViewById(R.id.recyclerViewMember);
        lists = new ArrayList<>();
        adapter = new MyGroupDetailAdapter(getApplicationContext(), lists,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), ZoomImage.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("imageUrl",myGroupImage);
                startActivity(it);
            }
        });

        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(),AddUser.class);
                it.putExtra("groupId",groupId);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });



        MyDatabase.myGroups().keepSynced(true);
        loadMyGroupData();

    }

    private void loadMyGroupData() {

        pbar.show();
        MyDatabase.groupDetail().child(groupId).child("member")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    lists.clear();
                    for(DataSnapshot myData : data.getChildren()) {
                        String groupKey = myData.getKey();
                        lists.add(new GroupList(groupKey));
                    }
                    adapter.addNewItem(lists); //sending to adapter
                }

                pbar.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                pbar.dismiss();
            }
        });


    }

    private void exitUserFromGroup() {
        pbar.show();
        MyDatabase.groupDetail().child(groupId).child("member").child(FirebaseAuth.getInstance().getUid())
                .removeValue();
        MyDatabase.myGroups().child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               pbar.dismiss();
               finish();
            }
        });

    }

    private void shareGroupLink() {
        String link = "Join Group using code : "+groupId+"\n Or\n"+"Join through link : \n";
        Intent it = new Intent();
        it.setAction(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_SUBJECT,"Choose One");
        it.putExtra(Intent.EXTRA_TEXT,link + myGroupLink+groupId);
        startActivity(Intent.createChooser(it,"Select"));
    }


    private void getGroupDataFromDatabase() {
//        MyDatabase.userDetail().keepSynced(true);
        MyDatabase.groupDetail().child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data != null) {
                    if (data.hasChild("name")) {
                        String myDataName = data.child("name").getValue().toString();
                        groupName.setText(myDataName);

                    }
                    if (data.hasChild("createdOn")) {
                        String myDataName = data.child("createdOn").getValue().toString();
                        createdOn.setText(myDataName);

                    }
                    if (data.hasChild("image")) {
                        myGroupImage = data.child("image").getValue().toString();
                        try {
                            ChatpageHelper.setImage(getApplicationContext(),groupImage,myGroupImage);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (data.hasChild("admin")) {
                        groupAdmin = data.child("admin").getValue().toString();
                    }
                    pbar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbar.dismiss();
            }
        });
    }


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        else {
            selectImage();
        }
    }

    public void selectImage() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("image/*");
        startActivityForResult(new Intent(Intent.createChooser(it, "Choose Image")), 120);
    }



    //result of image you select from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 && data != null && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
//            profileImage.setImageURI(imageUri);
            //image set successfully
            uploadImageToStorage(imageUri);

        }
    }

    //upload image to Firebase Storage
    void uploadImageToStorage(Uri uri) {
        pbar.show();
        StorageReference sRef = FirebaseStorage.getInstance().getReference()
                .child("users").child("group").child(groupId)
                .child("groupImage")
                .child(System.currentTimeMillis()+".jpg");

        sRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image url
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //uri -> download url
                                        pbar.dismiss();
                                        uploadImageUrlToDatabase(uri.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot data) {

                        long fileSize = data.getTotalByteCount();
                        long fileTransfer = data.getBytesTransferred();

                        int per = (int)(fileTransfer * 100 /fileSize);
                        pbar.setMessage("Uploading is "+per+"%"+" done");
                    }
                });
        //260 - 340   0 ->0%, 200kb -> 0%, 500kb -> 0% 700kb    , 0 , 100
        //450 -> 0 ,  66,  100
        //4500 -> 0 , 5 ,  18, 65, 88, 100
    }

    //after successful uploading fetch the url of image from storage and put in firebase database
    private void uploadImageUrlToDatabase(String url) {
        pbar.show();
        pbar.setMessage("Updating group");
        MyDatabase.groupDetail().child(groupId).child("image")
                .setValue(url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(getApplicationContext(), "Image changed successfully").show();
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

    public String getAdmin() {
        return groupAdmin;
    }

    public String getGroupId() {
        return groupId;
    }

}