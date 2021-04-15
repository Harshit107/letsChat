package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.MyGroupAdapter;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

public class MyGroup extends AppCompatActivity {

    private static final String TAG = "MyGroup";
    RecyclerView recyclerView;
    MyGroupAdapter adapter;
    ArrayList<GroupList> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);
        recyclerView = findViewById(R.id.recyclerView);
        lists = new ArrayList<>();
        adapter = new MyGroupAdapter(getApplicationContext(), lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

       loadMyGroupData();


    }

    private void loadMyGroupData() {
        //users->group->myId-| (key = MyId  value => Data ), data -> children => (key-GroupUniqueId , value-TimeStamp)
        //-> database -> datafetch -> databack (list add)
       MyDatabase.myGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    for(DataSnapshot myData : data.getChildren()) {
                        String groupKey = myData.getKey();
                        lists.add(new GroupList(groupKey));
                    }
                    adapter.addNewItem(lists); //sending to adapter

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        //empty


        // 45 ->
        //      Datbase
        //      datafetch
        //      databack (list-> add)
        //       send data to adapter

        //62

    }
}