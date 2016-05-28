/**
 * @author Sara Kinell and Annie Söderström
 * @version 1.0, 2016-05-27
 * @since 1.0
 * Generates information about buses for the chosen date.
 * Error message will be shown if the ValueEventListener fails to
 * access the server or is removed because of Firebase settings.
 *
 */

package com.example.eliasvensson.busify;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 *
 */
public class DataGenerator {

    private DatabaseReference ref;
    private FirebaseDatabase database;
    private String chosenDate;

    public DataGenerator() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }

    // Gets bus values for specified date
    public void getBusValues(String date) {
        this.chosenDate = date;
        // Add value event listener to the database reference
        ref.child(chosenDate).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FileSaver.createCsv(chosenDate, dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Cancelled ", databaseError.getMessage(), databaseError.toException());
            }
        });



    }

}




