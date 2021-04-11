package com.harshit.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.harshit.letschat.adapter.MyGroupAdapter;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

public class MyGroup extends AppCompatActivity {

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



    }
}