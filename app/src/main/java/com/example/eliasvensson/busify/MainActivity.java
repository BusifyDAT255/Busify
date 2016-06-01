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
 * This class works as the engine of the app. It manages the interaction with all other classes
 * associated, and changes the appearance for the main view of the app
 * <p/>
 * The main screen consists of a "Welcome" label, a "hint-label" to guide the user in
 * how to use the app, a date button to set the date and a button to share a .csv file
 * <p/>
 * The user simply chooses a date by clicking the date-button.
 * When pressing the send button, the default android mail-application starts with a
 * default email structure.
 * The default email contains a link to a .csv file which can then be accessed by the recipient
 * of the email.
 * <p/>
 * This app uses Firebase as a database and storage for .csv-files.
 * <p/>
 * NOTE: Data is only available for the 18th to the 24th of May 2016.
 * NOTE 2: If there is no existing file for the chosen date, a file will be created and uploaded to
 *         the Firebase storage. Otherwise, it will return the link to the already existing file.
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
     * Defines all the variables the app will need for execution.
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

        // Sets the view to be displayed upon the start of the app
        setContentView(R.layout.activity_main);

        // Initializes a DatabaseHandler
        databaseHandler = new DatabaseHandler(11, 5);

        // Initializes a CsvHandler object for handling everything concerning .csv-files
        csvHandler = new CsvHandler(MainActivity.this);

        // Initializes an EmailHandler for handling everything related to email sending
        emailHandler = new EmailHandler(MainActivity.this);

        // Initializes a new StorageHandler for handling all calls to the Firebase storage
        storageHandler = new StorageHandler();

        // Initializes a ProgressDialog, for use when the app is fetching data from database
        progressDialog = new ProgressDialog(this);

        // Initializes the buttons for setting date and sharing the link
        dateButton = (Button) findViewById(R.id.date_button);
        shareButton = (Button) findViewById(R.id.share_button);

        // Initializes the text view, where the date will appear
        reportDateText = (EditText) findViewById(R.id.txt_date);

        // Initializes a View.OnClickListener to listen for clicks on the dateButton and shareButton
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
                    // Opens a date dialog to let the user pick a date
                    openDateDialog();

                else if (v == shareButton) {
                    // Starts the database connection
                    databaseHandler.initiateDatabase(reportDate);

                    // Starts the progressDialog
                    progressDialog.setMessage("Generating report, please wait.");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();

                    // Finds or creates the URL for the specified date, and opens a mail-application
                    // with the link attached
                    shareReport();
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
     */
    private void shareReport() {

        // Make a reference to the date-specific file on storage
        storageHandler.setDateStorageReference("/reports/" + reportDate + ".csv");

        // Get the reference to the date-specific file on storage
        StorageReference dateReference = storageHandler.getDateStorageReference();

        // Try to get the file from Firebase storage, based on the reference defined above
        dateReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            /**
             * If the specified file exists on storage, find its URL and attach it to an email
             * @param downloadUrl
             */
            @Override
            public void onSuccess(Uri downloadUrl) {
                Log.e("LOG", "file already existed.");

                // If an URI was found, convert it to a string
                String downloadLink = downloadUrl.toString();

                // Stops the progressDialog from running
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
                buildCsv(reportDate);

                // Upload the file to Firebase storage
                storageHandler.uploadFile(csvHandler.getFilePath(reportDate));

                // Recursive call to share the report
                shareReport();
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