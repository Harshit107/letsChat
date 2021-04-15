package com.harshit.letschat.adapter;

import android.content.Context;
import android.util.Log;
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
import com.harshit.letschat.MyGroup;
import com.harshit.letschat.R;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.MyViewHolder> {

    private static final String TAG = "MyGroupAdapter";
    Context context;
    ArrayList<GroupList> groupList;

    public MyGroupAdapter(Context context, ArrayList<GroupList> lists) {
        this.context = context;
        this.groupList = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_group_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String groupId = groupList.get(position).getId();
        //searching group detail from every group Id
        MyDatabase.groupDetail().child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if(data.exists()) {
                    //forName
                    if(data.child("name").getValue() != null){
                        String groupName = data.child("name").getValue().toString();
                        holder.groupName.setText(groupName);
                    }
                    //forImage
                    if(data.child("image").getValue() != null){
                        String groupImage = data.child("image").getValue().toString(); //image in form of link
                        try {
                            Glide.with(context)
                                    .load(groupImage)
                                    .placeholder(R.drawable.logo)
                                    .into(holder.groupImage);

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


        holder.groupName.setText(groupList.get(position).getGroupName());


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

        CircleImageView groupImage;
        TextView groupName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
        }
    }
}
