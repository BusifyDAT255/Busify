/**
 * @author Elias Svensson and David Genelov
 * @version 1.0, 2015-05-04
 *
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, two different textfields (start date and end date), one button for each
 * textfield to set the date, and one button to send a .csv file
 *
 * The user simply chooses a start and an end date by clicking the buttons, and then clicks the
 * "send .csv"-button.
 *
 * Full functionality is not yet implemented.
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    /** Initiates all the functionality in the app when it is started
     * @param savedInstanceState The current state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Initiates the buttons for setting start and end date
        Button fromDateButton = (Button) findViewById(R.id.start_date_button);
        Button toDateButton = (Button) findViewById(R.id.end_date_button);

        // Initiates a View.OnClickListener to listen for clicks on the buttons
        View.OnClickListener listener = new View.OnClickListener() {
            @Override

            /** Lets the user specify a date, and updates the text of a view to match that date.
             * @param v The view which will get its text updated
             *
             */
            public void onClick(View v) {
                if (v == findViewById(R.id.start_date_button))
                    setDateToView(R.id.txt_from_date);

                else if (v == findViewById(R.id.end_date_button))
                    setDateToView(R.id.txt_to_date);
            }

            /**
             *This method creates an instance of the class DateDialog, which does the
             * @param  viewId  the ID of the view which the method will write the returned date to.
             *
             */
            private void setDateToView(int viewId){
                //Initiates a DateDialog object for user interaction when choosing the date
                DateDialog dialog = new DateDialog(findViewById(viewId));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        };

        //Assigns the pre-defined listener to listen to the two buttons
        fromDateButton.setOnClickListener(listener);
        toDateButton.setOnClickListener(listener);
    }
}