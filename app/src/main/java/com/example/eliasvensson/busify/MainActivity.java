/**
 * @author Elias Svensson, David Genelov, Annie Söderström, Melinda Fulöp, Sara Kinell
 * @version 1.0, 2016-05-04
 * @since 1.0
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, two different textfields (start date and end date), one button for each
 * textfield to set the date, and one button to send a .csv file
 *
 * The user simply chooses a start and an end date by clicking the startDatebutton and endDateButton,
 * and then enters one or several email addresses.
 * After that the app user clicks the "Send .csv"-button.
 *
 * When pressing the send button, an email with a .csv attachment will be sent to one or several
 * specified email addresses. The email will also contain sender, receiver, subject and body.
 *
 * Full functionality is not yet implemented.
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    EditText receiversmail;
    Button sendButton;
    //Button endDateButton;
    Button startDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        //Initiates the email-textfield for the receivers mail
        receiversmail = (EditText)  findViewById(R.id.receiver_mail);

        // Initiates the buttons for setting start and end date and send
        startDateButton = (Button) findViewById(R.id.start_date_button);
            //endDateButton = (Button) findViewById(R.id.end_date_button);
        sendButton = (Button) findViewById(R.id.button);

        // Initiates a View.OnClickListener to listen for clicks on the startDatebutton, endDatebutton
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v == findViewById(R.id.start_date_button))
                    setDateToView(R.id.txt_start_date);

                //else if (v == findViewById(R.id.end_date_button))
                  //  setDateToView(R.id.txt_end_date);
            }



            /**
             *Creates an instance of the class DateDialog, which opens the DateDialog
             * @param  viewId  the ID of the view which the method will write the returned date to.
             *
             */
            private void setDateToView(int viewId){
                //Initiates a DateDialog object for user interaction when choosing the date
                DateDialog dialog = new DateDialog(findViewById(viewId));
                //Sets a FragmentManager to track the interaction with the datedialog-fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //Sets the dateDialog as visible to the user
                dialog.show(ft, "DatePicker");
            }
        };

        //Assigns the pre-defined listener to listen to the two buttons
        startDateButton.setOnClickListener(listener);
        //endDateButton.setOnClickListener(listener);

        // Initiates a View.OnClickListener to listen for clicks on the send button
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Lists all receivers as a string
                String receiver =receiversmail.getText().toString();
                //Separates the receivers into a list and adds them to mail
                String[] ReceiversList = TextUtils.split(receiver, ",");
                //Fulkod för att hitta filen.
                String AttachmentLink = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-04-27.csv?alt=media&token=40aa1d0b-9e22-4ed4-a7a6-1b2e805711b6";

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , ReceiversList);
                i.putExtra(Intent.EXTRA_SUBJECT, "Your ElectriCity report");
                i.putExtra(Intent.EXTRA_TEXT   , AttachmentLink);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}