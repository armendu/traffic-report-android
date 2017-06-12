package com.example.abidat.trafficmenu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //First hide the action bar
        getSupportActionBar().hide();
        EasySplashScreen splashScreen = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(500)
                .withBackgroundColor(Color.parseColor("#3e492f"))
                .withLogo(R.drawable.barricade)
                .withHeaderText("")
                .withAfterLogoText("Welcome to Report Traffic");

        splashScreen.getHeaderTextView().setTextColor(Color.WHITE);

        View view = splashScreen.create();

        setContentView(view);
    }
}
