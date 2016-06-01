/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 1.0, 2016-06-01
 * @since 1.0
 * <p/>
 * Manages email sending for a specific activity.
 */

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class EmailHandler {

    // Initializes an activity, because a reference is needed to start the Email Activity
    private Activity activity;

    // Class constructor that sets the activity needed
    public EmailHandler(Activity act){
        this.activity = act;
    }

    /**
     * Opens Android's default mail-application with a subject and an email-text
     * @param subject: This will be the subject line of the mail
     * @param emailText: This will be the text in the mail
     */
    protected void sendEmail( String subject, String emailText) {
        //Opens up the choice for sharing, e.g. via Gmail, other email clients, Slack etc.
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");

        //Sets subject and content of email
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, emailText);

        // Starts the email client
        try {
            activity.startActivity(Intent.createChooser(i, "Choose application to share report"));

        // Shows a toast if there are no email clients available
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
