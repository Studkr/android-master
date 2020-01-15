package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.INT.apps.GpsspecialDevelopment.R;

public class Splash extends AppCompatActivity {
    private int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(SPLASH_TIME_OUT);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                    Splash.this.finish();
                }
            }
        };
        timerThread.start();
    }

}
