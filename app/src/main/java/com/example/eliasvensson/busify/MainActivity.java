/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 8.0, 2016-05-30
 * @since 1.0
 *
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, a date button to set the date and one button to send a .csv file
 *
 * The user simply chooses a date by clicking the date-button.
 * When pressing the send button, the default android mail-application starts with a
 * default email structure.
 * The default email contains a link to a .csv file which can then be accessed by the recipient
 * of the email.
 *
 * Note: The class is under construction. It can generate information shown in Android Monitor
 * for the following dates: 2016-05-18 and 2016-05-19. Please try these dates initially when testing.
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * Defines variables for the DatePicker button, the button used to share
     * the link and the link attached in the email to be sent.
     * A storage reference and DataGenerator is defined.
     */
    protected Button shareButton;
    protected Button dateButton;
    private static String attachmentLink;
    DataGenerator dgenerator;
    StorageReference storageRef;
    CsvHandler csvHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes a DataGenerator
        dgenerator = new DataGenerator(MainActivity.this, 11, 4);

        // Initiates a storage reference to the root reference
        storageRef = FirebaseStorage.getInstance().getReference();

        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        csvHandler = new CsvHandler(MainActivity.this);

        // Initiates the buttons for setting date and sharing the link
        dateButton = (Button) findViewById(R.id.date_button);
        shareButton = (Button) findViewById(R.id.share_button);

        // Initiates a View.OnClickListener to listen for clicks on the dateButton and shareButton
        View.OnClickListener listener = clickHandler();

        // Assigns the pre-defined listener to listen to the buttons
        dateButton.setOnClickListener(listener);
        shareButton.setOnClickListener(listener);

        // Disables the shareButton by default
        shareButton.setEnabled(false);
    }

    @NonNull
    private View.OnClickListener clickHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == findViewById(R.id.date_button))
                    setDateToView(R.id.txt_date);
                else if (v == findViewById(R.id.share_button)) {
                    // Disables the button to prohibit several mail-apps to open at once
                    shareButton.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Generating report, please wait", Toast.LENGTH_SHORT).show();

                    //Saves the date chosen by the user as a String
                    String callDate = ((EditText) findViewById(R.id.txt_date)).getText().toString();

                    // Checks if file already exists
                    // TODO: Fix this if-statement. It's broken and always returns true
                    StorageReference dateRef = storageRef.child("/" + callDate + ".csv");
                    File file = new File(dateRef.getPath());
                    if (!file.exists()) {
                        // Queries data from Firebase
                        String[][] busData = dgenerator.getBusInformation(callDate);
                        // Writes the data to a .csv-file
                        csvHandler.writeFileFromArray(callDate, busData);
                        // Saves the file path to that .csv-file to a String
                        String filePath = csvHandler.getFilePath(callDate);
                        // Shows the information in a String
                        // TODO: Delete this Toast when file upload to fireBase works
                        //Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();


                        // TODO: Take the filepath (URI) and upload file to FireBase
                        //csvHandler.csvUploader(filePath);
                        // TODO: return a String (URL) to file
                        csvHandler.csvUploader(filePath);
                        // TODO: Call method to open email app with URL attached

                    } else {
                        // TODO: refactor getUrlAsync method to two methods, getUrlAsync and sendEmail();
                        //Gets the URL of the file that already exists on Firebase Storage
                        getUrlAsync(callDate);
                    }
                }
            }
        };
    }

    /**
     * Opens Android's default mail-application with a message of attached link and
     * link to a file.
     */
    protected void sendEmail() {
        // Attachment message
        String attachmentMessage = "Please click the link to download report:\n\n";

        // Chosen date
        String date = ((EditText) findViewById(R.id.txt_date)).getText().toString();

        //Opens up the choice for sharing
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");

        //Sets subject and content of email
        i.putExtra(Intent.EXTRA_SUBJECT, "Your ElectriCity report for " + date);
        i.putExtra(Intent.EXTRA_TEXT, attachmentMessage + getDownloadLink());

        // Starts the email client
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
            // Shows a toast if there is no email client available
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates an instance of the class DateDialog, which opens the DateDialog
     *
     * @param viewId ID of the view which the method will write the returned date to.
     */
    private void setDateToView(int viewId) {
        // Initiates a DateDialog object for user interaction when choosing the date
        DateDialog dialog = new DateDialog(findViewById(viewId), MainActivity.this);

        // Sets a FragmentManager to track the interaction with the DateDialog-fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Sets the DateDialog as visible to the user
        dialog.show(ft, "DatePicker");
    }

    /**
     * Calls the server to securely obtain an unguessable download Url
     * using an async call.
     *
     * @param date should be in the format of "YYYY-MM-DD"
     *             onSuccess sets the the downloadLink by call to setDownloadLink
     *             and initiates the email by call to sendEmail
     *             onFailure opens a dialog telling the user that no report is available for this date.
     *             TODO: Comment this method
     */
    protected void getUrlAsync(String date) {

        // Points to the specific file depending on date
        StorageReference dateRef = storageRef.child("/" + date + ".csv");
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                setDownloadLink(downloadUrl);
                sendEmail();
                //Re-enables the "Share-button" when user returns to the view with share button
                shareButton.setEnabled(true);
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
     * Sets the downloadlink to the specified Uri.
     * @param link the URL link for the .csv-file
     */
    public static void setDownloadLink(Uri link) {
        attachmentLink = link.toString();
    }

    /**
     * Gets the download link to the last created attachment
     * @return The link to the created attachment
     */
    private String getDownloadLink() {
        return attachmentLink;
    }

}