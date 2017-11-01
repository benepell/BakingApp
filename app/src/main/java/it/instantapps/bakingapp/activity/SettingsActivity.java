package it.instantapps.bakingapp.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            FrameLayout frameLayout = findViewById(R.id.fragment_settings_container);
            LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(SettingsActivity.this, R.anim.layout_animation_from_right);
            frameLayout.setLayoutAnimation(layoutAnimationController);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.homeActivity(this);
    }


}
