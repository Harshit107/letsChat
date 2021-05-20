package com.harshit.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.adapter.MyGroupAdapter;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MyGroup extends AppCompatActivity {

    private static final String TAG = "MyGroup";
    RecyclerView recyclerView;
    MyGroupAdapter adapter;
    ArrayList<GroupList> lists;
    LinearLayout noLayout;
    ProgressDialog pbar;
    Button create,join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        pbar = new ProgressDialog(this);
        pbar.setMessage("Please wait...");
        pbar.setCanceledOnTouchOutside(false);

        noLayout = findViewById(R.id.noGroupLayout);
        create = findViewById(R.id.create);
        join = findViewById(R.id.join);

        recyclerView = findViewById(R.id.recyclerView);
        lists = new ArrayList<>();
        adapter = new MyGroupAdapter(getApplicationContext(), lists, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), JoinGoup.class);
                startActivity(it);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toasty.success(getApplicationContext(),"You just clicked", Toasty.LENGTH_LONG).show();
                Intent it = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(it);
            }
        });

        MyDatabase.myGroups().keepSynced(true);
        loadMyGroupData();


    }

    private void loadMyGroupData() {
        //users->group->myId-| (key = MyId  value => Data ), data -> children => (key-GroupUniqueId , value-TimeStamp)
        //-> database -> datafetch -> databack (list add)
        pbar.show();
       MyDatabase.myGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    lists.clear();
                    for(DataSnapshot myData : data.getChildren()) {
                        String groupKey = myData.getKey();
                        lists.add(new GroupList(groupKey));
                    }
                    adapter.addNewItem(lists); //sending to adapter
                    noLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
                else {
                    noLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                pbar.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                pbar.dismiss();
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