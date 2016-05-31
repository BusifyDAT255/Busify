package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Jonathan Fager
 * @version 2.0, 2016-05-06
 * @since 1.0, 2016-05-04
 *
 * The method of creating a SplashScreen, onCreate, uses the Thread class.
 * A fairly small app, such as this one, might load so fast that the
 * splash screen never shows, which for branding purposes
 * is unwanted. Therefore, we start a thread (timerThread) and puts it to sleep for
 * X amount of ms (here 3000). During this time the splash screen is shown.
 * After the activity has fulfilled its purpose, we destroy it using the onPause method.
 * Consequently, the user cannot accidentally return to the splash screen.
 * The finally block launches the actual app.
 *
 **/


public class SplashScreen extends Activity {

    protected static DataGenerator dataGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        dataGenerator = new DataGenerator(this, 100, 100);
        dataGenerator.getBusInformation("2016-05-18");

        //Creates new thread
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    //Sets the duration of the splash screen
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Launches MainActivity
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        //Runs when activity switches. Destroys the thread and therefore the splash screen
        super.onPause();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
