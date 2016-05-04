package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jonathanfager on 16-05-04.
 *
 * This method of creating a splashscreen uses Thread class.
 * A fiarly small app, such as this one, might load soo fast that the
 * splash screen never shows, which for branding purposes
 * is unwanted.
 * Therefore we start a thread (timerThread) and puts it to sleep for
 * X amount of ms (here 3000). During this time the splashscreen is shown.
 * After the activity has done its purpose, we destroy it using the .onPause method.
 * This is so that the user cannot accidently return to the splash screen.
 * The finally block is what launches the actuall app.
 **/


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //Create new thread
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    //sleep is the duration of the splashscreen
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //launches MainActivity
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        //onPause is run when activity switches. Destoyes the thread and therefor the splashscreen
        super.onPause();
        finish();
    }

}
