package us.tier5.u_rang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;

    ImageView backImage;
    ImageView ivTag;
    Animation zoom_out;
    Animation fade_in;
    TextView tvTag2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        backImage = (ImageView) findViewById(R.id.backImage);
        zoom_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);

        backImage.startAnimation(zoom_out);

        ivTag = (ImageView) findViewById(R.id.ivTag);

        ivTag.setVisibility(View.INVISIBLE);

        tvTag2 = (TextView) findViewById(R.id.tvTag2);
        tvTag2.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                ivTag.setVisibility(View.VISIBLE);
                tvTag2.setVisibility(View.VISIBLE);
                ivTag.startAnimation(fade_in);
                tvTag2.startAnimation(fade_in);

            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
                int seen_get_started = prefs.getInt("seen_get_started", 0);

                if (seen_get_started != 0)
                {
                    Intent i = new Intent(Splash.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(Splash.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
