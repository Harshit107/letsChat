package com.harshit.letschat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.PersonalChat;
import com.harshit.letschat.R;
import com.harshit.letschat.model.GroupList;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {

    int lastMessage = 0;
    private static final String TAG = "SearchUserAdapter";
    Context context;
    ArrayList<GroupList> groupList;
    String groupAdmin = "";
    Activity activity;

    public SearchUserAdapter(Context context, ArrayList<GroupList> lists, Activity activity) {
        this.context = context;
        this.groupList = lists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_group_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String userId = groupList.get(position).getId();
        //searching group detail from every group Id
        MyDatabase.publicDetail().child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    //forName
                    if(data.child("name").getValue() != null){
                        String myUserName = data.child("name").getValue().toString();
                        holder.userName.setText(myUserName);
                    }
                    //forImage
                    if(data.child("image").getValue() != null){
                        String groupImage = data.child("image").getValue().toString(); //image in form of link
                        try {
                            Glide.with(context)
                                    .load(groupImage)
                                    .placeholder(R.drawable.logo)
                                    .into(holder.userImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverId = groupList.get(position).getId();
                Intent it = new Intent(context, PersonalChat.class);
                it.putExtra("receiverId",receiverId );
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });



        holder.userName.setTextColor(context.getResources().getColor(R.color.white));


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void addNewItem(ArrayList<GroupList> lists) {
        this.groupList = lists;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.groupImage);
            userName = itemView.findViewById(R.id.groupName);
            userName.setTextColor(Color.parseColor("#000000"));
        }
    }




}
