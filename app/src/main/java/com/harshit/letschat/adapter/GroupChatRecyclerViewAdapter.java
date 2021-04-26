package com.harshit.letschat.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.harshit.letschat.R;
import com.harshit.letschat.model.UniversalMessageList;

import java.util.ArrayList;
import java.util.Random;

//outer class
public class GroupChatRecyclerViewAdapter extends RecyclerView.Adapter<GroupChatRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "GroupChatAdapter";
    ArrayList<UniversalMessageList> lists;
    Context context;
    Activity activity;
    String uid;

    public GroupChatRecyclerViewAdapter(ArrayList<UniversalMessageList> list, Context context, Activity activity) {
        this.activity = activity;
        this.lists = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_group_chat, parent, false);
        uid = FirebaseAuth.getInstance().getUid();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        String senderId = lists.get(position).getSenderId();
        String messageType = lists.get(position).getMessageType();

        int[] color = new int[]{R.color.colorAccent, R.color.green, R.color.yellow, R.color.orange, R.color.lightOrange, R.color.lightyellow};

//            Log.d(TAG, position+" S->"+senderId+" My->"+uid);

        holder.sender.setVisibility(View.GONE);
        holder.senderMessage.setVisibility(View.GONE);
        holder.senderTime.setVisibility(View.GONE);
        holder.receiver.setVisibility(View.GONE);
        holder.recName.setVisibility(View.GONE);
        holder.recTime.setVisibility(View.GONE);
        holder.recMessage.setVisibility(View.GONE);

        holder.receiverImageName.setVisibility(View.GONE);
        holder.receiverImage.setVisibility(View.GONE);
        holder.senderImage.setVisibility(View.GONE);

        //if UUID is mine //that mean i m sender
        if (senderId.equals(uid)) {

            //checking if message is of type image
            //if yes then we will only visible senderImage layout
            if (messageType.equals("image")) {

                holder.senderImage.setVisibility(View.VISIBLE);
                //loading image with glide
                try {
                    Glide.with(context)
                            .load(lists.get(position).getMessage())
                            .placeholder(R.drawable.loadindimage)
                            .into(holder.senderImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //if not then we will only visible senderMessage layout
            else {
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.senderMessage.setVisibility(View.VISIBLE);

                holder.senderMessage.setText(lists.get(position).getMessage());
                holder.senderTime.setText(lists.get(position).getTime());
            }

        }

        //if UUID is not mine //that mean it is not my message
        else {

            if (messageType.equals("image")) {
                holder.receiverImage.setVisibility(View.VISIBLE);
                String message = lists.get(position).getMessage();
                try {
                    Glide.with(context)
                            .load(message)
                            .into(holder.receiverImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                holder.receiver.setVisibility(View.VISIBLE);
                holder.recName.setVisibility(View.VISIBLE);
                holder.recMessage.setVisibility(View.VISIBLE);
                holder.recTime.setVisibility(View.VISIBLE);

                holder.recMessage.setText(lists.get(position).getMessage());
                holder.recName.setText(lists.get(position).getSenderName());
                holder.recTime.setText(lists.get(position).getTime());
                Random r = new Random();
                holder.recName.setTextColor(context.getResources().getColor(color[r.nextInt(color.length - 1)]));

            }
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
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage;
        TextView senderTime;
        TextView recName;
        TextView recMessage;
        TextView recTime;
        View receiver;
        View sender;

        ImageView senderImage;
        ImageView receiverImage;
        TextView receiverImageName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.senderMessage);
            senderTime = itemView.findViewById(R.id.senderTime);
            recMessage = itemView.findViewById(R.id.receivermessage);
            recName = itemView.findViewById(R.id.receiverName);
            recTime = itemView.findViewById(R.id.receivertime);
            receiver = itemView.findViewById(R.id.receiver);
            sender = itemView.findViewById(R.id.sender);

            senderImage = itemView.findViewById(R.id.senderImage);
            receiverImage = itemView.findViewById(R.id.receiverImage);
            receiverImageName = itemView.findViewById(R.id.imageReceiverName);
        }
    }

    public void renewList(ArrayList<UniversalMessageList> lists) {
        this.lists = lists;
//        notifyDataSetChanged();
    }

    public void addNewItem(UniversalMessageList l) {
        lists.add(l);
        notifyDataSetChanged();

    }
    // empty , 1

}
