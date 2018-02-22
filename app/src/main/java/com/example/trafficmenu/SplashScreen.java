package com.example.trafficmenu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * First Activity shown after the application starts
 */
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
                .withBackgroundColor(Color.TRANSPARENT)
                .withLogo(R.drawable.barricade_splash)
                //.withHeaderText("Welcome to our app")
                .withAfterLogoText("Welcome to Report Traffic");

        View view = splashScreen.create();

        setContentView(view);
    }
}
