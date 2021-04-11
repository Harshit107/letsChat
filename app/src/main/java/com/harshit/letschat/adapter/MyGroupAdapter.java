package com.harshit.letschat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harshit.letschat.MyGroup;
import com.harshit.letschat.R;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.MyViewHolder> {

    Context context;
    ArrayList<GroupList> lists;

    public MyGroupAdapter(Context context, ArrayList<GroupList> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.groupName.setText(lists.get(position).getGroupName());

        /*
            list -> groupName,
                    groupImage,
                    id

         */




        try {
            Glide.with(context)
                    .load(lists.get(position).getGroupImage())
                    .into(holder.groupImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
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
