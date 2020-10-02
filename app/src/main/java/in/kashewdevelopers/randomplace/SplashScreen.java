package in.kashewdevelopers.randomplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import in.kashewdevelopers.randomplace.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appIconMarker.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_marker_animation));
        binding.appIconShadow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_shadow_animation));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MapsActivity.class));
            }
        }, 1500);
    }

}