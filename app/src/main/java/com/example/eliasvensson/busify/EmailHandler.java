/**
 * @author David Genelöv
 * @author Annie Söderström
 * @version 1.0, 2016-06-01
 * @since 1.0
 * <p/>
 * Manages email sending for a specific activity.
 */

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

public class EmailHandler {

    private Activity activity;

    public EmailHandler(Activity act){
        this.activity = act;
    }

    /**
     * Opens Android's default mail-application with a message of attached link and
     * link to a file.
     */

    protected void sendEmail( String subject, String emailText, ProgressDialog progress) {

        // Stop the progress bar from running
        progress.cancel();

        //Opens up the choice for sharing, e.g. via Gmail, other email clients, Slack etc.
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");

        //Sets subject and content of email
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, emailText);

        // Starts the email client
        try {
            activity.startActivity(Intent.createChooser(i, "Choose application to share report"));
            // Shows a toast if there is no email client available
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
