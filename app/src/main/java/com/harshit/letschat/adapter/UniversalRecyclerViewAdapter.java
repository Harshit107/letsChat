package com.harshit.letschat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.harshit.letschat.R;
import com.harshit.letschat.model.UniversalMessageList;

import java.util.ArrayList;
import java.util.Random;

//outer class
public class UniversalRecyclerViewAdapter extends RecyclerView.Adapter<UniversalRecyclerViewAdapter.MyViewHolder>{

    private static final String TAG = "UniversalAdapter";
    ArrayList<UniversalMessageList> lists;
    Context context;
    Activity activity;
    String uid;

    public UniversalRecyclerViewAdapter(ArrayList<UniversalMessageList> list, Context context, Activity activity) {
        this.activity = activity;
        this.lists = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_universal_chat, parent, false);
        uid = FirebaseAuth.getInstance().getUid();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String senderId = lists.get(position).getSenderId();

        int[] color = new int[]{R.color.colorAccent,R.color.green,R.color.yellow,R.color.orange,R.color.lightOrange,R.color.lightyellow};

            Log.d(TAG, position+" S->"+senderId+" My->"+uid);

            holder.sender.setVisibility(View.GONE);
            holder.senderMessage.setVisibility(View.GONE);
            holder.senderTime.setVisibility(View.GONE);
            holder.receiver.setVisibility(View.GONE);
            holder.recName.setVisibility(View.GONE);
            holder.recTime.setVisibility(View.GONE);
            holder.recMessage.setVisibility(View.GONE);
//            holder.s.setVisibility(View.GONE);

            if (senderId.equals(uid)) {
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.senderMessage.setVisibility(View.VISIBLE);

                holder.senderMessage.setText(lists.get(position).getMessage());
                holder.senderTime.setText(lists.get(position).getTime());
            } else {
                holder.receiver.setVisibility(View.VISIBLE);
                holder.recName.setVisibility(View.VISIBLE);
                holder.recMessage.setVisibility(View.VISIBLE);
                holder.recTime.setVisibility(View.VISIBLE);

                holder.recMessage.setText(lists.get(position).getMessage());
                holder.recName.setText(lists.get(position).getSenderName());
                holder.recTime.setText(lists.get(position).getTime());
            Random r = new Random();
            holder.recName.setTextColor(context.getResources().getColor(color[r.nextInt(color.length-1)]));
            }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "AdapPos" + holder.getAdapterPosition() + ", senderId : " + lists.get(holder.getAdapterPosition()).getSenderId() + ", myID : " + FirebaseAuth.getInstance().getUid());
                }
            });


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }



    //inner class
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView senderMessage;
        TextView senderTime;
        TextView recName;
        TextView recMessage;
        TextView recTime;
        View receiver;
        View sender;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.senderMessage);
            senderTime = itemView.findViewById(R.id.senderTime);

            recMessage = itemView.findViewById(R.id.receivermessage);
            recName = itemView.findViewById(R.id.receiverName);
            recTime = itemView.findViewById(R.id.receivertime);

            receiver = itemView.findViewById(R.id.receiver);
            sender = itemView.findViewById(R.id.sender);
        }
    }

    public void renewList(ArrayList<UniversalMessageList> lists){
        this.lists = lists;
//        notifyDataSetChanged();
    }

    public void addNewItem(UniversalMessageList l){
       lists.add(l);
       notifyDataSetChanged();

    }

}
