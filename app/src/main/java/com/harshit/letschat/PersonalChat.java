package com.harshit.letschat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.PersonalChatAdapter;
import com.harshit.letschat.model.UniversalMessageList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class PersonalChat extends AppCompatActivity {

    private static final String TAG = "PersonalChat";
    ImageView back;
    CircleImageView profilePic;
    ImageView send;
    TextView name;
    TextView lastSeen;
    EditText typeUrMessage;
    RecyclerView recyclerView;
    PersonalChatAdapter personalAdapter;
    ArrayList<UniversalMessageList> list;
    String myName = "";
    String myId = "";
    ProgressDialog pbar;
    ImageView attach;
    Toolbar toolbar;
    View extraItemLayout;
    ImageView image, pdf, document;
    String fileName = "";
    String documentType = "";
    Intent get;
    String receiverID = "";
    private boolean close = true;
    String typeSelected = "message";
    RelativeLayout visibility;
    public ImageView scrollDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_group_chat);

        get = getIntent();
        receiverID = get.getStringExtra("receiverId");
        scrollDown = findViewById(R.id.scrollDowm);


        pbar = new ProgressDialog(this);
        pbar.setMessage("Please wait...");
        pbar.setCanceledOnTouchOutside(false);
        pbar.setCancelable(false);


        init();  //1
        extraItemLayout.setVisibility(View.GONE);
        MyDatabase.personalSenderChat(receiverID).keepSynced(true);

        receiverUserDetail(receiverID);

        list = new ArrayList<>(); //1234564
        personalAdapter = new PersonalChatAdapter(list, getApplicationContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(personalAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(typeUrMessage.getText().toString().trim(), "message");
                typeUrMessage.setText("");
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPDF();
            }
        });


        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDocument();
            }
        });


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), GroupDetail.class);
                ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(PersonalChat.this,
                        profilePic, ViewCompat.getTransitionName(profilePic));
                it.putExtra("groupId", receiverID);
                startActivity(it, option.toBundle());
            }
        });

        getSenderProfile(); //2
        getNewMessage(); //3
//        checkGroup();   //
        lastMessageMove();

        scrollDown.setVisibility(View.GONE);

        scrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(personalAdapter.getItemCount());
            }
        });


//        recyclerView.smoothScrollToPosition(groupAdapter.getItemCount());
//        recyclerView.setNestedScrollingEnabled(false);
//        lastMessageMove();


    }

    public void deleteMessage(String key) {

        MyDatabase.groupChat().child(receiverID).child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(getApplicationContext(),"Message deleted successfully").show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), e.getMessage()).show();
                    }
                });

    }

    private void receiverUserDetail(String groupId) {
        MyDatabase.userDetail().child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()) {
                    //forName
                    if (data.child("name").getValue() != null) {
                        String groupName = data.child("name").getValue().toString();
                        //to set group name
                        name.setText(groupName);

                    }
                    //forImage
                    if (data.child("image").getValue() != null) {
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

//        Query query = MyDatabase.groupChat().child(groupId).limitToLast(5);
        MyDatabase.personalSenderChat(receiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                        if (data.exists()) {
                            String key = data.getKey();
                            String message = "";
                            String time = "";
                            String senderId = "";
                            String senderImage = "";
                            String senderName = "";
                            String type = "message";
                            String myFileName = "";
                            String myDocumentType = "";

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

                            if (data.hasChild("fileName"))
                                myFileName = data.child("fileName").getValue().toString();

                            if (data.hasChild("type"))
                                type = data.child("type").getValue().toString();

                            if (data.hasChild("documentType"))
                                myDocumentType = data.child("documentType").getValue().toString();
//                        Log.d(TAG, "Key ="+key+" message =  "+message+" Time = "+time+"\n\n");
//                        list.add();
                            personalAdapter.addNewItem(new UniversalMessageList(message, key,
                                    time, senderName, senderImage,
                                    senderId, myId,
                                    type, myFileName, myDocumentType));
                            recyclerView.smoothScrollToPosition(personalAdapter.getItemCount());
                            Log.d(TAG, "At New Message");

                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                        Log.d(TAG, "My Data : "+data.toString());
                        recyclerView.smoothScrollToPosition(personalAdapter.getItemCount());

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot data) {
                        Log.d(TAG, data.toString());
                         for (UniversalMessageList myList : list) {
                             if(myList.getKey().equals(data.getKey())){
                                 list.remove(myList);
                                 personalAdapter.notifyDataSetChanged();
                                 break;
                             }
                         }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot data, @Nullable String previousChildName) {
                        Log.d(TAG, data.toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.toString());

                    }
                });


    }

    private void getSenderProfile() {

        MyDatabase.userDetail().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()) {

                    if (data.hasChild("name"))
                        myName = data.child("name").getValue().toString();
                    myId = data.getKey();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendMessage(final String message, String type) {
        typeSelected = "message";
        final String messageKey = MyDatabase.personalSenderChat(receiverID).push().getKey();
        if (message.isEmpty())
            Toast.makeText(getApplicationContext(), "Message cannot be Empty", Toast.LENGTH_LONG).show();
        else {

            final HashMap<String, Object> userData = new HashMap<>();
            userData.put("senderName", myName);
            userData.put("message", message);
            userData.put("time", System.currentTimeMillis());
            userData.put("messageId", messageKey);
            userData.put("senderId", myId);
            userData.put("senderImage", "default");
            userData.put("type", type);
            userData.put("fileName", fileName);
            userData.put("documentType", documentType);

            MyDatabase.personalSenderChat(receiverID).child(messageKey).setValue(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            MyDatabase.personalReceiverChat(receiverID).child(messageKey).setValue(userData);
//                            if(typeSelected.equals("image")){
//                                list.clear();
//                                getNewMessage();
//                            }

                            pbar.dismiss();
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

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        profilePic = findViewById(R.id.profileLogo);
        send = findViewById(R.id.sendBt);
        name = findViewById(R.id.name);
        lastSeen = findViewById(R.id.lastActive);
        typeUrMessage = findViewById(R.id.senderMessage);
        recyclerView = findViewById(R.id.recyclerView);
        attach = findViewById(R.id.attach);
        extraItemLayout = findViewById(R.id.extraItemLayout);
        image = findViewById(R.id.image);
        pdf = findViewById(R.id.pdf);
        document = findViewById(R.id.document);
        visibility = findViewById(R.id.visibility);

    }

    public void lastMessageMove() {

        MyDatabase.personalSenderChat(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(list.size());
                            Log.d(TAG, list.size() + "");
                        }
                    }, 300);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //check self permission and if not granted ask for permission
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else {

            if (close)
                extraItemLayout.setVisibility(View.VISIBLE);
            else
                extraItemLayout.setVisibility(View.GONE);
            close = !close;
        }
    }

    public void selectImage() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("image/*");
        startActivityForResult(new Intent(Intent.createChooser(it,
                "Choose Image")),
                120);
    }

    //result of image you select from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            fileName = getFileName(data.getData());
        }
        //for image
        if (requestCode == 120 && data != null && resultCode == RESULT_OK) {
            typeSelected = "image";
            Uri imageUri = data.getData();
            uploadImageToStorage(imageUri);
        }

        else {
            typeSelected = "document";

            //for pdf
            if (requestCode == 220 && data != null && resultCode == RESULT_OK) {
                documentType = "pdf";
                Log.d(TAG, "Pdf");
                Uri pdfUri = data.getData();
                uploadImageToStorage(pdfUri);
            }
            //document
            else if (requestCode == 520 && data != null && resultCode == RESULT_OK) {
                documentType = fileName.substring(fileName.lastIndexOf(".")+1);
                Uri pdfUri = data.getData();
                uploadImageToStorage(pdfUri);
            }

        }
    }

    //upload image to Firebase Storage
    void uploadImageToStorage(Uri uri) {
        pbar.show();
        StorageReference sRef = FirebaseStorage.getInstance().getReference()
                .child("users").child("group").child(receiverID)
                .child(System.currentTimeMillis() + ".jpg");

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

                        int per = (int) (fileTransfer * 100 / fileSize);
                        pbar.setMessage("Uploading is " + per + "%" + " done");
                    }
                });
        //260 - 340   0 ->0%, 200kb -> 0%, 500kb -> 0% 700kb    , 0 , 100
        //450 -> 0 ,  66,  100
        //4500 -> 0 , 5 ,  18, 65, 88, 100
    }

    //after successful uploading fetch the url of image from storage and put in firebase database
    private void uploadImageUrlToDatabase(String url) {
        pbar.show();
        pbar.setMessage("Uploading to database...");
        sendMessage(url, typeSelected);
    }

    String getFileName(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }
        return displayName;
    }

    //for pdf -> pdf
    //for word -> msword
    //for excel -> application/vnd.ms-excel
    //for any document -> application/*

    public void selectPDF() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("application/pdf");
        startActivityForResult(new Intent(Intent.createChooser(it,
                "Choose Image")),
                220);
    }

    public void selectDocument() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("*/*");
        startActivityForResult(new Intent(Intent.createChooser(it,
                "Choose Image")),
                520);
    }

    @Override
    protected void onStart() {
        super.onStart();
        visibility.setVisibility(View.GONE);
        Log.d(TAG, "GONE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        visibility.setVisibility(View.VISIBLE);
        Log.d(TAG, "VISIBLE");
    }
}