/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 3.0, 2016-06-01
 * @since 1.0, 2016-05-04
 *
 * The class handling the SplashScreen, which shows onCreate.
 * A fairly small app, such as this one, might load so fast that the
 * splash screen never shows, which for branding purposes
 * is unwanted. Therefore, we start a thread (timerThread) and puts it to sleep for
 * X amount of ms (here 3000). During this time the splash screen is shown.
 * After the activity has fulfilled its purpose, we destroy it using the onPause method.
 * Consequently, the user cannot accidentally return to the splash screen.
 * The finally block launches the actual app.
 *
 **/

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //Creates new thread for showing the splash screen
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
        //Runs when activity switches and destroys the thread, and therefore the splash screen
        super.onPause();
        finish();
    }
}
