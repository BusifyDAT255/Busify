package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jonathanfager on 2016-05-04.
 *
 * This method of creating a splashscreen uses Thread class.
 * A fairly small app, such as this one, might load so fast that the
 * splash screen never shows, which for branding purposes
 * is unwanted.
 * Therefore we start a thread (timerThread) and puts it to sleep for
 * X amount of ms (here 3000). During this time the splash screen is shown.
 * After the activity has done its purpose, we destroy it using the .onPause method.
 * This is so that the user cannot accidentally return to the splash screen.
 * The finally block is what launches the actual app.
 *
 * @author Jonathan Fager
 * @version 1.0
 * @since 1.0
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
                    //sleep is the duration of the splash screen
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //launches MainActivity
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        //onPause is run when activity switches. Destroys the thread and therefore the splash screen
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
