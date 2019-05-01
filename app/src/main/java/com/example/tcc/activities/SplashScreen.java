package com.example.tcc.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MostrarLogin();
            }
        }, 2000);
    }

    private void MostrarLogin() {
        Intent login = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(login);
        finish();
    }
}
