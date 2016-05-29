/**
 * @author Sara Kinell
 * @author Annie Söderström
 * @version 2.0, 2016-05-28
 * @since 1.0, 2016-05-27
 *
 * Information for buses from Firebase.
 * Error message will be shown if the ValueEventListener fails to
 * access the server or is removed because of Firebase settings.
 *
 */

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DataGenerator {

    /**
     * Defines variables handling the reference to the Firebase database
     * and the String containing the date for which bus information is to be shown.
     */
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String chosenDate;
    private final Activity activity;

    /**
     * Constructor for the DataGenerator class.
     */
    public DataGenerator(Activity act) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        this.activity = act;
    }

    /**
     * Gets bus information (bus-ID, driving distance (km), electric energy
     * consumption (kWh) and bus type) for specified date.
     * @param date the date to get information for
     */
    public void getBusInformation(String date) {
        this.chosenDate = date;

        // Adds activity value event listener to the database reference
        ref.child(chosenDate).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Creates activity csv file with activity snapshot of the data for the chosen date
                FileSaver.createCsv(chosenDate, dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Displays an error message if the listener fails or is removed
                Toast.makeText(activity, "Lost connection to database.", Toast.LENGTH_SHORT).show();
            }
        });



    }

}




