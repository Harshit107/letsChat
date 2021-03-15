package com.harshit.letschat;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScrActivity extends AppCompatActivity {
    private ImageView logoSplash, letsChatText, logoWhite;
    private Animation anim1, anim2, anim3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_scr);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        init();
        logoSplash.startAnimation(anim1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                logoSplash.startAnimation(anim2);
                logoSplash.setVisibility(View.GONE);
                logoWhite.startAnimation(anim3);
                letsChatText.startAnimation(anim3);
                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoWhite.setVisibility(View.VISIBLE);
                        letsChatText.setVisibility(View.VISIBLE);
                        finish();
                        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                            startActivity(new Intent(SplashScrActivity.this, LoginPage.class));
                        }
                        else
                            startActivity(new Intent(SplashScrActivity.this,MainActivity.class));
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }


    private void init(){

        logoSplash = findViewById(R.id.ivLogoSplash);
        logoWhite = findViewById(R.id.ivLogoWhite);
        letsChatText = findViewById(R.id.ivCHTtext);

        anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout);
        anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
    }


}
