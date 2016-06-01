/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 10.0, 2016-06-01
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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    /**
     * Defines variables for the DatePicker button, the button used to share
     * the link and the link attached in the email to be sent.
     * A storage reference and DatabaseHandler is defined.
     */
    private Button shareButton;
    private Button dateButton;
    private ProgressDialog progressDialog;
    private DatabaseHandler databaseHandler;
    private CsvHandler csvHandler;
    private String reportDate;
    private EmailHandler emailHandler;
    private StorageHandler storageHandler;
    private EditText reportDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes a DatabaseHandler
        databaseHandler = new DatabaseHandler(11, 5);

        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Creates a CsvHandler object for handling everything concerning .csv-files
        csvHandler = new CsvHandler(MainActivity.this);

        // Creates an EmailHandler for handling everything related to email sending
        emailHandler = new EmailHandler(MainActivity.this);

        //Creates a new StorageHandler for handling everything with Firebase storage
        storageHandler = new StorageHandler();

        // Initiates a ProgressDialog, for use when the app is fetching data from database
        progressDialog = new ProgressDialog(this);

        // Initiates the buttons for setting date and sharing the link
        dateButton = (Button) findViewById(R.id.date_button);
        shareButton = (Button) findViewById(R.id.share_button);

        //Initiate text view
        reportDateText = (EditText) findViewById(R.id.txt_date);

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
                if (v == dateButton)
                    openDateDialog();
                else if (v == shareButton) {

                    // Starts the database connection
                    databaseHandler.initiateDatabase(reportDate);

                    // Starts the progressDialog bar
                    progressDialog.setMessage("Generating report");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();

                    // Finds or creates the URL for the specified date, and opens a mail-application
                    // with the link attached
                    shareReport(reportDate);
                }
            }
        };
    }

    /**
     * Calls the server to securely obtain an unguessable download Url
     * using an async call.
     * onSuccess sets the the downloadLink by call to setDownloadLink
     * and initiates the email by call to sendEmail
     * onFailure opens a dialog telling the user that no report is available for this date.*
     * @param date should be in the format of "YYYY-MM-DD"
     */
    private void shareReport(final String date) {

        // Make a reference to the date-specific file on storage
        storageHandler.setDateStorageReference("/reports/" + date + ".csv");

        // Get the reference to the date-specific file on storage
        StorageReference dateRef = storageHandler.getDateStorageReference();

        // Try to get the file from Firebase storage, based on the reference defined above
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            /**
             * If the specified file exists on storage, find its URL and attach it to an email
             * @param downloadUrl
             */
            @Override
            public void onSuccess(Uri downloadUrl) {
                Log.e("LOG", "file already existed.");
                String downloadLink = downloadUrl.toString();

                // Stops the progressDialog bar from running
                progressDialog.cancel();

                // Sends an email with the URL attached
                emailHandler.sendEmail("Your ElectriCity report for " + reportDate
                        , "Please click the link to download report:\n\n" + downloadLink);
            }

        }).addOnFailureListener(new OnFailureListener() {
            /**
             * If the specified file does not exist on storage, make a new one and attach to an email
             */
            @Override
            public void onFailure(@NonNull Exception e) {

                //Create a .csv-file and upload to Firebase storage
                buildCsv(date);

                // Upload the file to Firebase storage
                storageHandler.uploadFile(csvHandler.getFilePath(reportDate));

                // Recursive call to share the report
                shareReport(reportDate);
            }
        });
    }

    /**
     * Takes a String with a date, gets the data from that date from database,
     * saves it as a .csv-file on internal storage, and displays the
     * filepath of this file in a toast.
     *
     * @param reportDate date of file to convert to a .csv-file.
     */
    private void buildCsv(String reportDate) {

        // Queries data from Firebase
        String[][] busData = databaseHandler.getBusInformation(reportDate);

        // Writes the data to a .csv-file
        csvHandler.writeFileFromArray(reportDate, busData);
    }

    /**
     * Creates an instance of the class DateDialog, which opens the DateDialog
     *
     */
    private void openDateDialog() {
        // Initiates a DateDialog object for user interaction when choosing the date
        DateDialog dialog = new DateDialog(MainActivity.this);

        // Sets a FragmentManager to track the interaction with the DateDialog-fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // Sets the DateDialog as visible to the user
        dialog.show(ft, "DatePicker");
    }

    protected void setReportDate(String reportDate){
        this.reportDate = reportDate;
        reportDateText.setText(reportDate);

        //Re-enables the shareButton in the MainActivity class when the text is set.
        shareButton.setEnabled(true);
    }

}