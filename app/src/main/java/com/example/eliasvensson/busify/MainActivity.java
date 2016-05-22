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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


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
                    String attachmentLink = niceLink(((EditText)findViewById(R.id.txt_date)).getText().toString());
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
     * Returns a link to a specific .csv-file, corresponding to a date input
     * @param date The date to find a file for
     * @return A String with the link corresponding to the date for the chosen file
     */
    /**
    private String chooseURL (String date) {
        String link;
        switch(date){
            case "19-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-19.csv?alt=media&token=20520547-18b7-458d-9019-e1dc3cdd83cd";
                break;
            case "20-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-20.csv?alt=media&token=ed2ee38e-c97a-4d81-ae5f-9824f842cfed";
                break;
            case "21-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-21.csv?alt=media&token=4b247cec-23ea-4522-a99a-e573d550230f";
                break;
            case "22-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-22.csv?alt=media&token=f05266c0-3281-4112-8a4e-1fbf96bd6929";
                break;
            case "23-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-23.csv?alt=media&token=222481d8-3d30-4069-b40d-4f631855a437";
                break;
            case "24-5-2016":
                link = "https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-24.csv?alt=media&token=6daa827c-8bbe-4189-999a-e89f21b3f483";
                break;
            case "25-5-2016":
                link ="https://firebasestorage.googleapis.com/v0/b/dat255-busify.appspot.com/o/2016-05-25.csv?alt=media&token=e4ea105a-40c8-439d-9006-56514a22dfcf";
                break;
            default:
                link ="Report missing for chosen date";
                break;
        }
        return link;
    }
**/
    private String niceLink (String date){
        String link;
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("/" + date + ".csv");
        link = dateRef.getDownloadUrl().toString();
        return link;
    }


}