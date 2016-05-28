/**
 * @author Elias Svensson, David Genelöv, Annie Söderström, Melinda Fulöp, Sara Kinell, Jonathan Fager
 * @version 6.0, 2016-05-24
 * @since 1.0
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, a date button to set the date and one button to send a .csv file
 *
 * The user simply chooses a date by clicking the date-button.
 *
 * When pressing the send button, the default android mail-application starts with a
 * default email structure.
 * The default email contains a link to a .csv file which can then be accessed by the recipient
 * of the email.
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    // Define variables
    Button sendButton;
    Button dateButton;
    String attachmentLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lines to test the data generator, uncomment line 63 til 77 when finished
        DataGenerator dg = new DataGenerator();
        dg.getBusValues("2016-05-18");

        /*
        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Initiates the buttons for setting date and sending emails
        dateButton = (Button) findViewById(R.id.date_button);
        sendButton = (Button) findViewById(R.id.send_button);


        // Initiates a View.OnClickListener to listen for clicks on the dateButton and sendButton
        View.OnClickListener listener = clickHandler();


        // Assigns the pre-defined listener to listen to the buttons
        dateButton.setOnClickListener(listener);
        sendButton.setOnClickListener(listener);

*/
    }

    @NonNull
    private View.OnClickListener clickHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == findViewById(R.id.date_button))
                    setDateToView(R.id.txt_date);
                else if (v == findViewById(R.id.send_button)){
                    String callDate = ((EditText)findViewById(R.id.txt_date)).getText().toString();

                        // Checks if app user has chosen a date
                         if (!callDate.isEmpty())
                             getUrlAsync(callDate);
                         else
                             Toast.makeText(MainActivity.this, "Please start by choosing a date", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    /**
     * Opens Android's default mail-application with a message of attached link and
     * link to a file.
     */
    private void sendEmail() {
        // Attachment message
        String attachmentMessage = "Please click the link to download report:\n\n";

        // Chosen date
        String date = ((EditText) findViewById(R.id.txt_date)).getText().toString();

        // Creates relevant information used the sending of the email
        // e.g. subject matter, attached message
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "Your ElectriCity report for " + date);
        i.putExtra(Intent.EXTRA_TEXT, attachmentMessage + getDownloadLink());
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
    private void setDateToView(int viewId) {
        // Initiates a DateDialog object for user interaction when choosing the date
        DateDialog dialog = new DateDialog(findViewById(viewId));
        // Sets a FragmentManager to track the interaction with the DateDialog-fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Sets the DateDialog as visible to the user
        dialog.show(ft, "DatePicker");
    }



    /**
     * Calls the server to securely obtain an unguessable download Url
     * using an async call.
     * @param date should be in the format of "YYYY-MM-DD"
     * onSuccess sets the the downloadLink by call to setDownloadLink
     * and initiates the email by call to sendEmail
     * onFailure opens a dialog telling the user that no report is available for this date.
     *
     */
   private void getUrlAsync (String date){
       Task<Uri> link;
       // Points to the root reference
       StorageReference storageRef = FirebaseStorage.getInstance().getReference();
       // Points to the specific file depending on date
       StorageReference dateRef = storageRef.child("/" + date + ".csv");
       link = dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
       {
           @Override
           public void onSuccess(Uri downloadUrl)
           {
               setDownloadLink(downloadUrl);
               sendEmail();
           }

       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, 1);
               builder.setMessage("Sorry, no report available for this date.");
               builder.setCancelable(true);

               builder.setPositiveButton(
                       "Ok!",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                           }
                       });


               AlertDialog alert = builder.create();
               alert.show();
           }
       });


    }

    /**
     * Getter and setter for download link.
     * @param link the URL link for the .csv-file
     */

    private void setDownloadLink(Uri link){
        attachmentLink = link.toString();
    }

    private String getDownloadLink(){
        return attachmentLink;
    }

}