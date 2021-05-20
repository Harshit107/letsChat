package com.harshit.letschat.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harshit.letschat.Firebase.MyDatabase;
import com.harshit.letschat.GroupChat;
import com.harshit.letschat.GroupDetail;
import com.harshit.letschat.R;
import com.harshit.letschat.model.GroupList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MyGroupDetailAdapter extends RecyclerView.Adapter<MyGroupDetailAdapter.MyViewHolder> {

    int lastMessage = 0;
    private static final String TAG = "MyGroupAdapter";
    Context context;
    ArrayList<GroupList> groupList;
    String groupAdmin = "";
    Activity activity;

    public MyGroupDetailAdapter(Context context, ArrayList<GroupList> lists, Activity activity) {
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String userUUID = groupList.get(position).getId();
                String groupAdmin = ((GroupDetail)activity).getAdmin();
                String myId = FirebaseAuth.getInstance().getUid();

                Log.d(TAG, userUUID+"\n"+ groupAdmin+"\n"+ myId);

                if(groupAdmin.equals(myId) && !userUUID.equals(myId)) {
                    deleteMember(userId);
                }

                return false;
            }
        });


        holder.groupName.setTextColor(context.getResources().getColor(R.color.white));


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
            groupName.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void deleteMember(String uid ) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Message")
                .setMessage("Dou you want to remove member permanently? ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String groupId = ((GroupDetail)activity).getGroupId();
                            MyDatabase.groupDetail().child(groupId).child("member").child(uid)
                                    .removeValue();
                            MyDatabase.userGroup().child(uid).child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toasty.success(context,"Member removed successfully").show();
                                }
                            });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }


}
