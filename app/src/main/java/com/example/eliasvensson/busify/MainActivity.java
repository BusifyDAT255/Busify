/**
 * @author Elias Svensson, David Genelöv, Annie Söderström, Melinda Fulöp, Sara Kinell
 * @version 5.0, 2016-05-22
 * @since 1.0
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, a date button to set the date and one button to send a .csv file
 *
 * The user simply chooses a date by clicking the date-button,
 *
 * When pressing the send button, the default android mail-application starts with a
 * default email structure.
 * The default email contains a link to a .csv file which can then be accessed by the recipient
 * of the email.
 *
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Initiates the buttons for setting date and sending emails
        dateButton = (Button) findViewById(R.id.date_button);
        sendButton = (Button) findViewById(R.id.send_button);

        // Initiates a View.OnClickListener to listen for clicks on the dateButton and sendButton
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == findViewById(R.id.date_button))
                    setDateToView(R.id.txt_date);
                else if (v == findViewById(R.id.send_button)){
                    //Returns a link to the file corresponding to the chosen date
                    String attachmentLink = chooseURL(((EditText)findViewById(R.id.txt_date)).getText().toString());
                    //Opens Androids default mail-app with a link to the above file attached.
                    sendEmail(attachmentLink);
                }
            }
        };

        // Assigns the pre-defined listener to listen to the button
        dateButton.setOnClickListener(listener);
        sendButton.setOnClickListener(listener);
    }

    /**
     * Opens Androids default mail-application with a link to a file attached.
     * @param  attachmentLink The link to the file
     *
     */
    private void sendEmail(String attachmentLink){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Your ElectriCity report");
        i.putExtra(Intent.EXTRA_TEXT   , attachmentLink);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates an instance of the class DateDialog, which opens the DateDialog
     * @param  viewId  the ID of the view which the method will write the returned date to.
     *
     */
    private void setDateToView(int viewId){
        // Initiates a DateDialog object for user interaction when choosing the date
        DateDialog dialog = new DateDialog(findViewById(viewId));
        // Sets a FragmentManager to track the interaction with the datedialog-fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Sets the dateDialog as visible to the user
        dialog.show(ft, "DatePicker");
    }

    /**
     * Returns a link to a specific .csv-file, corresponding to a date input.
     * Links are shortened using Bit.ly.
     * @param date The date to find a file for
     * @return A String with the link corresponding to the date for the chosen file
     */
    private String chooseURL (String date) {
        String link;
        switch(date){
            case "19-5-2016":
                link = "http://bit.ly/1TPhSX8";
                break;
            case "20-5-2016":
                link = "http://bit.ly/1swJzxr";
                break;
            case "21-5-2016":
                link = "http://bit.ly/1Tw2zp3";
                break;
            case "22-5-2016":
                link = "http://bit.ly/1TPhW9l";
                break;
            case "23-5-2016":
                link = "http://bit.ly/20mupWC";
                break;
            case "24-5-2016":
                link = "http://bit.ly/25g6ajx";
                break;
            case "25-5-2016":
                link ="http://bit.ly/20muYzv";
                break;
            default:
                link ="Report missing for chosen date";
                break;
        }
        return link;
    }
}