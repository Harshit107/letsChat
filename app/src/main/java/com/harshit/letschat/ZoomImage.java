package com.harshit.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

public class ZoomImage extends AppCompatActivity {

    ZoomageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        Intent get = getIntent();
        String url = get.getStringExtra("imageUrl");
        imageView = findViewById(R.id.zoomImage);


        try {
            Glide.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.loadindimage)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}