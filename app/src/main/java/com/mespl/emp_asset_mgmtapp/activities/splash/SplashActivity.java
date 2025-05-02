package com.mespl.emp_asset_mgmtapp.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.mespl.emp_asset_mgmtapp.activities.login.LoginActivity;
import com.mespl.emp_asset_mgmtapp.R;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }, 3000);

    }

}