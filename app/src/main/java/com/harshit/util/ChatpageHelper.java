package com.harshit.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.harshit.letschat.R;

public class ChatpageHelper {

    public static final String TAG = "ChatHelper";

    public static void setDocument(ImageView image, String type, Context context) {
        Log.d(TAG, type);
        if ("pdf".equals(type)) {
            image.setImageResource(R.drawable.pdf);
        } else {
            image.setImageResource(R.drawable.document);
        }

    }

    public static void setImage(Context context,ImageView imageView,String message) {

        try {
            Glide.with(context)
                    .load(message)
                    .placeholder(R.drawable.loadindimage)
                    .into(imageView);

        } catch (Exception e) {
            Glide.with(context)
                    .load(message)
                    .placeholder(R.drawable.loadindimage)
                    .into(imageView);
            e.printStackTrace();
        }

    }


}
