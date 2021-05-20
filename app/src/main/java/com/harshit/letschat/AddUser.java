package com.harshit.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.adapter.AddUserAdapter;
import com.harshit.letschat.adapter.SearchUserAdapter;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

public class AddUser extends AppCompatActivity {

    RecyclerView userRecycle;
    RelativeLayout noUserFound;

    EditText searchBox;
    ImageView button;
    RelativeLayout progress;
    AddUserAdapter searchUserAdapter;
    ArrayList<GroupList> list ;
    String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        Intent get = getIntent();
        groupId = get.getStringExtra("groupId");

        init();

        noUserFound.setVisibility(View.GONE);
        userRecycle.setVisibility(View.GONE);
        list = new ArrayList<>();
        searchUserAdapter = new AddUserAdapter(getApplicationContext(),list,this, groupId);
        userRecycle.setLayoutManager(new LinearLayoutManager(this));
        userRecycle.setAdapter(searchUserAdapter);
        progress.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchVal = searchBox.getText().toString();
                findUserFromDB(searchVal);
            }
        });


    }

    void findUserFromDB(String search) {

        list.clear();
        noUserFound.setVisibility(View.GONE);
        searchUserAdapter.notifyDataSetChanged();
        progress.setVisibility(View.VISIBLE);
        userRecycle.setVisibility(View.VISIBLE);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child("detail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot myData) {

                for(DataSnapshot data : myData.getChildren()) {
                    if(data.child("name").exists()) {
                        String name = data.child("name").getValue().toString();
                        if(name.toLowerCase().contains(search.toLowerCase())) {
                            list.add(new GroupList(data.getKey()));
                            searchUserAdapter.notifyDataSetChanged();
                        }
                    }

                }

                if(list.size() == 0) {
                    noUserFound.setVisibility(View.VISIBLE);
                    userRecycle.setVisibility(View.GONE);

                }

                progress.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public void init() {
        userRecycle = findViewById(R.id.userRecycle);
        noUserFound = findViewById(R.id.noUserFound);
        searchBox = findViewById(R.id.searchBox);
        button = findViewById(R.id.search_bt);
        progress = findViewById(R.id.progress);
    }
}