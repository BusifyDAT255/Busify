/**
 * @author Elias Svensson and David Genelov
 * @version 1.0, 2016-05-04
 * @since 1.0
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, two different textfields (start date and end date), one button for each
 * textfield to set the date, and one button to send a .csv file
 *
 * The user simply chooses a start and an end date by clicking the buttons, and then clicks the
 * "Send .csv"-button.
 *
 * When pressing the send button, an email with a .csv attachment will be sent to one or several
 * specified email addresses. The email will also contain sender, receiver, subject and body.
 *
 * Full functionality is not yet implemented.
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    EditText recieversmail;
    Button sendButton;
    Button endDateButton;
    Button startDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        //Initiates the email-textfield for the receivers mail
        recieversmail = (EditText)  findViewById(R.id.reciever_mail);

        // Initiates the buttons for setting start and end date and send
        startDateButton = (Button) findViewById(R.id.start_date_button);
        endDateButton = (Button) findViewById(R.id.end_date_button);
        sendButton = (Button) findViewById(R.id.button);

        // Initiates a View.OnClickListener to listen for clicks on the startDatebutton, endDatebutton
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == findViewById(R.id.start_date_button))
                    setDateToView(R.id.txt_start_date);

                else if (v == findViewById(R.id.end_date_button))
                    setDateToView(R.id.txt_end_date);
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
        endDateButton.setOnClickListener(listener);

        // Initiates a View.OnClickListener to listen for clicks on the send button
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Email account from which the email is sent
                Mail m = new Mail("busifydat255@gmail.com", "552tadyfisub");

                // Lists of receivers
                String reciever =recieversmail.getText().toString();
                String[] toArr = {reciever};
                m.set_to(toArr);

                // Subject and body of the email
                m.set_from("busifydat255@gmail.com");
                m.set_subject("ElectriCity Report");
                m.setBody("Please find the file attached.");

                try {
                    // Puts the .csv file (or any file type) in the sdcard of the AVD using Android Device Monitor (File Explorer)
                    m.addAttachment("/mnt/sdcard/file.csv");

                    // Shows if message was sent or not
                    if(m.send()) {
                        Toast.makeText(MainActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    Log.e("MailApp", "Could not send email", e);
                    e.printStackTrace();
                }
            }
        });
    }

}