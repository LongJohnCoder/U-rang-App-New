package us.tier5.u_rang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class Carousel extends AppCompatActivity {

    CarouselView carouselView;
    Button btnGetStarted;

    int[] sampleImages = {R.drawable.anim_one, R.drawable.anim_two, R.drawable.anim_three, R.drawable.anim_four};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);

        SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
        editor.putInt("seen_get_started", 1);
        editor.commit();

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {imageView.setImageResource(sampleImages[position]);
            }
        });

        btnGetStarted = (Button) findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Carousel.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });
    }
}
