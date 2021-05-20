package com.harshit.letschat.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.harshit.letschat.GroupChat;
import com.harshit.letschat.PersonalChat;
import com.harshit.letschat.R;
import com.harshit.letschat.ZoomImage;
import com.harshit.letschat.model.UniversalMessageList;
import com.harshit.util.ChatpageHelper;
import com.harshit.util.Helper;

import java.util.ArrayList;
import java.util.Random;

//outer class
public class PersonalChatAdapter extends RecyclerView.Adapter<PersonalChatAdapter.MyViewHolder> {

    int  lastMessage = 0;
    private static final String TAG = "PersonalChatAdapter";
    ArrayList<UniversalMessageList> lists;
    Context context;
    Activity activity;
    String uid;

    public PersonalChatAdapter(ArrayList<UniversalMessageList> list, Context context, Activity activity) {
        this.activity = activity;
        this.lists = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_personal_chat, parent, false);
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

        holder.documentSenderFileType.setVisibility(View.GONE);
        holder.documentReceiverFileName.setVisibility(View.GONE);
        holder.documentReceiverImage.setVisibility(View.GONE);
        holder.documentSenderFileTime.setVisibility(View.GONE);
        holder.documentSenderFileName.setVisibility(View.GONE);
        holder.documentSenderImage.setVisibility(View.GONE);
        holder.documentReceiverFileTime.setVisibility(View.GONE);
        holder.documentReceiverFileType.setVisibility(View.GONE);
        holder.documentReceiverName.setVisibility(View.GONE);

        holder.documentReceiverView.setVisibility(View.GONE);
        holder.documentSenderView.setVisibility(View.GONE);


        //every data
        String documentType = lists.get(position).getFileType();
        String fileName = lists.get(position).getFileName();
        String time = Helper.getTime(lists.get(position).getTime());
        String message = lists.get(position).getMessage();


        //if UUID is mine //that mean i m sender
        if (senderId.equals(uid)) {

            //checking if message is of type image
            //if yes then we will only visible senderImage layout
            if (messageType.equals("image")) {
                holder.senderImage.setVisibility(View.VISIBLE);
                //loading image with glide
                ChatpageHelper.setImage(context,holder.senderImage,message);
            }
            else if(messageType.equals("document")){
                holder.documentSenderImage.setVisibility(View.VISIBLE);
                holder.documentSenderFileName.setVisibility(View.VISIBLE);
                holder.documentSenderFileTime.setVisibility(View.VISIBLE);
                holder.documentSenderFileType.setVisibility(View.VISIBLE);
                holder.documentSenderView.setVisibility(View.VISIBLE);


                holder.documentSenderFileName.setText(fileName);
                holder.documentSenderFileType.setText(documentType);
                holder.documentSenderFileTime.setText(time);

                ChatpageHelper.setDocument(holder.documentSenderImage,documentType,context);            }
            //if not then we will only visible senderMessage layout
            else {
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.senderMessage.setVisibility(View.VISIBLE);

                holder.senderMessage.setText(message);
                holder.senderTime.setText(time);
            }

        }

        //if UUID is not mine //that mean it is not my message
        else {

            if (messageType.equals("image")) {
                holder.receiverImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context)
                            .load(message)
                            .into(holder.receiverImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(messageType.equals("document")) {

                holder.receiverImageName.setVisibility(View.VISIBLE);
                holder.documentReceiverFileName.setVisibility(View.VISIBLE);
                holder.documentReceiverImage.setVisibility(View.VISIBLE);
                holder.documentReceiverFileType.setVisibility(View.VISIBLE);
                holder.documentReceiverFileTime.setVisibility(View.VISIBLE);
                holder.documentReceiverView.setVisibility(View.VISIBLE);

                holder.documentReceiverFileName.setText(fileName);
                holder.documentReceiverFileType.setText(documentType);
                holder.documentReceiverFileTime.setText(time);

                ChatpageHelper.setDocument(holder.documentReceiverImage,documentType,context);
            }
            else {
                holder.receiver.setVisibility(View.VISIBLE);
                holder.recName.setVisibility(View.VISIBLE);
                holder.recMessage.setVisibility(View.VISIBLE);
                holder.recTime.setVisibility(View.VISIBLE);
                holder.recMessage.setText(message);
                holder.recName.setText(lists.get(position).getSenderName());
                holder.recTime.setText(time);
                Random r = new Random();
                holder.recName.setTextColor(context.getResources().getColor(color[r.nextInt(color.length - 1)]));

            }
        }

        holder.senderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((GroupChat)activity).lastMessageMove(lastMessage);
                String imageUrl = lists.get(position).getMessage();
                Intent it = new Intent(context, ZoomImage.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("imageUrl",imageUrl);
                context.startActivity(it);
            }
        });
        holder.receiverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((GroupChat)activity).lastMessageMove(lastMessage);
                String imageUrl = lists.get(position).getMessage();
                Intent it = new Intent(context, ZoomImage.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("imageUrl",imageUrl);
                context.startActivity(it);
            }
        });

        Log.d(TAG, holder.getAdapterPosition()+"");

        if(lists.size() >=7 && holder.getAdapterPosition() <= lists.size() - 10)
            ((PersonalChat)activity).scrollDown.setVisibility(View.VISIBLE);
        else
            ((PersonalChat)activity).scrollDown.setVisibility(View.GONE);

        holder.senderMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deletMessage(holder.getAdapterPosition());
                return false;
            }
        });

        holder.senderImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deletMessage(holder.getAdapterPosition());
                return false;
            }
        });

        holder.documentSenderView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deletMessage(holder.getAdapterPosition());
                return false;
            }
        });

        holder.documentSenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_VIEW,Uri.parse(lists.get(position).getMessage()));
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });


    }

    public void deletMessage(int key ) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Message")
                .setMessage("Dou you want to delete message permanently? ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((GroupChat)activity).deleteMessage(lists.get(key).getKey());
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

        ImageView documentSenderImage;
        ImageView documentReceiverImage;
        TextView documentSenderFileType;
        TextView documentReceiverFileType;
        TextView documentSenderFileName;
        TextView documentReceiverFileName;
        TextView documentSenderFileTime;
        TextView documentReceiverFileTime;
        TextView documentReceiverName;
        View documentReceiverView;
        View mainLayout;
        View documentSenderView;



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

            documentSenderImage = itemView.findViewById(R.id.documentSenderImage);
            documentSenderFileName = itemView.findViewById(R.id.senderFileName);
            documentSenderFileType = itemView.findViewById(R.id.senderFileType);
            documentSenderFileTime = itemView.findViewById(R.id.documentSenderTime);

            documentReceiverImage = itemView.findViewById(R.id.documentReceiverImage);
            documentReceiverFileName = itemView.findViewById(R.id.documentFileName);
            documentReceiverName = itemView.findViewById(R.id.documentReceiverName);
            documentReceiverFileType = itemView.findViewById(R.id.documentReceiverFileType);
            documentReceiverFileTime = itemView.findViewById(R.id.documentReceiverTime);

            documentSenderView = itemView.findViewById(R.id.senderDocumentLayout);
            documentReceiverView = itemView.findViewById(R.id.receiverDocumentLayout);

            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }


    public void addNewItem(UniversalMessageList l) {
        lists.add(l);
        notifyDataSetChanged();

    }
    // empty , 1


}
