/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 9.0, 2016-05-31
 * @since 1.0
 * <p/>
 * Manages the interaction with, and function of, the main view of the app.
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, a date button to set the date and one button to send a .csv file
 * <p/>
 * The user simply chooses a date by clicking the date-button.
 * When pressing the send button, the default android mail-application starts with a
 * default email structure.
 * The default email contains a link to a .csv file which can then be accessed by the recipient
 * of the email.
 * <p/>
 * Note: The class works as intended, but is in need of some serious refactoring.
 */

package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    /**
     * Defines variables for the DatePicker button, the button used to share
     * the link and the link attached in the email to be sent.
     * A storage reference and DataGenerator is defined.
     */
    protected Button shareButton;
    protected Button dateButton;
    private String attachmentLink;
    protected ProgressDialog progress;
    protected DataGenerator dataGenerator;
    protected StorageReference storageRef;
    protected CsvHandler csvHandler;
    protected String callDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes a DataGenerator
        dataGenerator = new DataGenerator(this, 11, 5);

        // Initiates a storage reference to the root reference
        storageRef = FirebaseStorage.getInstance().getReference();

        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Creates a CsvHandler object for handling everything concerning .csv-files
        csvHandler = new CsvHandler(MainActivity.this);

        // Initiates progressbar
        progress = new ProgressDialog(this);

        // Initiates the buttons for setting date and sharing the link
        dateButton = (Button) findViewById(R.id.date_button);
        shareButton = (Button) findViewById(R.id.share_button);

        // Initiates a View.OnClickListener to listen for clicks on the dateButton and shareButton
        View.OnClickListener listener = clickHandler();

        // Assigns the pre-defined listener to listen to the buttons
        dateButton.setOnClickListener(listener);
        shareButton.setOnClickListener(listener);

        // Disables the shareButton by default, so the user has to start by choosing date
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

                    // Saves the user specified date as a String
                    callDate = ((EditText) findViewById(R.id.txt_date)).getText().toString();

                    // Creates a thread to handle time delay in database access
                    Thread databaseTimer = new Thread() {
                        public void run() {
                            try {
                                // Makes a call to the database to get access
                                dataGenerator.getBusInformation(callDate);
                                // Waits to get access to the database
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    // Starts the timer
                    databaseTimer.start();

                    // Starts the progress bar
                    progress.setMessage("Generating report");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();

                    // Finds or creates the URL for the specified date, and opens a mail-application
                    // with the link attached
                    getUrlAsync(callDate);
                }
            }
        };
    }

    /**
     * Calls the server to securely obtain an unguessable download Url
     * using an async call.
     * onSuccess sets the the downloadLink by call to setDownloadLink
     * and initiates the email by call to sendEmail
     * onFailure opens a dialog telling the user that no report is available for this date.
     * TODO: refactor getUrlAsync method to two methods, getUrlAsync and sendEmail();
     *
     * @param date should be in the format of "YYYY-MM-DD"
     */
    private void getUrlAsync(final String date) {

        // Make a reference to the date-specific file on storage
        StorageReference dateRef = storageRef.child("/reports/" + date + ".csv");

        // Try to get the file from Firebase storage, based on the reference defined above
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            /**
             * If the specified file exists on storage, find its URL and attach it to an email
             * @param downloadUrl
             */
            @Override
            public void onSuccess(Uri downloadUrl) {
                setDownloadLink(downloadUrl);
                sendEmail();
            }

        }).addOnFailureListener(new OnFailureListener() {
            /**
             * If the specified file does not exist on storage, make a new one and attach to an email
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                buildCsv(date);
            }
        });
    }

    /**
     * Takes a String with a date, gets the data from that date from database,
     * saves it as a .csv-file on internal storage, and displays the
     * filepath of this file in a toast.
     *
     * @param callDate date of file to convert to a .csv-file.
     */
    private void buildCsv(String callDate) {
        // Queries data from Firebase
        String[][] busData = dataGenerator.getBusInformation(callDate);

        // Writes the data to a .csv-file
        csvHandler.writeFileFromArray(callDate, busData);

        // Saves the file path to that .csv-file to a String
        String filePath = csvHandler.getFilePath(callDate);

        // Upload the file to storage and open email app with the link to the file attached
        csvHandler.csvUploader(filePath);
    }

    /**
     * Opens Android's default mail-application with a message of attached link and
     * link to a file.
     */
    protected void sendEmail() {

        // Stop the progress bar from running
        progress.cancel();

        //Opens up the choice for sharing, e.g. via Gmail, other email clients, Slack etc.
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");

        //Sets subject and content of email
        i.putExtra(Intent.EXTRA_SUBJECT, "Your ElectriCity report for " + callDate);
        i.putExtra(Intent.EXTRA_TEXT, "Please click the link to download report:\n\n" + getDownloadLink());

        // Starts the email client
        try {
            startActivity(Intent.createChooser(i, "Choose application to share report"));
            // Shows a toast if there is no email client available
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates an instance of the class DateDialog, which opens the DateDialog
     *
     * @param viewId the ID of the view which the method will write the returned date to.
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
     * Getter and setter for download link.
     *
     * @param link the URL link for the .csv-file
     */
    public void setDownloadLink(Uri link) {
        attachmentLink = link.toString();
    }

    /**
     * Gets the download link to the last created attachment
     *
     * @return The link to the created attachment
     */
    private String getDownloadLink() {
        return attachmentLink;
    }
}